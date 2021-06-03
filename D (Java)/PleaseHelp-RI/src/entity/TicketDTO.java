package entity;

import java.util.List;
import java.util.Date;

public class TicketDTO {

    private String id;
    private String email;
    private String title;
    private String description;
    private TicketStatus status;
    private Date dateCreated;
    private List<CommentDTO> comments;
    private CategoryDTO category;

    public TicketDTO(String id, String email, String title, String description, TicketStatus status, Date dateCreated, List<CommentDTO> comments, CategoryDTO category) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dateCreated = dateCreated;
        this.comments = comments;
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public Date getDatecreated() {
        return dateCreated;
    }

    public void setDatecreated(Date datecreated) {
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

}
