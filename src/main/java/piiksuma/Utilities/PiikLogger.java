package piiksuma.Utilities;


import java.io.IOException;
import java.util.logging.*;

/*
 * =====================================================================================================================
 * ---------------------------------------------------------------------------------------------------------------------
 * Info:
 *  1.- The PiikLogger will show all the message on console and will save them in a log file on the resources folder
 * ---------------------------------------------------------------------------------------------------------------------
 * How to use:
 *  1.- To use it just call PiikLogger.getInstance(Level.levelWanted, message, exceptionError (optional))
 * ---------------------------------------------------------------------------------------------------------------------
 * Log levels:
 *  1.- SEVERE
 *  2.- WARNING
 *  3.- CONFIG
 *  4.- FINE
 *  5.- FINER
 *  6.- FINEST
 * ---------------------------------------------------------------------------------------------------------------------
 * ---------------------------------------------------------------------------------------------------------------------
 * =====================================================================================================================
 */

/**
 * Singleton log class for error handling
 */
public class PiikLogger {
    private static PiikLogger piikLogger;
    private static Logger logger;

    // Usefull during development
    private static Boolean DEBUG = Boolean.FALSE;

    /**
     * Constructor that generates the log console and file
     */
    private PiikLogger() {
        logger = Logger.getLogger(PiikLogger.class.getName());
        LogManager.getLogManager().reset();
        logger.setLevel(Level.ALL);

        /* Un-comment for debugging
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.ALL);
<<<<<<< HEAD
        logger.addHandler(ch);
        */
=======
        if (DEBUG) {
            logger.addHandler(ch);
        }
>>>>>>> c204c06cad5e33a20c8af98a4d8c3dd01913c87e

        FileHandler fh;

        try {
            fh = new FileHandler("src/main/resources/log/PiikLogger.log");
            fh.setLevel(Level.ALL);
            if (DEBUG) {
                logger.addHandler(fh);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't create log file", e);
        }
    }

    /**
     * Returns the logger, if it has not been called before gets created
     *
     * @return Logger
     */
    public static Logger getInstance() {
        if (piikLogger == null) {
            piikLogger = new PiikLogger();
        }
        return logger;
    }
}
