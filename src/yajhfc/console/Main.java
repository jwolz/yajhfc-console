/*
 * YAJHFC - Yet another Java Hylafax client
 * Copyright (C) 2011 Jonas Wolz
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

import static yajhfc.console.i18n.Msgs._;
import gnu.hylafax.HylaFAXClient;
import gnu.hylafax.Job;
import gnu.inet.ftp.ServerResponseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

import yajhfc.FaxNotification;
import yajhfc.FaxResolution;
import yajhfc.HylaClientManager;
import yajhfc.IDAndNameOptions;
import yajhfc.PaperSize;
import yajhfc.SenderIdentity;
import yajhfc.Utils;
import yajhfc.file.textextract.FaxnumberExtractor;
import yajhfc.file.textextract.RecipientExtractionMode;
import yajhfc.launch.Launcher2;
import yajhfc.phonebook.convrules.DefaultPBEntryFieldContainer;
import yajhfc.plugin.PluginManager;
import yajhfc.send.LocalFileTFLItem;
import yajhfc.send.SendController;
import yajhfc.send.SendControllerListener;
import yajhfc.send.SendFaxArchiver;
import yajhfc.send.StreamTFLItem;
import yajhfc.send.email.MailException;
import yajhfc.send.email.YajMailer;
import yajhfc.server.Server;
import yajhfc.server.ServerManager;
import yajhfc.server.ServerOptions;
import yajhfc.ui.YajOptionPane;
import yajhfc.ui.console.ConsoleIO;
import yajhfc.ui.console.ConsoleProgressUI;
import yajhfc.util.BOMInputStream;
import yajhfc.util.ExternalProcessExecutor;

/**
 * @author jonas
 *
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    
    static final Semaphore faxLock = new Semaphore(0);
    
    public static final int EXIT_CODE_SUCCESS = 0;
    public static final int EXIT_CODE_GENERAL_FAILURE = 1;
    public static final int EXIT_CODE_WRONG_PARAMETERS = 2;
    public static final int EXIT_CODE_SEND_FAX_FAILED = 3;
    public static final int EXIT_CODE_WRONG_BATCH_DATA = 4;
    
    public static void printError(String text) {
        printError(ConsoleIO.VERBOSITY_ERROR, text);
    }
    
    public static void printError(int priority, String text) {
        ConsoleIO.getDefault().println(priority, text);
        if (Utils.debugMode) {
            log.info("Error (prio " + priority + "): " + text);
        }
    }
    
    private static boolean validateCommandLineOpts(ConsCommandLineOpts opts) {
        if (opts.stdin && "-".equals(opts.batchInput)) {
            printError(_("Invalid command line arguments:") + " " + _("You cannot specify both --stdin and use batch input from stdin."));
            return false;
        }
        
        if (opts.successDir != null && (!new File(opts.successDir).isDirectory())) {
            printError("success-dir " + opts.successDir + ": " + _("Does not exist or is not a directory."));
            return false;
        }
        if (opts.errorDir != null && (!new File(opts.errorDir).isDirectory())) {
            printError("error-dir " + opts.errorDir + ": " + _("Does not exist or is not a directory."));
            return false;
        }
        
        if (!opts.isBatch() || opts.isSendAction())
        	return validatePerJobCommandLineOpts(opts, _("Invalid command line arguments:"));
        else 
        	return true;
    }

    private static boolean validatePerJobCommandLineOpts(ConsCommandLineOpts opts, String caption) {
        if (opts.queryJobStatus.size() > 0) {
            if (opts.fileNames.size() > 0 || opts.stdin) {
                printError(caption + " " + _("You cannot both query a job status and specify documents to send."));
                return false;
            }
            if (opts.recipients.size() > 0) {
                printError(caption + " " + _("You cannot both query a job status and specify recipients."));
                return false;
            }
        } else {
            if (opts.recipients.size() == 0 && opts.extractRecipients != RecipientExtractionMode.YES) {
                printError(caption + " " + _("You have to specify at least one recipient or query a job status or use batch mode."));
                return false;
            }
            if (opts.poll) {
                if (opts.fileNames.size() > 0 || opts.stdin) {
                    printError(caption + " " +  _("You cannot specify poll mode and have documents to send."));
                    return false;
                }
            } 
        }
        if (opts.mailRecipients && !YajMailer.isAvailable()) {
            printError(_("enable-mail-recipients specified, but plugin to send mails is not installed,"));
            return false;
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
        ConsoleIO.getDefault().setVerbosity(opts.verbosity);
        
        PluginManager.initializeAllKnownPlugins(PluginManager.STARTUP_MODE_CONSOLE); 
        
        if (!validateCommandLineOpts(opts)) {
            System.exit(EXIT_CODE_WRONG_PARAMETERS);
        }
        if (opts.isSendAction()) {
        	processCommandLineForJob(opts, opts);
        }
		if (opts.isBatch()) {
			processBatch(opts);
		}
        System.exit(0);
    }
    
    protected static void processBatch(ConsCommandLineOpts opts) {
    	if ("cmdline".equalsIgnoreCase(opts.batchFormat)) {
    		processBatchCmdLineFmt(opts);
    	} else {
    	    ConsoleIO.getDefault().println(ConsoleIO.VERBOSITY_ERROR, _("Unsupported batch data format") + ": " + opts.batchFormat);
    		System.exit(EXIT_CODE_WRONG_PARAMETERS);
    	}
    }
    
    protected static void processBatchCmdLineFmt(ConsCommandLineOpts opts) {
    	try {
			InputStream inStream;
			if ("-".equals(opts.batchInput)) {
				inStream = System.in;
			} else {
				inStream = new FileInputStream(opts.batchInput);
			}
			BOMInputStream bomIn = new BOMInputStream(inStream);
			String encoding = bomIn.getDetectedCharset();
			if (encoding == null) {
			    encoding = System.getProperty("file.encoding", "ISO8859-1");
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(bomIn, encoding));
			
			String line;
			String[] dummy = new String[0];
			int lineNum = 0;
			while ((line = r.readLine()) != null) {
			    lineNum++;
				line = line.trim();
				if (line.length() == 0 || line.startsWith("#"))
					continue;
				
				String[] args = ExternalProcessExecutor.splitCommandLine(line, false).toArray(dummy);
				stripQuotesFromArgs(args);
				ConsCommandLineOpts jobOpts = new ConsCommandLineOpts();
				jobOpts.parse(args, true);
				
				if (validatePerJobCommandLineOpts(jobOpts, _("Line") + " " + lineNum + ": " + _("Invalid arguments specified in batch mode:"))) {
					processCommandLineForJob(jobOpts, opts);
				} else {
					printError( _("Line") + " " + lineNum + " " + _("data was") + ": " + line);
					System.exit(EXIT_CODE_WRONG_BATCH_DATA);
				}
			}
			r.close();
		} catch (IOException e) {
			Launcher2.application.getDialogUI().showExceptionDialog(MessageFormat.format(_("Error processing the batch data from \"{0}\":"), opts.batchInput), e);
		}
    }
    
    public static void stripQuotesFromArgs(final String[] args) {
    	StringBuilder buf = new StringBuilder();
    	for (int i=0; i<args.length; i++) {
    		final String arg = args[i];
    		buf.setLength(0);
    		
    		char quoteChar = 0;
    		boolean chg = false;
    		for (int j=0; j<arg.length(); j++) {
    			char c=arg.charAt(j);
    			if (quoteChar == 0) { // Not between quotes
    				switch (c) {
    				case '"':
    				case '\'':
    					quoteChar = c;
    					chg = true;
    					break;
    				default:
    					buf.append(c);
    					break;
    				}
    			} else { // Between quotes
    				if (c == quoteChar) {
    					quoteChar = 0;
    				} else {
    					buf.append(c);
    				}
    			}
    		}
    		args[i] = chg ? buf.toString() : arg;
    	}
    }

    /**
     * Processes command line options for this fax job
     * @param opts the options for this job
     * @param globalOpts the globally set options (may be identical to opts)
     */
    protected static void processCommandLineForJob(ConsCommandLineOpts opts, ConsCommandLineOpts globalOpts) {
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
            if (opts.isCustomServerOptions()) {
                ServerOptions newSO = new ServerOptions(server.getOptions());
                opts.fillCustomServerOptions(newSO);
                server = new Server(newSO);
            }
            if (opts.admin) {
                server.getClientManager().setAdminMode(opts.admin);
            }
            
            if (opts.queryJobStatus.size() > 0) {
                queryJobState(server, opts, globalOpts);
            } else {
                sendFax(server, opts, globalOpts);
            }
        } catch (Exception ex) {
            dialogs.showExceptionDialog(_("Error performing the requested operation:"), ex);
            System.exit(EXIT_CODE_SEND_FAX_FAILED);
        }
    }


    
    
    protected static void sendFax(final Server server, final ConsCommandLineOpts opts, final ConsCommandLineOpts globalOpts)
            throws IOException, FileNotFoundException {
        
        final ConsoleProgressUI progressUI = new ConsoleProgressUI();
        final YajOptionPane dialogs = Launcher2.application.getDialogUI();
    
        final SendController sendController = new SendController(server, dialogs, opts.poll, progressUI);
        sendController.addSendControllerListener(new SendControllerListener() {
           public void sendOperationComplete(boolean success) {
        	   if (success) {
        		   faxLock.release();
        		   ConsoleIO.getDefault().setLogFileWriter(null);
        	   } else {
                   printError(_("Sending a fax failed, exiting program."));
                   System.exit(EXIT_CODE_SEND_FAX_FAILED);
        	   }
           } 
        });
        SendFaxArchiver archiver = null;
        if (globalOpts.successDir != null || globalOpts.errorDir != null || globalOpts.errorMail != null) {
            final StringWriter logger = new StringWriter();
            ConsoleIO.getDefault().setLogFileWriter(logger);
            archiver = new SendFaxArchiver(sendController, dialogs, globalOpts.successDir, globalOpts.errorDir, globalOpts.errorMail, logger);
        } 
        
        SenderIdentity identity;
        if (opts.identity != null) {
            identity = IDAndNameOptions.getItemFromCommandLineCoding(Utils.getFaxOptions().identities, opts.identity);
            if (identity == null) {
                log.warning("Identity not found, using default instead: " + opts.identity);
                identity = server.getDefaultIdentity();
            }
        } else {
            identity = server.getDefaultIdentity();
        }
        if (opts.fromIdentity != null) {
            identity = new SenderIdentity(identity);
            DefaultPBEntryFieldContainer.parseStringToPBEntryFieldContainer(identity, opts.fromIdentity);
        }
        sendController.setIdentity(identity);
        
        if (opts.archiveJob != null) 
            sendController.setArchiveJob(opts.archiveJob.booleanValue());
        if (opts.stdin) 
            sendController.getFiles().add(new StreamTFLItem(System.in, null));
        if (opts.useCover != null) 
            sendController.setUseCover(opts.useCover.booleanValue());
        if (opts.comment != null) {
            if (opts.comment.startsWith("@")) {
                FileReader fr = new FileReader(opts.comment.substring(1));
                sendController.setComment(Utils.readFully(fr));
                fr.close();
            } else {
                sendController.setComment(opts.comment);
            }
        }
        if (opts.customCover != null) {
            sendController.setCustomCover(new File(opts.customCover));
        }
        if (opts.customProperties.size() > 0) {
            for (String prop : opts.customProperties) {
                int pos = prop.indexOf('=');
                if (pos <= 0) {
                    printError(_("Invalid custom property") + ": " + prop);
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
                printError(_("The kill time must be in the future; using default instead."));
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
            String n = opts.notification.trim().toUpperCase(Locale.ENGLISH);
            try {
            	FaxNotification not = Enum.valueOf(FaxNotification.class, n);
                sendController.setNotificationType(not);
            } catch (Exception e) {
                if (Utils.debugMode) {
                    log.log(Level.FINE, "FaxNotification not found in enum", e);
                }
                if ("DONE+REQUEUE".equals(n)) {
                    sendController.setNotificationType(FaxNotification.DONE_AND_REQUEUE);
                } else if ("NONE".equals(n)) {
                    sendController.setNotificationType(FaxNotification.NEVER);
                } else {
                    printError(MessageFormat.format(_("Invalid notification type {0}, using default instead."), n));
                }
            }
        }
        if (opts.paperSize != null) {
            try {
                PaperSize ps = Enum.valueOf(PaperSize.class, opts.paperSize.trim().toUpperCase(Locale.ENGLISH));
                sendController.setPaperSize(ps);
            } catch (Exception e) {
                if (Utils.debugMode) {
                    log.log(Level.WARNING, "Invalid paper size", e);
                }
                printError(MessageFormat.format(_("Invalid paper size {0}, using default instead"), opts.paperSize));
            }
        }
        
        if (opts.resolution != null) {
            String r = opts.resolution.trim().toUpperCase(Locale.ENGLISH);
            try {
                FaxResolution res = Enum.valueOf(FaxResolution.class, r);
                sendController.setResolution(res);
            } catch (Exception e) {
                if (Utils.debugMode) {
                    log.log(Level.FINE, "FaxResolution not found in enum", e);
                }
                if ("98".equals(r)) {
                    sendController.setResolution(FaxResolution.LOW);
                } else if ("196".equals(r)) {
                    sendController.setResolution(FaxResolution.HIGH);
                } else {
                    printError(MessageFormat.format(_("Invalid fax resolution {0}, using default instead"), r));
                }
            }
        }
        if (opts.sendTime != null) {
            sendController.setSendTime(opts.sendTime);
        }
        if (opts.subject != null)
            sendController.setSubject(opts.subject);
        
        // n.b.: All documents should have been added at this point
        List<String> mailRecipients = null;
        if ((opts.extractRecipients == RecipientExtractionMode.YES)
                || (opts.extractRecipients == RecipientExtractionMode.AUTO)) {
            try {
                int num;
                if (opts.mailRecipients) {
                    mailRecipients = new ArrayList<String>();
                    FaxnumberExtractor extractor = new FaxnumberExtractor(FaxnumberExtractor.getDefaultPattern(), YajMailer.getDefaultMailPattern());
                    num = extractor.extractFromMultipleDocuments(sendController.getFiles(), opts.recipients, mailRecipients);
                } else {
                    FaxnumberExtractor extractor = new FaxnumberExtractor();
                    num = extractor.extractFromMultipleDocuments(sendController.getFiles(), opts.recipients);
                }
                if (num == 0) {
                    printError(_("Warning: No recipients could be found in the specified documents."));
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Error extracting recipients", e);
            }
        }
        
        if (opts.recipients.size() == 0 && (!opts.mailRecipients || opts.mailRecipients && mailRecipients.size()==0)) {
            printError(_("No recipients specified for fax, exiting program."));
            if (archiver != null)
                archiver.saveFaxAsError();
            System.exit(EXIT_CODE_WRONG_PARAMETERS);
        }
        if (opts.mailRecipients && mailRecipients.size()>0) {
            if (YajMailer.isAvailable())
                try {
                    YajMailer.getInstance().mailToRecipients(sendController, mailRecipients);
                } catch (MailException e) {
                    dialogs.showExceptionDialog("Error sending mail to " + mailRecipients, e);
                }
            else
                printError("Cannot send mail: SendControllerMailer not available!");
        }
        if (opts.recipients.size() > 0) {
            DefaultPBEntryFieldContainer.parseCmdLineStrings(sendController.getNumbers(), opts.recipients);

            if (sendController.validateEntries()) {
                sendController.sendFax();

                try {
                    faxLock.acquire();
                } catch (InterruptedException e) {
                    log.log(Level.SEVERE, "Error waiting for fax to be sent", e);
                    System.exit(EXIT_CODE_GENERAL_FAILURE);
                }
            } else {
                printError(_("Invalid data specified for fax, exiting program."));
                if (archiver != null)
                    archiver.saveFaxAsError();
                System.exit(EXIT_CODE_WRONG_PARAMETERS);
            }
        }
    }

    protected static void queryJobState(Server server, ConsCommandLineOpts opts, final ConsCommandLineOpts globalOpts)
            throws IOException, FileNotFoundException, ServerResponseException {
        YajOptionPane dialogs = Launcher2.application.getDialogUI();
        
        HylaClientManager clientMan = server.getClientManager();
        HylaFAXClient hyfc = clientMan.beginServerTransaction(dialogs);
        if (hyfc == null)
            System.exit(EXIT_CODE_GENERAL_FAILURE);
        synchronized (hyfc) {
			try {
				for (Integer jobID : opts.queryJobStatus) {
					Job job = hyfc.getJob(jobID);
					
					String state = job.getProperty("state");
					System.out.println("Job " + jobID + " status: " + state);
					for (String prop : opts.queryProperties) {
						String val = job.getProperty(prop);
						System.out.println("Job " + jobID + " property " + prop + ": " + val);
					}
				}
			} finally {
				clientMan.endServerTransaction();
			}
		}
    }
}
