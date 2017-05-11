package omnikryptec.test;

import omnikryptec.debug.VariableChangeListener;
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
        System.err.println("Test");
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
                Thread.sleep(1000 / 0);
                System.exit(0);
            } catch (Exception ex) {
                Logger.logErr("Error: " + ex, ex);
                System.exit(-1);
            }
        }).start();
    }

}
