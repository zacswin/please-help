package session;

import entity.Category;
import entity.Comment;
import entity.CommentDTO;
import entity.Role;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import entity.Ticket;
import entity.TicketDTO;
import entity.TicketStatus;
import entity.User;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.SessionContext;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@DeclareRoles({"administrator", "user", "readonly"})
@Stateless
public class TicketManagement implements TicketManagementRemote {

    @Resource
    SessionContext ctx;

    @EJB
    private TicketFacadeLocal ticketFacade;

    @EJB
    private UserFacadeLocal userFacade;

    @EJB
    private CategoryFacadeLocal categoryFacade;

    private Comment createCommentFromDTO(CommentDTO commentDTO) {

        return new Comment(
                commentDTO.getId(),
                commentDTO.getText(),
                commentDTO.getDate(),
                commentDTO.getAuthor());
    }

    private CommentDTO createDTOFromComment(Comment comment) {
        if (comment == null) {
            return null;
        }

        return new CommentDTO(
                comment.getId(),
                comment.getText(),
                comment.getDate(),
                comment.getAuthor());
    }

    private Ticket createTicketFromDTO(TicketDTO ticketDTO) {
        if (ticketDTO == null) {
            return null;
        }
        Ticket ticket = new Ticket(
                ticketDTO.getId(),
                ticketDTO.getEmail(),
                ticketDTO.getTitle(),
                ticketDTO.getDescription(),
                ticketDTO.getStatus(),
                ticketDTO.getDatecreated()
        );

        List<Comment> comments = ticketDTO.getComments().stream().map(this::createCommentFromDTO).collect(Collectors.toList());
        ticket.setCommentList(comments);

        ticket.setCategory(CategoryManagement.createCategoryFromDTO(ticketDTO.getCategory()));

        return ticket;
    }

    private TicketDTO createDTOFromTicket(Ticket ticket) {
        if (ticket == null) {
            return null;
        }

        return new TicketDTO(
                ticket.getId(),
                ticket.getEmail(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getDateCreated(),
                ticket.getCommentList().stream().map(this::createDTOFromComment).collect(Collectors.toList()),
                CategoryManagement.createDTOFromCategory(ticket.getCategory()));

    }

    private String getTicketEmailDetails(Ticket ticket, String action, String message) {
        return "<h2>Your ticket &quot;" + ticket.getTitle() + "&quot; " + action + "</h2>"
                + "<p>" + message + "</p>"
                + "<p>Click <a href=\"http://localhost:8080/PleaseHelp-war/faces/respondToTicket.xhtml?token=" + ticket.getToken() + "\">here</a>"
                + " to view the current status of your ticket</p>";
    }

    private void sendEmail(String to, String subject, String body) {
        String smtpServer = "127.0.0.1";
        String from = "noreply@please-help.com";

        String emailUser = from;
        String password = "P4ssw0rd";
        try {
            Properties props = System.getProperties();
            // -- Attaching to default Session, or we could start a new one --
            props.put("mail.smtp.host", smtpServer);
            props.put("mail.smtp.port", 1025);
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            // -- prepare a password authenticator --
            // get a session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUser, password);
                }
            });
            // -- Create a new message --
            Message msg = new MimeMessage(session);

            // -- Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // -- Set the subject and body text --
            msg.setSubject("[Please Help!] - " + subject);

            // -- Set some other header information --
            msg.setHeader("X-Mailer", "MailDev");
            msg.setContent(body, "text/html");
            msg.setSentDate(new Date());

