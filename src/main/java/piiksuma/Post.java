package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

@MapperTable(nombre = "post")
public class Post {
    @MapperColumn(pkey = true, columna="author")
    private String postAuthor;
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn
    private String text;
    @MapperColumn(hasDefault = true)
    private Timestamp publicationDate;/*Date when the father post was creaetd*/
    @MapperColumn
    private String sugarDaddy;
    @MapperColumn(columna = "authorDaddy")
    private String fatherPost;
    @MapperColumn
    private String multimedia;

    public Post() {

    }

    public Post(String postAuthor, Timestamp publicationDate) {

        if (postAuthor == null) {
            this.postAuthor = "";
        } else {
            this.postAuthor = postAuthor;
        }

        if (publicationDate == null) {
            this.publicationDate = null;
        } else {
            this.publicationDate = publicationDate;
        }

        this.fatherPost = "";
        this.multimedia = "";
    }

    public Post(String postAuthor, String id, String text, Timestamp publicationDate, String sugarDaddy, String fatherPost, String multimedia) {
        this.postAuthor = postAuthor;
        this.id = id;
        this.text = text;
        this.publicationDate = publicationDate;
        this.sugarDaddy = sugarDaddy;
        this.fatherPost = fatherPost;
        this.multimedia = multimedia;
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

    public Timestamp getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Timestamp publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getFatherPost() {
        return fatherPost;
    }

    public void setFatherPost(String fatherPost) {
        this.fatherPost = fatherPost;
    }

    public String getSugarDaddy() {
        return sugarDaddy;
    }

    public void setSugarDaddy(String sugarDaddy) {
        this.sugarDaddy = sugarDaddy;
    }

    public String getPostAuthor() {
        return postAuthor;
    }

    public void setPostAuthor(String postAuthor) {
        this.postAuthor = postAuthor;
    }

    public String getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(String multimedia) {
        this.multimedia = multimedia;
    }

    /**
     * Function to check that the attributes with restriction 'not null' are not null
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    public boolean checkNotNull(){
        // Check that the primary keys are not null
        if(!checkPrimaryKey()){
            return false;
        }

        return(text != null && !text.isEmpty());
    }

    /**
     * Function to check that the primary keys are not null
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    public boolean checkPrimaryKey(){
        // Check that the primary keys are not null
        if(getId() == null || getId().isEmpty()){
            return false;
        }

        if(getPostAuthor() == null || getPostAuthor().isEmpty()){
            return false;
        }

        return true;
    }


    public String toString() {
        return "Post{" +
                " Id:" + '\'' + this.id + '\'' +
                " Texto:" + '\'' + this.text + '\'' +
                " Author:" + '\'' + this.postAuthor + '\'' + "}";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return postAuthor.equals(post.postAuthor) &&
                id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postAuthor, id);
    }
}
