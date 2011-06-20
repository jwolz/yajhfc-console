/**
 * 
 */
package yajhfc.ui.console;

import java.io.Console;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Logger;

import yajhfc.Utils;

/**
 * Implements console IO that can either use the JDK6 console object
 * or an alternate implementation using System.out/System.in
 * 
 * @author jonas
 *
 */
public abstract class ConsoleIO {
    static final Logger log = Logger.getLogger(ConsoleIO.class.getName());
    
    protected static ConsoleIO DEFAULT = null;
    
    /**
     * Debug messages
     */
    public static final int VERBOSITY_DEBUG   =  0;
    /**
     * Detailed status information usually no one wants to see
     */
    public static final int VERBOSITY_FINER   = 10;
    /**
     * Detail messages that may be interesting, but are not shown by default
     */
    public static final int VERBOSITY_FINE    = 20;
    /**
     * Status messages printed during normal operation
     */
    public static final int VERBOSITY_NORMAL  = 30;
    /**
     * Warnings the user does not absolutely need to see.
     */
    public static final int VERBOSITY_WARNING = 40;
    /**
     * Prompts for user input (should always be printed)
     */
    public static final int VERBOSITY_USERPROMPT = 50;
    /**
     * Fatal errors that should always be printed
     */
    public static final int VERBOSITY_ERROR   = 100;
    
    
    /**
     * The minimum supported verbosity
     */
    public static final int VERBOSITY_MIN   = VERBOSITY_DEBUG;
    /**
     * The maximum supported verbosity
     */
    public static final int VERBOSITY_MAX   = VERBOSITY_ERROR;
    /**
     * The default step between verbosity levels
     */
    public static final int VERBOSITY_STEP   = 10;
    
    /**
     * The verbosity for this application
     */
    protected int verbosity = VERBOSITY_NORMAL;
    
    public static ConsoleIO getDefault() {
        if (DEFAULT == null) {
            Console cons = System.console();
            if (cons == null) {
                DEFAULT = new StreamConsoleIO();
            } else {
                DEFAULT = new ConsoleConsoleIO(cons);
            }
        }
        return DEFAULT;
    }


    public abstract String readLine(String fmt, Object... args);

    public abstract String readPassword(String fmt, Object... args);

    /**
     * Returns a PrintWriter writing to stdout
     * @return
     */
    public abstract PrintWriter writer();
    
    /**
     * Returns a PrintWriter writing to stderr
     * @return
     */
    public abstract PrintWriter errWriter();
    
    public int getVerbosity() {
        return verbosity;
    }

    public void setVerbosity(int verbosity) {
        this.verbosity = verbosity;
    }
    /**
     * Prints the given formatted string to stderr 
     * @param priority
     * @param format
     * @param args
     */
    public void printf(int priority, String format, Object... args) {
        if (Utils.debugMode)
            log.fine("printf(" + priority + ", " + format + ", " + Arrays.toString(args) + ")");
        if (priority >= verbosity)
            printfImpl(format, args);
    }
    
    /**
     * Prints the given string to stderr 
     * @param priority
     * @param format
     * @param args
     */
    public void print(int priority, String text) {
        if (Utils.debugMode)
            log.fine("print(" + priority + ", " + text + ")");
        if (priority >= verbosity)
            printImpl(text);
    }
    
    /**
     * Prints the given string to stderr 
     * @param priority
     * @param format
     * @param args
     */
    public void println(int priority, String text) {
        if (Utils.debugMode)
            log.fine("println(" + priority + ", " + text + ")");
        if (priority >= verbosity)
            printlnImpl(text);
    }
    
    
    protected abstract void printfImpl(String format, Object[] args);

    protected abstract void printImpl(String text);
    
    protected abstract void printlnImpl(String text);
}
