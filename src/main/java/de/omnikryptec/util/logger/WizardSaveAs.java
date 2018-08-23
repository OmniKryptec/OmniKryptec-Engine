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

import de.codemakers.io.file.AdvancedFile;
import de.codemakers.lang.LanguageManager;
import de.codemakers.lang.LanguageReloader;
import de.omnikryptec.swing.JCheckBoxList;
import de.omnikryptec.util.logger.LogEntryFormatter.LogEntryFormatTile;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.Instant;
import java.util.HashMap;

/**
 *
 * @author Panzer1119
 */
public class WizardSaveAs extends javax.swing.JDialog implements ActionListener, LanguageReloader, WindowListener {

    /**
     *
     */
    private static final long serialVersionUID = 2124926401011599655L;
    private final Console console;
    private LogEntry logEntry = null;
    private AdvancedFile file = null;
    private AdvancedFile folder = null;
    private final JFileChooser fileChooser = new JFileChooser();
    private final JCheckBoxList checkBoxList = new JCheckBoxList();

    /**
     * Creates new form WizardSaveAs
     */
    public WizardSaveAs(Console console) {
        this.console = console;
        initComponents();
        init();
    }

    private void init() {
        addWindowListener(this);
        setModal(true);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        folder = AdvancedFile.folderOfPath(System.getProperty("user.dir"));
        reloadInfo();
        LanguageManager.addLanguageReloader(this);
        reloadLanguage();
    }

