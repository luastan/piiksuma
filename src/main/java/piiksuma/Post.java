package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

@MapperTable(nombre = "post")
public class Post {
    @MapperColumn(pkey = true)
    private String postAuthor;
    @MapperColumn(pkey = true)
    private String id;
    @MapperColumn
    private String text;
    private String publicationDate;/*Date when the father post was creaetd*/
    private String fatherPost;
    private String multimedia;

    public Post() {

    }

    public Post(String postAuthor, String publicationDate) {

        if (postAuthor == null) {
            this.postAuthor = "";
        } else {
            this.postAuthor = postAuthor;
        }

        if (publicationDate == null) {
            this.publicationDate = "";
        } else {
            this.publicationDate = publicationDate;
        }

        this.fatherPost = "";
        this.multimedia = "";
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

    public String getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(String publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getFatherPost() {
        return fatherPost;
    }

    public void setFatherPost(String fatherPost) {
        this.fatherPost = fatherPost;
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

    public String toString() {
        return "Post{" +
                " Id:" + '\'' + this.id + '\'' +
                " Texto:" + '\'' + this.text + '\'' +
                " Author:" + '\'' + this.postAuthor + '\'' + "}";

    }
}
