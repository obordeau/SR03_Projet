package fr.utc.sr03.chat.model;

public class OutputMessage extends Message{
    private String time;

    public OutputMessage(String from, String text, String time) {
        this.setFrom(from);
        this.setText(text);
        this.time = time;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
