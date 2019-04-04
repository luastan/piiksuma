package piiksuma;

public class Post {
    private String publicationDate;
    private String fatherPost;/*Date when the father post was creaetd*/
    private String postAuthor;
    private String multimedia;

    public Post(){

    }

    public Post(String postAuthor, String publicationDate){

        if(postAuthor == null){
            this.postAuthor = "";
        } else {
            this.postAuthor = postAuthor;
        }

        if(publicationDate == null){
            this.publicationDate = "";
        } else {
            this.publicationDate = publicationDate;
        }

        this.fatherPost = "";
        this.multimedia = "";
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
}
