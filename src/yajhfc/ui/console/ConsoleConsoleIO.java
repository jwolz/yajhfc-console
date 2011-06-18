/**
 * 
 */
package yajhfc.ui.console;

import java.io.Console;
import java.io.PrintWriter;

/**
 * @author jonas
 *
 */
public class ConsoleConsoleIO extends ConsoleIO {
    protected final Console console;

    public void printfImpl(String format, Object[] args) {
        console.printf(format, args);
    }

    public String readLine(String fmt, Object... args) {
        return console.readLine(fmt, args);
    }

    public String readPassword(String fmt, Object... args) {
        return new String(console.readPassword(fmt, args));
    }

    public PrintWriter writer() {
        return console.writer();
    }

    @Override
    public void printImpl(String text) {
        console.writer().print(text);
    }
    
    @Override
    public void printlnImpl(String text) {
        console.writer().println(text);
    }
    
    
    public ConsoleConsoleIO(Console console) {
        super();
        this.console = console;
    }
}
