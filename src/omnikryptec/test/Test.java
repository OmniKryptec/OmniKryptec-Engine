package omnikryptec.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.InputStream;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

import omnikryptec.swing.JCheckBoxList;
import omnikryptec.util.action.ActionProcessor;
import omnikryptec.util.debug.VariableChangeListener;
import omnikryptec.util.lang.ILanguage;
import omnikryptec.util.lang.LanguageManager;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;
import omnikryptec.util.logger.SystemInputStream;

/**
 *
 * @author Panzer1119
 */
public class Test implements ILanguage {

    public static String test_data = "Troll";

    public static Thread thread_test = new Thread(() -> {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(1000);
                troll();
                Thread.sleep(1000);
                troll();
                troll();
            }
        } catch (Exception ex) {
            Logger.logErr("Error 1: " + ex, ex);
        }
    });

    /**
     * Test fuer den VariableChangeListener
     *
     * @param args Egal
     */
    public static void main(String[] args) {
        Logger.enableLoggerRedirection(true);
        Test test = new Test();
        LanguageManager.addLanguageListener(test);
        Logger.CONSOLE.setExitWhenLastOne(true);
        Logger.showConsoleDirect();
        new Thread(() -> {
            try {
                Thread.sleep(4000);
                LanguageManager.setLanguage("EN");
                Thread.sleep(4000);
                LanguageManager.setLanguage("DE");
            } catch (Exception ex) {
                Logger.logErr("Error FTW: " + ex, ex);
            }
        }).start();
        VisualThreadMonitor vtm = new VisualThreadMonitor(thread_test);
        thread_test.start();
        new Thread(() -> vtm.start()).start();
        ActionProcessor.addActionsToProcessor(() -> System.out.println("TTTT 1"), () -> System.out.println("TTTT 2"), () -> System.out.println("TTTT 3"), () -> System.out.println("TTTT 4"), () -> System.out.println("TTTT 5"), () -> System.out.println("TTTT 6"));
        // LanguageManager.collectAllLanguageKeys(new File("lang_TE.txt"));
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
                InputStream inputStream = System.in;
                Logger.log("Using System-InputStream: " + inputStream);
                /*
				 * byte[] buffer = new byte[0]; while(inputStream.available() >
				 * 0) { byte read = (byte) inputStream.read(); buffer =
				 * Arrays.copyOf(buffer, buffer.length + 1);
				 * buffer[buffer.length - 1] = read; if(read == ((byte) 10)) {
				 * Logger.log("Scanned Line: " + new String(buffer)); buffer =
				 * new byte[0]; } }
                 */
                String line = null;
                while ((line = SystemInputStream.nextLine()) != null) {
                    Logger.log("Scanned NextLine: " + line);
                }
                /*
				 * final Scanner scanner = new Scanner(inputStream);
				 * while(scanner.hasNextLine()) { Logger.log("Scanned: " +
				 * scanner.nextLine()); //FIXME InputStream wird zwar
				 * abgefangen, aber dann nicht mehr hierher weitergeleitet }
                 */
                Logger.log("Scanner stopped");
                // scanner.close();
            } catch (Exception ex) {

            }
        }).start();
        if (true) {
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
            } catch (Exception ex) {
                Logger.logErr("Error: " + ex, ex);
                Logger.log("Test 1", LogLevel.FINE);
                Logger.log("Test 2", LogLevel.INFO);
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

    public static void troll() {
        Logger.log("troll");
    }

}
