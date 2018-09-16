/*
 *    Copyright 2017 - 2018 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util.logger;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.lang.LanguageManager;
import de.codemakers.lang.LanguageReloader;
import de.omnikryptec.main.OmniKryptecEngine;
import de.omnikryptec.util.EnumCollection.GameState;
import de.omnikryptec.util.logger.LogEntryFormatter.LogEntryFormatTile;

/**
 * @author Panzer1119
 */
public class Console extends JFrame implements ActionListener, LanguageReloader, KeyListener, WindowListener {
    
    /**
     *
     */
    private static final long serialVersionUID = 6039442939368637162L;
    
    public static final String ICON = "/de/omnikryptec/res/icons/Farm-Fresh_application_xp_terminal.png";
    
    protected final HashMap<LogLevel, Boolean> logLevelVisibilities = new HashMap<LogLevel, Boolean>() {
        /**
         *
         */
        private static final long serialVersionUID = 1290229250089508063L;
        
        {
            for (LogLevel ll : LogLevel.values()) {
                put(ll, false);
            }
        }
    };
    protected final HashMap<JCheckBoxMenuItem, LogLevel> logLevelCheckBoxes = new HashMap<JCheckBoxMenuItem, LogLevel>() {
        /**
         *
         */
        private static final long serialVersionUID = 4298043426018363787L;
        
        {
            for (LogLevel ll : LogLevel.values()) {
                final JCheckBoxMenuItem cbmi = new JCheckBoxMenuItem(String.format(getLang("show_f", "Show %s"), ll.toLocalizedText()));
                put(cbmi, ll);
            }
        }
    };
    private final ArrayList<LogEntry> logInput = new ArrayList<>();
    private int depth = 0;
    private boolean visible = false;
    private boolean showed = false;
    private boolean exitWhenLastOne = true;
    private WizardSaveAs wizardSaveAs = null;
    private boolean blockAdding = false;
    
    private final SpinnerNumberModel spinnerNumberModel_fontSize = new SpinnerNumberModel(13, 0, 20, 1);
    private final JSpinner spinner = new JSpinner(spinnerNumberModel_fontSize);
    
    public Console() {
        initComponents();
        init();
        initListeners();
        addWindowListener(this);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        try {
            setIconImage(ImageIO.read(Console.class.getResourceAsStream(ICON)));
        } catch (Exception ex) {
            Logger.logErr("Error while loading console icon: " + ex, ex);
        }
        LanguageManager.addLanguageReloader(this);
        reloadLanguage();
    }
    
    private void initListeners() {
        textField_input.addKeyListener(this);
        button_input.addActionListener(this);
        M1I1.addActionListener(this);
        M1I2.addActionListener(this);
        M1I3.addActionListener(this);
        M1I4.addActionListener(this);
        M2C1.addActionListener(this);
        M2C2.addActionListener(this);
        M2C3.addActionListener(this);
        M2C4.addActionListener(this);
        M2C5.addActionListener(this);
        M2I1.addActionListener(this);
        spinner.addChangeListener((e) -> {
            setFont((int) spinner.getModel().getValue());
        });
    }
    
    private void setFont(int size) {
        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
    }
    
    private void init() {
        setFont(13);
        setSize(new Dimension(1200, 500));
        M2C1.setSelected(false);
        M2C2.setSelected(false);
        M2C3.setSelected(false);
        M2C4.setSelected(false);
        M2C5.setSelected(Logger.isDebugMode());
        for (LogLevel ll : LogLevel.values()) {
            logLevelVisibilities.put(ll, Logger.isMinimumLogLevel(ll));
            getCheckBoxMenuItem(ll).setSelected(false);
        }
    }
    
    private boolean processInput() {
        boolean done = processInput(textField_input.getText());
        if (done) {
            textField_input.setText("");
        }
        return done;
    }
    
    private boolean processInput(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        try {
            final LogEntry logEntry = Logger.NEWSYSOUT.getLogEntry(input, Instant.now());
            logEntry.setLogLevel(input.startsWith(Command.COMMANDSTART) ? LogLevel.COMMAND : LogLevel.INPUT);
            logInput.add(logEntry);
            depth = 0;
            Logger.log(logEntry);
            return true;
        } catch (Exception ex) {
            Logger.logErr("Error while processing input: " + ex, ex);
            return false;
        }
    }
    
