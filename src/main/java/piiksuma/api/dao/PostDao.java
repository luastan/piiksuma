package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;

import java.sql.Connection;
import java.util.List;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


    /* Methods */

    /**
     * Function that add the post to the database
     * @param post post to add to the database with the creator
     * @param current current user logged in the app
     * @return post passed by parameter
     */
    public void createPost(Post post, User current) {

        if(post == null){
            return;
        }

        if(current == null){
            return;
        }

        // Check that the current user is an administrator
        if(current.getType().equals(UserType.user)){
            return;
        }

        // Check that the post satisfies with the restrictions
        if(!post.checkNotNull()){
            return;
        }

        new InsertionMapper<Post>(super.getConnection()).add(post).definirClase(Post.class).insertar();
    }

    public Post updatePost(Post post, User user, User current) {
        return null;
    }

    public void removePost(Post post, User user, User current) {
        if (post == null){
            return;
        }
        if (!post.checkPrimaryKey()){
            return;
        }

        new DeleteMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).delete();




    }

    public Post getPost(Post post, User user, User current) {
        return null;
    }

    public List<Post> getPost(Hashtag hashtag, User current) {
        return null;
    }

    public List<Post> getPost(User user, User current) {
        return null;
    }

    public Post repost(Post repost, User userRepost, Post post, User userPost, User current) {
        return null;
    }

    public void removeRepost(Post repost, User user, User current) {


    }

    public Post reply(Post reply, User userReply, Post post, User userPost, User current) {
        return null;
    }

    public Hashtag createHashtag(Hashtag hashtag, User current) {
        return null;
    }

    /**
     * Function to get the hashtag that meet with the specifications
     * @param hashtag hashtag that contains the specification about the hashtag to search
     * @param current current user logged in the app
     * @return hashtag with all the information
     */
    public Hashtag getHashtag(Hashtag hashtag, User current){
        if(hashtag == null){
            return null;
        }

        if(!hashtag.checkPrimaryKey()){
            return null;
        }

        return new QueryMapper<Hashtag>(super.getConnection()).crearConsulta("SELECT * FROM hashtag " +
                "WHERE name LIKE '?'").definirEntidad(Hashtag.class).definirParametros(hashtag.getName()).findFirst();
    }

    /**
     * Function to get the hashtags that meet with the specifications
     * @param hashtag hashtag that contains the specification about the hashtags to search
     * @param current current user logged in the app
     * @return hashtags with all the information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, User current){
        if(hashtag == null){
            return null;
        }

        return new QueryMapper<Hashtag>(super.getConnection()).crearConsulta("SELECT * FROM hashtag " +
                "WHERE name LIKE '%?%'").definirEntidad(Hashtag.class).definirParametros(hashtag.getName()).list();
    }

    /**
     * Function to search posts which have a specific text in it
     * @param text given text to search
     * @param currentUser current user logged in the app
     * @return list of the posts which has the given text in it
     */
    public List<Post> searchByText(String text, User currentUser){
        if(text==null){
            //TODO lanzar excepciones oportunas o avisar al usuario etc...
            return null;
        }

        return new QueryMapper<Post>(super.getConnection()).crearConsulta("SELECT * FROM post WHERE UPPER(text) " +
                "LIKE UPPER(?)").definirEntidad(Post.class).definirParametros("%" + text + "%").list();
    }
}
