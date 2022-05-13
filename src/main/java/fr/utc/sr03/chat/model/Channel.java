package fr.utc.sr03.chat.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "channel")
public class Channel {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "start_date")
    private Date start_date;
    @Column(name = "end_date")
    private Date end_date;
    @Column(name = "owner")
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

    public Date getStartDate() {
        return start_date;
    }

    public void setStartDate(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEndDate() {
        return end_date;
    }

    public void setEndDate(Date end_date) {
        this.end_date = end_date;
    }
}
