/**
 * 
 */
package yajhfc.ui.console;

import javax.swing.JOptionPane;

import yajhfc.ui.YajOptionPane;
import yajhfc.util.ProgressWorker.ProgressUI;

/**
 * @author jonas
 *
 */
public class ConsoleYajOptionPane extends YajOptionPane {
    
    protected void printMessage(ConsoleIO cons, int priority, String title, String message) {
        cons.println(priority, title + ": " + message);
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showExceptionDialog(java.lang.String, java.lang.String, java.lang.Exception)
     */
    @Override
    public void showExceptionDialog(String title, String message, Exception exc) {
        ConsoleIO cons = ConsoleIO.getDefault();
        printMessage(cons, ConsoleIO.VERBOSITY_ERROR, title, message);
        exc.printStackTrace(cons.writer());
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showExceptionDialog(java.lang.String, java.lang.Exception)
     */
    @Override
    public void showExceptionDialog(String message, Exception exc) {
        showExceptionDialog("Error", message, exc);
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showPasswordDialog(java.lang.String, java.lang.String, java.lang.String, boolean)
     */
    @Override
    public String[] showPasswordDialog(String title, String prompt,
            String userName, boolean editableUsername) {
        return showPasswordDialog(title, prompt, userName, editableUsername, false);
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showPasswordDialog(java.lang.String, java.lang.String, java.lang.String, boolean, boolean)
     */
    @Override
    public String[] showPasswordDialog(String title, String prompt,
            String userName, boolean editableUsername,
            boolean allowEmptyPassword) {
        ConsoleIO cons = ConsoleIO.getDefault();
        String user = userName;
        
        printMessage(cons, ConsoleIO.VERBOSITY_USERPROMPT, title, prompt);
        if (editableUsername) {
            user = cons.readLine("User name [%s]: ", userName);
            if (user == null)
                return null;
            
            if (user.length() == 0) {
                user = userName;
            }
        }
        String pass = null;
        do {
            pass = cons.readPassword("Password for user %s: ", user);
            if (pass == null)
                return null;
        } while (!allowEmptyPassword && pass.length() == 0);
        
        return new String[] { user, pass };
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showMessageDialog(java.lang.String, java.lang.String, int)
     */
    @Override
    public void showMessageDialog(String message, String title, int messageType) {
        ConsoleIO cons = ConsoleIO.getDefault();
        printMessage(cons, ConsoleIO.VERBOSITY_WARNING, title, message);
    }

    /* (non-Javadoc)
     * @see yajhfc.ui.YajOptionPane#showConfirmDialog(java.lang.String, java.lang.String, int, int)
     */
    @Override
    public int showConfirmDialog(String message, String title, int optionType,
            int messageType) {
        ConsoleIO cons = ConsoleIO.getDefault();
        printMessage(cons, ConsoleIO.VERBOSITY_USERPROMPT, title, message);
        
        String options, errorMsg, prompt;
        switch (optionType) {
        case JOptionPane.YES_NO_OPTION:
            options = "yn";
            prompt = "[y/n]? ";
            errorMsg = "Please enter y for Yes or N for No!";
            break;
        case JOptionPane.YES_NO_CANCEL_OPTION:
        default:
            options = "ync";
            errorMsg = "Please enter y for Yes, N for No or C for Cancel!";
            prompt = "[y/n/c]? ";
            break;
        }
        
        int rv = -1;
        while (rv == -1) {
            String res = cons.readLine(prompt);
            if (res == null)
                res = "c"; // Cancel
            if (res.length() == 1) {
                char a = res.charAt(0);
                if (options.indexOf(a) >= 0) {
                    switch (a) {
                    case 'y':
                    case 'Y':
                        rv = JOptionPane.YES_OPTION;
                        break;
                    case 'n':
                    case 'N':
                        rv = JOptionPane.NO_OPTION;
                        break;
                    case 'c':
                    case 'C':
                        rv = JOptionPane.CANCEL_OPTION;
                        break;
                    default:
                        rv = -1;
                        break;
                    }
                }
            }
            if (rv == -1) {
                cons.println(ConsoleIO.VERBOSITY_USERPROMPT, errorMsg);
            }
        }
        return rv;
    }

    @Override
    public void invokeLater(Runnable toRun) {
        toRun.run();
    }

    @Override
    public ProgressUI createDefaultProgressMonitor(String message, String note,
            int min, int max) {
        ConsoleProgressUI res = new ConsoleProgressUI();
        res.showDeterminateProgress(message, note, min, max);
        return res;
    }

}
