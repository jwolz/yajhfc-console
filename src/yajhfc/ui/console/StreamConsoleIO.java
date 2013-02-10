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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * ConsoleIO implementation using System.in/out/err
 * 
 * @author jonas
 *
 */
//!!! IMPORTANT: Do not use yajhfc.Utils in here !!!
public class StreamConsoleIO extends ConsoleIO {
    protected final PrintWriter writer;
    protected final PrintWriter errWriter;
    protected final BufferedReader reader;
    
    @Override
    public String readLine(String fmt, Object... args) {
        writer.format(fmt, args);
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public String readPassword(String fmt, Object... args) {
        return readLine(fmt, args);
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.console.ConsoleIO#getWriter()
     */
    @Override
    protected Writer getWriter() {
        return writer;
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.console.ConsoleIO#getErrorWriter()
     */
    @Override
    protected Writer getErrorWriter() {
        return errWriter;
    }
    
    public StreamConsoleIO() {
        writer = new PrintWriter(System.out, true);
        errWriter = new PrintWriter(System.err, true);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
