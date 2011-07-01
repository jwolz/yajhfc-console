YajHFC for console
==================

QUICK START
-----------

- Copy the contents of this archive to a folder convenient for you
- Invoke cyajhfc.sh for UNIX or cyajhfc.exe for Windows to start the application (please note that both jar files must be in the same directory as the sh/exe file)

Configuration of the application must have been done using the graphical interface before (just start YajHFC or double click yajhfc.jar to do that). 

EXAMPLES
--------
a) Send the document "fax.pdf" to the fax numbers 555 and 666:
cyajhfc -r555 -r666 fax.pdf

b) Send the document "fax.pdf" to "John Doe, Doe Street 5, Johnstown, fax number 12345" using a cover page with the subject "Test" and the comment "Hello John!":
cyajhfc --use-cover --subject="Test" --comment="Hello John!" "--recipient=givenname:John;surname:Doe;street:Doe Street 5;location:Johnstown;faxnumber:12345" fax.pdf

c) Query the status of jobs 123 and 125:
cyajhfc --query-job-status=123 --query-job-status=125


d) Doing all of the above in batch mode:

1. Create a file named batch.txt with the following content:
# -- begin batch.txt
# Lines starting with # are treated as comments, empty lines are ignored

# Send the document "fax.pdf" to the fax numbers 555 and 666:
-r555 -r666 fax.pdf

# Send the document "fax.pdf" to "John Doe, Doe Street 5, Johnstown, fax number 12345" using a cover page with the subject "Test" and the comment "Hello John!":
--use-cover --subject="Test" --comment="Hello John!" "--recipient=givenname:John;surname:Doe;street:Doe Street 5;location:Johnstown;faxnumber:12345" fax.pdf

# Query the status of jobs 123 and 125:
--query-job-status=123 --query-job-status=125
# -- end batch.txt

2. Start cyajhfc:
cyajhfc --batch=batch.txt


SUPPORTED COMMAND LINE PARAMETERS
---------------------------------


Usage:
cyajhfc [OPTIONS]... [FILES TO SEND]...

Argument description:
-A, --admin                                         Start up in admin mode. 
    --admin-password=PASSWORD                       Specifies the administrative password to use. 
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
-H, --host=HOSTNAME                                 Specifies the host name of the HylaFAX server to connect to. 
    --hylafax-encoding=CHARSET                      Specifies the character encoding (character set) to use for communicating with the HylaFAX server. 
-I, --identity=IDENTITY                             Sets the sender identity to use when sending the fax. Specify either the identity's name (e.g. "My 
                                                    identity"), the identity's list index (0 is the first element) or the ID in the form "#ID". 
-k, --kill-time=TIMESPEC                            The time to "kill" the fax job after if it has not been sent successfully until that point of time. You can
                                                    specify the time in the following formats: "+n days|hours|minutes|seconds" or "yyyy-MM-dd hh:mm:ss" or your
                                                    locale's default short date format. 
    --loaddriver=JARFILE                            Specifies the location of a JDBC driver JAR file to load. 
    --loadplugin=JARFILE                            Specifies a jar file of a YajHFC plugin to load. 
-l, --logfile=LOGFILE                               The log file to log debug information to (if not specified, use stdout). 
    --max-dials=DIALS                               Specifies the maximum number of dials when trying to send a fax. 
-m, --max-tries=TRIES                               The maximum number of attempts in delivering the fax HylaFAX will perform. 
-M, --modem=MODEM                                   Sets the modem to send the fax. Specify either the modem's name (e.g. ttyS0) or "any" to use any modem. 
    --no-check                                      Suppresses the check for the Java version at startup. 
    --no-plugins                                    Disables loading plugins from the plugin.lst file. 
-N, --notification=none|done|requeued|done+requeued The type of mail status notification for this fax job done by HylaFAX. 
    --notify-address=EMAIL                          Specifies the e-mail address to be used by the HylaFAX server fax status notifications. 
    --number-prefix=NUMBERS                         Specifies a prefix prepended to all fax numbers before sending them to the HylaFAX server. 
    --override-setting=KEY=VALUE                    Overrides the value of the specified setting for this session. The overridden setting is not saved. 
-p, --paper-size=a4|a5|legal|letter                 The paper size to use for sending the fax. 
    --passive[=yes|no]                              Use passive mode (yes) or active mode (no) when communicating with the HylaFAX server? 
-w, --password=PASSWORD                             Specifies the password to use. 
    --poll                                          Poll the recipient for faxes instead of sending one to it. 
    --port=1-65535                                  Specifies the TCP port to connect to on the HylaFAX server. 
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
-U, --user=USERNAME                                 Specifies the user name to log into the HylaFAX server. 
-v, --verbose                                       Print more messages. You can specify this multiple times to be more verbose. 
    --version                                       Prints version information. 
    
============
NOTE: The following command line options are global and are ignored when given inside "batch files":
    --appendlogfile=LOGFILE
-b, --batch[=FILE]
    --batch-format=cmdline 
-c, --configdir=DIRECTORY
-d, --debug
-h, --help[=COLUMNS]
    --loaddriver=JARFILE
    --loadplugin=JARFILE
-l, --logfile=LOGFILE
    --no-check
    --no-plugins
    --override-setting=KEY=VALUE
-q, --quiet
    --stdin (You can only specify this for at most one document, so it is treated as a global option)
-v, --verbose