    private void reloadInfo() {
        checkBoxList.setModel(getCheckBoxListModel());
        checkBox_save_infos_appearance
                .setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.CLASSLINE));
        checkBox_save_infos_timestamp
                .setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.DATETIME));
        checkBox_save_infos_logLevel
                .setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.LOGLEVEL));
        checkBox_save_infos_thread
                .setSelected(LogEntryFormatter.isPrinting(Logger.LOGENTRYFORMAT, LogEntryFormatTile.THREAD));
    }

    public LogEntry showSaveAsDialog(Component c) {
        reloadInfo();
        try {
            textField_center_path.setText("");
            if (folder != null && folder.exists() && folder.isDirectory()) {
                fileChooser.setCurrentDirectory(folder.toFile());
            } else {
                folder = AdvancedFile.folderOfPath(System.getProperty("user.dir"));
                fileChooser.setCurrentDirectory(folder.toFile());
            }
            fileChooser.setSelectedFile(new AdvancedFile(false, "").toFile());
        } catch (Exception ex) {
            Logger.logErr("Error while setting showSaveAsDialog: " + ex, ex);
        }
        setLocationRelativeTo(c);
        setVisible(true);
        return finish();
    }

    private LogEntry finish() {
        try {
            file = AdvancedFile.fileOfPath(textField_center_path.getText());
            logEntry = new LogEntry(file, Instant.now(), LogLevel.INPUT);
            logEntry.setLogEntryFormat(getLogEntryFormat());
            folder = file.getParent();
        } catch (Exception ex) {
            logEntry = null;
            Logger.logErr("Error while finishing save as: " + ex, ex);
        }
        dispose();
        return logEntry;
    }

    private void close() {
        logEntry = null;
        file = null;
        textField_center_path.setText("");
        dispose();
    }

    public void searchComputer(Component c) {
        int result = fileChooser.showSaveDialog(c);
        AdvancedFile file_temp = new AdvancedFile(false, fileChooser.getSelectedFile());
        if (result == JFileChooser.APPROVE_OPTION && !file_temp.isIntern()) {
            file = file_temp;
            textField_center_path.setText(file.toFile().getAbsolutePath());
        }
    }

    public AdvancedFile getFile() {
        return file;
    }

    public WizardSaveAs setFile(AdvancedFile file) {
        this.file = file;
        return this;
    }

    private DefaultListModel<JCheckBox> getCheckBoxListModel() {
        final DefaultListModel<JCheckBox> model = new DefaultListModel<>();
        for (LogLevel ll : console.getLogLevels()) {
            JCheckBox checkBox = new JCheckBox(ll.toLocalizedText());
            checkBox.setSelected(console.logLevelVisibilities.get(ll));
            model.addElement(checkBox);
        }
        return model;
    }

    public String getLogEntryFormat() {
        return LogEntryFormatter.toggleFormat(LogEntry.STANDARD_LOGENTRYFORMAT,
                checkBox_save_infos_timestamp.isSelected(), checkBox_save_infos_appearance.isSelected(),
                checkBox_save_infos_thread.isSelected(), checkBox_save_infos_logLevel.isSelected(), true, true);
    }

    public HashMap<LogLevel, Boolean> getLogLevels() {
        final HashMap<LogLevel, Boolean> logLevels = new HashMap<>();
        int i = 0;
        for (LogLevel ll : console.getLogLevels()) {
            logLevels.put(ll, checkBoxList.getModel().getElementAt(i).isSelected());
            i++;
        }
        return logLevels;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated
	// Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		panel_buttons = new javax.swing.JPanel();
		jseparator_buttons = new javax.swing.JSeparator();
		button_bottom_cancel = new javax.swing.JButton();
		button_bottom_finish = new javax.swing.JButton();
		button_bottom_next = new javax.swing.JButton();
		button_bottom_back = new javax.swing.JButton();
		panel_settings = new javax.swing.JPanel();
		panel_settings_2 = new javax.swing.JPanel();
		textField_center_path = new javax.swing.JTextField();
		button_center_path = new javax.swing.JButton();
		label_center_path = new javax.swing.JLabel();
		panel_settings_settings = new javax.swing.JPanel();
		jseparator_settings = new javax.swing.JSeparator();
		label_save_level = new javax.swing.JLabel();
		label_save_infos = new javax.swing.JLabel();
		checkBox_save_infos_logLevel = new javax.swing.JCheckBox();
		checkBox_save_infos_appearance = new javax.swing.JCheckBox();
		checkBox_save_infos_timestamp = new javax.swing.JCheckBox();
		checkBox_save_infos_thread = new javax.swing.JCheckBox();
		scrollPane_save_level = new javax.swing.JScrollPane(checkBoxList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

		button_bottom_cancel.setText("Cancel");
		button_bottom_cancel.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_bottom_cancelActionPerformed(evt);
			}
		});

		button_bottom_finish.setText("Finish");
		button_bottom_finish.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_bottom_finishActionPerformed(evt);
			}
		});

		button_bottom_next.setText("Next >");
		button_bottom_next.setEnabled(false);

		button_bottom_back.setText("< Back");
		button_bottom_back.setEnabled(false);

		javax.swing.GroupLayout panel_buttonsLayout = new javax.swing.GroupLayout(panel_buttons);
		panel_buttons.setLayout(panel_buttonsLayout);
		panel_buttonsLayout.setHorizontalGroup(panel_buttonsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jseparator_buttons)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_buttonsLayout.createSequentialGroup()
						.addGap(227, 227, 227)
						.addComponent(button_bottom_back, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button_bottom_next, javax.swing.GroupLayout.DEFAULT_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button_bottom_finish, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(button_bottom_cancel, javax.swing.GroupLayout.DEFAULT_SIZE, 73, Short.MAX_VALUE)
						.addContainerGap()));
		panel_buttonsLayout.setVerticalGroup(panel_buttonsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_buttonsLayout.createSequentialGroup()
						.addGap(39, 39, 39)
						.addComponent(jseparator_buttons, javax.swing.GroupLayout.DEFAULT_SIZE, 21, Short.MAX_VALUE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(panel_buttonsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(button_bottom_cancel).addComponent(button_bottom_finish)
								.addComponent(button_bottom_next).addComponent(button_bottom_back))
						.addGap(23, 23, 23)));

		getContentPane().add(panel_buttons, java.awt.BorderLayout.PAGE_END);

		button_center_path.setText("Search Computer");
		button_center_path.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				button_center_pathActionPerformed(evt);
			}
		});

		label_center_path.setText("File");

		panel_settings_settings.setBorder(javax.swing.BorderFactory
				.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Settings"));

		label_save_level.setText("Save Level");

		label_save_infos.setText("Save Infos");

		checkBox_save_infos_logLevel.setText("Level");

		checkBox_save_infos_appearance.setText("Appearance");

		checkBox_save_infos_timestamp.setText("Timestamp");

		checkBox_save_infos_thread.setText("Thread");

		javax.swing.GroupLayout panel_settings_settingsLayout = new javax.swing.GroupLayout(panel_settings_settings);
		panel_settings_settings.setLayout(panel_settings_settingsLayout);
		panel_settings_settingsLayout.setHorizontalGroup(panel_settings_settingsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel_settings_settingsLayout.createSequentialGroup().addGroup(panel_settings_settingsLayout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
								jseparator_settings)
						.addGroup(panel_settings_settingsLayout.createSequentialGroup().addContainerGap()
								.addGroup(panel_settings_settingsLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(panel_settings_settingsLayout.createSequentialGroup()
												.addComponent(label_save_infos)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														161, Short.MAX_VALUE)
												.addComponent(checkBox_save_infos_timestamp)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(checkBox_save_infos_appearance)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(checkBox_save_infos_thread)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
												.addComponent(checkBox_save_infos_logLevel))
										.addGroup(panel_settings_settingsLayout.createSequentialGroup()
												.addComponent(label_save_level).addGap(18, 18, 18).addComponent(
														scrollPane_save_level)))))
						.addContainerGap()));
		panel_settings_settingsLayout.setVerticalGroup(
				panel_settings_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(panel_settings_settingsLayout.createSequentialGroup().addContainerGap()
								.addGroup(panel_settings_settingsLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(label_save_level).addComponent(scrollPane_save_level,
												javax.swing.GroupLayout.PREFERRED_SIZE, 101,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(7, 7, 7)
								.addComponent(jseparator_settings, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(panel_settings_settingsLayout
										.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(label_save_infos).addComponent(checkBox_save_infos_logLevel)
										.addComponent(checkBox_save_infos_appearance)
										.addComponent(checkBox_save_infos_timestamp)
										.addComponent(checkBox_save_infos_thread))
								.addGap(0, 22, Short.MAX_VALUE)));

		javax.swing.GroupLayout panel_settings_2Layout = new javax.swing.GroupLayout(panel_settings_2);
		panel_settings_2.setLayout(panel_settings_2Layout);
		panel_settings_2Layout.setHorizontalGroup(panel_settings_2Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel_settings_2Layout.createSequentialGroup().addContainerGap()
						.addGroup(panel_settings_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
								.addComponent(panel_settings_settings, javax.swing.GroupLayout.DEFAULT_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(panel_settings_2Layout.createSequentialGroup()
										.addComponent(label_center_path, javax.swing.GroupLayout.DEFAULT_SIZE,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(textField_center_path, javax.swing.GroupLayout.DEFAULT_SIZE, 350,
												Short.MAX_VALUE)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(button_center_path, javax.swing.GroupLayout.DEFAULT_SIZE, 145,
												Short.MAX_VALUE)))
						.addContainerGap()));
		panel_settings_2Layout.setVerticalGroup(panel_settings_2Layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(panel_settings_2Layout.createSequentialGroup().addContainerGap(28, Short.MAX_VALUE)
						.addGroup(panel_settings_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(textField_center_path, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addComponent(button_center_path).addComponent(label_center_path))
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(panel_settings_settings, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addGap(56, 56, 56)));

		javax.swing.GroupLayout panel_settingsLayout = new javax.swing.GroupLayout(panel_settings);
		panel_settings.setLayout(panel_settingsLayout);
		panel_settingsLayout.setHorizontalGroup(panel_settingsLayout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(panel_settings_2,
						javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		panel_settingsLayout
				.setVerticalGroup(panel_settingsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(panel_settingsLayout.createSequentialGroup()
								.addComponent(panel_settings_2, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(0, 0, Short.MAX_VALUE)));

		getContentPane().add(panel_settings, java.awt.BorderLayout.CENTER);

		pack();
	}// </editor-fold>//GEN-END:initComponents

    private void button_bottom_cancelActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_bottom_cancelActionPerformed
        close();
    }// GEN-LAST:event_button_bottom_cancelActionPerformed

    private void button_bottom_finishActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_bottom_finishActionPerformed
        if (textField_center_path.getText().isEmpty()) {
            return;
        }
        finish();
    }// GEN-LAST:event_button_bottom_finishActionPerformed

    private void button_center_pathActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_button_center_pathActionPerformed
        searchComputer(this);
    }// GEN-LAST:event_button_center_pathActionPerformed

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
            java.util.logging.Logger.getLogger(WizardSaveAs.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WizardSaveAs.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WizardSaveAs.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WizardSaveAs.class.getName()).log(java.util.logging.Level.SEVERE, null,
                    ex);
        }
        // </editor-fold>
        // </editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WizardSaveAs(Logger.CONSOLE).setVisible(true);
            }
        });
    }

	// Variables declaration - do not modify//GEN-BEGIN:variables
	public javax.swing.JButton button_bottom_back;
	public javax.swing.JButton button_bottom_cancel;
	public javax.swing.JButton button_bottom_finish;
	public javax.swing.JButton button_bottom_next;
	public javax.swing.JButton button_center_path;
	public javax.swing.JCheckBox checkBox_save_infos_appearance;
	public javax.swing.JCheckBox checkBox_save_infos_logLevel;
	public javax.swing.JCheckBox checkBox_save_infos_thread;
	public javax.swing.JCheckBox checkBox_save_infos_timestamp;
	private javax.swing.JSeparator jseparator_buttons;
	private javax.swing.JSeparator jseparator_settings;
	public javax.swing.JLabel label_center_path;
	public javax.swing.JLabel label_save_infos;
	public javax.swing.JLabel label_save_level;
	private javax.swing.JPanel panel_buttons;
	private javax.swing.JPanel panel_settings;
	private javax.swing.JPanel panel_settings_2;
	public javax.swing.JPanel panel_settings_settings;
	private javax.swing.JScrollPane scrollPane_save_level;
	public javax.swing.JTextField textField_center_path;
	// End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (e.getSource() == this) {
            close();
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
        setTitle(getLang("save_as", "Save as"));
        button_center_path.setText(getLang("search_computer", "Search Computer"));
        button_bottom_back.setText(getLang("wizard_back", "< Back"));
        button_bottom_next.setText(getLang("wizard_next", "Next >"));
        button_bottom_finish.setText(getLang("finish", "Finish"));
        button_bottom_cancel.setText(getLang("cancel", "Cancel"));
        label_center_path.setText(getLang("file", "File"));
        label_save_infos.setText(getLang("save_infos", "Save Infos"));
        label_save_level.setText(getLang("save_level", "Save Level"));
        checkBox_save_infos_appearance.setText(getLang("appearance", "Appearance"));
        checkBox_save_infos_logLevel.setText(getLang("level", "Level"));
        checkBox_save_infos_thread.setText(getLang("thread", "Thread"));
        checkBox_save_infos_timestamp.setText(getLang("timestamp", "Timestamp"));
        panel_settings_settings
                .setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), getLang("settings", "Settings")));
        int i = 0;
        for (LogLevel ll : console.getLogLevels()) {
            checkBoxList.getModel().getElementAt(i).setText(ll.toLocalizedText());
            i++;
        }
    }

}