    private final void showFontSizeChanger() {
        final int oldFontSize = (int) spinner.getModel().getValue();
        final int result = JOptionPane.showOptionDialog(this, spinner, getLang("console_change_font_size_q", "Enter new font size"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == JOptionPane.CANCEL_OPTION) {
            setFont(oldFontSize);
        } else if (result == JOptionPane.OK_OPTION) {
            setFont((int) spinner.getModel().getValue());
        }
    }
    
    private void goConsoleCommand(int go) {
        depth += go;
        if (depth < 0) {
            depth = 0;
        } else if (depth > logInput.size()) {
            depth = logInput.size();
        }
        if (depth == 0) {
            textField_input.setText("");
        } else {
            textField_input.setText(logInput.get(logInput.size() - depth).getLogEntry().toString());
        }
    }
    
    public Console reloadConsole(ArrayList<LogEntry> logEntries, boolean update) {
        blockAdding = true;
        StyledDocument doc = textPane.getStyledDocument();
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException ex) {
            Logger.logErr("Error while clearing the console: " + ex, ex);
        }
        final Style style = doc.addStyle("Style", null);
        for (LogEntry logEntry : logEntries) {
            if (update) {
                logEntry.update();
            }
            boolean doesLog = logLevelVisibilities.get(logEntry.getLogLevel());
            if (doesLog) {
                StyleConstants.setBackground(style, logEntry.getLogLevel().getBackgroundColor());
                StyleConstants.setForeground(style, logEntry.getLogLevel().getForegroundColor());
                try {
                    doc.insertString(doc.getLength(), logEntry.toString(), style);
                } catch (Exception ex) {
                    Logger.logErr("Error while adding to the console: " + ex, ex);
                }
            }
        }
        blockAdding = false;
        return this;
    }
    
    public Console addToConsole(LogEntry logEntry, boolean update) {
        while (blockAdding) {
            try {
                Thread.sleep(1);
            } catch (Exception ex) {
            }
        }
        StyledDocument doc = textPane.getStyledDocument();
        final Style style = doc.addStyle("Style", null);
        if (update) {
            logEntry.update();
        }
        boolean doesLog = logLevelVisibilities.get(logEntry.getLogLevel());
        if (doesLog) {
            StyleConstants.setBackground(style, logEntry.getLogLevel().getBackgroundColor());
            StyleConstants.setForeground(style, logEntry.getLogLevel().getForegroundColor());
            try {
                doc.insertString(doc.getLength(), logEntry.toString(), style);
            } catch (Exception ex) {
                Logger.logErr("Error while adding to the console: " + ex, ex);
            }
            textPane.setCaretPosition(doc.getLength());
        }
        return this;
    }
    
    public void reloadConsole() {
        reloadConsole(true);
    }
    
    public void closeConsole() {
        if (visible || showed) {
            dispose();
            visible = false;
            showed = false;
        }
        reloadConsole();
        checkForExit();
    }
    
    public void hideConsole() {
        if (visible && showed) {
            setVisible(false);
            visible = false;
            showed = true;
        }
        reloadConsole();
        checkForExit();
    }
    
    public void showConsole(Component c) {
        if (wizardSaveAs == null) {
            wizardSaveAs = new WizardSaveAs(this);
        }
        if (!visible && !showed) {
            reloadCheckBoxSelectionsFromSave();
            setLocationRelativeTo(c);
            setVisible(true);
            visible = true;
            showed = true;
        }
        reloadConsole();
    }
    
    public void reShowConsole(Component c) {
        if (!visible && showed) {
            reloadCheckBoxSelectionsFromSave();
            setVisible(true);
            visible = true;
            showed = true;
        } else if (!showed) {
            showConsole(c);
        }
        reloadConsole();
    }
    
    private void checkForExit() {
        if (exitWhenLastOne) {
            try {
                if (OmniKryptecEngine.instance() == null || OmniKryptecEngine.instance().getState() == GameState.STOPPED) {
                    Commands.COMMANDEXIT.run("-java");
                }
            } catch (Exception ex) {
                Commands.COMMANDEXIT.run("-java");
            }
        }
    }
    
    public boolean isExitWhenLastOne() {
        return exitWhenLastOne;
    }
    
    public Console setExitWhenLastOne(boolean exitWhenLastOne) {
        this.exitWhenLastOne = exitWhenLastOne;
        return this;
    }
    
