package fr.utc.sr03.chat.model;

public class PasswordVerification {
    private String passwordRepetition;

    private String previousPassword;

    private String newPassword;

    public String getPasswordRepetition() {
        return passwordRepetition;
    }

    public String getPreviousPassword() {
        return previousPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setPasswordRepetition(String passwordRepetition) {
        this.passwordRepetition = passwordRepetition;
    }

    public void setPreviousPassword(String previousPassword) {
        this.previousPassword = previousPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
