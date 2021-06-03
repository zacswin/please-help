package entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Zac
 */
@Entity
@Table(name = "COMMENT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Comment.findAll", query = "SELECT c FROM Comment c")
    , @NamedQuery(name = "Comment.findById", query = "SELECT c FROM Comment c WHERE c.id = :id")
    , @NamedQuery(name = "Comment.findByText", query = "SELECT c FROM Comment c WHERE c.text = :text")
    , @NamedQuery(name = "Comment.findByDate", query = "SELECT c FROM Comment c WHERE c.date = :date")})
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(generator = "uuid")
    @Basic(optional = false)
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "TEXT")
    private String text;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;
    @Basic(optional = false)
    @Size(min = 1, max = 255)
    @Column(name = "AUTHOR")
    private String author;
    @JoinColumn(name = "TICKETID", referencedColumnName = "ID")
    @ManyToOne
    private Ticket ticketid;

    public Comment() {
        this.id = UUID.randomUUID().toString();
    }

    public Comment(String id) {
        this.id = id;
    }

    public Comment(String id, String text, Date date, String author) {
        this.id = id;
        this.text = text;
        this.date = date;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Ticket getTicketid() {
        return ticketid;
    }

    public void setTicketid(Ticket ticketid) {
        this.ticketid = ticketid;
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
        if (!(object instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Comment[ id=" + id + " ]";
    }

}
