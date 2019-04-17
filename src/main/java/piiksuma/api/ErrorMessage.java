package piiksuma.api;

public class ErrorMessage {

    private static final String NULL_PARAMETER = "The given parameter is null or contains missing data";
    private static final String PERMISSION_DENIED = "The actual user is not allowed to perform the requested action";
    private static final String PK_CONSTRAINT = "The given parameter does not meet the database's primary key " +
                                                "constraints";
    private static final String NEGATIVE_LIMIT = "The given limit must be greater than '0'";
    private static final String NOT_EXISTS = "The given parameter does not exist in the database";

    public static String getNullParameterMessage(String parameter) {

        if(parameter != null ) {
            return("(" + parameter + ") " + NULL_PARAMETER );
        }

        else {
            return(NULL_PARAMETER);
        }
    }

    public static String getPermissionDeniedMessage() {
        return(PERMISSION_DENIED);
    }

    public static String getPkConstraintMessage(String parameter) {

        if(parameter != null) {
            return("(" + parameter + ") " + PK_CONSTRAINT);
        }

        else {
            return(PK_CONSTRAINT);
        }
    }

    public static String getNegativeLimitMessage() {
        return(NEGATIVE_LIMIT);
    }

    public static String getNotExistsMessage(String parameter) {

        if(parameter != null) {
            return("(" + parameter + ") " + NOT_EXISTS);
        }

        else {
            return(NOT_EXISTS);
        }
    }
}
