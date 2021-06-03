package web;

import entity.CategoryDTO;
import entity.CommentDTO;
import entity.TicketDTO;
import entity.TicketStatus;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import javax.ejb.EJB;
import javax.inject.Named;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import session.TicketManagementRemote;

@Named(value = "ticketManagementBean")
@Stateless
public class TicketManagementBean implements Serializable {

    @EJB
    private TicketManagementRemote ticketManagement;

    private List<TicketDTO> tickets;

    private TicketStatus filter;

    private String id;
    private String email;
    private String title;
    private String description;
    private TicketStatus status;
    private Date dateCreated;
    private List<CommentDTO> comments;
    private CategoryDTO category;
    private String commentText;
    private String saveResult;

    public TicketManagementBean() {
        filter = null;
    }

    private void loadTickets() {
        tickets = ticketManagement.findAll(filter);
    }

    private void updateTicketDetails(TicketDTO ticket) {
        id = ticket.getId();
        email = ticket.getEmail();
        title = ticket.getTitle();
        description = ticket.getDescription();
        status = ticket.getStatus();
        dateCreated = ticket.getDatecreated();
        category = ticket.getCategory();
        commentText = "";
        saveResult = "";

        List<CommentDTO> newComments = ticket.getComments();
        newComments.sort(new Comparator<CommentDTO>() {
            @Override
            public int compare(CommentDTO a, CommentDTO b) {
                return a.getDate().compareTo(b.getDate());
            }
        });
        this.comments = newComments;
    }

    public String renderTicket(String ticketId) {
        TicketDTO ticket = ticketManagement.find(ticketId);

        if (ticket == null) {
            return "failure";
        }

        updateTicketDetails(ticket);
        return "success";
    }

    public String addComment() {
        if (ticketManagement.addComment(id, commentText)) {
            saveResult = "success";
            return renderTicket(id);
        }
        saveResult = "failure";
        return saveResult;
    }

    public String setStatus(TicketStatus status) {
        if (ticketManagement.setStatus(id, status)) {
            this.status = status;
            saveResult = "success";
            return renderTicket(id);
        }
        saveResult = "failure";
        return saveResult;

    }

    public List<TicketDTO> getTickets() {
        loadTickets();
        System.out.println(tickets);
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TicketStatus getFilter() {
        return filter;
    }

    public String setFilter(TicketStatus filter) {
        this.filter = filter;
        loadTickets();
        return "success";
    }

    public String removeFilter() {
        filter = null;
        loadTickets();
        return "success";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date datecreated) {
        this.dateCreated = datecreated;
    }

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getSaveResult() {
        return saveResult;
    }

    public void setSaveResult(String saveResult) {
        this.saveResult = saveResult;
    }

}
