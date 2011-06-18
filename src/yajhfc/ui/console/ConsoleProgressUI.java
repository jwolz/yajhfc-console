/**
 * 
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
        cons.println(ConsoleIO.VERBOSITY_NORMAL, message);
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
