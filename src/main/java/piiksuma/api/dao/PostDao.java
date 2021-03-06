package piiksuma.api.dao;

import piiksuma.Hashtag;
import piiksuma.Multimedia;
import piiksuma.Post;
import piiksuma.User;
import piiksuma.api.ErrorMessage;
import piiksuma.api.MultimediaType;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static piiksuma.api.ErrorMessage.getPkConstraintMessage;

public class PostDao extends AbstractDao {

    /* Constructor */

    public PostDao(Connection connection) {
        super(connection);
    }


// ======================================================= POSTS =======================================================

    /**
     * Checks if the specified post contains the a given hashtag
     * @param hashtag hashtag that will be checked
     * @param post post which may contain the given hashtag
     * @return if the given hashtag is contained in the specified post
     */
    public boolean hashtagInPost(Hashtag hashtag, Post post) throws PiikDatabaseException {
        if (hashtag == null || !hashtag.checkNotNull(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        if (post == null || !post.checkNotNull(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }

        return((new QueryMapper<>(super.getConnection()).createQuery("SELECT * FROM ownHashtag WHERE hashtag = ? AND " +
                "post = ? AND author = ?").defineParameters(hashtag.getName(), post.getId(),
                post.getPostAuthor().getPK()).mapList().size()) > 0);
    }

    /*******************************************************************************************************************
     * Inserts a post on the db
     *
     * @param post Post to be inserted
     * @return returns the post
     * @throws PiikDatabaseException Thrown if post or its primary key are null
     */
    public Post createPost(Post post) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }

        // It will be returned when the method executes successfully
        Post completePost = new Post(post);

        Multimedia multimedia = post.getMultimedia();

        // Which may-be-null parameters will be inserted
        boolean sugarDaddyExists = post.getFatherPost() != null && post.getFatherPost().checkPrimaryKey(false);
        boolean authorDaddyExists = sugarDaddyExists && post.getFatherPost().getAuthor() != null &&
                post.getFatherPost().getAuthor().checkPrimaryKey(false);
        if (authorDaddyExists) {

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


            /* Isolation level */

            // Default in PostgreSQL
            super.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


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


            ResultSet keys = statement.executeQuery();


            // ID generation successful
            if (keys.next()) {

                completePost.setId(keys.getString("id"));
            } else {

                throw new PiikDatabaseException("Post ID generation failed");
            }


            /* Statement for hashtags */


            clause = new StringBuilder();
            // Hashtags which do not already exist are inserted in the database
            clause.append("INSERT INTO hashtag(name) SELECT ? WHERE NOT EXISTS (SELECT * FROM hashtag WHERE name = ? " +
                    "FOR UPDATE); ");

            statementHashtags = con.prepareStatement(clause.toString());

            for (Hashtag hashtag : post.getHashtags()) {
                statementHashtags.setString(1, hashtag.getName());
                statementHashtags.setString(2, hashtag.getName());
                statementHashtags.executeUpdate();
            }


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
        // Return Post
        return completePost;
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Remove a post from db
     *
     * @param post post to be removed
     * @throws PiikDatabaseException Thrown if post or its primary key are null
     */
    public void removePost(Post post) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }

        // Delete post
        new DeleteMapper<Post>(super.getConnection()).add(post).defineClass(Post.class).delete();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Update content (text) of a post
     *
     * @param post Post to be updated
     * @throws PiikDatabaseException Thrown if post or its primary key are null
     */

    public void updatePost(Post post) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
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


            /* Isolation level */

            // Default in PostgreSQL
            super.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


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


            /* Hashtag association */

            clause = new StringBuilder();
            // Hashtags which do not already exist are inserted in the database
            clause.append("INSERT INTO hashtag(name) SELECT ? WHERE NOT EXISTS (SELECT * FROM hashtag WHERE name = ? " +
                    "FOR UPDATE); ");

            statementHashtags = con.prepareStatement(clause.toString());

            for (Hashtag hashtag : post.getHashtags()) {
                statementHashtags.setString(1, hashtag.getName());
                statementHashtags.setString(2, hashtag.getName());
                statementHashtags.executeUpdate();
            }


            clause = new StringBuilder();
            // TODO it would by nice to erase only those who were removed
            // First we need to the rid of the old hashtags because anyone may have been removed
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
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Archive a post requested by an user
     *
     * @param post Post to be archived
     * @throws PiikDatabaseException Thrown if post/user or its primary keys are null
     */
    public void archivePost(Post post, User user) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }
        // Check if user or its primary key are null
        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        // Archive post
        new InsertionMapper<>(super.getConnection()).createUpdate("INSERT INTO archivepost VALUES (?,?,?)")
                .defineParameters(post.getId(), user.getPK(), post.getPostAuthor().getId())
                .executeUpdate();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Returns a post form db
     *
     * @param post Post wanted
     * @return Post
     * @throws PiikDatabaseException Thrown if post or its primary keys are null
     */
    public Post getPost(Post post) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }

