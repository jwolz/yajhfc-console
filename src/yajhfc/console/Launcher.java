/**
 * 
 */
package yajhfc.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import yajhfc.FaxNotification;
import yajhfc.FaxResolution;
import yajhfc.IDAndNameOptions;
import yajhfc.PaperSize;
import yajhfc.SenderIdentity;
import yajhfc.Utils;
import yajhfc.launch.Launcher2;
import yajhfc.phonebook.convrules.DefaultPBEntryFieldContainer;
import yajhfc.plugin.PluginManager;
import yajhfc.send.LocalFileTFLItem;
import yajhfc.send.SendController;
import yajhfc.send.SendControllerListener;
import yajhfc.send.StreamTFLItem;
import yajhfc.server.Server;
import yajhfc.server.ServerManager;
import yajhfc.server.ServerOptions;
import yajhfc.ui.YajOptionPane;
import yajhfc.ui.console.ConsoleIO;
import yajhfc.ui.console.ConsoleProgressUI;

/**
 * @author jonas
 *
 */
public class Launcher {
    private static final Logger log = Logger.getLogger(Launcher.class.getName());
    
    private static void printValidationError(String text) {
        System.err.println(text);
        if (Utils.debugMode) {
            log.warning("Invalid command line option specified: " + text);
        }
    }
    
    private static boolean validateCommandLineOpts(ConsCommandLineOpts opts) {
        if (opts.stdin && "-".equals(opts.batchInput)) {
            printValidationError("You cannot specify both --stdin and use batch input from stdin.");
            return false;
        }
        
        return validatePerJobCommandLineOpts(opts);
    }

    private static boolean validatePerJobCommandLineOpts(ConsCommandLineOpts opts) {
        if (opts.poll) {
            if (opts.fileNames.size() > 0 || opts.stdin) {
                printValidationError("You cannot specify poll mode and documents to send.");
                return false;
            }
            if (opts.queryJobStatus.size() > 0) {
                printValidationError("You cannot specify poll mode and query job status.");
                return false;
            }
        } else if (opts.queryJobStatus.size() > 0) {
            if (opts.fileNames.size() > 0 || opts.stdin) {
                printValidationError("You cannot both query a job status and specify documents to send.");
                return false;
            }
            if (opts.recipients.size() > 0) {
                printValidationError("You cannot both query a job status and specify recipients.");
                return false;
            }
        } else {
            if (opts.recipients.size() == 0) {
                printValidationError("In console mode you have to specify at least one recipient.");
                return false;
            }
            if (opts.fileNames.size() == 0 && !opts.stdin) {
                printValidationError("In console mode you have to specify at least one file to send or --stdin.");
                return false;
            }
        }
        return true;
    }
    
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {      
        ConsCommandLineOpts opts = new ConsCommandLineOpts(args);
        
        Launcher2.setupFirstStage(args, opts); // IMPORTANT: Don't access Utils before this line!
        
        Launcher2.application = new ConsoleMainFrame();
        
        PluginManager.initializeAllKnownPlugins(PluginManager.STARTUP_MODE_NO_GUI); // TODO: Startup mode constant?

        if (!validateCommandLineOpts(opts)) {
            System.exit(1);
        }
        processCommandLineForJob(opts);
    }

    protected static void processCommandLineForJob(ConsCommandLineOpts opts) {
        YajOptionPane dialogs = Launcher2.application.getDialogUI();
        
        try {            
            Server server;
            if (opts.server == null) {
                server = ServerManager.getDefault().getCurrent(); 
            } else {
                ServerOptions so = IDAndNameOptions.getItemFromCommandLineCoding(Utils.getFaxOptions().servers, opts.server);
                if (so != null) {
                    server = ServerManager.getDefault().getServerByID(so.id);
                } else {
                    log.warning("Server not found, using default instead: " + opts.server);
                    server = ServerManager.getDefault().getCurrent(); 
                }
            }
            if (opts.admin) {
                server.getClientManager().setAdminMode(opts.admin);
            }
            
            if (opts.queryJobStatus.size() > 0) {
                queryJobState(server, opts);
            } else {
                sendFax(server, opts);
            }
        } catch (Exception ex) {
            dialogs.showExceptionDialog(Utils._("Error sending the fax:"), ex);
            System.exit(2);
        }
    }

