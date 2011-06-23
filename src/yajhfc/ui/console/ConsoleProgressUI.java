/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2011 Jonas Wolz
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package yajhfc.ui.console;

import yajhfc.util.ProgressWorker.ProgressUI;

/**
 * @author jonas
 *
 */
public class ConsoleProgressUI implements ProgressUI {
    protected int minimum = 0;
    protected int maximum = -1;
    protected int lastPrintedProgress = 0;
    
    protected boolean showingIndeterminate;
    
    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#close()
     */
    public void close() {
        // NOP
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#setNote(java.lang.String)
     */
    public void setNote(String note) {
        ConsoleIO cons = ConsoleIO.getDefault();
        cons.println(ConsoleIO.VERBOSITY_FINE, "--> " + note);
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#setProgress(int)
     */
    public void setProgress(int progress) {
        int span = maximum - minimum;
        if (span > 0 && Math.abs(progress - lastPrintedProgress) * 20 >= span) {
            ConsoleIO cons = ConsoleIO.getDefault();
            int percent = ((progress-minimum) * 100 / span);
            cons.printf(ConsoleIO.VERBOSITY_FINER, "%d%%...", percent);
            lastPrintedProgress = progress;
        }
        showingIndeterminate = false;
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#setMaximum(int)
     */
    public void setMaximum(int progress) {
        this.maximum = progress;
        showingIndeterminate = false;
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#supportsIndeterminateProgress()
     */
    public boolean supportsIndeterminateProgress() {
        return true;
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#showIndeterminateProgress(java.lang.String, java.lang.String)
     */
    public void showIndeterminateProgress(String message, String initialNote) {
        ConsoleIO cons = ConsoleIO.getDefault();
        cons.println(ConsoleIO.VERBOSITY_NORMAL, message + "...");
        setNote(initialNote);
        showingIndeterminate = true;
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#isShowingIndeterminate()
     */
    public boolean isShowingIndeterminate() {
        return showingIndeterminate;
    }

    /* (non-Javadoc)
     * @see yajhfc.util.ProgressWorker.ProgressUI#showDeterminateProgress(java.lang.String, java.lang.String, int, int)
     */
    public void showDeterminateProgress(String message, String initialNote,
            int min, int max) {
        ConsoleIO cons = ConsoleIO.getDefault();
        cons.println(ConsoleIO.VERBOSITY_NORMAL, message);
        setNote(initialNote);
        minimum = min;
        maximum = max;
        lastPrintedProgress = min;
        showingIndeterminate = false;
    }

}