        // Get the base query
        String query = getQueryPost();

        query += "WHERE post.id = ? and post.author = ?";

        List<Map<String, Object>> result = new QueryMapper<>(super.getConnection()).createQuery(query)
                .defineParameters(post.getId(), post.getPostAuthor().getPK()).mapList();

        if (result == null || result.isEmpty()) {
            return null;
        }

        Post resultPost = new Post();

        // The post information is found in the first item, except the hashtags
        Map<String, Object> columnsPost = result.get(0);

        User user = new User();

        Object sugarDaddy = columnsPost.get("sugarDaddy");
        Object authorDaddy = columnsPost.get("authorDaddy");

        if(sugarDaddy != null) {
            Post fatherPost = new Post();
            User authorFatherPost = new User();

            fatherPost.setId((String) sugarDaddy);

            authorFatherPost.setId((String) authorDaddy);
            fatherPost.setAuthor(authorFatherPost);

            resultPost.setFatherPost(fatherPost);
            columnsPost.put("fatherPost", resultPost);
        }

        Object multimedia = columnsPost.get("multimedia");

        if(multimedia != null){
            Multimedia multimediaPost = new Multimedia();
            multimediaPost.setHash((String) multimedia);

            resultPost.setMultimedia(multimediaPost);
            columnsPost.put("multimedia", multimediaPost);
        }

        Object idUser = columnsPost.get("author");

        if (idUser instanceof String) {
            user.setId((String) idUser);
            columnsPost.put("author", user);
        }

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
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Gets a list of post containing a given hashtag
     *
     * @param hashtag Hashtag to be searched for
     * @return List with all the posts that have the hashtag
     * @throws PiikDatabaseException Thrown if hashtag or its primary keys are null
     */
    public List<Post> getPost(Hashtag hashtag) throws PiikDatabaseException {

        // Check if hashtag or its primary key are null
        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        String query = getQueryPost();

        // Get the List
        List<Map<String, Object>> result = new QueryMapper<>(super.getConnection()).createQuery(query +
                "  WHERE post.id IN (SELECT p.id FROM ownhashtag as o, post as p WHERE o.hashtag = ? AND p.id=o.post " +
                "AND p.author=o.author)").defineParameters(hashtag.getName()).mapList();
        // Return List
        return getPosts(result);
    }

    /**
     * Function that returns the answers / children of the indicated post
     *
     * @param post
     * @return
     */
    public List<Post> getAnswers(Post post) throws PiikDatabaseException {
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }

        List<Post> posts = new QueryMapper<Post>(getConnection()).defineClass(Post.class).createQuery(
                "WITH RECURSIVE searchChildren(id, author) AS (" +
                    " SELECT *" +
                    " FROM post" +
                    " WHERE id = ? AND author = ?" +
                    " UNION ALL" +
                    " SELECT p.*" +
                    " FROM post as p JOIN searchChildren as s ON (p.sugardaddy = s.id AND p.authordaddy = s.author)" +
                    ")" +
                    "" +
                    "SELECT *" +
                    "FROM searchChildren;"
        ).defineParameters(post.getId(), post.getAuthor().getPK()).list();

        posts.remove(0);