    protected static void sendFax(Server server, ConsCommandLineOpts opts)
            throws IOException, FileNotFoundException {
        
        ConsoleProgressUI progressUI = new ConsoleProgressUI();
        YajOptionPane dialogs = Launcher2.application.getDialogUI();
    
        SendController sendController = new SendController(server, dialogs, opts.poll, progressUI);
        sendController.addSendControllerListener(new SendControllerListener() {
           public void sendOperationComplete(boolean success) {
               ConsoleIO.getDefault().println(ConsoleIO.VERBOSITY_NORMAL, success ? "Fax(es) sent successfully" : "Sending fax(es) failed");
               System.exit(success ? 0 : 1);
           } 
        });

        if (opts.identity != null) {
            SenderIdentity identity = IDAndNameOptions.getItemFromCommandLineCoding(Utils.getFaxOptions().identities, opts.identity);
            if (identity != null) {
                sendController.setFromIdentity(identity);
            } else {
                log.warning("Identity not found, using default instead: " + opts.identity);
                sendController.setFromIdentity(server.getDefaultIdentity());
            }
        } else {
            sendController.setFromIdentity(server.getDefaultIdentity());
        }
        
        if (opts.archiveJob != null) 
            sendController.setArchiveJob(opts.archiveJob.booleanValue());
        if (opts.stdin) 
            sendController.getFiles().add(new StreamTFLItem(System.in, null));
        if (opts.useCover != null) 
            sendController.setUseCover(opts.useCover.booleanValue());
        if (opts.comment != null)
            sendController.setComments(opts.comment);
        if (opts.customCover != null) {
            sendController.setCustomCover(new File(opts.customCover));
        }
        if (opts.customProperties.size() > 0) {
            for (String prop : opts.customProperties) {
                int pos = prop.indexOf('=');
                if (pos <= 0) {
                    printValidationError("Invalid custom property: " + prop);
                } else {
                    sendController.getCustomProperties().put(prop.substring(0, pos), prop.substring(pos+1));
                }
            }
        }
        for (String file : opts.fileNames) {
            sendController.getFiles().add(new LocalFileTFLItem(file));
        }
        // identity: see above
        
        if (opts.killTime != null) {
            long delayMillis = opts.killTime.getTime() - System.currentTimeMillis();
            if (delayMillis <= 0) {
                printValidationError("The kill time must be in the future!");
            } else {
                sendController.setKillTime((int)(delayMillis / (1000*60)));
            }
        }
        if (opts.maxTries > 0) {
            sendController.setMaxTries(opts.maxTries);
        }
        if (opts.modem != null)
            sendController.setSelectedModem(opts.modem);
        if (opts.notification != null) {
            String n = opts.notification;
            if ("NEVER".equalsIgnoreCase(n)) {
                sendController.setNotificationType(FaxNotification.NEVER);
            } else if ("DONE".equalsIgnoreCase(n)) {
                sendController.setNotificationType(FaxNotification.DONE);
            } else if ("REQUEUE".equalsIgnoreCase(n)) {
                sendController.setNotificationType(FaxNotification.REQUEUE);
            } else if ("DONE_AND_REQUEUE".equalsIgnoreCase(n) || "DONE+REQUEUE".equalsIgnoreCase(n)) {
                sendController.setNotificationType(FaxNotification.DONE_AND_REQUEUE);
            } else{
                printValidationError("Invalid notification type: " + n);
            }
        }
        if (opts.paperSize != null) {
            try {
                PaperSize ps = Enum.valueOf(PaperSize.class, opts.paperSize.trim().toUpperCase());
                sendController.setPaperSize(ps);
            } catch (Exception e) {
                if (Utils.debugMode) {
                    log.log(Level.WARNING, "Invalid paper size", e);
                }
                printValidationError("Invalid paper size: " + opts.paperSize);
            }
        }
        
        DefaultPBEntryFieldContainer.parseCmdLineStrings(sendController.getNumbers(), opts.recipients);
        
        if (opts.resolution != null) {
            String r = opts.resolution;
            if ("LOW".equalsIgnoreCase(r) || "98".equals(r)) {
                sendController.setResolution(FaxResolution.LOW);
            } else if ("HIGH".equalsIgnoreCase(r) || "196".equals(r)) {
                sendController.setResolution(FaxResolution.HIGH);
            } else if ("EXTENDED".equalsIgnoreCase(r)) {
                sendController.setResolution(FaxResolution.EXTENDED);
            } else {
                printValidationError("Invalid fax resolution: " + r);
            }
        }
        if (opts.sendTime != null) {
            sendController.setSendTime(opts.sendTime);
        }
        if (opts.subject != null)
            sendController.setSubject(opts.subject);
        
        if (sendController.validateEntries()) {
            sendController.sendFax();
        }
    }

    protected static void queryJobState(Server server, ConsCommandLineOpts opts)
            throws IOException, FileNotFoundException {
        //TODO
    }
}
