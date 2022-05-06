package fr.utc.sr03.chat.model;

import javax.persistence.*;
import java.sql.Time;

@Entity
@Table(name = "message")
public class Message {
    private Integer user;
    private String content;
    private Time hour;
    private Integer channel;

    public Message(){}

    public Integer getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }

    public Time getHour() {
        return hour;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setHour(Time hour) {
        this.hour = hour;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}