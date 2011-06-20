YajHFC for console
==================

Quick start:
- Copy the contents of this archive to a folder convenient for you
- Invoke cyajhfc.sh for UNIX or cyajhfc.exe for Windows to start the application (please note that both jar files must be in the same directory as the sh/exe file)

Invocation:
cyajhfc [OPTIONS]... [FILES TO SEND]...

Argument description:
-A, --admin                                         Start up in admin mode. 
    --appendlogfile=LOGFILE                         Append debug information to the given log file. 
    --archive-job[=yes|no]                          Set the HylaFAX "archive" flag for this job. 
-b, --batch[=FILE]                                  Submits multiple faxes. The specified file (or stdin) should contain one set of valid command line options 
                                                    per line. 
    --batch-format=cmdline                          The format of the batch file. Currently only "cmdline" is supported. 
    --comment=COMMENT                               The comment for the cover page. 
-c, --configdir=DIRECTORY                           Sets the configuration directory to use instead of ~/.yajhfc 
    --custom-cover=FILE                             The path to the cover page template to use for this fax job. 
-P, --custom-property=KEY=VALUE                     Sets a custom HylaFAX property (JPARM) for this fax job. To set multiple properties, specify this multiple 
                                                    times. 
-d, --debug                                         Output some debugging information. 
-h, --help[=COLUMNS]                                Displays this text (formatted for COLUMNS columns if given). 
-I, --identity=IDENTITY                             Sets the sender identity to use when sending the fax. Specify either the identity's name (e.g. "My 
                                                    identity"), the identity's list index (0 is the first element) or the ID in the form "#ID". 
-k, --kill-time=TIMESPEC                            The time to "kill" the fax job after if it has not been sent successfully until that point of time. You can
                                                    specify the time in the following formats: "+n days|hours|minutes|seconds" or "yyyy-MM-dd hh:mm:ss" or your
                                                    locale's default short date format. 
    --loaddriver=JARFILE                            Specifies the location of a JDBC driver JAR file to load. 
    --loadplugin=JARFILE                            Specifies a jar file of a YajHFC plugin to load. 
-l, --logfile=LOGFILE                               The log file to log debug information to (if not specified, use stdout). 
-m, --max-tries=TRIES                               The maximum number of attempts in delivering the fax HylaFAX will perform. 
-M, --modem=MODEM                                   Sets the modem to send the fax. Specify either the modem's name (e.g. ttyS0) or "any" to use any modem. 
    --no-check                                      Suppresses the check for the Java version at startup. 
    --no-plugins                                    Disables loading plugins from the plugin.lst file. 
-N, --notification=none|done|requeued|done+requeued The type of mail status notification for this fax job done by HylaFAX. 
    --override-setting=KEY=VALUE                    Overrides the value of the specified setting for this session. The overridden setting is not saved. 
-p, --paper-size=a4|a5|legal|letter                 The paper size to use for sending the fax. 
    --poll                                          Poll the recipient for faxes instead of sending one to it. 
    --print-jobids[=FILE]                           Prints the job IDs of newly sent faxes to stdout or to the specified file. One job per line is printed, in 
                                                    the format "yyyy-mm-dd hh:mm:ss NEW_FAXJOB jobid". 
-Q, --query-job-status=JOBID                        Querys the status of the fax job identified by the given ID. You can specify this parameter multiple times 
                                                    to query the status of multiple jobs. 
    --query-property=PROPERTYNAME                   In addition to the status, query HylaFAX for additional fax job properties ("JPARMs"). You can specify this
                                                    argument multiple times to query multiple additional properties. 
-q, --quiet                                         Print less messages. You can specify this multiple times to be more quiet. 
-r, --recipient=RECIPIENT                           Specifies a recipient to send the fax to. You may specify either a fax number or detailed cover page 
                                                    information (see the FAQ for the format in the latter case). You may specify --recipient multiple times for
                                                    multiple recipients. 
-R, --resolution=low|high|extended                  The resolution to use for sending the fax. 
-t, --send-time=TIMESPEC                            The point of time when the first attempt to send the fax will be made. You can specify the time in the 
                                                    following formats: "now" or "+n days|hours|minutes|seconds" or "yyyy-MM-dd hh:mm:ss" or your locale's 
                                                    default short date format. 
-S, --server=SERVER                                 Sets the server to send the fax over. Specify either the server's name (e.g. "My server"), the server's 
                                                    list index (0 is the first element) or the ID in the form "#ID". 
    --stdin                                         Read the file to send from standard input. 
-s, --subject=SUBJECT                               The fax subject for the cover page. 
-C, --use-cover[=yes|no]                            Use a cover page for sending a fax. 
-v, --verbose                                       Print more messages. You can specify this multiple times to be more verbose. 
