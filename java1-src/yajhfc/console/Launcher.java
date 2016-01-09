/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2005-2011 Jonas Wolz <info@yajhfc.de>
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
package yajhfc.console;

import yajhfc.JavaVersionParser;


/**
 * Java 1.1 compatible launcher to show a nice error message when Java is too old
 * @author jonas
 *
 */
public class Launcher {

    /**
     * @param args
     */
    public static void main(String[] args) {
        boolean docheck = true;
        for (int i=0; i<args.length; i++) {
            if (args[i].equals("--no-check")) {
                docheck = false;
                break;
            }
        }

        if (docheck) {
            JavaVersionParser jVersion = new JavaVersionParser();
            
            if (jVersion.isLessThan(1, 6)) {
                System.err.println("You need at least Java 1.6 (Java 6) to run cyajhfc.\nThe installed version is " + jVersion + ".");
                System.exit(1);
            }

            String vmName = System.getProperty("java.vm.name");
            if (vmName != null && vmName.indexOf("gcj") >= 0) {
                System.err.println("You are apparently using GNU gcj/gij to run YajHFC.");
                System.err.println("Running YajHFC with gcj is not recommended and may cause problems.");
                System.err.println("Note: You may use the --no-check command line parameter to suppress this warning.");
            }
        }

        try {
            startRealLauncher(args);
        } catch (Throwable t) {
            System.err.println("Error starting YajHFC:");
            t.printStackTrace();
            
            System.exit(1);
        }
    }
    
    public static void startRealLauncher(String[] args) {
        Main.main(args);
    }

}
