package omnikryptec.test;

import java.util.Scanner;
import javax.swing.JFrame;
import omnikryptec.debug.VariableChangeListener;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;

/**
 *
 * @author Panzer1119
 */
public class Test {

    public static String test_data = "Troll";

    /**
     * Test fuer den VariableChangeListener
     * 
     * @param args Egal
     */
    public static void main(String[] args) {
        Logger.enableLoggerRedirection(true);
        System.out.println("Test 1");
        System.err.println("Test 2");
        JFrame frame = new JFrame();
        frame.setVisible(true);
        new Thread(() -> {
            try {
                final Scanner scanner = new Scanner(System.in);
                while(scanner.hasNextLine()) {
                    Logger.log("Scanned: " + scanner.nextLine());
                }
                Logger.log("Scanner stopped");
                scanner.close();
            } catch (Exception ex) {
                
            }
        }).start();
        if(true) {
            return;
        }
        VariableChangeListener vcl = new VariableChangeListener(250) {

            @Override
            public Object getVariable() {
                return test_data;
            }

            @Override
            public void variableChanged(Object oldValue, Object newValue) {
                System.out.println(String.format("Variable changed from \"%s\" to \"%s\"", oldValue, newValue));
            }

        };
        vcl.start();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                test_data = "Troll 2";
                Thread.sleep(1000);
                test_data = "Troll 3";
                Thread.sleep(1000);
                Logger.setLogEntryFormat("me <-  dtcl");
                test_data = "Troll 4";
                Thread.sleep(1000);
                test_data = "Troll 5";
                Thread.sleep(1000 / 0);
                System.exit(0);
            } catch (Exception ex) {
                Logger.logErr("Error: " + ex, ex);
                Logger.log("Test 1", LogLevel.FINE);
                Logger.log("Test 2", LogLevel.INFO);
                System.exit(-1);
            }
        }).start();
    }

}
