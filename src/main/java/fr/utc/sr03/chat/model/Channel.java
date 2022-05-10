package fr.utc.sr03.chat.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String title;
    private String description;
    private Date startDate;
    private Date endDate;
    private Integer owner;

    public Channel() {}

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public Integer getOwner() {
        return owner;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // public void addUser(Integer newuser) {
    //}

    // public void removeUser(Integer user) {
    //}

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
