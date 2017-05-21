package omnikryptec.logger;

import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.main.OmniKryptecEngine;
import omnikryptec.main.OmniKryptecEngine.ShutdownOption;

/**
 *
 * @author Panzer1119
 */
public class Commands {
    
    public static final Command COMMANDEXIT = new Command("exit") {
        
        @Override
        public void run(String arguments) {
            ShutdownOption shutdownOption = ShutdownOption.JAVA;
            if(!arguments.isEmpty()) {
                boolean found = false;
                String[] args = getArguments(arguments);
                for(String g : args) {
                    if(g.equalsIgnoreCase("-engine")) {
                        shutdownOption = ShutdownOption.ENGINE;
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    Logger.log(getHelp());
                    return;
                }
            }
            try {
                OmniKryptecEngine.instance().close(shutdownOption);
                Logger.log("Engine was successfully exited", LogLevel.FINE);
            } catch (Exception ex) {
                if(shutdownOption == ShutdownOption.JAVA) {
                    shutdownCompletely();
                } else {
                    Logger.log("No engine running", LogLevel.WARNING);
                }
            }
        }
        
    }.setUseArguments(true).setHelp("Usage:\nexit [-engine]\nParameter - Description\nengine - Stops only the engine");
    
    public static final Command COMMANDTEST = new Command("test") {
        
        @Override
        public void run(String arguments) {
            try {
                String[] args = getArguments(arguments);
                for(String arg : args) {
                    Logger.log(arg);
                }
            } catch (Exception ex) {
                Logger.logErr("Error while executing command \"test " + arguments + "\": " + ex, ex);
            }
        }
        
    }.setUseArguments(true).setHelp("Usage:\ntest <...>");
    
    public static final void initialize() {
        //Nothing, this function only registers automatically all standard Command's 
    }
    
    private static final void shutdownCompletely() {
        while(true) {
            try {
                System.exit(0);
            } catch (Exception ex) {
                System.exit(-1);
            }
        }
    }
    
}
