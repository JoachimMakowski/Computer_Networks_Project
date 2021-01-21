package client;

/**
 * Message class
 */
public class Message {

    //Sender's nickname
    private String nick;

    //Text message
    private String message;

    //Time the message was sent
    private String messageDate;


    public Message(String nick, String message, String messageDate) {
        this.nick = nick;
        this.message = message;
        this.messageDate = messageDate;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentDate() {
        return messageDate;
    }

    public void setSentDate(String messageDate) {
        this.messageDate = messageDate;
    }

}