        return posts;
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Gets a list of post containing a given user
     *
     * @param user User to be searched for
     * @return List post created by the user
     * @throws PiikDatabaseException Thrown if user or its primary keys are null
     */
    public List<Post> getPost(User user) throws PiikDatabaseException {

        // Check if hashtag or its primary key are null
        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        // Get the base query
        StringBuilder string = new StringBuilder();
        string.append('(');
        string.append(getQueryPost());

        string.append("WHERE post.author = ?) UNION (");
        string.append(getQueryPost());
        string.append("WHERE EXISTS (SELECT * FROM repost as r WHERE r.usr = ? AND r.author = post.author AND r.post " +
                "= post.id))");

        List<Map<String, Object>> result = new QueryMapper<Post>(super.getConnection()).createQuery(string.toString())
                .defineClass(Post.class).defineParameters(user.getPK(), user.getPK()).mapList();

        // Return posts
        return getPosts(result);
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Retrieves the posts that a user has archived
     *
     * @param user User whose archived posts will be retrieved
     * @return List of posts founded
     * @throws PiikDatabaseException Thrown if user or its primary key are null
     */
    public List<Post> getArchivedPosts(User user) throws PiikDatabaseException {

        // Check if hashtag or its primary key are null
        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        // List of post
        return new QueryMapper<Post>(super.getConnection()).defineClass(Post.class)
                .createQuery("SELECT p.* FROM post as p JOIN archivepost as a ON(p.id = a.post AND p.author = " +
                        "a.author) WHERE a.usr = ?").defineParameters(user.getPK()).list();
    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Repost an already published post
     *
     * @param userRepost User who reposts
     * @param post       Post to be reposted
     * @param userPost   Owner of the post
     * @throws PiikDatabaseException Thrown if userRepost/post/userPost or its primary keys are null
     */
    public void repost(User userRepost, Post post, User userPost) throws PiikDatabaseException {
        // Check if userRepost or its primary key are null
        if (userRepost == null || !userRepost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("userRepost"));
        }
        // Check if post or its primary key are null
        if (post == null || !post.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("post"));
        }
        // Check if userPost or its primary key are null
        if (userPost == null || !userPost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("userPost"));
        }


        // Repost
        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO repost(post,usr,author) " +
                "VALUES (?,?,?)").defineClass(Object.class).defineParameters(post.getId(), userRepost.getPK(),
                userPost.getPK()).executeUpdate();

    }
    //******************************************************************************************************************

    /*******************************************************************************************************************
     * Function to remove a repost
     *
     * @param repost Repost to be removed
     * @param currentUser Current user logged into the app
     * @throws PiikDatabaseException Thrown if repost or its primary key are null
     */
    public void removeRepost(Post repost, User currentUser) throws PiikDatabaseException {
        // Check if repost or its primary key are null
        if (repost == null || !repost.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("repost"));
        }
        // Delete repost
        new DeleteMapper<Object>(super.getConnection()).createUpdate("DELETE FROM repost WHERE post=? AND usr=? " +
                "AND author=?").defineClass(Object.class).defineParameters(repost.getId(), currentUser.getPK(),
                repost.getAuthor().getPK()).executeUpdate();
    }
    //******************************************************************************************************************