            Transport.send(msg, emailUser, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean isTicketOpen(Ticket ticket) {
        return ticket.getStatus() == TicketStatus.OPEN || ticket.getStatus() == TicketStatus.WAITING;
    }

    private Role getRoleFilter() {
        Principal callerPrincipal = ctx.getCallerPrincipal();
        String userId = callerPrincipal.getName();
        User user = userFacade.find(Integer.parseInt(userId));

        Role roleFilter = user.getRole();
        if (roleFilter == null) {
            throw new Error("Fatal error, expecter user to have a role");
        }
        // If the user is an administrator, they can view/modify any ticket
        if (user.getRole().getPrivilege().equals("administrator")) {
            roleFilter = null;
        }
        return roleFilter;
    }

    private boolean isAdmin() {
        return getRoleFilter() == null;
    }

    private boolean canAccessTicket(String ticketId) {
        Role requestRole = getRoleFilter();

        // Admins can access any ticket
        if (isAdmin()) {
            return true;
        }

        Ticket ticket = ticketFacade.find(ticketId);
        if (ticket == null) {
            return false;
        }

        return ticket.getCategory().getRoleList().stream().anyMatch(role -> role.equals(requestRole));
    }

    @Override
    @RolesAllowed({"readonly", "user", "administrator"})
    public List<TicketDTO> findAll() {
        return findAll(null);
    }

    @Override
    @RolesAllowed({"readonly", "user", "administrator"})
    public List<TicketDTO> findAll(TicketStatus filter) {
        return ticketFacade.findAll(getRoleFilter(), filter).stream().map(this::createDTOFromTicket).collect(Collectors.toList());
    }

    @Override
    @RolesAllowed({"readonly", "user", "administrator"})
    public TicketDTO find(String ticketId) {
        if (!canAccessTicket(ticketId)) {
            return null;
        }
        return createDTOFromTicket(ticketFacade.find(ticketId));
    }

    @Override
    @RolesAllowed({"user", "administrator"})
    public boolean addComment(String ticketId, String text) {
        if (!canAccessTicket(ticketId)) {
            return false;
        }

        Ticket ticket = ticketFacade.find(ticketId);
        if (ticket == null || !isTicketOpen(ticket)) {
            return false;
        }

        Principal callerPrincipal = ctx.getCallerPrincipal();
        Integer userId = Integer.parseInt(callerPrincipal.getName());
        User user = userFacade.find(userId);
        if (user == null) {
            return false;
        }

        ticketFacade.addComment(text, user.getName(), ticket);
        ticket.setStatus(TicketStatus.WAITING);

        sendEmail(
                ticket.getEmail(),
                "New ticket comment",
                getTicketEmailDetails(ticket, " has received a new comment", user.getName() + " said: &quot;" + text + "&quot;"));
        return ticketFacade.updateTicket(ticket);
    }

    @Override
    @RolesAllowed({"user", "administrator"})
    public boolean setStatus(String ticketId, TicketStatus newStatus) {
        if (!canAccessTicket(ticketId)) {
            return false;
        }

        Ticket ticket = ticketFacade.find(ticketId);

        if (ticket == null) {
            return false;
        }

        if (ticket.getStatus() == TicketStatus.LOCKED // Locked tickets cannot be modified
                || (newStatus == TicketStatus.WAITING) // WAITING is only set internally
                || (newStatus == TicketStatus.LOCKED && !isAdmin()) // Only admins can lock tickets
                || (newStatus == TicketStatus.CLOSED && !isTicketOpen(ticket)) // Cannot close an already closed ticket
                || (newStatus == TicketStatus.OPEN && isTicketOpen(ticket))) {  // Cannot open an already open ticket
            return false;
        }

        ticket.setStatus(newStatus);
        sendEmail(
                ticket.getEmail(),
                "Ticket status update",
                getTicketEmailDetails(ticket, " has been updated", "Your ticket has been updated to " + newStatus.toString().toLowerCase() + "."));
        return ticketFacade.updateTicket(ticket);
    }

    /* Public methods */
    @Override
    public TicketDTO findByToken(String token
    ) {
        return createDTOFromTicket(ticketFacade.findByToken(token));
    }

    @Override
    public boolean addPublicComment(String ticketToken, String email, String commentText) {
        if (commentText == null || commentText.isEmpty()) {
            return false;
        }

        Ticket ticket = ticketFacade.findByToken(ticketToken);
        if (ticket == null) {
            return false;
        }

        if (!isTicketOpen(ticket)) {
            return false;
        }

        // This email acts as a password, since this page is technically 'public'.
        // However, the token is unique and should not be shared. (it is extremely unlikely that a user will bruteforce a correct UUID)
        if (!ticket.getEmail().equals(email)) {
            return false;
        }

        // The author is null, to represent that the comment was posted by the user.
        ticketFacade.addComment(commentText, null, ticket);
        ticket.setStatus(TicketStatus.OPEN);
        return ticketFacade.updateTicket(ticket);
    }

    @Override
    public boolean submitTicket(String email, String title, String description, Integer categoryId) {
        if (email == null || email.isEmpty() || title == null || title.isEmpty() || categoryId == null) {
            return false;
        }

        Category category = categoryFacade.find(categoryId);
        if (category == null) {
            return false;
        }

        Ticket newTicket = new Ticket();
        newTicket.setEmail(email);
        newTicket.setTitle(title);
        newTicket.setDescription(description);
        newTicket.setDateCreated(Calendar.getInstance().getTime());
        newTicket.setStatus(TicketStatus.OPEN);
        newTicket.setCategory(category);

        sendEmail(
                newTicket.getEmail(),
                "Ticket received",
                getTicketEmailDetails(newTicket, " has been received", "A member of staff will reply shortly."));

        return ticketFacade.addTicket(newTicket);
    }
}
