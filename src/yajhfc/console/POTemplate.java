package yajhfc.console;

import gnu.getopt.LongOpt;

import java.io.PrintWriter;

public class POTemplate {

    public static void printPOTemplate(LongOpt[] opts, PrintWriter out) {
        out.print("# Help texts for the command line.\n"+
                "# Messages ending in -desc contain descriptions of the arguments,\n"+
                "# while those ending in -arg contain the description of parameter to the argument\n"+
                "\n"+
                "msgid \"\"\n"+
                "msgstr \"\"\n"+
                "\"MIME-Version: 1.0\\n\"\n"+
                "\"Content-Type: text/plain; charset=utf-8\\n\"\n"+
                "\"Content-Transfer-Encoding: 8bit\\n\"\n"+
                "\n"+
                "# Untranslated text: Usage\n"+
                "msgid \"usage\"\n"+
                "msgstr \"Usage\"\n"+
                "\n"+
                "# Untranslated text: Argument description\n"+
                "msgid \"argument-description\"\n"+
                "msgstr \"Argument description\"\n"+
                "\n"+
                "# Untranslated text: OPTIONS\n"+
                "msgid \"options\"\n"+
                "msgstr \"OPTIONS\"\n"+
                "\n"+
                "# Untranslated text: FILES TO SEND\n"+
                "msgid \"files-to-send\"\n"+
                "msgstr \"FILES TO SEND\"\n\n");
        
        for (LongOpt opt : opts) {
            if (opt.getName().startsWith("X"))
                continue;
            
            out.println("# Untranslated text: ");
            out.println("msgid \"" + opt.getName() + "-desc\"");
            out.println("msgstr \"\"");
            out.println();
            
            if (opt.getHasArg() != LongOpt.NO_ARGUMENT) {
                out.println("# Untranslated text: ");
                out.println("msgid \"" + opt.getName() + "-arg\"");
                out.println("msgstr \"\"");
                out.println();
            }
        }
        
        out.close();
    }
}
