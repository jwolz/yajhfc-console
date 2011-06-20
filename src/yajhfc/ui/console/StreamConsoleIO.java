package yajhfc.ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StreamConsoleIO extends ConsoleIO {
    protected final PrintWriter writer;
    protected final PrintWriter errWriter;
    protected final BufferedReader reader;
    
    @Override
    public void printfImpl(String format, Object[] args) {
        writer.format(format, args);
    }

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

    @Override
    public PrintWriter writer() {
        return writer;
    }

    @Override
    public void printImpl(String text) {
        errWriter.print(text);
    }
    
    @Override
    public void printlnImpl(String text) {
        errWriter.println(text);
    }
    
    @Override
    public PrintWriter errWriter() {
        return errWriter;
    }
    
    public StreamConsoleIO() {
        writer = new PrintWriter(System.out, true);
        errWriter = new PrintWriter(System.err, true);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
