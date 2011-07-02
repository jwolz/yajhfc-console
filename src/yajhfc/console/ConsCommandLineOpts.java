package yajhfc.console;
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

import static yajhfc.console.i18n.Msgs._;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import yajhfc.Utils;
import yajhfc.launch.CommonCommandLineOpts;
import yajhfc.launch.HelpPrinter;
import yajhfc.launch.Launcher2;
import yajhfc.launch.ManPrinter;
import yajhfc.plugin.PluginManager.PluginInfo;
import yajhfc.plugin.PluginType;
import yajhfc.server.ServerOptions;
import yajhfc.ui.console.ConsoleIO;

public class ConsCommandLineOpts extends CommonCommandLineOpts {
    
    private static final String CMDLINE_NAME = "cyajhfc";
    
    /**
     * Files to submit. 
     */
    public final List<String> fileNames = new ArrayList<String>();
    /**
     * Recipients to submit to. 
     */
    public final List<String> recipients = new ArrayList<String>();

    /**
     * Admin mode?
     */
    public boolean admin = false;

    /**
     * File to read batch input from
     */
    public String batchInput = null;
    
    /**
     * Format of the batch input data
     */
    public String batchFormat = "cmdline";
    
    /**
     * The desired verbosity
     */
    public int verbosity = ConsoleIO.VERBOSITY_NORMAL;

    /**
     * Read fax from stdin
     */
    public boolean stdin = false;


    /**
     * Archive the fax job?
     */
    public Boolean archiveJob = null;
    
    /**
     * Comment for the fax
     */
    public String comment = null;
    
    /**
     * Path to the custom cover page to use
     */
    public String customCover = null;
    
    /**
     * Recipients to submit to. 
     */
    public final List<String> customProperties = new ArrayList<String>();
    
    /**
     * The desired identity
     */
    public String identity = null;

    /**
     * The desired kill time
     */
    public Date killTime = null; 
    
    /**
     * The maximum number of tries
     */
    public int maxTries = -1;
    
    /**
     * The notification type
     */
    public String notification = null;
    
    /**
     * The modem to use
     */
    public String modem = null;
    
    /**
     * The paper size to use
     */
    public String paperSize = null;
    
    /**
     * Poll for a fax?
     */
    public boolean poll = false;
    
    /**
     * The list of jobs to query status for
     */
    public final List<Integer> queryJobStatus = new ArrayList<Integer>();
    
    /**
     * The list of properties to query 
     */
    public final List<String> queryProperties = new ArrayList<String>();
    
    /**
     * The resolution to use
     */
    public String resolution = null;
    
    /**
     * The time to send
     */
    public Date sendTime = null;
    
    /**
     * The server to use
     */
    public String server = null;
    
    /**
     * The subject to use
     */
    public String subject = null;    
    
    /**
     * Use a cover page?
     */
    public Boolean useCover = null;
    
    /**
     * The values overridden for the from identity
     */
    public String fromIdentity = null;
    
    
    // Server options allowed to set
    
    /**
     * The HylaFAX server's host name
     */
    public String host = null;
    /**
     * The TCP port to connect to 
     */
    public int port = -1;
    /**
     * Use passive mode for HylaFAX protocol operations
     */
    public Boolean passive = null;
    /**
     * The user name used to connect to the HylaFAX server
     */
    public String user = null;
    /**
     * The password used to connect to the HylaFAX server
     */
    public String password = null;
    /**
     * The administrative password used to connect to the HylaFAX server in admin mode
     */
    public String adminPassword = null;
    
    /**
     * The character encoding used by the HylaFAX server
     */
    public String hylaFAXEncoding = null;

    /**
     * The default maximum number of dials sending a fax
     */
    public int maxDials = -1;
    /**
     * The notification e-mail address sent to the HylaFAX server
     */
    public String notifyAddress = null;
    
