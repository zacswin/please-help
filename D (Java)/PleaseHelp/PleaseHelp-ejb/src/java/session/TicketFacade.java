package session;

import entity.Comment;
import entity.Role;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entity.Ticket;
import entity.TicketStatus;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Query;

@Stateless
public class TicketFacade implements TicketFacadeLocal {

    @PersistenceContext(unitName = "PleaseHelp-PersistenceUnit")
    private EntityManager em;

    public TicketFacade() {
    }

    private void create(Ticket entity) {
        em.persist(entity);
    }

    private void edit(Ticket entity) {
        em.merge(entity);
    }

    @Override
    public Ticket find(String ticketId) {
        return em.find(Ticket.class, ticketId);
    }

    @Override
    public List<Ticket> findAll(Role onlyFindForRole) {
        return findAll(onlyFindForRole, null);
    }

    @Override
    public List<Ticket> findAll(Role onlyFindForRole, TicketStatus filter) {
        Query query;
        if (filter == null) {
            query = em.createNamedQuery("Ticket.findAll");
        } else {
            query = em.createNamedQuery("Ticket.findByStatus");
            query.setParameter("status", filter);
        }

        List<Ticket> tickets = query.getResultList();
        if (onlyFindForRole != null) {
            return tickets.stream()
                    .filter(ticket -> ticket.getCategory().getRoleList().contains(onlyFindForRole))
                    .collect(Collectors.toList());
        }

        return tickets;
    }

    @Override
    public boolean hasTicket(String ticketId) {
        return find(ticketId) != null;
    }

    @Override
    public boolean addTicket(Ticket ticket) {
        // check again - just to play it safe
        Ticket e = find(ticket.getId());
        System.out.println(ticket);
        if (e != null) {
            // could not add one
            return false;
        }
        create(ticket);

        return true;
    }

    @Override
    public boolean updateTicket(Ticket ticket) {
        Ticket e = find(ticket.getId());

        if (e == null) {
            return false;
        }

        edit(ticket);

        return true;
    }

    @Override
    public void addComment(String text, String author, Ticket ticket) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setDate(Calendar.getInstance().getTime());
        comment.setAuthor(author);
        comment.setTicketid(ticket);

        List<Comment> comments = ticket.getCommentList();
        comments.add(comment);
        ticket.setCommentList(comments);
        edit(ticket);

    }

    @Override
    public Ticket findByToken(String token) {
        Query query = em.createNamedQuery("Ticket.findByToken");
        query.setParameter("token", token);
        List<Ticket> tickets = query.getResultList();

        // No ticket found with that token
        if (tickets.isEmpty()) {
            return null;
        }

        if (tickets.size() > 1) {
            throw new Error("Fatal error, two tickets share the same token");
        }

        return ((List<Ticket>) query.getResultList()).get(0);
    }

}
