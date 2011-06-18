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
        writer.flush();
    }

    @Override
    public String readLine(String fmt, Object... args) {
        writer.format(fmt, args);
        writer.flush();
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
        writer.flush();
    }
    
    @Override
    public void printlnImpl(String text) {
        writer.println(text);
        writer.flush();
    }
    
    public StreamConsoleIO() {
        writer = new PrintWriter(System.out);
        reader = new BufferedReader(new InputStreamReader(System.in));
    }
}
