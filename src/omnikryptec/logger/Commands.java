package omnikryptec.logger;

import omnikryptec.main.OmniKryptecEngine;

/**
 *
 * @author Panzer1119
 */
public class Commands {
    
    public static final Command COMMANDEXIT = new Command("exit") {
        
        @Override
        public void run(String arguments) {
            try {
                OmniKryptecEngine.instance().close(OmniKryptecEngine.ShutdownOption.JAVA);
            } catch (Exception ex) {
                //Logger.logErr("Error while exiting program: " + ex, ex);
                OmniKryptecEngine.shutdownCompletely();
            }
        }
        
    }.setUseArguments(false).setHelp("Usage:\n/exit");
    
    static {
        COMMANDEXIT.getCommand();
    }
    
    public static final void initialize() {
        //Nothing, this function only registers automatically all standard Command's 
    }
    
}