    /**
     * A prefix prepended to the fax number before sending it to HylaFAX
     */
    public String numberPrefix = null;
    
    
    // max non-char-opt: 23
    final static LongOpt[] longOptsOnlyOnce = new LongOpt[] {
            new LongOpt("appendlogfile", LongOpt.REQUIRED_ARGUMENT, null, 1),
            new LongOpt("batch", LongOpt.OPTIONAL_ARGUMENT, null, 'b'),
            new LongOpt("batch-format", LongOpt.REQUIRED_ARGUMENT, null, 13),
            new LongOpt("configdir", LongOpt.REQUIRED_ARGUMENT, null, 'c'),
            new LongOpt("debug", LongOpt.NO_ARGUMENT, null, 'd'),
            new LongOpt("help", LongOpt.OPTIONAL_ARGUMENT, null, 'h'),
            new LongOpt("loaddriver", LongOpt.REQUIRED_ARGUMENT, null, 3),
            new LongOpt("loadplugin", LongOpt.REQUIRED_ARGUMENT, null, 4),
            new LongOpt("logfile", LongOpt.REQUIRED_ARGUMENT, null, 'l'),
            new LongOpt("no-check", LongOpt.NO_ARGUMENT, null, -2),
            new LongOpt("no-plugins", LongOpt.NO_ARGUMENT, null, 5),
            new LongOpt("override-setting", LongOpt.REQUIRED_ARGUMENT, null, 6),
            new LongOpt("print-jobids", LongOpt.OPTIONAL_ARGUMENT, null, 7),
            new LongOpt("quiet", LongOpt.NO_ARGUMENT, null, 'q'),
            new LongOpt("stdin", LongOpt.NO_ARGUMENT, null, 8),
            new LongOpt("verbose", LongOpt.NO_ARGUMENT, null, 'v'),
            new LongOpt("version", LongOpt.NO_ARGUMENT, null, 15),
            new LongOpt("Xprint-manpage", LongOpt.NO_ARGUMENT, null, -3),
            new LongOpt("Xprint-po-template", LongOpt.OPTIONAL_ARGUMENT, null, -4),
    };
    final static String shortOptsOnlyOnce = "b::c:dh::l:qv"; 
    
