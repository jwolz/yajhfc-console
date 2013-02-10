/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2013 Jonas Wolz <info@yajhfc.de>
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

import java.io.IOException;
import java.io.Writer;

/**
 * A writer that copies everything written to it to all child writers.
 * 
 * @author jonas
 *
 */
public class CopyWriter extends Writer {
    protected final Writer[] childs;
    
    public CopyWriter(Writer... childs) {
        super();
        this.childs = childs;
    }

    @Override
    public void write(int c) throws IOException {
        for (Writer w : childs) {
            w.write(c);
        }
    }

    @Override
    public void write(char[] cbuf) throws IOException {
        for (Writer w : childs) {
            w.write(cbuf);
        }
    }

    @Override
    public void write(String str) throws IOException {
        for (Writer w : childs) {
            w.write(str);
        }
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        for (Writer w : childs) {
            w.write(str, off, len);
        }
    }

    @Override
    public Writer append(CharSequence csq) throws IOException {
        for (Writer w : childs) {
            w.append(csq);
        }
        return this;
    }

    @Override
    public Writer append(CharSequence csq, int start, int end)
            throws IOException {
        for (Writer w : childs) {
            w.append(csq, start, end);
        }
        return this;
    }

    @Override
    public Writer append(char c) throws IOException {
        for (Writer w : childs) {
            w.append(c);
        }
        return this;
    }

    /* (non-Javadoc)
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        for (Writer w : childs) {
            w.write(cbuf, off, len);
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
        for (Writer w : childs) {
            w.flush();
        }
    }

    /* (non-Javadoc)
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
        for (Writer w : childs) {
            w.close();
        }
    }

}
