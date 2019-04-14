package piiksuma.api.dao;

import piiksuma.Hashtag;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
     * Function that adds a post into the database
     *
     * @param post post to add, with its creator, into the database
     */
    public void createPost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        new InsertionMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).insert();
    }


    /**
     * Function to update the text content of the post
     *
     * @param post post to be updated
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */

    public void updatePost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        new UpdateMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).update();
    }

    /**
     * Function to archive a post privately by an user
     *
     * @param post post to be archived
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */
    public void archivePost(Post post, User user) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new QueryMapper<Post>(super.getConnection()).createQuery("INSERT into archivePost values (?,?,?)").defineClass(Post.class).defineParameters(post.getId(), user.getId(), post.getPostAuthor()).executeUpdate();
    }

    /**
     * Function that removes a post from the database
     *
     * @param post post to remove from the database
     */
    public void removePost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        new DeleteMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).delete();
    }

    public Post getPost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT * FROM post WHERE id = ? " +
                "and author = ?").defineParameters(post.getId(), post.getPostAuthor()).findFirst();
    }

    public List<Post> getPost(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT p.* FROM ownHashtag as o, post as p" +
                "WHERE o.hashtag = ? AND p.id=o.post AND p.author=o.author").defineParameters(hashtag.getName()).list();
    }

    public List<Post> getPost(User user) throws PiikDatabaseException {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT p.* FROM post as p WHERE p.author = " +
                "?").defineParameters(user.getEmail()).list();
    }

    /**
     * Retrieves the posts that a user has archived
     *
     * @param user user whose archived posts will be retrieved
     * @return found posts
     */
    public List<Post> getArchivedPosts(User user) throws PiikDatabaseException {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT p.* FROM post as p, archivePost as a" +
                "WHERE p.id = a.post AND p.author = a.author AND a.usr = ?").defineParameters(user.getEmail()).list();
    }

    public Post repost(Post repost, User userRepost, Post post, User userPost) throws PiikDatabaseException {

        if (repost == null || !repost.checkNotNull()) {
            throw new PiikDatabaseException("(repost) Primary key constraints failed");
        }

        if (userRepost == null || !userRepost.checkNotNull()) {
            throw new PiikDatabaseException("(userRepost) Primary key constraints failed");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        if (userPost == null || !userPost.checkNotNull()) {
            throw new PiikDatabaseException("(userPost) Primary key constraints failed");
        }

        return null;
    }

    /**
     * Function to do a retweet on a post
     *
     * @param userRetweet User who does the retweet
     * @param post        Post to be retweeted
     * @param userPost    User who owns the post
     * @throws PiikDatabaseException
     */
    public void retweet(User userRetweet, Post post, User userPost) throws PiikDatabaseException {
        if (userRetweet == null || !userRetweet.checkNotNull()) {
            throw new PiikDatabaseException("(userRetweet) Primary key constraints failed");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        if (userPost == null || !userPost.checkNotNull()) {
            throw new PiikDatabaseException("(userPost) Primary key constraints failed");
        }

        new QueryMapper<Object>(super.getConnection()).createQuery("INSERT INTO repost(post,usr,author) " +
                "VALUES (?,?,?)").defineClass(Object.class).defineParameters(post.getId(), userRetweet.getEmail(), userPost.getEmail()).executeUpdate();
    }

    public void removeRepost(Post repost) throws PiikDatabaseException {

        if (repost == null || !repost.checkNotNull()) {
            throw new PiikDatabaseException("(repost) Primary key constraints failed");
        }

    }

    public Post reply(Post reply, User userReply, Post post, User userPost) throws PiikDatabaseException {

        if (reply == null || !reply.checkNotNull()) {
            throw new PiikDatabaseException("(reply) Primary key constraints failed");
        }

        if (userReply == null || !userReply.checkNotNull()) {
            throw new PiikDatabaseException("(userReply) Primary key constraints failed");
        }

        if (post == null || !post.checkNotNull()) {
            throw new PiikDatabaseException("(post) Primary key constraints failed");
        }

        if (userPost == null || !userPost.checkNotNull()) {
            throw new PiikDatabaseException("(userPost) Primary key constraints failed");
        }

        return null;
    }

    public void createHashtag(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }

        new InsertionMapper<Hashtag>(super.getConnection()).add(hashtag).defineClass(Hashtag.class).insert();
    }

    /**
     * Function to get the hashtag that matchs with the given specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @return hashtag that matches the given information
     */
    public Hashtag getHashtag(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }

        return new QueryMapper<Hashtag>(super.getConnection()).createQuery("SELECT * FROM hashtag " +
                "WHERE name LIKE '?'").defineClass(Hashtag.class).defineParameters(hashtag.getName()).findFirst();
    }

    /**
     * Function to get the hashtags that match with the specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param limit   maximum number of tickets to retrieve
     * @return hashtags that match the given information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        return new QueryMapper<Hashtag>(super.getConnection()).createQuery("SELECT * FROM hashtag " +
                "WHERE name LIKE '%?%' LIMIT ?").defineClass(Hashtag.class).defineParameters(hashtag.getName(),
                limit).list();
    }

    /**
     * Lets a user follow a hashtag
     *
     * @param hashtag hashtag to follow
     * @param user    user who will follow the given hashtag
     */
    public void followHastag(Hashtag hashtag, User user) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkNotNull()) {
            throw new PiikDatabaseException("(hashtag) Primary key constraints failed");
        }

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        new InsertionMapper<Hashtag>(super.getConnection()).
                createUpdate("INSERT INTO followhastag (piiuser, hastag) VALUES (?, ?) " +
                        "WHERE EXISTS (SELECT FROM hastag WHERE name = ?)").
                defineClass(Hashtag.class).
                defineParameters(hashtag.getName(), user.getEmail(), hashtag.getName()).executeUpdate();
    }

    /**
     * Function to search the posts which contain a given text, ordered by descending publication date
     *
     * @param text  text to be searched
     * @param limit maximum number of tickets to retrieve
     * @return list of the posts which contain the given text
     */
    public List<Post> searchByText(String text, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT * FROM post WHERE UPPER(text) " +
                "LIKE UPPER(?) ORDER BY publicationDate DESC LIMIT ?").defineClass(Post.class).defineParameters(
                "%" + text + "%", limit).list();
    }

    /**
     * Function that composes a user's feed; the following posts are included:
     * - Posts made by followed users
     * - Posts made by the user
     * - The 20 most reacted to posts that are in the user's followed hashtags
     *
     * @param user  user whose feed will be retrieved
     * @param limit limit of the posts
     * @return posts that make up the user's feed
     */
    public List<Post> getFeed(User user, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkNotNull()) {
            throw new PiikDatabaseException("(user) Primary key constraints failed");
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        java.util.ArrayList<Post> result = new ArrayList<>();
        ResultSet rs;

        // Connect to the database
        Connection con = getConnection();

        try {
            // We'll use a prepared statement to avoid malicious intentions :(
            PreparedStatement stm = con.prepareStatement("\n" +
                    "-- We obtain the followed users just for convenience\n" +
                    "WITH followedUsers AS (\n" +
                    "    SELECT followed\n" +
                    "    FROM followUser\n" +
                    "    WHERE follower = ?\n" +
                    "),\n" +
                    "-- We obtain the hashtags followed by the user just for convenience\n" +
                    "followedHashtags AS (\n" +
                    "    SELECT hashtag\n" +
                    "    FROM followHashtag\n" +
                    "    WHERE piiuser = ?\n" +
                    "),\n" +
                    "-- We obtain the blocked and silenced users to filter out their posts\n" +
                    "filteredUsers AS (\n" +
                    "    SELECT blocked\n" +
                    "    FROM blockUser\n" +
                    "    WHERE usr = ?\n" +
                    "\n" +
                    "    UNION\n" +
                    "\n" +
                    "    SELECT silenced\n" +
                    "    FROM silenceUser\n" +
                    "    WHERE usr = ?\n" +
                    ")\n" +
                    "\n" +
                    "-- 'UNION' already gets rid of duplicated results\n" +
                    "SELECT *\n" +
                    "\n" +
                    "FROM (\n" +
                    "\n" +
                    "    -- We obtain the posts made by the followed users who are not filtered out\n" +
                    "    (SELECT *, 'following' as type\n" +
                    "    FROM post\n" +
                    "    WHERE author IN (followedUsers) AND author NOT IN (filteredUsers))\n" +
                    "\n" +
                    "    UNION\n" +
                    "\n" +
                    "    -- We obtain the posts that the user made\n" +
                    "    (SELECT *, 'own' as type\n" +
                    "    FROM post\n" +
                    "    WHERE author = ?)\n" +
                    "\n" +
                    "    UNION\n" +
                    "\n" +
                    "    -- We obtain the 20 most reacted to posts which are in the user's followed\n" +
                    "    -- hashtags; the parentheses are needed to apply the 'ORDER BY' only to\n" +
                    "    -- this query, instead on applying it to the whole 'UNION'\n" +
                    "    (SELECT candidates.author, candidates.id, candidates.text,\n" +
                    "        candidates.publicationdate, candidates.sugardaddy,\n" +
                    "        candidates.authordaddy, candidates.multimedia, 'hashtag' as type\n" +
                    "    -- First we obtain the posts that are in the user's followed hashtags and\n" +
                    "    -- that are not made by filtered out users\n" +
                    "    FROM (\n" +
                    "        SELECT *\n" +
                    "        FROM post as p\n" +
                    "        -- We need to make sure that, for each post, at least one of its\n" +
                    "        -- related hashtags is followed by the user\n" +
                    "        WHERE EXISTS (\n" +
                    "            SELECT *\n" +
                    "            FROM ownhashtag as h\n" +
                    "            WHERE h.post = p.post AND h.autor = p.author AND h.hashtag IN\n" +
                    "                (followedHashtags)\n" +
                    "        ) AND p.author NOT IN (filteredUsers)\n" +
                    "    ) as candidates, react as r\n" +
                    "    -- We associate each post with its reactions\n" +
                    "    WHERE candidates.author = r.author AND candidates.id = r.post\n" +
                    "    -- And we filter the posts with the number of reactions obtained for each\n" +
                    "    -- one\n" +
                    "    GROUP BY candidates.author, candidates.id\n" +
                    "    ORDER BY COUNT (r.reactiontype) DESC\n" +
                    "    LIMIT 20)\n" +
                    "\n" +
                    ") as results\n" +
                    "\n" +
                    "ORDER BY results.publicationdate DESC\n" +
                    "LIMIT ?");

            // We set the identifier of the user whose feed will be retrieved
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getEmail());
            stm.setString(5, user.getEmail());
            stm.setInt(6, limit);

            try {
                // We execute the composed query
                rs = stm.executeQuery();

                // We store each result in a post
                while (rs.next()) {
                    result.add(new Post(rs.getString("author"), rs.getString("id"),
                            rs.getString("text"), rs.getTimestamp("publicationdate"),
                            rs.getString("sugardaddy"), rs.getString("authordaddy"),
                            rs.getString("multimedia")));
                }
            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            } finally {
                try {
                    // We must close the prepared statement as it won't be used anymore
                    stm.close();
                } catch (SQLException e) {
                    throw new PiikDatabaseException(e.getMessage());
                }
            }
        } catch (SQLException e) {
            throw new PiikDatabaseException(e.getMessage());
        }

        return (result);
    }

    public List<Hashtag> getTrendingTopics(Integer limit) throws PiikInvalidParameters {

        if (limit <= 0) {
            throw new PiikInvalidParameters("(limit) must be greater than 0");
        }

        return new QueryMapper<Hashtag>(getConnection()).defineClass(Hashtag.class)
                .createQuery("SELECT hashtag, COUNT(*) as count " +
                        "FROM ownHashtag " +
                        "GROUP BY hashtag " +
                        "ORDER BY count DESC " +
                        "LIMIT ?").defineParameters(limit).list();

    }
}
