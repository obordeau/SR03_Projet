package fr.utc.sr03.chat.model;

/* cette classe permettra de support au messages transmis via les WebSockets */
public class Message {

    private String from;
    private String text;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}