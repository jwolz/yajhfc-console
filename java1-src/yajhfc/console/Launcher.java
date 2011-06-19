/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2005-2008 Jonas Wolz
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
package yajhfc.console;


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
            int javaMinor = -1, javaMajor = -1;
            String javaVer = System.getProperty("java.version");
            try {
                if (javaVer != null) {
                    int firstDot = javaVer.indexOf('.');
                    if (firstDot < 0) {
                        javaMajor = Integer.parseInt(javaVer);
                    } else {
                        int secondDot = javaVer.indexOf('.', firstDot+1);
                        if (secondDot < 0)
                            secondDot = javaVer.length();

                        javaMajor = Integer.parseInt(javaVer.substring(0, firstDot));
                        javaMinor = Integer.parseInt(javaVer.substring(firstDot+1, secondDot));
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Could not determine Java version.\n Reason:");
                e.printStackTrace();
            }
            if (javaMajor < 1 || (javaMajor == 1 && javaMinor < 6)) {
                System.err.println("You need at least Java 1.6 (Java 6) to run cyajhfc.\nThe installed version is " + javaVer + ".");
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
