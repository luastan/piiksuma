package piiksuma.api.dao;

import piiksuma.*;
import piiksuma.api.ErrorMessage;
import piiksuma.database.DeleteMapper;
import piiksuma.database.InsertionMapper;
import piiksuma.database.MapperColumn;
import piiksuma.database.QueryMapper;
import piiksuma.exceptions.PiikDatabaseException;
import piiksuma.exceptions.PiikInvalidParameters;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserDao extends AbstractDao {

    public UserDao(Connection connection) {
        super(connection);
    }

    /**
     * Function to remove a user from the database
     *
     * @param user user to remove
     */
    public void removeUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new DeleteMapper<User>(super.getConnection()).defineClass(User.class).add(user).setIsolationLevel(
                Connection.TRANSACTION_SERIALIZABLE).delete();
    }

    /**
     * Function to insert a new user into the database
     *
     * @param user user to insert into the database
     */
    public void createUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        Multimedia multimedia = user.getMultimedia();

        // If not null required attributes will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);


        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();
        StringBuilder clauseAux = new StringBuilder();

        try {

            /* Statement */

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            // (we can't be sure that the app hasn't given us the same old multimedia or a new one)
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                clause.append("INSERT INTO multimediaImage SELECT ? WHERE NOT EXISTS (SELECT * FROM " +
                        "multimediaImage WHERE hash = ? FOR UPDATE); ");
            }

            if(user.getPhones() != null && !user.getPhones().isEmpty()){
                for(String phone : user.getPhones()) {
                    if(phone != null && !phone.isEmpty() && phone.length() >= 4) {
                        clause.append("INSERT INTO phone(prefix, phone, usr) VALUES(?, ?, ?);");
                    }
                }
            }

            // TODO dates may need to be between ''
            clause.append("INSERT INTO piiUser (");
            clauseAux.append("VALUES (");

            ArrayList<Object> columnValues = new ArrayList<>();

            // For each user's field
            // TODO get rid of reflection
            for (Field field : user.getClass().getDeclaredFields()) {

                // Required to
                if (field.isAnnotationPresent(MapperColumn.class)) {

                    field.setAccessible(true);
                    MapperColumn mapperColumn = field.getAnnotation(MapperColumn.class);

                    // Database's column name:
                    //  True -> equals to the attribute's name
                    //  False -> they don't match; the proper name is put in the class
                    String column = mapperColumn.columna().equals("") ? field.getName() : mapperColumn.columna();

                    Object value = null;

                    try {
                        value = field.get(user);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    // Default values are skipped (registration date)
                    if(!mapperColumn.hasDefault()) {
                        // The iterated field cannot be null and have a default value
                        if (mapperColumn.pkey() || mapperColumn.notNull()) {
                            clauseAux.append("?");
                            // To preserve the order when filling values in the prepared statement
                            columnValues.add(value);
                        } else {
                            // Unable to set a value in prepared statement or it is an empty string
                            if (value == null || (value instanceof String && ((String)value).isEmpty())) {
                                clauseAux.append("NULL");
                            } else {
                                clauseAux.append("?");
                                // To preserve the order when filling values in the prepared statement
                                if(value instanceof Multimedia) {
                                    columnValues.add(((Multimedia)value).getHash());
                                }
                                else {
                                    columnValues.add(value);
                                }
                            }
                        }
                        clause.append(column).append(", ");
                        clauseAux.append(", ");
                    }
                }
            }

            // Getting rid of the last ", " and putting ") "
            clause.delete(clause.length() - 2, clause.length() - 1);
            clause.append(") ");

            clauseAux.delete(clauseAux.length() - 2, clauseAux.length() - 1);
            clauseAux.append(") ");

            clause.append(clauseAux).append("; ");

            // The user may be an administrator
            if (user.checkAdministrator()) {
                clause.append("INSERT INTO administrator(id) VALUES(?); ");
            }


            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            // Multimedia insertion
            if (multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            // Phones insertion
            if(user.getPhones() != null && !user.getPhones().isEmpty()){
                for(String phone : user.getPhones()) {
                    if(phone != null && !phone.isEmpty() && phone.length() >= 4) {
                        String prefix = phone.substring(0, 2);
                        String withoutPrefix = phone.substring(3);
                        statement.setString(offset++, prefix);
                        statement.setString(offset++, withoutPrefix);
                        statement.setString(offset++, user.getPK());
                    }
                }
            }

            // User's data insertion
            for (Object value : columnValues) {
                statement.setObject(offset++, value);
            }

            // Upgrades and downgrades from admin
            if (user.checkAdministrator()) {
                statement.setString(offset, user.getId());
            }


            /* Execution */

            statement.executeUpdate();


        } catch (SQLException e) {
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

    public void updateUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        Multimedia multimedia = user.getMultimedia();

        // If not null required attributes will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);
        boolean oldIDExists = user.getOldID() != null && !user.getOldID().isEmpty();


        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        try {

            /* Statement */

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            // (we can't be sure that the app hasn't given us the same old multimedia or a new one)
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT ?, ?, ? WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = ? FOR UPDATE); ");

                clause.append("INSERT INTO multimediaImage SELECT ? WHERE NOT EXISTS (SELECT * FROM " +
                        "multimediaImage WHERE hash = ? FOR UPDATE); ");
            }

            if(user.getPhones() != null && !user.getPhones().isEmpty()){
                for(String phone : user.getPhones()) {
                    if(phone != null && !phone.isEmpty() && phone.length() >= 4) {
                        clause.append("INSERT INTO phone(prefix, phone, usr) SELECT ?, ?, ? WHERE NOT EXISTS" +
                                " (SELECT * FROM phone WHERE prefix = ? and phone = ? and usr = ?);");
                    }
                }
            }

            // TODO dates may need to be between ''
            clause.append("UPDATE piiUser SET ");

            ArrayList<Object> columnValues = new ArrayList<>();

            // For each user's field
            for (Field field : user.getClass().getDeclaredFields()) {

                // Required to
                if (field.isAnnotationPresent(MapperColumn.class)) {

                    field.setAccessible(true);
                    MapperColumn mapperColumn = field.getAnnotation(MapperColumn.class);

                    // Database's column name:
                    //  True -> equals to the attribute's name
                    //  False -> they don't match; the proper name is put in the class
                    String column = mapperColumn.columna().equals("") ? field.getName() : mapperColumn.columna();
                    clause.append(column).append(" = ");

                    Object value = null;

                    try {
                        value = field.get(user);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                    // The iterated field cannot be null
                    if (mapperColumn.pkey() || mapperColumn.notNull()) {
                        clause.append("?");
                        // To preserve the order when filling values in the prepared statement
                        columnValues.add(value);
                    } else {
                        // Unable to set a value in prepared statement or it is an empty string
                        if (value == null || (value instanceof String && ((String)value).isEmpty())) {
                            clause.append("NULL");
                        } else {
                            clause.append("?");
                            // To preserve the order when filling values in the prepared statement
                            columnValues.add(value);
                        }
                    }

                    clause.append(", ");
                }
            }

            clause.deleteCharAt(clause.length() - 2);
            clause.append(" WHERE id = ?; ");

            // The user may haven been promoted to administrator
            if (user.checkAdministrator())
                clause.append("INSERT INTO administrator(id) SELECT ? WHERE NOT EXISTS (SELECT * FROM " +
                        "administrator WHERE id = ? FOR UPDATE); ");

                // Or he may have been downgraded
            else {
                clause.append("DELETE FROM administrator WHERE id = ?; ");
            }

            statement = con.prepareStatement(clause.toString());


            /* Clause's data insertion */

            int offset = 1;

            // Multimedia insertion
            if (multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            for(String phone : user.getPhones()){
                if(phone != null && !phone.isEmpty() && phone.length() >= 4){
                    String prefix = phone.substring(0, 2);
                    String withoutPrefix = phone.substring(3);

                    statement.setString(offset++, prefix);
                    statement.setString(offset++, withoutPrefix);
                    statement.setString(offset++, user.getPK());
                }
            }

            // User's data insertion
            for (Object value : columnValues) {
                statement.setObject(offset++, value);
            }

            // Row to be modified
            String id = oldIDExists ? user.getOldID() : user.getId();
            statement.setObject(offset++, id);

            // Upgrades and downgrades from admin
            if (user.checkAdministrator()) {
                statement.setString(offset++, user.getId());
                statement.setString(offset, user.getId());
            }
            else {
                statement.setString(offset, id);
            }


            /* Execution */

            statement.executeUpdate();


        } catch (SQLException e) {
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

    /*public void updateUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        Multimedia multimedia = user.getMultimedia();

        // If not null required attributes will be inserted
        boolean multimediaExists = multimedia != null && multimedia.checkPrimaryKey(false);


        // Connection to the database
        Connection con = getConnection();
        // SQL clause
        PreparedStatement statement = null;

        // Built clause
        StringBuilder clause = new StringBuilder();

        ArrayList<Boolean> attributesExist = new ArrayList<>();

        attributesExist.add(user.getGender() != null && !user.getGender().isEmpty());
        attributesExist.add(user.getDescription() != null && !user.getDescription().isEmpty());
        attributesExist.add(user.getHome() != null && !user.getHome().isEmpty());
        attributesExist.add(user.getPostalCode() != null && !user.getPostalCode().isEmpty());
        attributesExist.add(user.getProvince() != null && !user.getProvince().isEmpty());
        attributesExist.add(user.getCountry() != null && !user.getCountry().isEmpty());
        attributesExist.add(user.getCity() != null && !user.getCity().isEmpty());
        attributesExist.add(user.getBirthplace() != null && !user.getBirthplace().isEmpty());
        attributesExist.add(user.getDeathdate() != null);
        attributesExist.add(user.getReligion() != null && !user.getReligion().isEmpty());
        attributesExist.add(user.getEmotionalSituation() != null && !user.getEmotionalSituation().isEmpty());
        attributesExist.add(user.getJob() != null && !user.getJob().isEmpty());
        attributesExist.add(multimediaExists);


        ArrayList<String> attributesNames = new ArrayList<>();

        attributesNames.add("gender");
        attributesNames.add("description");
        attributesNames.add("home");
        attributesNames.add("postalCode");
        attributesNames.add("province");
        attributesNames.add("country");
        attributesNames.add("city");
        attributesNames.add("birthplace");
        attributesNames.add("deathdate");
        attributesNames.add("religion");
        attributesNames.add("emotionalSituation");
        attributesNames.add("job");
        attributesNames.add("profilePicture");

        ArrayList<Object> attributesValues = new ArrayList<>();

        attributesValues.add(user.getGender());
        attributesValues.add(user.getDescription());
        attributesValues.add(user.getHome());
        attributesValues.add(user.getPostalCode());
        attributesValues.add(user.getProvince());
        attributesValues.add(user.getCountry());
        attributesValues.add(user.getCity());
        attributesValues.add(user.getBirthplace());
        attributesValues.add(user.getDeathdate());
        attributesValues.add(user.getReligion());
        attributesValues.add(user.getEmotionalSituation());
        attributesValues.add(user.getJob());
        if(multimediaExists) {
            attributesValues.add(multimedia.getHash());
        }



        try {

            ///// Statement

            // If the message will display some kind of media, it gets inserted if it does not exist in the database
            // (we can't be sure that the app hasn't given us the same old multimedia or a new one)
            if (multimediaExists) {
                clause.append("INSERT INTO multimedia(hash, resolution, uri) SELECT '?', '?', '?' WHERE NOT EXISTS" +
                        " (SELECT * FROM multimedia WHERE hash = '?' FOR UPDATE); ");

                clause.append("INSERT INTO multimediaImage SELECT '?' WHERE NOT EXISTS (SELECT * FROM " +
                        "multimediaImage WHERE hash = '?' FOR UPDATE); ");
            }

            // TODO dates may need to be between ''
            clause.append("UPDATE user SET id = '?', email = '?', name = '?', pass = '?', birthdate = ?, " +
                    "registrationDate = ?");

            // Some attributes may be null
            for(int i = 0; i < attributesNames.size(); i++) {

                // ", attribute = "
                clause.append(", ").append(attributesNames.get(i)).append(" = ");

                // If the attribute exists
                if(attributesExist.get(i)) {

                    Object value = attributesValues.get(i);

                    if(value instanceof String) {
                        clause.append("'?'");
                    }

                    else if(value instanceof Timestamp) {
                        clause.append("?");
                    }

                } else {
                    clause.append("NULL");
                }
            }

            clause.append(" WHERE id = '?'; ");

            // The user may haven been promoted to administrator
            if(user.checkAdministrator())
                clause.append("INSERT INTO administrator(id) SELECT '?' WHERE NOT EXISTS (SELECT * FROM " +
                        "administrator WHERE id = '?' FOR UPDATE); ");

            // Or he may have been downgraded
            else {
                clause.append("DELETE FROM administrator WHERE id = '?'; ");
            }

            statement = con.prepareStatement(clause.toString());


            ///// Clause's data insertion

            int offset = 1;

            // Multimedia insertion
            if(multimediaExists) {
                statement.setString(1, multimedia.getHash());
                statement.setString(2, multimedia.getResolution());
                statement.setString(3, multimedia.getUri());
                statement.setString(4, multimedia.getHash());

                statement.setString(5, multimedia.getHash());
                statement.setString(6, multimedia.getHash());

                offset += 6;
            }

            // User's data insertion
            statement.setString(offset++, user.getId());
            statement.setString(offset++, user.getEmail());
            statement.setString(offset++, user.getName());
            statement.setString(offset++, user.getPass());
            statement.setTimestamp(offset++, user.getBirthday());
            statement.setTimestamp(offset++, user.getRegistrationDate());

            // Some attributes may be null
            for(int i = 0; i < attributesNames.size(); i++) {

                // If the attribute exists
                if(attributesExist.get(i)) {

                    Object value = attributesValues.get(i);

                    if(value instanceof String) {
                        statement.setString(offset++, (String)value);
                    }

                    else if(value instanceof Timestamp) {
                        statement.setTimestamp(offset++, (Timestamp)value);
                    }
                }
            }


            // Upgrades and downgrades from admin
            statement.setString(offset++, user.getId());
            statement.setString(offset, user.getId());


            ///// Execution

            statement.executeUpdate();


        } catch (SQLException e) {
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
    }*/

    /**
     * Function to get the user that matches the given specifications
     *
     * @param user user that contains the requirements that will be applied in the search
     * @return user that meets the given information
     */
    public User getUser(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        // Get the list with the user and the phones
        List<Map<String, Object>> listObject = new QueryMapper<>(super.getConnection()).createQuery(
                "SELECT * " +
                "FROM piiUser LEFT JOIN phone ON(id = usr) " +
                "WHERE id LIKE ?").defineParameters(user.getPK()).mapList();

        User returnUser = new User();

        if(listObject == null || listObject.isEmpty()){
            return null;
        } else {

            // The user information is found in the first item, except the phones
            Map<String, Object> columnsUsr = listObject.get(0);

            // User information is saved
            try {

                for (Field field : returnUser.getClass().getDeclaredFields()) {
                    field.setAccessible(true);

                    if (field.isAnnotationPresent(MapperColumn.class)) {

                        String columnName = field.getAnnotation(MapperColumn.class).columna();
                        columnName = columnName.equals("") ? field.getName() : columnName;

                        if (columnsUsr.containsKey(columnName)) {
                            field.set(returnUser, columnsUsr.get(columnName));
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            // Get the phones
            for (Map<String, Object> usrInfo : listObject) {
                if (usrInfo.containsKey("phone")) {
                    String phone = (String) usrInfo.get("prefix");
                    phone += usrInfo.get("phone");
                    returnUser.addPhone(phone);
                }
            }
        }

        return returnUser;
    }

    /**
     * Function to get the users that match with the specifications
     *
     * @param user  user that contains the requirements that will be applied in the search
     * @param limit maximum of users to return
     * @return users that meet the given information
     */
    public List<User> searchUser(User user, Integer limit) throws PiikDatabaseException, PiikInvalidParameters {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        if (limit <= 0) {
            throw new PiikInvalidParameters(ErrorMessage.getNegativeLimitMessage());
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser " +
                "WHERE id LIKE '%?%' and name LIKE '%?%' LIMIT ?").defineClass(User.class).defineParameters(
                user.getPK(), user.getName(), limit).list();
    }


    public List<Achievement> getAchievements(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<Achievement>(super.getConnection()).createQuery("SELECT a.* FROM ownAchievement as o, " +
                "achievement as a WHERE o.usr = ? AND o.achiev = a.id").defineClass(Achievement.class).defineParameters(
                user.getPK()).list();
    }


    /**
     * Function to login into the app; it will try to find a user that meets the given keys
     *
     * @param user user whose primary fields will be used in the search
     * @return user from database that meets the required attributes
     */
    public User login(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return new QueryMapper<User>(super.getConnection()).createQuery("SELECT * FROM piiUser WHERE id = ? and " +
                "pass = ?").defineClass(User.class).defineParameters(user.getPK(), user.getPass()).setIsolationLevel(
                Connection.TRANSACTION_SERIALIZABLE).findFirst();
    }

    public void createAchievement(Achievement achievement) throws PiikDatabaseException {

        if (achievement == null || !achievement.checkPrimaryKey(true)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("achievement"));
        }

        new InsertionMapper<Achievement>(super.getConnection()).add(achievement).defineClass(Achievement.class).insert();
    }

    public void unlockAchievement(Achievement achievement, User user) throws PiikDatabaseException {

        if (achievement == null || !achievement.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("achievement"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<>(super.getConnection()).createUpdate("INSERT INTO ownAchievement VALUES " +
                "(?, ?)").defineParameters(achievement.getId(), user.getPK()).executeUpdate();
    }

    /**
     * Function to follow a user
     *
     * @param followed User to be followed
     * @param follower User who follows
     * @throws PiikDatabaseException
     */
    public void followUser(User followed, User follower) throws PiikDatabaseException {

        if (followed == null || !followed.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("followed"));
        }

        if (follower == null || !follower.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("follower"));
        }

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO followuser(followed,follower) " +
                "VALUES (?,?)").defineClass(Object.class).defineParameters(followed.getPK(), follower.getPK())
                .executeUpdate();

    }

    /**
     * Function to unfollow a user
     *
     * @param followed User to be unfollowed
     * @param follower User who wants to unfollow the followed user
     * @throws PiikDatabaseException
     */
    public void unfollowUser(User followed, User follower) throws PiikDatabaseException {

        if (followed == null || !followed.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("followed"));
        }

        if (follower == null || !follower.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("follower"));
        }

        new DeleteMapper<Object>(super.getConnection()).createUpdate("DELETE FROM followUser WHERE followed=? AND " +
                "follower=?").defineClass(Object.class).defineParameters(followed.getPK(), follower.getPK())
                .executeUpdate();
    }

    /**
     * Function to block a user
     *
     * @param blockedUser User to be blocked
     * @param user        User who wants to block the other user
     * @throws PiikDatabaseException
     */
    public void blockUser(User blockedUser, User user) throws PiikDatabaseException {

        if (blockedUser == null || !blockedUser.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("blockedUser"));
        }
        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO blockUser(usr,blocked) VALUES " +
                "(?,?)").defineClass(Object.class).defineParameters(user.getPK(), blockedUser.getPK()).executeUpdate();
    }

    /**
     * Function to silence a user
     *
     * @param silencedUser User to be silenced
     * @param user         User who wants to silence the other user
     * @throws PiikDatabaseException
     */
    public void silenceUser(User silencedUser, User user) throws PiikDatabaseException {
        if (silencedUser == null || !silencedUser.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("silencedUser"));
        }

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        new InsertionMapper<Object>(super.getConnection()).createUpdate("INSERT INTO silenceUser(usr,silenced) VALUES " +
                "(?,?)").defineClass(Object.class).defineParameters(user.getPK(), silencedUser.getPK()).executeUpdate();
    }

    /**
     * Function that returns the statistics of the given user
     *
     * @param user user about whose statistics we want to know
     * @return computed statistics
     */
    public Statistics getUserStatistics(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        Statistics statistics = new Statistics();

        List<Map<String, Object>> estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                //Query to take the number of followers the given user has
                "SELECT count(follower) AS followers \n" +
                        "FROM followuser \n" +
                        "WHERE followed LIKE ? \n").defineParameters(user.getPK()).setIsolationLevel(
                Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setFollowers((Long) estatistics.get(0).get("followers"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "\n" +//Query to take the number of users that the user follows
                        "SELECT count(followed) AS followed \n" +
                        "FROM followuser \n" +
                        "WHERE follower LIKE ? \n").defineParameters(user.getPK()).setIsolationLevel(
                Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setFollowing((Long) estatistics.get(0).get("followed"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "\n" +//Query to take the number of users that followback the given user
                        "WITH followerstable AS( SELECT * \n" +
                        "FROM followuser \n" +
                        "WHERE followed LIKE ? ),\n" +
                        "\n" +
                        "followedtable AS( SELECT * \n" +
                        "FROM followuser \n" +
                        "WHERE follower LIKE ? )\n" +
                        "\n" +
                        "SELECT COUNT(*) AS followback " +
                        "FROM followedtable, followerstable " +
                        "WHERE followedtable.followed=followerstable.follower"
        ).defineParameters(user.getPK()).setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setFollowBack((Long) estatistics.get(0).get("followback"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='LikeIt' ").defineParameters(
                user.getPK()).setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setMaxLikeIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='LoveIt' ").defineParameters(
                user.getPK()).setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setMaxLoveIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='HateIt' ").defineParameters(
                user.getPK()).setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setMaxHateIt((Long) estatistics.get(0).get("reaction"));

        estatistics = new QueryMapper<User>(super.getConnection()).createQuery(
                "SELECT count(reactiontype) AS reaction " +
                        "FROM react " +
                        "WHERE author LIKE ? AND reactiontype='MakesMeAngry' ").defineParameters(
                user.getPK()).setIsolationLevel(Connection.TRANSACTION_SERIALIZABLE).mapList();

        statistics.setMaxMakesMeAngry((Long) estatistics.get(0).get("reaction"));

        return statistics;
    }

    /**
     * Retrieves userType from a desired user
     *
     * @param user User whose type wants to be known
     * @return UserType
     */
    public UserType getUserType(User user) throws PiikDatabaseException {

        if (user == null || !user.checkPrimaryKey(false)) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        return getUserType(user.getPK());
    }

    /**
     * Retrieves userType from a desired user
     *
     * @param pk Primary key from the desired user
     * @return UserType
     * @throws PiikDatabaseException When null values are passed as parameters
     */
    public UserType getUserType(String pk) throws PiikDatabaseException {
        if (pk == null) {
            throw new PiikDatabaseException(ErrorMessage.getPkConstraintMessage("user"));
        }

        if (!(new QueryMapper<User>(this.getConnection()).createQuery("SELECT id FROM piiuser where id=?")
                .defineParameters(pk).defineClass(User.class).list(false).size() > 0)) {
            throw new PiikDatabaseException(ErrorMessage.getNotExistsMessage("user"));
        }

        return new QueryMapper<User>(this.getConnection()).createQuery("SELECT id FROM administrator where id=?")
                .defineParameters(pk).defineClass(User.class).list(false).size() > 0 ?
                UserType.administrator : UserType.user;
    }
}
