package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.Objects;

@MapperTable(nombre = "message")
public class Message extends PiikObject {
    @MapperColumn(pkey = true, hasDefault = true)
    private String id;
    @MapperColumn(notNull = true)
    private String text;
    @MapperColumn(pkey = true, columna = "author", fKeys = "sender", targetClass = User.class)
    private User sender;
    @MapperColumn(fKeys = "multimedia", targetClass = Multimedia.class)
    private Multimedia multimedia;
    @MapperColumn(pkey = true, hasDefault = true)
    private Timestamp date;
    @MapperColumn(fKeys = "ticket", targetClass = Ticket.class)
    private Ticket ticket;

    public Message() {
    }

    public Message(Ticket ticket) {
        this.ticket = ticket;
        this.text = ticket.getTextProblem();
        this.setDate(new Timestamp(10000));  // Todo fix Timestamp usage
        this.sender = ticket.getUser();
    }

    public Message(Message message) {
        this.id = message.getId();
        this.text = message.getText();
        this.sender = message.getSender();
        this.multimedia = message.getMultimedia();
        this.date = message.getDate();
        this.ticket = message.getTicket();
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

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    /**
     * This function checks if the values of the primary keys are not null or are not empty
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
    // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        return id != null && sender != null;
    }*/

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    /*
    public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        return getText() != null && !getText().isEmpty();
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id) &&
                sender.equals(message.sender) &&
                date.equals(message.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sender, date);
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                '}';
    }
}
