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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import session.CategoryManagementRemote;
import session.TicketManagementRemote;

@Named(value = "publicTicketBean")
@Stateless
public class PublicTicketBean implements Serializable {

    @EJB
    private TicketManagementRemote ticketManagement;

    @EJB
    private CategoryManagementRemote categoryManagement;

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
    private String token;
    private List<CategoryDTO> categories;
    private Integer categoryId;

    private boolean ticketExists;

    public PublicTicketBean() {
    }

    @PostConstruct
    public void loadCategories() {
        categories = categoryManagement.findAll();
    }

    public void loadTicket() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = context.getExternalContext().getRequestParameterMap();
        token = paramMap.get("token");

        if (token == null || token.isEmpty()) {
            ticketExists = false;
            return;
        }

        TicketDTO ticket = ticketManagement.findByToken(token);

        if (ticket == null) {
            ticketExists = false;
            return;
        }

        ticketExists = true;
        updateTicketDetails(ticket);

    }

    private void updateTicketDetails(TicketDTO ticket) {
        id = ticket.getId();
        title = ticket.getTitle();
        description = ticket.getDescription();
        status = ticket.getStatus();
        dateCreated = ticket.getDatecreated();
        category = ticket.getCategory();
        commentText = "";

        List<CommentDTO> newComments = ticket.getComments();
        newComments.sort(new Comparator<CommentDTO>() {
            @Override
            public int compare(CommentDTO a, CommentDTO b) {
                return a.getDate().compareTo(b.getDate());
            }
        });
        this.comments = newComments;
    }

    public String addComment() {
        if (ticketManagement.addPublicComment(token, email, commentText)) {
            return "success";
        }
        return "failure";
    }

    public String submitTicket() {
        if (ticketManagement.submitTicket(email, title, description, categoryId)) {
            return "success";
        }
        return "failure";
    }

    public String getId() {
        loadTicket();
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

    public boolean isTicketExists() {
        return ticketExists;
    }

    public void setTicketExists(boolean ticketExists) {
        this.ticketExists = ticketExists;
    }

    public String getSaveResult() {
        return saveResult;
    }

    public void setSaveResult(String saveResult) {
        this.saveResult = saveResult;
    }

    public List<CategoryDTO> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryDTO> categories) {
        this.categories = categories;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

}
