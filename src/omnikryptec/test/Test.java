package omnikryptec.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Scanner;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import omnikryptec.debug.VariableChangeListener;
import omnikryptec.lang.ILanguage;
import omnikryptec.lang.LanguageManager;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.swing.JCheckBoxList;

/**
 *
 * @author Panzer1119
 */
public class Test implements ILanguage {

    public static String test_data = "Troll";

    /**
     * Test fuer den VariableChangeListener
     * 
     * @param args Egal
     */
    public static void main(String[] args) {
        Logger.enableLoggerRedirection(true);
        Test test = new Test();
        LanguageManager.addLanguageListener(test);
        LanguageManager.collectAllLanguageKeys(new File("lang_TE.txt"));
        System.out.println("Test 1");
        System.err.println("Test 2 " + System.getProperty("user.home"));
        JFrame frame = new JFrame("Test");
        frame.setSize(new Dimension(400, 400));
        frame.setLayout(new BorderLayout());
        DefaultListModel<JCheckBox> model = new DefaultListModel<>();
        model.addElement(new JCheckBox(LanguageManager.getLang("test_key_1", "test_1")));
        JCheckBoxList cbl = new JCheckBoxList(model);
        frame.add(cbl, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        new Thread(() -> {
            try {
                final Scanner scanner = new Scanner(System.in);
                while(scanner.hasNextLine()) {
                    Logger.log("Scanned: " + scanner.nextLine()); //FIXME InputStream wird zwar abgefangen, aber dann nicht mehr hierher weitergeleitet
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

    @Override
    public void reloadLanguage() {
        getLang("file", "File");
        getLang("edit", "Edit");
        getLang("remove", "Remove");
        getLang("add", "Add");
        getLang("exit", "Exit");
        getLang("restart", "Restart");
    }

}
