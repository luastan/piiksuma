package piiksuma.api.dao;

import java.sql.Connection;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


    /* Methods */

    public Post createPost(Post post, User user, User current) {

    }

    public Post updatePost(Post post, User user, User current) {

    }

    public void removePost(Post post, User user, User current) {

    }

    public Post getPost(Post post, User user, User current) {

    }

    public List<Post> getPost(Hashtag hashtag, User current) {

    }

    public List<Post> getPost(User user, User current) {

    }

    public Post repost(Post repost, User userRepost, Post post, User userPost, User current) {

    }

    public removeRepost(Post repost, User user, User current) {

    }

    public Post reply(Post reply, User userReply, Post post, User userPost, User current) {

    }

    public Hashtag createHashtag(Hashtag hashtag, User current) {

    }
}