//======================================================================================================================

    private String getQueryPost() {
        return "SELECT post.*, o.hashtag FROM post LEFT JOIN ownhashtag o ON post.id = o.post AND post.author = " +
                "o.author ";
    }

    /**
     * Function to swap information from the database to the Post class
     *
     * @param result the information from the database
     * @return list of posts
     */
    private List<Post> getPosts(List<Map<String, Object>> result) {
        if (result == null || result.isEmpty()) {
            return null;
        }

        ArrayList<Post> posts = new ArrayList<>();

        for (Map<String, Object> columnsPost : result) {
            Post resultPost = new Post();
            User user = new User();

            Object sugarDaddy = columnsPost.get("sugarDaddy");
            Object authorDaddy = columnsPost.get("authorDaddy");

            if(sugarDaddy != null) {
                Post fatherPost = new Post();
                User authorFatherPost = new User();

                fatherPost.setId((String) sugarDaddy);

                authorFatherPost.setId((String) authorDaddy);
                fatherPost.setAuthor(authorFatherPost);

                resultPost.setFatherPost(fatherPost);
                columnsPost.put("fatherPost", resultPost);
            }

            Object multimedia = columnsPost.get("multimedia");

            if(multimedia != null){
                Multimedia multimediaPost = new Multimedia();
                multimediaPost.setHash((String) multimedia);

                resultPost.setMultimedia(multimediaPost);
                columnsPost.put("multimedia", multimediaPost);
            }

            Object idUser = columnsPost.get("author");

            if (idUser instanceof String) {
                user.setId((String) idUser);
                columnsPost.put("author", user);
            }


            resultPost.addInfo(columnsPost);

            // The post may already be in the list, because a row is generated for each combination idPost - Hashtag
            // combination
            boolean postContains = false;
            if (posts.contains(resultPost)) {
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

            if (!postContains) {
                posts.add(resultPost);
            }
        }

        return posts;
    }

    /**
     * Creates a new hashtag
     *
     * @param hashtag Hashtag you want to create
     * @throws PiikDatabaseException Thrown if hashtag or its primary key are null
     */
    public void createHashtag(Hashtag hashtag) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
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
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
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
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return new QueryMapper<Hashtag>(super.getConnection()).createQuery("SELECT * FROM hashtag " +
                "SELECT WHERE name LIKE '%?%' LIMIT ?").defineClass(Hashtag.class).defineParameters(hashtag.getName(),
                limit).list();
    }

    /**
     * Lets a user follow a hashtag
     *
     * @param hashtag hashtag to follow
     * @param user    user who will follow the given hashtag
     */
    public void followHashtag(Hashtag hashtag, User user) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        new InsertionMapper<Hashtag>(super.getConnection()).
                createUpdate("INSERT INTO followhashtag (piiUser, hashtag) VALUES (?, (SELECT name FROM hashtag WHERE name = ?))").
                defineClass(Hashtag.class).
                defineParameters(user.getPK(), hashtag.getName()).executeUpdate();
    }

    /**
     * Lets a user unfollow a hashtag
     *
     * @param hashtag hashtag to unfollow
     * @param user    user who will unfollow the given hashtag
     */
    public void unfollowHashtag(Hashtag hashtag, User user) throws PiikDatabaseException {

        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        new DeleteMapper<>(super.getConnection()).
                createUpdate("DELETE FROM followhashtag WHERE hashtag = ? AND piiUser = ?").
                defineParameters(hashtag.getName(), user.getPK()).executeUpdate();
    }

    /**
     * Checks if a given user follows a given hashtag
     * @param hashtag Hashtag from which we want to know if it's followed
     * @param user User from who we want to know if it's following the hashtag
     * @return TRUE if the user is following the hashtag, FALSE in the other case
     * @throws PiikDatabaseException
     */
    public boolean userFollowsHashtag(Hashtag hashtag, User user) throws PiikDatabaseException {
        if (hashtag == null || !hashtag.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("hashtag"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        return(!(new QueryMapper<>(super.getConnection()).createQuery("SELECT * FROM followhashtag WHERE hashtag = ? " +
                "AND piiUser = ?").defineParameters(hashtag.getName(), user.getPK()).mapList()).isEmpty());
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
            throw new PiikDatabaseException(getPkConstraintMessage("user"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        java.util.ArrayList<Post> result = new ArrayList<>();
        ResultSet rs;

        // Connect to the database
        Connection con = getConnection();

        try {

            /* Isolation level */

            // Default in PostgreSQL
            super.getConnection().setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


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
                            "         (SELECT p.*\n" +
                            "          FROM post as p\n" +
                            "          WHERE p.author IN (SELECT * FROM followedUsers)\n" +
                            "            AND p.author NOT IN (SELECT * FROM filteredUsers))\n" +
                            "\n" +
                            "         UNION\n" +
                            "\n" +
                            "         -- We obtain the reposts made by the followed users who are not filtered out\n" +
                            "         (SELECT p.*\n" +
                            "          FROM post as p\n" +
                            "          WHERE EXISTS (\n" +
                            "                         SELECT *\n" +
                            "                         FROM repost as r\n" +
                            "                         WHERE r.author IN (SELECT * FROM followedUsers)\n" +
                            "                           AND r.author NOT IN (SELECT * FROM filteredUsers)" +
                            "                           AND r.author = p.author\n" +
                            "                           AND r.post = p.id\n" +
                            "                     ))\n" +
                            "\n" +
                            "         UNION\n" +
                            "\n" +
                            "         -- We obtain the posts that the user made\n" +
                            "         (SELECT *\n" +
                            "          FROM post\n" +
                            "          WHERE author = ?)\n" +
                            "\n" +
                            "         UNION\n" +
                            "\n" +
                            "         -- We obtain the reposts that the user made\n" +
                            "         (SELECT p.*\n" +
                            "          FROM post as p\n" +
                            "          WHERE EXISTS (\n" +
                            "                        SELECT *\n" +
                            "                        FROM repost as r\n" +
                            "                        WHERE r.usr = ?\n" +
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
                            "         (SELECT p.*\n" +
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


            /* Multimedia info retrieval */

            stm = con.prepareStatement("SELECT * FROM multimedia WHERE hash = ?");

            try {
                // We retrieve the corresponding multimedia info for each post that references the multimedia table
                for(Post post : result) {

                    if(post.getMultimedia() != null && !post.getMultimedia().getHash().isEmpty()) {

                        // Setting iterated multimedia's PK
                        stm.setString(1, post.getMultimedia().getHash());

                        // Executing the composed query
                        rs = stm.executeQuery();

                        // Info retrieval successful
                        if(rs.next()) {
                            post.getMultimedia().setResolution(rs.getString("resolution"));
                            post.getMultimedia().setUri(rs.getString("uri"));
                        }

                        else {
                            throw new PiikDatabaseException("Data retrieval from multimedia row \"" +
                                    post.getMultimedia().getHash() + "\" failed");
                        }
                    }
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


            /* Hashtag retrieval */

            stm = con.prepareStatement("SELECT * FROM ownhashtag WHERE post = ? AND author = ?");

            try {
                // We retrieve the associated hashtags for each obtained post
                for(Post post : result) {

                    // Setting iterated post's PKs
                    stm.setString(1, post.getId());
                    stm.setString(2, post.getAuthor().getPK());

                    // Executing the composed query
                    rs = stm.executeQuery();

                    // Hashtags retrieval
                    while(rs.next()) {
                        post.getHashtags().add(new Hashtag(rs.getString("hashtag")));
                    }
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

    /**
     * Gets a List of the most used hashtags
     *
     * @param limit   Number of hashtags to put on the list
     * @return Return the list of hastags
     * @throws PiikDatabaseException
     * @throws PiikInvalidParameters
     */
    public List<Hashtag> getTrendingTopics(Integer limit) throws PiikInvalidParameters, PiikDatabaseException {

        // TODO check null
        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        // TODO filter by recent publication date
        return new QueryMapper<Hashtag>(getConnection()).defineClass(Hashtag.class)
                .createQuery("SELECT * FROM hashtag as h WHERE h.name IN (SELECT o.hashtag\n" +
                        "FROM ownhashtag as o\n" +
                        "WHERE EXISTS (\n" +
                        "    SELECT *\n" +
                        "    FROM post as p\n" +
                        "    WHERE p.id = o.post\n" +
                        "        AND p.author = o.author\n" +
                        "        -- Less than 1 day\n" +
                        "        AND DATE_PART('day', now() - p.publicationdate) < 1\n" +
                        ")\n" +
                        "GROUP BY o.hashtag\n" +
                        "ORDER BY count(*) DESC\n" +
                        "LIMIT ?)").defineParameters(limit).list();

    }

    /**
     * Function to check if an user has already reposted a post
     *
     * @param user User we want to check if he has already reposted
     * @param post Post we want to check
     * @param current Current user logged in the app
     * @return  "True" if the user reposted the post, otherwise "false"
     */
    public boolean checkUserResposted(User user, Post post, User current) throws PiikDatabaseException{

        List<Map<String, Object>> repost = new QueryMapper<Object>(super.getConnection()).createQuery(
                "SELECT post FROM repost WHERE usr = ? AND post = ? AND author = ?").defineParameters(current.getPK(),
                post.getId(), user.getPK()).mapList();

        return (!repost.isEmpty()) ;
    }

    /**
     * Function to check if a user has already archived a post
     *
     * @param post Post from which we want to know if it's archived
     * @param user User from who we want to know if he had archived the post
     * @return TRUE if the user has archived the post, FALSE in the other case
     * @throws PiikDatabaseException
     */
    public boolean isPostArchived(Post post, User user) throws PiikDatabaseException{

        List<Map<String, Object>> archived = new QueryMapper<Object>(super.getConnection()).createQuery(
                "SELECT post FROM archivePost WHERE usr = ? AND post = ? AND author = ?").defineParameters(user.getPK(),
                post.getId(), post.getAuthor().getId()).mapList();

        return (!archived.isEmpty()) ;
    }

    /**
     * Function to remove an archive post from an user
     * @param post Post which is going to be removed from the archived posts
     * @param user User which is going to removed the post from archived posts
     * @throws PiikDatabaseException
     */
    public void removeArchivePost(Post post, User user) throws PiikDatabaseException {

        new DeleteMapper<>(super.getConnection()).createUpdate("DELETE FROM archivepost WHERE usr = ? AND post = ? AND author = ?").defineParameters(
                user.getPK(), post.getId(), post.getAuthor().getId()
        ).executeUpdate();
    }
}
