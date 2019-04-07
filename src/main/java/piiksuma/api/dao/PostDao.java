package piiksuma.api.dao;

import piiksuma.Hashtag;
import piiksuma.Message;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.database.QueryMapper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


    /* Methods */

    public Post createPost(Post post, User user, User current) {
        return null;
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
