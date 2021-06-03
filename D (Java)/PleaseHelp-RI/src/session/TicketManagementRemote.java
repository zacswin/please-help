package session;

import javax.ejb.Remote;
import entity.TicketDTO;
import entity.TicketStatus;
import java.util.List;

@Remote
public interface TicketManagementRemote {

    List<TicketDTO> findAll();

    List<TicketDTO> findAll(TicketStatus filter);

    TicketDTO find(String ticketId);

    TicketDTO findByToken(String token);

    boolean addComment(String ticketId, String text);

    boolean addPublicComment(String ticketToken, String email, String commentText);

    boolean setStatus(String ticketId, TicketStatus newStatus);
    
    boolean submitTicket(String email, String title, String description, Integer categoryId);

}
