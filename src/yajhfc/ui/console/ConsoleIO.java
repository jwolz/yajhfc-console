/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2011 Jonas Wolz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package yajhfc.ui.console;

import java.io.Console;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Implements console IO that can either use the JDK6 console object
 * or an alternate implementation using System.out/System.in
 * 
 * @author jonas
 *
 */
// !!! IMPORTANT: Do not use yajhfc.Utils in here !!!
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
    
    private static boolean isPropertyTrue(String propName) {
        String value = System.getProperty(propName);
        if (value != null) {
            if ("true".equals(value) || "yes".equals(value))
                return true;
            if ("false".equals(value) || "no".equals(value))
                return false;
            
            int i_val = 0;
            try {
                i_val = Integer.parseInt(value);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing " + propName + ":");
                e.printStackTrace();
            }
            return (i_val != 0);
        } else {
            return false;
        }
    }
    
    public static ConsoleIO getDefault() {
        if (DEFAULT == null) {
            if (isPropertyTrue("yajhfc.ui.console.ConsoleIO.forceStreamConsoleIO")) {
                DEFAULT = new StreamConsoleIO();
            } else {
                Console cons = System.console();
                if (cons == null) {
                    DEFAULT = new StreamConsoleIO();
                } else {
                    DEFAULT = new ConsoleConsoleIO(cons);
                }
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
        if (log.isLoggable(Level.FINE))
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
        if (log.isLoggable(Level.FINE))
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
        if (log.isLoggable(Level.FINE))
            log.fine("println(" + priority + ", " + text + ")");
        if (priority >= verbosity)
            printlnImpl(text);
    }
    
    
    protected abstract void printfImpl(String format, Object[] args);

    protected abstract void printImpl(String text);
    
    protected abstract void printlnImpl(String text);
}