    private void reloadCheckBoxSelectionsFromSave() {
        M2C1.setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.DATETIME)); // Timestamp
        M2C2.setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.CLASSLINE)); // Appearance
        M2C3.setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.THREAD)); // Thread
        M2C4.setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.LOGLEVEL)); // LogLevel
        M2C5.setSelected(Logger.isDebugMode());
        for (JCheckBoxMenuItem cbmi : getCheckBoxMenuItems()) {
            cbmi.setSelected(logLevelVisibilities.get(logLevelCheckBoxes.get(cbmi)));
        }
    }
    
    private void reloadCheckBoxSelectionsToSave() {
        Logger.setLogEntryFormat(LogEntryFormatter.toggleFormat(LogEntry.STANDARD_LOGENTRYFORMAT, M2C1.isSelected(), M2C2.isSelected(), M2C3.isSelected(), M2C4.isSelected(), LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.MESSAGE), LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.EXCEPTION)));
        Logger.setDebugMode(M2C5.isSelected());
        for (LogLevel ll : logLevelVisibilities.keySet()) {
            logLevelVisibilities.put(ll, getCheckBoxMenuItem(ll).isSelected());
        }
        reloadConsole(true);
    }
    
    public Console reloadConsole(boolean update) {
        final ArrayList<LogEntry> logEntries = getLogEntriesSorted(update, logLevelVisibilities);
        reloadConsole(logEntries, true);
        return this;
    }
    
    public ArrayList<LogEntry> getLogEntriesSorted(boolean update, HashMap<LogLevel, Boolean> logLevels) {
        final ArrayList<LogEntry> logEntries = new ArrayList<>();
        Logger.blockInput = true;
        for (LogEntry logEntry : Logger.LOG) {
            if (logLevels.get(logEntry.getLogLevel())) {
                logEntries.add(logEntry);
            }
        }
        Logger.blockInput = false;
        sortLogEntries(logEntries, true);
        if (update) {
            for (LogEntry logEntry : logEntries) {
                logEntry.update();
            }
        }
        return logEntries;
    }
    
    public boolean isShowed() {
        return showed;
    }
    
    @Override
    public boolean isVisible() {
        return visible;
    }
    
    private JCheckBoxMenuItem getCheckBoxMenuItem(LogLevel logLevel) {
        return de.omnikryptec.test.Utils.getKeysByValue(logLevelCheckBoxes, logLevel).toArray(new JCheckBoxMenuItem[1])[0];
    }
    
    protected ArrayList<JCheckBoxMenuItem> getCheckBoxMenuItems() {
        final ArrayList<JCheckBoxMenuItem> checkBoxMenuItems = new ArrayList<>();
        for (JCheckBoxMenuItem cbmi : logLevelCheckBoxes.keySet()) {
            checkBoxMenuItems.add(cbmi);
        }
        checkBoxMenuItems.sort(Comparator.comparingInt((JCheckBoxMenuItem o) -> logLevelCheckBoxes.get(o).getLevel()));
        return checkBoxMenuItems;
    }
    
    protected ArrayList<LogLevel> getLogLevels() {
        final ArrayList<LogLevel> logLevels = new ArrayList<>();
        for (LogLevel ll : logLevelVisibilities.keySet()) {
            logLevels.add(ll);
        }
        logLevels.sort(Comparator.comparingInt(LogLevel::getLevel));
        return logLevels;
    }
    
    public AdvancedFile saveAs() {
        AdvancedFile file = null;
        try {
            LogEntry logEntry_temp = wizardSaveAs.showSaveAsDialog((visible ? this : null));
            if (logEntry_temp == null || logEntry_temp.getLogEntry() == null || !(logEntry_temp.getLogEntry() instanceof AdvancedFile)) {
                return null;
            }
            file = (AdvancedFile) logEntry_temp.getLogEntry();
            if (file == null || (file.getParent() != null ? !file.getParent().exists() : true)) {
                return null;
            }
            if (!file.exists()) {
                file.createAdvancedFile();
            }
            final String logEntryFormat = wizardSaveAs.getLogEntryFormat();
            final HashMap<LogLevel, Boolean> logLevels = wizardSaveAs.getLogLevels();
            final ArrayList<LogEntry> logEntries = getLogEntriesSorted(false, logLevels);
            BufferedWriter bw = file.getWriter(false);
            for (LogEntry logEntry : logEntries) {
                try {
                    logEntry.setLogEntryFormat(logEntryFormat);
                    bw.write(logEntry.toString());
                    if (logEntry.isNewLine()) {
                        bw.newLine();
                    }
                } catch (Exception ex) {
                }
            }
            bw.close();
        } catch (Exception ex) {
            Logger.logErr("Error while exporting console to file: " + ex, ex);
        }
        return file;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        
        panel_input = new javax.swing.JPanel();
        textField_input = new javax.swing.JTextField();
        button_input = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();
        menubar = new javax.swing.JMenuBar();
        M1 = new javax.swing.JMenu();
        M1I3 = new javax.swing.JMenuItem();
        M1S2 = new javax.swing.JPopupMenu.Separator();
        M1I4 = new javax.swing.JMenuItem();
        M1S1 = new javax.swing.JPopupMenu.Separator();
        M1I2 = new javax.swing.JMenuItem();
        M1I1 = new javax.swing.JMenuItem();
        M2 = new javax.swing.JMenu();
        M2S1 = new javax.swing.JPopupMenu.Separator();
        M2C1 = new javax.swing.JCheckBoxMenuItem();
        M2C2 = new javax.swing.JCheckBoxMenuItem();
        M2C3 = new javax.swing.JCheckBoxMenuItem();
        M2C4 = new javax.swing.JCheckBoxMenuItem();
        M2S2 = new javax.swing.JPopupMenu.Separator();
        M2C5 = new javax.swing.JCheckBoxMenuItem();
        M2S3 = new javax.swing.JPopupMenu.Separator();
        M2I1 = new javax.swing.JMenuItem();
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        
        button_input.setText("Enter");
        
        javax.swing.GroupLayout panel_inputLayout = new javax.swing.GroupLayout(panel_input);
        panel_input.setLayout(panel_inputLayout);
        panel_inputLayout.setHorizontalGroup(panel_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(panel_inputLayout.createSequentialGroup().addComponent(textField_input, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(button_input, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)));
        panel_inputLayout.setVerticalGroup(panel_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_inputLayout.createSequentialGroup().addGap(0, 11, Short.MAX_VALUE).addGroup(panel_inputLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(textField_input, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(button_input))));
        
        getContentPane().add(panel_input, java.awt.BorderLayout.PAGE_END);
        
        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);
        
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);
        
        M1.setText("File");
        
        M1I3.setText("Reload");
        M1.add(M1I3);
        M1.add(M1S2);
        
        M1I4.setText("Save As");
        M1.add(M1I4);
        M1.add(M1S1);
        
        M1I2.setText("Restart");
        M1.add(M1I2);
        
        M1I1.setText("Exit");
        M1.add(M1I1);
        
        menubar.add(M1);
        
        M2.setText("Options");
        for (JCheckBoxMenuItem cbmi : getCheckBoxMenuItems()) {
            cbmi.addActionListener(this);
            M2.add(cbmi);
        }
        M2.add(M2S1);
        
        M2C1.setSelected(true);
        M2C1.setText("Show Timestamp");
        M2.add(M2C1);
        
        M2C2.setSelected(true);
        M2C2.setText("Show Appearance");
        M2.add(M2C2);
        
        M2C3.setSelected(true);
        M2C3.setText("Show Thread");
        M2.add(M2C3);
        
        M2C4.setSelected(true);
        M2C4.setText("Show Level");
        M2.add(M2C4);
        M2.add(M2S2);
        
        M2C5.setSelected(true);
        M2C5.setText("Enable Debug Mode");
        M2.add(M2C5);
        M2.add(M2S3);
        
        M2I1.setText("Change Font Size");
        M2.add(M2I1);
        
        menubar.add(M2);
        
        setJMenuBar(menubar);
        
        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        // <editor-fold defaultstate="collapsed" desc=" Look and feel setting
        // code (optional) ">
        /*
         * If Nimbus (introduced in Java SE 6) is not available, stay with the
         * default look and feel. For details see
         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.
         * html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Console.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        // </editor-fold>
        // </editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Console().setVisible(true));
    }
    
    public static ArrayList<LogEntry> sortLogEntries(ArrayList<LogEntry> array, boolean ascending) {
        array.sort((Object o1, Object o2) -> {
            LogEntry le1 = (LogEntry) o1;
            LogEntry le2 = (LogEntry) o2;
            long le1epochmilli = le1.getTimeStamp().toEpochMilli();
            long le2epochmilli = le2.getTimeStamp().toEpochMilli();
            if (le1epochmilli == le2epochmilli) {
                return 0;
            } else if (le1epochmilli > le2epochmilli) {
                return 1 * ((ascending) ? 1 : -1);
            } else if (le1epochmilli < le2epochmilli) {
                return -1 * ((ascending) ? 1 : -1);
            }
            return 0;
        });
        return array;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu M1;
    private javax.swing.JMenuItem M1I1;
    private javax.swing.JMenuItem M1I2;
    private javax.swing.JMenuItem M1I3;
    private javax.swing.JMenuItem M1I4;
    private javax.swing.JPopupMenu.Separator M1S1;
    private javax.swing.JPopupMenu.Separator M1S2;
    private javax.swing.JMenu M2;
    private javax.swing.JCheckBoxMenuItem M2C1;
    private javax.swing.JCheckBoxMenuItem M2C2;
    private javax.swing.JCheckBoxMenuItem M2C3;
    private javax.swing.JCheckBoxMenuItem M2C4;
    private javax.swing.JCheckBoxMenuItem M2C5;
    private javax.swing.JMenuItem M2I1;
    private javax.swing.JPopupMenu.Separator M2S1;
    private javax.swing.JPopupMenu.Separator M2S2;
    private javax.swing.JSeparator M2S3;
    private javax.swing.JButton button_input;
    private javax.swing.JMenuBar menubar;
    private javax.swing.JPanel panel_input;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JTextField textField_input;
    private javax.swing.JTextPane textPane;
    // End of variables declaration//GEN-END:variables
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button_input) {
            processInput();
        } else if (e.getSource() == M1I1) {
            closeConsole();
        } else if (e.getSource() == M1I2) {
            closeConsole();
            showConsole(null);
        } else if (e.getSource() == M1I3) {
            reloadConsole(true);
        } else if (e.getSource() == M1I4) {
            saveAs();
        } else if (e.getSource() == M2I1) {
            showFontSizeChanger();
        } else if (e.getSource() instanceof JCheckBoxMenuItem) {
            reloadCheckBoxSelectionsToSave();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getSource() == textField_input) {
            if (e.getKeyChar() == SystemInputStream.NEWLINECHAR) {
                processInput();
            }
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == textField_input) {
            if (e.getKeyCode() == KeyEvent.VK_UP) {
                goConsoleCommand(1);
            } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                goConsoleCommand(-1);
            }
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
    }
    
    @Override
    public void windowOpened(WindowEvent e) {
    }
    
    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getSource() == this) {
            hideConsole();
        }
    }
    
    @Override
    public void windowClosed(WindowEvent e) {
    }
    
    @Override
    public void windowIconified(WindowEvent e) {
    }
    
    @Override
    public void windowDeiconified(WindowEvent e) {
    }
    
    @Override
    public void windowActivated(WindowEvent e) {
    }
    
    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    @Override
    public void reloadLanguage() {
        setTitle(getLang("console", "Console"));
        M1.setText(getLang("file", "File"));
        M2.setText(getLang("options", "Options"));
        M1I1.setText(getLang("exit", "Exit"));
        M1I2.setText(getLang("restart", "Restart"));
        M1I3.setText(getLang("reload", "Reload"));
        M1I4.setText(getLang("save_as", "Save as"));
        M2C1.setText(String.format(getLang("show_f", "Show %s"), getLang("timestamp", "Timestamp")));
        M2C2.setText(String.format(getLang("show_f", "Show %s"), getLang("appearance", "Appearance")));
        M2C3.setText(String.format(getLang("show_f", "Show %s"), getLang("thread", "Thread")));
        M2C4.setText(String.format(getLang("show_f", "Show %s"), getLang("level", "Level")));
        M2C5.setText(getLang("enable_debug_mode", "Enable Debug Mode"));
        M2I1.setText(getLang("console_change_font_size", "Change Font Size"));
        button_input.setText(getLang("enter", "Enter"));
        for (JCheckBoxMenuItem cbmi : logLevelCheckBoxes.keySet()) {
            cbmi.setText(String.format(getLang("show_f", "Show %s"), logLevelCheckBoxes.get(cbmi).toLocalizedText()));
        }
    }
    
}
