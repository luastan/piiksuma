package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.Objects;

@MapperTable(nombre = "privateMessage")
public class Message {
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn
    private String text;
    @MapperColumn(pkey = true)
    private String sender;
    @MapperColumn
    private String multimedia;
    @MapperColumn(pkey = true, hasDefault = true)
    private Timestamp date;
    @MapperColumn
    private String ticket;

    public Message() {
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * This function checks if the values of the primary keys are not null or are not empty
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey(){
        if(id==null || sender ==null){
            return false;
        }
        return true;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }

        if(getText() == null || getText().isEmpty()){
            return false;
        }

        return true;
    }

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
}
