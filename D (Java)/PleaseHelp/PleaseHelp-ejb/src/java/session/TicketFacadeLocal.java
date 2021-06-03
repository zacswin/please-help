package session;

import entity.Role;
import entity.Ticket;
import entity.TicketStatus;
import java.util.List;
import javax.ejb.Local;

@Local
public interface TicketFacadeLocal {

    List<Ticket> findAll(Role onlyFindForRole);

    List<Ticket> findAll(Role onlyFindForRole, TicketStatus filter);

    Ticket find(String ticketId);
    
    Ticket findByToken(String token);

    boolean hasTicket(String ticketId);

    boolean addTicket(Ticket ticket);

    boolean updateTicket(Ticket ticket);

    void addComment(String text, String author, Ticket ticket);

}
