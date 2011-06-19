package yajhfc.ui.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StreamConsoleIO extends ConsoleIO {
    protected PrintWriter writer;
    protected BufferedReader reader;
    
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
        writer.print(text);
    }
    
    @Override
    public void printlnImpl(String text) {
        writer.println(text);
    }
    
    public StreamConsoleIO() {
        writer = new PrintWriter(System.out, true);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
