package piiksuma.api;

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
}
