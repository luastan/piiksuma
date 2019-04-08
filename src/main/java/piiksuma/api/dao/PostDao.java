package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.QueryMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


    /* Methods */

    /**
     * Function that add the post to the database
     * @param post post to add to the database
     * @param user user that create the post
     * @param current current user logged in the app
     * @return post passed by parameter
     */
    public Post createPost(Post post, User user, User current) {

        if(post == null){
            return null;
        }

        if(user == null){
            return null;
        }

        if(current == null){
            return null;
        }

        // Check that the current user is an administrator
        if(current.getType().equals(UserType.user)){
            return null;
        }

        // Check that the user satisfies with the restrictions
        if(!user.checkNotNull()){
            return null;
        }

        // Check that the post satisfies with the restrictions
        if(!post.checkNotNull()){
            return null;
        }

        // Get connection
        Connection con = super.getConnection();
        PreparedStatement stmPost = null;

        try{
            // Prepare insertion
            stmPost = con.prepareStatement("insert into post(author, id, text, sugarDaddy, authorDaddy, " +
                    "multimedia) values(?,?,?,?,?,?)");

            // Set attributes
            stmPost.setString(1, user.getId());
            stmPost.setString(2, post.getId());
            stmPost.setString(3, post.getText());
            stmPost.setString(4, post.getSugarDaddy());
            stmPost.setString(5, post.getFatherPost());
            stmPost.setString(6, post.getMultimedia());

            stmPost.executeUpdate();
        } catch(SQLException e){
            e.printStackTrace();
        } finally {
            try {
                stmPost.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }

        return post;
    }

    public Post updatePost(Post post, User user, User current) {
        return null;
    }

    public void removePost(Post post, User user, User current) {

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

        List<Post> resultPosts=new QueryMapper<Post>(super.getConnection()).crearConsulta("SELECT * FROM post WHERE text LIKE ?")
                                                                            .definirEntidad(Post.class).definirParametros("%" + text + "%").list();

        return resultPosts;
    }
}
