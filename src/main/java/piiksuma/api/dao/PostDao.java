package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.database.InsertionMapper;
import piiksuma.database.QueryMapper;
import piiksuma.database.UpdateMapper;

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
     * Function that add the post to the database
     *
     * @param post    post to add to the database with the creator
     * @param current current user logged in the app
     * @return post passed by parameter
     */
    public void createPost(Post post, User current) {

        if (post == null) {
            return;
        }

        if (current == null) {
            return;
        }

        // Check that the current user is an administrator
        if (current.getType().equals(UserType.user)) {
            return;
        }

        // Check that the post satisfies with the restrictions
        if (!post.checkNotNull()) {
            return;
        }

        new InsertionMapper<Post>(super.getConnection()).add(post).definirClase(Post.class).insertar();
    }

    public Post updatePost(Post post, User user, User current) {
        return null;
    }

    public void removePost(Post post, User user, User current) {
        if (post == null) {
            return;
        }
        if (!post.checkPrimaryKey()) {
            return;
        }


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
     *
     * @param hashtag hashtag that contains the specification about the hashtag to search
     * @param current current user logged in the app
     * @return hashtag with all the information
     */
    public Hashtag getHashtag(Hashtag hashtag, User current) {
        if (hashtag == null) {
            return null;
        }

        if (!hashtag.checkPrimaryKey()) {
            return null;
        }

        return new QueryMapper<Hashtag>(super.getConnection()).crearConsulta("SELECT * FROM hashtag " +
                "WHERE name LIKE '?'").definirEntidad(Hashtag.class).definirParametros(hashtag.getName()).findFirst();
    }

    /**
     * Function to get the hashtags that meet with the specifications
     *
     * @param hashtag hashtag that contains the specification about the hashtags to search
     * @param current current user logged in the app
     * @return hashtags with all the information
     */
    public List<Hashtag> searchHashtag(Hashtag hashtag, User current) {
        if (hashtag == null) {
            return null;
        }

        return new QueryMapper<Hashtag>(super.getConnection()).crearConsulta("SELECT * FROM hashtag " +
                "WHERE name LIKE '%?%'").definirEntidad(Hashtag.class).definirParametros(hashtag.getName()).list();
    }

    /**
     * Function to search posts which have a specific text in it
     *
     * @param text        given text to search
     * @param currentUser current user logged in the app
     * @return list of the posts which has the given text in it
     */
    public List<Post> searchByText(String text, User currentUser) {
        if (text == null) {
            //TODO lanzar excepciones oportunas o avisar al usuario etc...
            return null;
        }

        return new QueryMapper<Post>(super.getConnection()).crearConsulta("SELECT * FROM post WHERE UPPER(text) " +
                "LIKE UPPER(?)").definirEntidad(Post.class).definirParametros("%" + text + "%").list();
    }

    public List<Post> getFeed(User user, User currentUser) {

        java.util.ArrayList<Post> resultado = new ArrayList<>();
        ResultSet rs;

        if (user == null) {
            return (null);
        }

        // Connect to the database
        Connection con = getConnection();

        try {
            // We'll use a prepared statement to avoid malicious intentions :(
            PreparedStatement stm = con.prepareStatement("\n" +
                    "-- We obtain the followed users just for convenience\n" +
                    "WITH followedUsers AS (\n" +
                    "    SELECT followed\n" +
                    "    FROM followuser\n" +
                    "    WHERE follower = ?\n" +
                    "),\n" +
                    "-- We obtain the hashtags followed by the user just for convenience\n" +
                    "followedHashtags AS (\n" +
                    "    SELECT hashtag\n" +
                    "    FROM followhashtag\n" +
                    "    WHERE piiuser = ?\n" +
                    "),\n" +
                    "-- We obtain the blocked and silenced users to filter out their posts\n" +
                    "filteredUsers AS (\n" +
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
                    "ORDER BY results.publicationdate DESC");

            // We set the identifier of the user whose feed will be retrieved
            stm.setString(1, user.getEmail());
            stm.setString(2, user.getEmail());
            stm.setString(3, user.getEmail());
            stm.setString(4, user.getEmail());
            stm.setString(5, user.getEmail());

            try {
                rs = stm.executeQuery();

                while (rs.next()) {
                    resultado.add(new Post(rs.getString("author"), rs.getString("id"),
                            rs.getString("text"), rs.getTimestamp("publicationdate"),
                            rs.getString("sugardaddy"), rs.getString("authordaddy"),
                            rs.getString("multimedia")));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    stm.close();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }

        return(resultado);
    }
}
