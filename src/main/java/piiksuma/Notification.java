package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable
public class Notification {
    @MapperColumn
    private String id;
    @MapperColumn
    private String creationDate;
    @MapperColumn
    private String content;

    public Notification(){

    }

    public Notification(String id, String creationDate, String content) {
        this.id = id;
        this.creationDate = creationDate;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean checkPrimaryKey(){
        if(id==null || id.isEmpty()){
            return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notification)) return false;

        Notification notification = (Notification) o;

        return getId().equals(notification.getId());

    }
}
