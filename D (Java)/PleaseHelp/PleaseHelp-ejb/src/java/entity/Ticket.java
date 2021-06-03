package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "TICKET")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Ticket.findAll", query = "SELECT t FROM Ticket t")
    , @NamedQuery(name = "Ticket.findById", query = "SELECT t FROM Ticket t WHERE t.id = :id")
    , @NamedQuery(name = "Ticket.findByEmail", query = "SELECT t FROM Ticket t WHERE t.email = :email")
    , @NamedQuery(name = "Ticket.findByTitle", query = "SELECT t FROM Ticket t WHERE t.title = :title")
    , @NamedQuery(name = "Ticket.findByDescription", query = "SELECT t FROM Ticket t WHERE t.description = :description")
    , @NamedQuery(name = "Ticket.findByStatus", query = "SELECT t FROM Ticket t WHERE t.status = :status")
    , @NamedQuery(name = "Ticket.findByToken", query = "SELECT t FROM Ticket t WHERE t.token = :token")
    , @NamedQuery(name = "Ticket.findByDateCreated", query = "SELECT t FROM Ticket t WHERE t.dateCreated = :dateCreated")
    , @NamedQuery(name = "Ticket.findByCategory", query = "SELECT t FROM Ticket t WHERE t.category = :category")})
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "EMAIL")
    private String email;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TITLE")
    private String title;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS")
    @Convert(converter = TicketStatusConverter.class)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 36)
    @Column(name = "TOKEN")
    private String token;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATECREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    @JoinColumn(name = "CATEGORYID", referencedColumnName = "ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Category category;
    @OneToMany(mappedBy = "ticketid")
    private List<Comment> commentList;

    public Ticket() {
        this.id = UUID.randomUUID().toString();
        this.token = UUID.randomUUID().toString();
        this.commentList = new ArrayList<Comment>();
    }

    public Ticket(String id) {
        this.id = id;
    }

    public Ticket(String id, String email, String title, String description, TicketStatus status, Date dateCreated) {
        this.id = id;
        this.email = email;
        this.title = title;
        this.description = description;
        this.status = status;
        this.token = UUID.randomUUID().toString();
        this.dateCreated = dateCreated;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @XmlTransient
    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Ticket)) {
            return false;
        }
        Ticket other = (Ticket) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Ticket[ id=" + id + " ]";
    }

}
