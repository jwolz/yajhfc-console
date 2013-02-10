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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConsoleIO implementation using the JDK6 Console class
 * 
 * @author jonas
 *
 */
//!!! IMPORTANT: Do not use yajhfc.Utils in here !!!
public class ConsoleConsoleIO extends ConsoleIO {
    protected final Console console;
    protected final Writer errWriter;

    public String readLine(String fmt, Object... args) {
        return console.readLine(fmt, args);
    }

    public String readPassword(String fmt, Object... args) {
        return new String(console.readPassword(fmt, args));
    }
    
    /* (non-Javadoc)
     * @see yajhfc.ui.console.ConsoleIO#getWriter()
     */
    @Override
    protected Writer getWriter() {
        return console.writer();
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.console.ConsoleIO#getErrorWriter()
     */
    @Override
    protected Writer getErrorWriter() {
        return errWriter;
    }
    
    /**
     * Workaround to find the correct console encoding
     * @param console
     * @return
     */
    private String findEncoding(Console console) {
        try {
            PrintWriter pw = console.writer();
            Field fOut = PrintWriter.class.getDeclaredField("out");
            fOut.setAccessible(true);
            Object pwOut = fOut.get(pw);
            
            Method getEncoding = pwOut.getClass().getMethod("getEncoding"); //Support both the StreamEncoder and OutputStreamWriter
            String encoding = getEncoding.invoke(pwOut).toString();
            Logger.getLogger(ConsoleConsoleIO.class.getName()).fine("Found console encoding: " + encoding);
            return encoding;
        } catch (Throwable e) {
            Logger.getLogger(ConsoleConsoleIO.class.getName()).log(Level.SEVERE, "Error getting console encoding", e);
            return System.getProperty("file.encoding", "utf-8");
        } 
    }
    
    public ConsoleConsoleIO(Console console) {
        super();
        this.console = console;
        Writer err;
        try {
            err = new OutputStreamWriter(System.err, findEncoding(console));
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(ConsoleConsoleIO.class.getName()).log(Level.SEVERE, "Unsupported encoding??", e);
            err = console.writer();
        }
        this.errWriter = err;
    }
}
