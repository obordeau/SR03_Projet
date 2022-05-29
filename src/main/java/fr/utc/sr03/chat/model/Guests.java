package fr.utc.sr03.chat.model;

import javax.persistence.*;

@Entity
@Table(name = "guests")
public class Guests {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY) // strategy=GenerationType.IDENTITY => obligatoire pour auto increment mysql
    private long id;
    private int user;

    private int channel;

    public Guests(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }
}
