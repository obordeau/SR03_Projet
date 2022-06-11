package fr.utc.sr03.chat.model;

import javax.persistence.*;

@Entity
@Table(name = "attempts")
public class Attempts {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;

    @OneToOne @JoinColumn(name = "user")
    private User user;

    private Integer number_attempts;

    private Integer account_blocked;

    public Attempts() {}

    public long getId() {
        return id;
    }

    public Integer getAccount_blocked() {
        return account_blocked;
    }

    public User getUser() {
        return user;
    }

    public Integer getNumber_attempts() {
        return number_attempts;
    }

    public void setAccount_blocked(Integer account_blocked) {
        this.account_blocked = account_blocked;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setNumber_attempts(Integer number_attempts) {
        this.number_attempts = number_attempts;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
