package piiksuma.api;

import piiksuma.api.dao.*;

import java.sql.Connection;


/**
 * Singleton Class used as an etryPoint to te system backend. Holds the
 * different facades and serves them statically
 */
public class ApiFacade {

    private static final String PATH_CONFIG = "/baseDatos.properties";

    private static ApiFacade entrypoint;
    private Connection connection;
    private InsertionFacade insertionFacade;
    private DeletionFacade deletionFacade;
    private SearchFacade searchFacade;
    private SystemFacade systemFacade;

    // Helpful when implementing unit tests
    private PostDao postDao;
    private UserDao userDao;
    private MultimediaDao multimediaDao;
    private MessagesDao messagesDao;
    private InteractionDao interactionDao;

    static {
        entrypoint = new ApiFacade();
    }

    /**
     * Private instance. It instanciates the connection to the database and the
     * different Facades
     */
    private ApiFacade() {
        connection = new ConnectionProxy(PATH_CONFIG).getConnection();
        insertionFacade = new InsertionFacade(this);
        deletionFacade = new DeletionFacade(this);
        searchFacade = new SearchFacade(this);
        systemFacade = new SystemFacade(this);

        postDao = new PostDao(connection);
        userDao = new UserDao(connection);
        multimediaDao = new MultimediaDao(connection);
        messagesDao = new MessagesDao(connection);
        interactionDao = new InteractionDao(connection);
    }

    public static ApiFacade getEntrypoint() {
        return entrypoint;
    }

    public static void setEntrypoint(ApiFacade entrypoint) {
        ApiFacade.entrypoint = entrypoint;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public InsertionFacade getInsertionFacade() {
        return insertionFacade;
    }

    public void setInsertionFacade(InsertionFacade insertionFacade) {
        this.insertionFacade = insertionFacade;
    }

    public DeletionFacade getDeletionFacade() {
        return deletionFacade;
    }

    public void setDeletionFacade(DeletionFacade deletionFacade) {
        this.deletionFacade = deletionFacade;
    }

    public SearchFacade getSearchFacade() {
        return searchFacade;
    }

    public void setSearchFacade(SearchFacade searchFacade) {
        this.searchFacade = searchFacade;
    }

    public SystemFacade getSystemFacade() {
        return systemFacade;
    }

    public void setSystemFacade(SystemFacade systemFacade) {
        this.systemFacade = systemFacade;
    }

    public static String getPathConfig() {
        return PATH_CONFIG;
    }

    public PostDao getPostDao() {
        return postDao;
    }

    public void setPostDao(PostDao postDao) {
        this.postDao = postDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public MultimediaDao getMultimediaDao() {
        return multimediaDao;
    }

    public void setMultimediaDao(MultimediaDao multimediaDao) {
        this.multimediaDao = multimediaDao;
    }

    public MessagesDao getMessagesDao() {
        return messagesDao;
    }

    public void setMessagesDao(MessagesDao messagesDao) {
        this.messagesDao = messagesDao;
    }

    public InteractionDao getInteractionDao() {
        return interactionDao;
    }

    public void setInteractionDao(InteractionDao interactionDao) {
        this.interactionDao = interactionDao;
    }
}