    final static LongOpt[] longOptsPerJob = new LongOpt[] {
        	new LongOpt("admin", LongOpt.NO_ARGUMENT, null, 'A'),
        	new LongOpt("admin-password", LongOpt.REQUIRED_ARGUMENT, null, 16),
            new LongOpt("archive-job", LongOpt.OPTIONAL_ARGUMENT, null, 9),
            new LongOpt("comment", LongOpt.REQUIRED_ARGUMENT, null, 10),
            new LongOpt("custom-cover", LongOpt.REQUIRED_ARGUMENT, null, 11),
            new LongOpt("custom-property", LongOpt.REQUIRED_ARGUMENT, null, 'P'),
            new LongOpt("from-identity", LongOpt.REQUIRED_ARGUMENT, null, 23),
            new LongOpt("host", LongOpt.REQUIRED_ARGUMENT, null, 'H'),
            new LongOpt("hylafax-encoding", LongOpt.REQUIRED_ARGUMENT, null, 17),
            new LongOpt("identity", LongOpt.REQUIRED_ARGUMENT, null, 'I'),
            new LongOpt("kill-time", LongOpt.REQUIRED_ARGUMENT, null, 'k'),
            new LongOpt("max-dials", LongOpt.REQUIRED_ARGUMENT, null, 18),
            new LongOpt("max-tries", LongOpt.REQUIRED_ARGUMENT, null, 'm'),
            new LongOpt("notification", LongOpt.REQUIRED_ARGUMENT, null, 'N'),
            new LongOpt("notify-address", LongOpt.REQUIRED_ARGUMENT, null, 19),
            new LongOpt("number-prefix", LongOpt.REQUIRED_ARGUMENT, null, 20),
            new LongOpt("modem", LongOpt.REQUIRED_ARGUMENT, null, 'M'),
            new LongOpt("paper-size", LongOpt.REQUIRED_ARGUMENT, null, 'p'),
            new LongOpt("passive", LongOpt.OPTIONAL_ARGUMENT, null, 21),
            new LongOpt("password", LongOpt.REQUIRED_ARGUMENT, null, 'w'),
            new LongOpt("poll", LongOpt.NO_ARGUMENT, null, 12),
            new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null, 22),
            new LongOpt("query-job-status", LongOpt.REQUIRED_ARGUMENT, null, 'Q'),
            new LongOpt("query-property", LongOpt.REQUIRED_ARGUMENT, null, 14),
            new LongOpt("recipient", LongOpt.REQUIRED_ARGUMENT, null, 'r'),
            new LongOpt("resolution", LongOpt.REQUIRED_ARGUMENT, null, 'R'),
            new LongOpt("send-time", LongOpt.REQUIRED_ARGUMENT, null, 't'),
            new LongOpt("server", LongOpt.REQUIRED_ARGUMENT, null, 'S'),
            new LongOpt("subject", LongOpt.REQUIRED_ARGUMENT, null, 's'),
            new LongOpt("use-cover", LongOpt.OPTIONAL_ARGUMENT, null, 'C'),
            new LongOpt("user", LongOpt.REQUIRED_ARGUMENT, null, 'U'),
    };
    final static String shortOptsPerJob = "AP:H:I:k:m:N:M:p:w:Q:r:R:t:S:s:C::U:"; 
    
    
    /**
     * Parses the command line arguments and does some initial processing for the --help and --logfile options.
     * @param args
     */
    public void parse(String[] args, boolean parseOnlyPerJob) {
        final LongOpt[] longOpts;
        final String shortOpts;
        if (parseOnlyPerJob) {
            longOpts = longOptsPerJob;
            shortOpts = shortOptsPerJob;
        } else {
            longOpts = new LongOpt[longOptsPerJob.length + longOptsOnlyOnce.length];
            System.arraycopy(longOptsOnlyOnce, 0, longOpts, 0, longOptsOnlyOnce.length);
            System.arraycopy(longOptsPerJob, 0, longOpts, longOptsOnlyOnce.length, longOptsPerJob.length);
            
            shortOpts = shortOptsOnlyOnce + shortOptsPerJob;
        }
        final String[] argsWork = args.clone();
        
        Getopt getopt = new Getopt(CMDLINE_NAME, argsWork, shortOpts, longOpts);
        int opt;
        String optarg;
        while ((opt = getopt.getopt()) != -1) {
            switch (opt) {
            case -4: // Xprint-po-template
                try {
                    POTemplate.printPOTemplate(longOpts, new PrintWriter((getopt.getOptarg() == null) ? System.out : new FileOutputStream(getopt.getOptarg())));
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
            case -3: //Xprint-manpage // TODO
                try {
                    new ManPrinter("yajhfc.console.i18n.CommandLineOpts").printManPage(Launcher2.getConsoleWriter(), sortLongOpts(longOpts));
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                break;
            case -2: // no-check (in general: ignore)
                break;
            case 1: // appendlogfile
                logFile = getopt.getOptarg();
                appendToLog = true;
                break;
            case 'b': // batch
                optarg = getopt.getOptarg();
                batchInput = (optarg == null) ?  "-" : optarg;
                break;
            case 13: // batch-format
                batchFormat = getopt.getOptarg();
                break;
            case 'c': // configdir
                configDir = getopt.getOptarg();
                break;
            case 'd': // debug
                debugMode = true;
                break;
            case 'h': // help 
                new HelpPrinter("yajhfc.console.i18n.CommandLineOpts").printHelp(Launcher2.getConsoleWriter(), sortLongOpts(longOpts), getopt.getOptarg(), CMDLINE_NAME);
                System.exit(0);
                break;
            case 3: // loaddriver
                plugins.add(new PluginInfo(new File(getopt.getOptarg()),
                        PluginType.JDBCDRIVER, false));
                break;
            case 4: // loadplugin
                plugins.add(new PluginInfo(new File(getopt.getOptarg()),
                        PluginType.PLUGIN, false));
                break;
            case 'l': // logfile
                logFile = getopt.getOptarg();
                appendToLog = false;
                break;
            case 5: // no-plugins
                noPlugins = true;
                break;
            case 6: // override-setting
                optarg = getopt.getOptarg();
                for (int i = 0; i < optarg.length(); i++) {
                    char c = optarg.charAt(i);
                    if (c < 128) {
                        overrideSettings.append(c);
                    } else {
                        // Escape non-ASCII chars
                        overrideSettings.append("\\u")
                                        .append(Character.forDigit((c >> 12) & 0xf, 16))
                                        .append(Character.forDigit((c >>  8) & 0xf, 16))
                                        .append(Character.forDigit((c >>  4) & 0xf, 16))
                                        .append(Character.forDigit( c        & 0xf, 16));
                    }
                }
                overrideSettings.append('\n');
                break;
            case 7: // print-jobids
                optarg = getopt.getOptarg();
                jobIDOutput = (optarg == null) ? "-" : optarg;
                break;
            case 'q': // quiet
                if (verbosity < ConsoleIO.VERBOSITY_MAX)
                    verbosity += ConsoleIO.VERBOSITY_STEP;
                break;
            case 8: //stdin
                stdin = true;
                break;
            case 'v': //verbose
                if (verbosity > ConsoleIO.VERBOSITY_MIN)
                    verbosity -= ConsoleIO.VERBOSITY_STEP;
                break;
            case 15: //version
            	printVersionInfo();
                System.exit(0);
                break;
            case 'A': // admin
                admin = true;
                break;
            case 16: // admin-password
                adminPassword = getopt.getOptarg();
                break;
            case 9: //archive-job
                archiveJob = parseOptionalBoolean(getopt.getOptarg());
                break;
            case 10: // comment
                comment = getopt.getOptarg();
                break;
            case 11: // custom-cover
                customCover = getopt.getOptarg();
                break;
            case 'P': //custom-property
                customProperties.add(getopt.getOptarg());
                break;
            case 23: // from-identity
                fromIdentity = getopt.getOptarg();
                break;
            case 'H': // host
                host = getopt.getOptarg();
                break;
            case 17: // hylafax-encoding
                hylaFAXEncoding = getopt.getOptarg();
                break;
            case 'I': // identity
                identity = getopt.getOptarg();
                break;
            case 'k': // kill-time
                killTime = parseDate(getopt.getOptarg(), "kill-time");
                break;
            case 18: // max-dials
                maxDials = parseInt(getopt.getOptarg(), "max-dials");
                break;
            case 'm': // max-tries
                maxTries = parseInt(getopt.getOptarg(), "max-tries");
                break;
            case 'N': // notification
                notification = getopt.getOptarg();
                break;
            case 19: // notify-address
                notifyAddress = getopt.getOptarg();
                break;
            case 20: // number-prefix
                numberPrefix = getopt.getOptarg();
                break;
            case 'M': // modem
                modem = getopt.getOptarg();
                break;
            case 'p': // paper-size
                paperSize = getopt.getOptarg();
                break;
            case 21: // passive
                passive = parseOptionalBoolean(getopt.getOptarg());
                break;
            case 'w': // password
                password = getopt.getOptarg();
                break;
            case 12: // poll
                poll = true;
                break;
            case 22: // port
                port = parseInt(getopt.getOptarg(), "port");
                break;
            case 'Q': // query-job-status
                int val = parseInt(getopt.getOptarg(), "query-job-status");
                if (val > 0)
                    queryJobStatus.add(Integer.valueOf(val));
                break;
            case 14: // query-property
                queryProperties.add(getopt.getOptarg());
                break;
            case 'r': // recipient
                recipients.add(getopt.getOptarg());
                break;
            case 'R': // resolution
                resolution = getopt.getOptarg();
                break;
            case 't': // send-time
                sendTime = parseDate(getopt.getOptarg(), "send-time");
                break;
            case 'S': // server
                server = getopt.getOptarg();
                break;
            case 's': // subject
                subject = getopt.getOptarg();
                break;
            case 'C': // use-cover
                useCover = parseOptionalBoolean(getopt.getOptarg());
                break;
            case 'U': // user
                user = getopt.getOptarg();
                break;
            case '?':
                break;
            default:
                System.err.println("Unknown option \'" + (char)opt + "\' in " + argsWork[getopt.getOptind()]);
                break;
            }
        }
        // Add non-option arguments:
        for (int i=getopt.getOptind(); i<argsWork.length; i++) {
            fileNames.add(argsWork[i]);
        }
        
        
    }

    protected Boolean parseOptionalBoolean(String optarg) {
        if (optarg == null || optarg.equals("") || Character.toLowerCase(optarg.charAt(0)) == 'y' || optarg.equals("true")) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }   
    }
    
    protected int parseInt(String optarg, String valueName) {
        try {
            return Integer.parseInt(optarg);
        } catch (NumberFormatException nfe) {
            System.err.println(MessageFormat.format(_("The value \"{0}\" specified for argument {1} is not a number."), optarg, valueName));
            return -1;
        }
    }
    
    protected Date parseDate(String optarg, String valueName) {
        optarg = optarg.trim();
        if ("now".equalsIgnoreCase(optarg)) {
            return new Date();
        } else {
            Pattern delayPattern = Pattern.compile("([+-])?\\s*(\\d+)\\s*((?:mi?n?(?:ute)?s?)|(?:s(?:ec(?:ond)?s?)?)|(?:h(?:our)?s?)|(?:d(?:ay)?s?))", Pattern.CASE_INSENSITIVE);
            Matcher m = delayPattern.matcher(optarg);
            if (m.matches()) {
                String sPlusMinus = m.group(1);
                String sNumber    = m.group(2);
                String sUnit      = m.group(3);
                
                int sign;
                int number, unit;
                if (sPlusMinus == null || "+".equals(sPlusMinus)) {
                    sign =  1;
                } else if ("-".equals(sPlusMinus)) {
                    sign = -1;
                } else {
                    throw new IllegalArgumentException("Unknown sign");
                }
                
                number = Integer.parseInt(sNumber);
                
                switch (sUnit.charAt(0)) {
                case 'm':
                case 'M':
                    unit = Calendar.MINUTE;
                    break;
                case 's':
                case 'S':
                    unit = Calendar.SECOND;
                    break;
                case 'h':
                case 'H':
                    unit = Calendar.HOUR;
                    break;
                case 'd':
                case 'D':
                    unit = Calendar.DAY_OF_MONTH;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown unit");
                }
                
                Calendar cal = Calendar.getInstance();
                cal.add(unit, number * sign);
                return cal.getTime();
            } else {
                Date rv;
                DateFormat[] tryListDateTime = {
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
                        DateFormat.getDateTimeInstance()
                };
                rv = tryDateFormats(tryListDateTime, optarg);
                if (rv != null) {
                    return rv;
                } else {
                    DateFormat[] tryListTime = {
                            new SimpleDateFormat("HH:mm:ss"),
                            DateFormat.getTimeInstance()
                    };
                    rv = tryDateFormats(tryListTime, optarg);
                    if (rv != null) {
                        Calendar cal = Calendar.getInstance();
                        long now = cal.getTimeInMillis();
                        
                        cal.set(Calendar.HOUR, 0);
                        cal.set(Calendar.MINUTE, 0);
                        cal.set(Calendar.SECOND, 0);
                        cal.set(Calendar.MILLISECOND, 0);
                        long millisTillNow = now - cal.getTimeInMillis();

                        if (rv.getTime() < millisTillNow) { // if the time specified is in the past, use the respective time tomorrow
                            cal.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        cal.add(Calendar.MILLISECOND, (int)rv.getTime());
                        return cal.getTime();
                    } else {
                        System.err.println(MessageFormat.format(_("The value \"{0}\" specified for argument {1} is not a recognized date/time value."), optarg, valueName));
                        return null;
                    }
                }
            }
        }
    }
    
    private Date tryDateFormats(DateFormat[] tryList, String text) {
        for (DateFormat df : tryList) {
            try {
                Date rv = df.parse(text);
                if (rv != null)
                    return rv;
            } catch (ParseException e) {
                // do nothing
            }
        }
        return null;
    }
    
    public boolean isBatch() {
    	return (batchInput != null);
    }
    
    public boolean isSendAction() {
    	return (poll || recipients.size() > 0 || queryJobStatus.size() > 0);
    }
    
    public boolean isCustomServerOptions() {
        return 
          (host != null) ||
          (port > 0) ||
          (passive != null) ||
          (user != null) ||
          (password != null) ||
          (adminPassword != null) ||
          (hylaFAXEncoding != null) ||
          (maxDials > 0) ||
          (notifyAddress != null) ||
          (numberPrefix != null);
    }
    
    public void fillCustomServerOptions(ServerOptions target) {
          if (host != null) 
              target.host = host;
          if (port > 0)
              target.port = port;
          if (passive != null)
              target.pasv = passive.booleanValue();
          if (user != null) {
              target.user = user;
              target.askUsername = false;
          }
          if (password != null) {
              target.pass.setPassword(password);
              target.askPassword = false;
          }
          if (adminPassword != null) {
              target.AdminPassword.setPassword(adminPassword);
              target.askAdminPassword = false;
          }
          if (hylaFAXEncoding != null) 
              target.hylaFAXCharacterEncoding = hylaFAXEncoding;
          if (maxDials > 0)
              target.maxDial = maxDials;
          if (notifyAddress != null)
              target.notifyAddress = notifyAddress;
          if (numberPrefix != null)
              target.numberPrefix = numberPrefix;
          
          // Ensure we log out after sending the fax(es)
          target.useDisconnectedMode = true;
    }
    
    static LongOpt[] sortLongOpts(LongOpt[] in) {
        LongOpt[] rv = in.clone();
        Arrays.sort(rv, new Comparator<LongOpt>() {
            @Override
            public int compare(LongOpt o1, LongOpt o2) {
                String n1 = o1.getName();
                String n2 = o2.getName();
                
                // Move options starting with X to the end
                if ( n1.startsWith("X") && !n2.startsWith("X")) {
                    return 1;
                }
                if (!n1.startsWith("X") &&  n2.startsWith("X")) {
                    return -1;
                }
                return n1.compareTo(n2);
            }
        });
        return rv;
    }
    
    public static void printVersionInfo() {
    	PrintWriter out = ConsoleIO.getDefault().writer();
    	out.println(Utils.AppShortName + " " + Utils.AppVersion);
    	out.println(Utils.AppCopyright);
    	out.println("For more information, please visit: " + Utils.HomepageURL);
    	out.println();
    	out.println("Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ')');
    	out.println("     " + System.getProperty("java.runtime.name") + ' ' + System.getProperty("java.runtime.version"));
    	out.println("     " + System.getProperty("java.vm.name"));
    }
    
    public ConsCommandLineOpts() {
        super();
    }
    
    public ConsCommandLineOpts(String[] args) {
        this();
        parse(args, false);
    }
}
