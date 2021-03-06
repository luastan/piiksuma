package piiksuma;

import piiksuma.database.MapperColumn;
import piiksuma.database.MapperTable;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@MapperTable(nombre = "post")
public class Post extends PiikObject{
    @MapperColumn(pkey = true, fKeys = "author:id", targetClass = User.class)
    private User author;
    @MapperColumn(pkey = true, hasDefault = true)
    private String id;
    @MapperColumn(notNull = true)
    private String text;
    @MapperColumn(hasDefault = true, columna = "publicationdate")
    private Timestamp publicationDate;
    @MapperColumn(fKeys = "sugarDaddy:id authorDaddy:author", targetClass = Post.class)
    private Post fatherPost;
    @MapperColumn(fKeys = "multimedia:hash", targetClass = Multimedia.class)
    private Multimedia multimedia;
    private List<Hashtag> hashtags;

    public Post() {
        this.hashtags = new ArrayList<>();
    }

    public Post(Post post) {
        this.author = post.getAuthor();
        this.id = post.getId();
        this.text = post.getText();
        this.publicationDate = post.getPublicationDate();
        this.fatherPost = post.getFatherPost();
        this.multimedia = post.getMultimedia();
        this.hashtags = post.getHashtags();
        this.hashtags = new ArrayList<>();
    }

    public Post(User author, Timestamp publicationDate) {

        this.author = author;

        this.publicationDate = publicationDate;

        this.fatherPost = null;
        this.multimedia = null;
        this.hashtags = new ArrayList<>();
    }

    public Post(User author, String id, String text, Timestamp publicationDate, Post father, Multimedia multimedia) {
        this.author = author;
        this.id = id;
        this.text = text;
        this.publicationDate = publicationDate;
        this.fatherPost = father;
        this.multimedia = multimedia;
        this.hashtags = new ArrayList<>();
    }

    public Post(String idAuthor, String id, String text, Timestamp publicationDate, String sugarDaddy,
                String authorDaddy, String idMultimedia){

        this(new User("", idAuthor, ""), id, text, publicationDate,
                new Post(new User("", authorDaddy, ""), null),
                new Multimedia(idMultimedia, "", ""));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    public void addHashtag(Hashtag hashtag){
        if(hashtag != null) {
            this.hashtags.add(hashtag);
        }
    }

    public void addAllHashtags(List<Hashtag> hashtags){
        if(hashtags != null){
            this.hashtags.addAll(hashtags);
        }
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

    public Post getFatherPost() {
        return fatherPost;
    }

    public void setFatherPost(Post fatherPost) {
        this.fatherPost = fatherPost;
    }

    // TODO duplicated
    public User getPostAuthor() {
        return author;
    }

    public void setPostAuthor(User postAuthor) {
        this.author = postAuthor;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(Multimedia multimedia) {
        this.multimedia = multimedia;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    // TODO hashtags are missing
    /**
     * Function to check that the attributes with restriction 'not null' are not null
     *
     * @return the function return "true" if the attributes are not null, otherwise return "false"
     */
    /*public boolean checkNotNull() {
        // Check that the primary keys are not null
        if (!checkPrimaryKey()) {
            return false;
        }
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        return (text != null && !text.isEmpty());
    }*/

    /**
     * Function to check that the primary keys are not null
     *
     * @return the function return "true" if the primary keys are not null, otherwise return "false"
     */
    /*public boolean checkPrimaryKey() {
        // Check that the primary keys are not null
        if (getId() == null || getId().isEmpty()) {
            return false;
        }
        // TODO Eliminar esto una vez se compruebe el funcionamiento del PiikObject
        return getPostAuthor() != null && getPostAuthor().checkPrimaryKey();

    }*/


    public String toString() {
        return "Post{" +
                " Id:" + '\'' + this.id + '\'' +
                " Parent:" + '\'' + this.fatherPost + '\'' +
                " Texto:" + '\'' + this.text + '\'' +
                " Author:" + '\'' + this.author + '\'' + "}";

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return author.equals(post.author) &&
                id.equals(post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, id);
    }
}
