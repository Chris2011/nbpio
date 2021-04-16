package nbpio.serialmonitor.ui.configuration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import nbpio.serialmonitor.SerialMonitorConfigModel;

/**
 *
 * @author Chrl
 */
public class SerialMonitorConfigDialog extends JDialog {
    private final ActionListener connectActionHandler;
    private final SerialMonitorConfigModel model;
    private final List<JComboBox<String>> parameterCombos;

    /**
     * Creates new form SerialMonitorConfigDialog
     */
    public SerialMonitorConfigDialog(SerialMonitorConfigModel model, ActionListener connectActionHandler) {
        if (model == null) {
            throw new NullPointerException("The model cannot be null!");
        }

        this.model = model;
        this.connectActionHandler = connectActionHandler;

        initComponents();

        parameterCombos = new ArrayList<>();

        preparePortNameSelectorModel(model);
        prepareParameterComboBoxModel(baudRateSelector, model.getAvailableBaudRates(), model.getCurrentBaudRate(), model::setCurrentBaudRate);
        prepareParameterComboBoxModel(flowControlSelector, model.getAvailableFlowControl(), model.getCurrentFlowControl(), model::setCurrentFlowControl);
        prepareParameterComboBoxModel(dataBitsSelector, model.getAvailableDataBits(), model.getCurrentDataBits(), model::setCurrentDataBits);
        prepareParameterComboBoxModel(paritySelector, model.getAvailableStopBits(), model.getCurrentStopBits(), model::setCurrentStopBits);
        prepareParameterComboBoxModel(stopBitsSelector, model.getAvailableParities(), model.getCurrentParity(), model::setCurrentParity);

        // Disable all combos below the port name combo if there are no valid serial ports available:
        if (portNameSelector.getItemCount() == 0) {
            disableComponents();
        }

        // Add a selection listener to the port name combo to enable all combos below port name combo when a serial port is selected
        portNameSelector.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableComponents();
            } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                disableComponents();
            }
        });
    }

    private void enableComponents() {
        parameterCombos.forEach((c) -> c.setEnabled(true));
        connectBtn.setEnabled(true);
    }

    private void disableComponents() {
        parameterCombos.forEach((c) -> c.setEnabled(false));
        connectBtn.setEnabled(false);
    }

    private void prepareParameterComboBoxModel(JComboBox<String> selector, String[] values, String selectedValue, Consumer<String> modelSetter) {
        selector.setModel(new DefaultComboBoxModel<>(values));

        selector.setSelectedItem(selectedValue);
        selector.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                modelSetter.accept(e.getItem().toString());
            }
        });

        parameterCombos.add(selector);
    }

    private void preparePortNameSelectorModel(SerialMonitorConfigModel configProvider) {
        portNameSelector.setModel(new DefaultComboBoxModel<>(configProvider.getAvailablePortNames()));

        portNameSelector.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                String[] portNames = configProvider.getAvailablePortNames();
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) portNameSelector.getModel();
                List<String> currentItems = new ArrayList<>();

                for (int i = 0; i < model.getSize(); i++) {
                    currentItems.add(model.getElementAt(i));
                }

                List<String> newItems = Arrays.asList(portNames);

                List<String> toBeRemovedItems = new ArrayList<>(currentItems);
                toBeRemovedItems.removeAll(newItems);

                List<String> toBeAddedItems = new ArrayList<>(newItems);
                toBeAddedItems.removeAll(currentItems);

                toBeRemovedItems.forEach((portName) -> {
                    model.removeElement(portName);
                });

                toBeAddedItems.forEach((portName) -> {
                    model.addElement(portName);
                });

                if (model.getSize() > 0) {
                    parameterCombos.forEach((c) -> c.setEnabled(true));
                } else {
                    parameterCombos.forEach((c) -> c.setEnabled(false));
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        portNameSelector.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                model.setCurrentPortName(e.getItem().toString());
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        portNameLabel = new javax.swing.JLabel();
        portNameSelector = new javax.swing.JComboBox<>();
        baudRateLabel = new javax.swing.JLabel();
        baudRateSelector = new javax.swing.JComboBox<>();
        connectBtn = new javax.swing.JButton();
        cancelConfigBtn = new javax.swing.JButton();
        advancedSettingsPanel = new javax.swing.JPanel();
        flowControlLabel = new javax.swing.JLabel();
        flowControlSelector = new javax.swing.JComboBox<>();
        editBitsLabel = new javax.swing.JLabel();
        dataBitsSelector = new javax.swing.JComboBox<>();
        parityLabel = new javax.swing.JLabel();
        paritySelector = new javax.swing.JComboBox<>();
        stopBitsLabel = new javax.swing.JLabel();
        stopBitsSelector = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(portNameLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.portNameLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(baudRateLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.baudRateLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(connectBtn, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.connectBtn.text")); // NOI18N
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(cancelConfigBtn, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.cancelConfigBtn.text")); // NOI18N
        cancelConfigBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelConfigBtnActionPerformed(evt);
            }
        });

        advancedSettingsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.advancedSettingsPanel.border.title"))); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(flowControlLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.flowControlLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(editBitsLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.editBitsLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(parityLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.parityLabel.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(stopBitsLabel, org.openide.util.NbBundle.getMessage(SerialMonitorConfigDialog.class, "SerialMonitorConfigDialog.stopBitsLabel.text")); // NOI18N

        javax.swing.GroupLayout advancedSettingsPanelLayout = new javax.swing.GroupLayout(advancedSettingsPanel);
        advancedSettingsPanel.setLayout(advancedSettingsPanelLayout);
        advancedSettingsPanelLayout.setHorizontalGroup(
            advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedSettingsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(flowControlLabel)
                    .addComponent(editBitsLabel)
                    .addComponent(parityLabel)
                    .addComponent(stopBitsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(flowControlSelector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dataBitsSelector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(paritySelector, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(stopBitsSelector, 0, 115, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        advancedSettingsPanelLayout.setVerticalGroup(
            advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(advancedSettingsPanelLayout.createSequentialGroup()
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(flowControlLabel)
                    .addComponent(flowControlSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dataBitsSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editBitsLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paritySelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parityLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(advancedSettingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stopBitsSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stopBitsLabel))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(connectBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelConfigBtn)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(advancedSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(baudRateLabel)
                                    .addComponent(portNameLabel))
                                .addGap(24, 24, 24)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(baudRateSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(portNameSelector, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portNameLabel)
                    .addComponent(portNameSelector))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baudRateSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baudRateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(advancedSettingsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelConfigBtn)
                    .addComponent(connectBtn))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
        if (connectActionHandler != null) {
            connectActionHandler.actionPerformed(new ActionEvent(SerialMonitorConfigDialog.this, 0, "connectToPort"));

            this.setVisible(false);
        }
    }//GEN-LAST:event_connectBtnActionPerformed

    private void cancelConfigBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelConfigBtnActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelConfigBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel advancedSettingsPanel;
    private javax.swing.JLabel baudRateLabel;
    private javax.swing.JComboBox<String> baudRateSelector;
    private javax.swing.JButton cancelConfigBtn;
    private javax.swing.JButton connectBtn;
    private javax.swing.JComboBox<String> dataBitsSelector;
    private javax.swing.JLabel editBitsLabel;
    private javax.swing.JLabel flowControlLabel;
    private javax.swing.JComboBox<String> flowControlSelector;
    private javax.swing.JLabel parityLabel;
    private javax.swing.JComboBox<String> paritySelector;
    private javax.swing.JLabel portNameLabel;
    private javax.swing.JComboBox<String> portNameSelector;
    private javax.swing.JLabel stopBitsLabel;
    private javax.swing.JComboBox<String> stopBitsSelector;
    // End of variables declaration//GEN-END:variables
}
