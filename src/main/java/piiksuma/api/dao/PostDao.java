package piiksuma.api.dao;

import piiksuma.Hashtag;
import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ErrorMessage;
import piiksuma.api.MultimediaType;
import piiksuma.database.*;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
     * @return post containing the given data and its generated ID
     */
    public Post createPost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        // It will be returned when the method executes successfully
        Post completePost = new Post(post);

        Multimedia multimedia = post.getMultimedia();

        // Which may-be-null parameters will be inserted
        boolean sugarDaddyExists = post.getFatherPost() != null && post.getFatherPost().checkPrimaryKey(false);
        boolean authorDaddyExists = sugarDaddyExists && post.getFatherPost().getAuthor() != null &&
                post.getFatherPost().getAuthor().checkPrimaryKey(false);
        if(authorDaddyExists) {
            System.out.println("Dafuq");
        }
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);

        // Connection to the database
        Connection con = getConnection();
        // SQL clauses
        PreparedStatement statement = null;
        PreparedStatement statementHashtags = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The post won't be created unless there's no error modifying all related tables and generating its ID
            con.setAutoCommit(false);


            /* Statement */

            // If the post will display some kind of media, it gets inserted if it does not exist in the database
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaimage " :
                        "multimediavideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT ? WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = ? FOR UPDATE); ");

                statement = con.prepareStatement(clause.toString());


                /* Clause's data insertion */

                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());


                /* Execution */

                statement.executeUpdate();

                clause = new StringBuilder();
            }

            // Publication date will automatically be set as "NOW()" because it is its default value
            // ID gets autogenerated
            clause.append("INSERT INTO post(author, text, sugardaddy, authordaddy, multimedia) VALUES(?, ?");

            // Some attributes may be null
            if (sugarDaddyExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (authorDaddyExists) {
                clause.append(", ?");
            } else {
                clause.append(", NULL");
            }

            if (multimediaExists) {
                clause.append(", ?) ");
            } else {
                clause.append(", NULL) ");
            }

            clause.append("RETURNING id");

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            statement.setString(offset++, post.getAuthor().getPK());
            statement.setString(offset++, post.getText());

            if (sugarDaddyExists) {
                statement.setString(offset++, post.getFatherPost().getId());
            }

            if (authorDaddyExists) {
                statement.setString(offset++, post.getFatherPost().getAuthor().getPK());
            }

            if (multimediaExists) {
                statement.setString(offset, multimedia.getHash());
            }


            /* Execution and key retrieval */

            System.out.println("Resultados");
            ResultSet keys = statement.executeQuery();
            System.out.println("Resultados obtenidos");

            // ID generation successful
            if (keys.next()) {
                System.out.println("hay");
                completePost.setId(keys.getString("id"));
            } else {
                System.out.println("No hay");
                throw new PiikDatabaseException("Post ID generation failed");
            }


            /* Statement for hashtags */

            System.out.println("empezada insercion de hashtags");
            clause = new StringBuilder();
            // Hashtags which do not already exist are inserted in the database
            clause.append("INSERT INTO hashtag(name) SELECT ? WHERE NOT EXISTS (SELECT * FROM hashtag WHERE name = ? " +
                    "FOR UPDATE); ");

            statementHashtags = con.prepareStatement(clause.toString());

            for(Hashtag hashtag : post.getHashtags()) {
                statementHashtags.setString(1, hashtag.getName());
                statementHashtags.setString(2, hashtag.getName());
                statementHashtags.executeUpdate();
            }
            System.out.println("pasada inserción de hashtags");

            /* Statement for owning hashtags */

            clause = new StringBuilder();
            clause.append("INSERT INTO ownhashtag(hashtag, post, author) VALUES (?, ?, ?)");

            statementHashtags = con.prepareStatement(clause.toString());

            statementHashtags.setString(2, completePost.getId());
            statementHashtags.setString(3, completePost.getAuthor().getPK());

            for (Hashtag hashtag : post.getHashtags()) {
                statementHashtags.setString(1, hashtag.getName());
                statementHashtags.executeUpdate();
            }

            System.out.println("pasada inserción de tener hashtags");
            /* Commit */

            con.commit();

            // Restoring auto-commit to its default value
            con.setAutoCommit(true);


        } catch (SQLException e) {
            // Performed modifications in the database are rolled-back
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new PiikDatabaseException(ex.getMessage());
            }

            throw new PiikDatabaseException(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            }
        }

        return completePost;
    }


    /**
     * Function to update the text content of the post
     *
     * @param post post to be updated
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */

    public void updatePost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        Multimedia multimedia = post.getMultimedia();

        // Which may-be-null parameters will be inserted
        boolean sugarDaddyExists = post.getFatherPost() != null && post.getFatherPost().checkPrimaryKey(false);
        boolean authorDaddyExists = sugarDaddyExists && post.getFatherPost().getAuthor() != null &&
                post.getFatherPost().getAuthor().checkPrimaryKey(false);
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);

        // Connection to the database
        Connection con = getConnection();
        // SQL clauses
        PreparedStatement statement = null;
        PreparedStatement statementHashtags = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Auto-commit */

            // The post won't be modified unless there's no error modifying all related tables
            con.setAutoCommit(false);


            /* Statement */

            // If the post will display some kind of media, it gets inserted if it does not exist in the database
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                String type = multimedia.getType().equals(MultimediaType.image) ? "multimediaimage " :
                        "multimediavideo ";
                clause.append("INSERT INTO ").append(type).append("SELECT ? WHERE NOT EXISTS (SELECT * " +
                        "FROM ").append(type).append("WHERE hash = ? FOR UPDATE); ");
            }

            // TODO date may need to be between ''
            // Publication date will automatically be set as "NOW()" because it is its default value
            clause.append("UPDATE post SET text = ?, sugardaddy = ");

            // Some attributes may be null
            if (sugarDaddyExists) {
                clause.append("?");
            } else {
                clause.append("NULL");
            }

            clause.append(", authordaddy = ");

            if (authorDaddyExists) {
                clause.append("?");
            } else {
                clause.append("NULL");
            }

            clause.append(", multimedia = ");

            if (multimediaExists) {
                clause.append("?");
            } else {
                clause.append("NULL");
            }

            clause.append(" WHERE author = ? AND id = ?");

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            if (multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            statement.setString(offset++, post.getText());

            if (sugarDaddyExists) {
                statement.setString(offset++, post.getFatherPost().getId());
            }

            if (authorDaddyExists) {
                statement.setString(offset++, post.getFatherPost().getAuthor().getPK());
            }

            if (multimediaExists) {
                statement.setString(offset++, multimedia.getHash());
            }

            statement.setString(offset++, post.getAuthor().getPK());
            statement.setString(offset, post.getId());


            /* Execution */

            statement.executeUpdate();


            /* Statement for hashtags */

            clause = new StringBuilder();
            // TODO it would by nice to erase only those who were removed
            // First we need to the rid of the old hashtags bacause one may have been removed
            clause.append("DELETE FROM ownhashtag WHERE post = ? AND author = ?");

            statementHashtags = con.prepareStatement(clause.toString());
            statementHashtags.setString(1, post.getId());
            statementHashtags.setString(2, post.getAuthor().getPK());

            statementHashtags.executeUpdate();

            // Now we associate all the given hashtags
            clause = new StringBuilder();
            clause.append("INSERT INTO ownhashtag(hashtag, post, author) VALUES (?, ?, ?)");

            statementHashtags = con.prepareStatement(clause.toString());

            statementHashtags.setString(2, post.getId());
            statementHashtags.setString(3, post.getAuthor().getPK());

            for (Hashtag hashtag : post.getHashtags()) {
                statementHashtags.setString(1, hashtag.getName());
                statementHashtags.executeUpdate();
            }


            /* Commit */

            con.commit();

            // Restoring auto-commit to its default value
            con.setAutoCommit(true);


        } catch (SQLException e) {
            // Performed modifications in the database are rolled-back
            try {
                con.rollback();
            } catch (SQLException ex) {
                throw new PiikDatabaseException(ex.getMessage());
            }

            throw new PiikDatabaseException(e.getMessage());

        } finally {

            try {
                if (statement != null) {
                    statement.close();
                }

            } catch (SQLException e) {
                throw new PiikDatabaseException(e.getMessage());
            }
        }
    }

    /**
     * Function to archive a post privately by an user
     *
     * @param post post to be archived
     * @throws PiikDatabaseException Duplicated keys and null values that shouldn't be
     */
    public void archivePost(Post post, User user) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<Post>(super.getConnection()).createUpdate("INSERT into archivepost values (?,?,?)")
                .defineClass(Post.class).defineParameters(post.getId(), user.getPK(), post.getPostAuthor())
                .executeUpdate();
    }

    /**
     * Function that removes a post from the database
     *
     * @param post post to remove from the database
     */
    public void removePost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        new DeleteMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).delete();
    }

    private String getQueryPost() {
        return "SELECT post.*, o.hashtag FROM post LEFT JOIN ownhashtag o ON post.id = o.post AND post.author = " +
                "o.author ";
    }

    /**
     * Function to get the indicated post from the database
     *
     * @param post post to search from the database
     * @return the post
     * @throws PiikDatabaseException
     */
    public Post getPost(Post post) throws PiikDatabaseException {

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        // Get the base query
        String query = getQueryPost();

        query += "WHERE id = ? and author = ?";

        List<Map<String, Object>> result = new QueryMapper<>(super.getConnection()).createQuery(query)
                .defineParameters(post.getId(), post.getPostAuthor().getPK()).mapList();

        if (result == null || result.isEmpty()) {
            return null;
        }

        Post resultPost = new Post();

        // The post information is found in the first item, except the hashtags
        Map<String, Object> columnsPost = result.get(0);

        // Post information is saved
        resultPost.addInfo(columnsPost);

        // Get the hashtags
        for (Map<String, Object> postInfo : result) {
            if (postInfo.containsKey("hashtag")) {
                String hashtag = (String) postInfo.get("hashtag");

                if (hashtag != null && !hashtag.isEmpty()) {
                    resultPost.addHashtag(new Hashtag(hashtag));
                }
            }
        }

        return resultPost;
    }

    /**
     * Funcion that return a list with all the posts that have the indicated hashtag
     *
     * @param hashtag hashtag to search in the posts
     * @return a list with all the posts that have the hashtag
     * @throws PiikDatabaseException
     */
    public List<Post> getPost(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("hashtag"));
        }

        List<Map<String, Object>> result =  new QueryMapper<>(super.getConnection()).createQuery("SELECT p.*," +
                " o.hashtag FROM ownhashtag as o, post as p WHERE o.hashtag = ? AND p.id=o.post AND p.author=o.author")
                .defineParameters(hashtag.getName()).mapList();

        return getPosts(result);
    }

    /**
     * Function that return a list with all the user's post
     *
     * @param user user who created the post
     * @return a list with all the user's posts
     * @throws PiikDatabaseException
     */
    public List<Post> getPost(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        // Get the base query
        String query = getQueryPost();

        query += "WHERE post.author = ?";
        List<Map<String, Object>> result = new QueryMapper<Post>(super.getConnection()).createQuery(query)
                .defineClass(Post.class).defineParameters(user.getPK()).mapList();

        return getPosts(result);
    }

    /**
     * Function to swap information from the database to the Post class
     *
     * @param result the information from the database
     * @return
     */
    private List<Post> getPosts(List<Map<String, Object>> result) {
        if(result == null || result.isEmpty()){
            return null;
        }

        ArrayList<Post> posts = new ArrayList<>();

        for(Map<String, Object> columnsPost : result){
            Post resultPost = new Post();
            User user = new User();

            Object idUser = columnsPost.get("author");

            if(idUser instanceof String){
                user.setId((String) idUser);
                columnsPost.put("author", user);
            }

            resultPost.addInfo(columnsPost);

            // The post may already be in the list, because a row is generated for each combination idPost - Hashtag
            // combination
            boolean postContains = false;
            if(posts.contains(resultPost)){
                resultPost = posts.get(posts.indexOf(resultPost));
                postContains = true;
            }

            // Get the hashtags
            if (columnsPost.containsKey("hashtag")) {
                String htag = (String) columnsPost.get("hashtag");

                if (htag != null && !htag.isEmpty()) {
                    resultPost.addHashtag(new Hashtag(htag));
                }
            }

            if(!postContains){
                posts.add(resultPost);
            }
        }

        return posts;
    }

    /**
     * Retrieves the posts that a user has archived
     *
     * @param user user whose archived posts will be retrieved
     * @return found posts
     */
    public List<Post> getArchivedPosts(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<Post>(super.getConnection()).createQuery("SELECT p.* FROM post as p, archivepost as a " +
                "WHERE p.id = a.post AND p.author = a.author AND a.usr = ?").defineParameters(user.getPK()).list();
    }

    /**
     * Function to do a repost on a post
     *
     * @param userRepost user who does the repost
     * @param post       post to be reposted
     * @param userPost   user who owns the post
     */
    public void repost(User userRepost, Post post, User userPost) throws PiikDatabaseException {

        if (userRepost == null || !userRepost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("userRepost"));
        }

        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("post"));
        }

        if (userPost == null || !userPost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("userPost"));
        }

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO repost(post,usr,author) " +
                "VALUES (?,?,?)").defineClass(Object.class).defineParameters(post.getId(), userRepost.getPK(),
                userPost.getPK()).executeUpdate();

    }

    /**
     * Function to remove a repost
     *
     * @param repost repost to remove from the database
     * @throws PiikDatabaseException
     */
    public void removeRepost(Post repost) throws PiikDatabaseException {

        if (repost == null || !repost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("repost"));
        }

        new DeleteMapper<Object>(super.getConnection()).createUpdate("DELETE FROM repost WHERE post=? AND usr=? " +
                "AND author=?").defineClass(Object.class).defineParameters(repost.getFatherPost().getId(),
                repost.getPostAuthor().getPK(), repost.getFatherPost().getPostAuthor().getPK()).executeUpdate();

    }

    public void createHashtag(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("hashtag"));
        }

        new InsertionMapper<>(super.getConnection()).createUpdate("INSERT INTO hashtag(name) SELECT ? WHERE NOT " +
                "EXISTS (SELECT * FROM hashtag WHERE name = ? FOR UPDATE)").defineParameters(hashtag.getName(),
                hashtag.getName()).executeUpdate();
    }

    /**
     * Function to get the hashtag that matches the given specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @return hashtag that matches the given information
     */
    public Hashtag getHashtag(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("hashtag"));
        }

        return new QueryMapper<Hashtag>(super.getConnection()).createQuery("SELECT * FROM hashtag " +
                "WHERE name LIKE ?").defineClass(Hashtag.class).defineParameters(hashtag.getName()).findFirst();
    }

    /**
     * Function to get the hashtags that match with the specifications
     *
     * @param hashtag hashtag whose properties will be used in the search
     * @param limit   maximum number of tickets to retrieve
     * @return hashtags that match the given information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("hashtag"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
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

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("hashtag"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<Hashtag>(super.getConnection()).
                createUpdate("INSERT INTO followhastag (piiuser, hashtag) VALUES (?, ?) " +
                        "WHERE EXISTS (SELECT FROM hastag WHERE name = ?)").
                defineClass(Hashtag.class).
                defineParameters(hashtag.getName(), user.getPK(), hashtag.getName()).executeUpdate();
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
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
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

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        java.util.ArrayList<Post> result = new ArrayList<>();
        ResultSet rs;

        // Connect to the database
        Connection con = getConnection();

        try {
            // We'll use a prepared statement to avoid malicious intentions :(
            PreparedStatement stm = con.prepareStatement(
                    "-- We obtain the followed users just for convenience\n" +
                    "WITH followedUsers (followed) AS (\n" +
                    "    SELECT followed\n" +
                    "    FROM followuser\n" +
                    "    WHERE follower = ?\n" +
                    "),\n" +
                    "-- We obtain the hashtags followed by the user just for convenience\n" +
                    "followedHashtags (followed) AS (\n" +
                    "    SELECT hashtag\n" +
                    "    FROM followhashtag\n" +
                    "    WHERE piiuser = ?\n" +
                    "),\n" +
                    "-- We obtain the blocked and silenced users to filter out their posts\n" +
                    " filteredUsers (filtered) AS (\n" +
                    "    SELECT blocked\n" +
                    "    FROM blockuser\n" +
                    "    WHERE usr = ?\n" +
                    "\n" +
                    "    UNION\n" +
                    "\n" +
                    "    SELECT silenced\n" +
                    "    FROM silenceuser\n" +
                    "    WHERE usr = ?\n" +
                    ")\n" +
                    "\n" +
                    "-- 'UNION' already gets rid of duplicated results\n" +
                    "SELECT *\n" +
                    "\n" +
                    "FROM (\n" +
                    "\n" +
                    "         -- We obtain the posts made by the followed users who are not filtered out\n" +
                    "         (SELECT p.*, 'following' as type\n" +
                    "          FROM post as p\n" +
                    "          WHERE p.author IN (SELECT * FROM followedUsers)\n" +
                    "            AND p.author NOT IN (SELECt * FROM filteredUsers))\n" +
                    "\n" +
                    "         UNION\n" +
                    "\n" +
                    "         -- We obtain the posts that the user made\n" +
                    "         (SELECT *, 'own' as type\n" +
                    "          FROM post\n" +
                    "          WHERE author = ?)\n" +
                    "\n" +
                    "         UNION\n" +
                    "\n" +
                    "         -- We obtain the reposts that the user made\n" +
                    "         (SELECT p.*, 'repost' as type\n" +
                    "          FROM post as p\n" +
                    "          WHERE EXISTS(\n" +
                    "                        SELECT *\n" +
                    "                        FROM repost as r\n" +
                    "                        WHERE r.author = ?\n" +
                    "                          AND r.author = p.author\n" +
                    "                          AND r.post = p.id\n" +
                    "                    ))\n" +
                    "\n" +
                    "         UNION\n" +
                    "\n" +
                    "         -- We obtain the 20 most reacted to posts which are in the user's\n" +
                    "         -- followed hashtags; the parentheses are needed to apply the\n" +
                    "         -- 'ORDER BY' only to this query, instead on applying it to the whole\n" +
                    "         -- 'UNION'\n" +
                    "         (SELECT p.*, 'hashtag' as type\n" +
                    "          FROM (SELECT candidates.author,\n" +
                    "                       candidates.id\n" +
                    "                -- First we obtain the posts that are in the user's followed\n" +
                    "                -- hashtags and that are not made by filtered out users\n" +
                    "                FROM (\n" +
                    "                         SELECT *\n" +
                    "                         FROM post as p\n" +
                    "                         -- We need to make sure that, for each post, at least\n" +
                    "                         -- one of its related hashtags is followed by the user\n" +
                    "                         WHERE EXISTS(\n" +
                    "                                 SELECT *\n" +
                    "                                 FROM ownhashtag as h\n" +
                    "                                 WHERE h.post = p.id\n" +
                    "                                   AND h.author = p.author\n" +
                    "                                   AND h.hashtag IN (SELECT * FROM\n" +
                    "                                       followedHashtags)\n" +
                    "                             )\n" +
                    "                           AND p.author NOT IN (SELECT * FROM filteredUsers)\n" +
                    "                     ) as candidates,\n" +
                    "                     react as r\n" +
                    "                -- We associate each post with its reactions\n" +
                    "                WHERE candidates.author = r.author\n" +
                    "                  AND candidates.id = r.post\n" +
                    "                -- And we filter the posts with the number of reactions\n" +
                    "                -- obtained for each one\n" +
                    "                GROUP BY candidates.author, candidates.id\n" +
                    "                ORDER BY COUNT(r.reactiontype) DESC\n" +
                    "                LIMIT 20) as subquery,\n" +
                    "               post as p\n" +
                    "          WHERE subquery.id = p.id\n" +
                    "            AND subquery.author = p.author\n" +
                    "         )\n" +
                    "\n" +
                    " ) as results\n" +
                    "\n" +
                    "ORDER BY results.publicationdate DESC\n" +
                    "LIMIT ?\n");

            // We set the identifier of the user whose feed will be retrieved
            stm.setString(1, user.getPK());
            stm.setString(2, user.getPK());
            stm.setString(3, user.getPK());
            stm.setString(4, user.getPK());
            stm.setString(5, user.getPK());
            stm.setString(6, user.getPK());
            stm.setInt(7, limit);

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

    public List<Hashtag> getTrendingTopics(Integer limit) throws PiikInvalidParameters, PiikDatabaseException {

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return new QueryMapper<Hashtag>(getConnection()).defineClass(Hashtag.class)
                .createQuery("SELECT hashtag, COUNT(*) as count " +
                        "FROM ownhashtag " +
                        "GROUP BY hashtag " +
                        "ORDER BY count DESC " +
                        "LIMIT ?").defineParameters(limit).list();

    }
}
