package piiksuma.api.dao;

import piiksuma.Hashtag;
import piiksuma.Post;
import piiksuma.User;

import java.sql.Connection;
import java.util.List;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


    /* Methods */
/*
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
}
