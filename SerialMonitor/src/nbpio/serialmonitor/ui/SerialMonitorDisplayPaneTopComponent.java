package nbpio.serialmonitor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.TooManyListenersException;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import nbpio.serialmonitor.DefaultSerialMonitorConfigModel;
import nbpio.serialmonitor.SerialMonitorConfigModel;
import nbpio.serialmonitor.SerialPortCommunicator;
import nbpio.serialmonitor.ui.configuration.SerialMonitorConfigDialog;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import org.openide.util.Exceptions;
import purejavacomm.PureJavaIllegalStateException;
import purejavacomm.UnsupportedCommOperationException;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//nbpio.serialmonitor.ui//SerialMonitorDisplayPane//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "SerialMonitorDisplayPaneTopComponent",
        iconBase = "nbpio/serialmonitor/serialPort.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Window", id = "nbpio.serialmonitor.ui.SerialMonitorDisplayPaneTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SerialMonitorDisplayPaneAction",
        preferredID = "SerialMonitorDisplayPaneTopComponent"
)
@Messages({
    "CTL_SerialMonitorDisplayPaneAction=SerialMonitorDisplayPane",
    "CTL_SerialMonitorDisplayPaneTopComponent=SerialMonitorDisplayPane Window",
    "HINT_SerialMonitorDisplayPaneTopComponent=This is a SerialMonitorDisplayPane window"
})
public final class SerialMonitorDisplayPaneTopComponent extends TopComponent {
    private static final Charset MESSAGE_CHARSET = Charset.forName("US-ASCII");

    private StyledDocument document;
    private Style inputStyle;
    private Style outputStyle;
    private Style notificationStyle;
    private JToggleButton crSwitch;
    private JToggleButton lfSwitch;
    
    private String portName;

    private AdjustmentListener scrollBarAdjustmentListener;
    private boolean adjustScrollBar = true;
    private int previousScrollBarValue = -1;
    private int previousScrollBarMaximum = -1;

    private SerialPortCommunicator communicator;
    private final SerialMonitorConfigModel configModel;

    public SerialMonitorDisplayPaneTopComponent() {
        initComponents();

        setName(Bundle.CTL_SerialMonitorDisplayPaneTopComponent());
        setToolTipText(Bundle.HINT_SerialMonitorDisplayPaneTopComponent());

        configModel = new DefaultSerialMonitorConfigModel();
        communicator = new SerialPortCommunicator(configModel.getCurrentConfig());

        createTopPane();
        createCenterPane();
        createSidePane();

        connectToPort();
    }

    public void connectToPort() {
        try {
            communicator.connect((reconnected) -> {
                String formattedMessage = null;
                portName = communicator.getConfig().getPortName();

                if (reconnected) {
                    String rawMessage = getLocalizedText("reconnectedNotification");
                    formattedMessage = MessageFormat.format(rawMessage, portName);
                } else {
                    String rawMessage = getLocalizedText("connectedNotification");
                    formattedMessage = MessageFormat.format(rawMessage, portName);
                }

                toggleComponents(true);
                setName(nbpio.serialmonitor.ui.Bundle.CTL_SerialMonitorDisplayPaneTopComponent() + " - " + (portName != null ? String.format("Connected to port: %s", portName) : "Not connected"));

                printNotificationLine(formattedMessage);
            }, (is) -> {
                try {
                    byte[] buffer = new byte[is.available()];
                    int n = is.read(buffer);
                    printInput(new String(buffer, 0, n));
                } catch (IOException ex) {
                    printNotificationLine(getLocalizedText("disconnectedNotification"));
                    toggleComponents(false);

                    communicator.disconnect();
                    communicator.startScanningForPort();
                }
            });
        } catch (TooManyListenersException | UnsupportedCommOperationException | PortInUseException | NoSuchPortException | IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void toggleComponents(boolean enableComponents) {
        disconnectButton.setEnabled(enableComponents);
        reconnectButton.setEnabled(enableComponents);
        inputField.setEnabled(enableComponents);
        sendButton.setEnabled(enableComponents);
    }

    private void printOutputLine(String message) {
        print(message + "\n", outputStyle);
    }

    private void printInput(String message) {
        print(message, inputStyle);
    }

    private void printNotificationLine(String message) {
        print(message + "\n", notificationStyle);
    }

    private void print(final String message, final Style style) {
        SwingUtilities.invokeLater(() -> {
            try {
                document.insertString(document.getLength(), message, style);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        });
    }

    private JComponent createCenterPane() {
        textPane.setAutoscrolls(false);
        textPane.getCaret().setVisible(false);
        textPane.setBackground(Color.BLACK);
        textPane.setForeground(Color.LIGHT_GRAY);
        textPane.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char character = e.getKeyChar();
                inputField.setText("" + character);
                inputField.requestFocusInWindow();
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
        textPane.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        DefaultCaret caret = (DefaultCaret) textPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        inputStyle = textPane.addStyle("input", null);
        outputStyle = textPane.addStyle("output", null);
        notificationStyle = textPane.addStyle("notification", null);
        StyleConstants.setForeground(inputStyle, Color.LIGHT_GRAY);
        StyleConstants.setForeground(outputStyle, Color.GREEN);
        StyleConstants.setForeground(notificationStyle, Color.GRAY);
        StyleConstants.setBold(notificationStyle, true);

        document = textPane.getStyledDocument();

        scrollBarAdjustmentListener = (e) -> SwingUtilities.invokeLater(() -> checkScrollBar(e));

        scrollPane.setWheelScrollingEnabled(true);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(scrollBarAdjustmentListener);

        return scrollPane;
    }

    private JComponent createTopPane() {
        inputField.addActionListener((e) -> sendMessage());
        inputField.setMaximumSize(inputField.getPreferredSize());

        sendButton.addActionListener((e) -> sendMessage());

        topPane.setLayout(new BoxLayout(topPane, BoxLayout.LINE_AXIS));

        crSwitch = new JToggleButton("CR", true);
        lfSwitch = new JToggleButton("LF", true);

        topPane.add(inputField);
        topPane.add(Box.createRigidArea(new Dimension(5, 0)));
        topPane.add(sendButton);
        topPane.add(Box.createGlue());
        topPane.add(crSwitch);
        topPane.add(Box.createRigidArea(new Dimension(5, 0)));
        topPane.add(lfSwitch);

        topPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 0));

        return topPane;
    }

    private JComponent createSidePane() {
        configureButton.setToolTipText(getLocalizedComponentTooltip("configureButton"));
        configureButton.addActionListener((e) -> handleConfigure());

        disconnectButton.setToolTipText(getLocalizedComponentTooltip("disconnectButton"));
        disconnectButton.addActionListener((e) -> disconnect());

        reconnectButton.setToolTipText(getLocalizedComponentTooltip("reconnectButton"));
        reconnectButton.addActionListener((e) -> reconnect());

        clearButton.setToolTipText(getLocalizedComponentTooltip("clearButton"));
        clearButton.addActionListener((e) -> clear());

        sidePane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

        return sidePane;
    }

    // Adapted from https://tips4java.wordpress.com/2013/03/03/smart-scrolling/
    private void checkScrollBar(AdjustmentEvent e) {
        //  The scroll bar listModel contains information needed to determine
        //  whether the viewport should be repositioned or not.

        JScrollBar scrollBar = (JScrollBar) e.getSource();
        BoundedRangeModel scrollBarModel = scrollBar.getModel();
        int value = scrollBarModel.getValue();
        int extent = scrollBarModel.getExtent();
        int maximum = scrollBarModel.getMaximum();

        boolean valueChanged = previousScrollBarValue != value;
        boolean maximumChanged = previousScrollBarMaximum != maximum;

        //  Check if the user has manually repositioned the scrollbar
        if (valueChanged && !maximumChanged) {
            adjustScrollBar = value + extent >= maximum;
        }

        //  Reset the "value" so we can reposition the viewport and
        //  distinguish between a user scroll and a program scroll.
        //  (ie. valueChanged will be false on a program scroll)
        if (adjustScrollBar) {
            //  Scroll the viewport to the end.d
            scrollBar.removeAdjustmentListener(scrollBarAdjustmentListener);
            value = maximum - extent;
            scrollBar.setValue(value);
            scrollBar.addAdjustmentListener(scrollBarAdjustmentListener);
        }

        previousScrollBarValue = value;
        previousScrollBarMaximum = maximum;
    }

    private static String getLocalizedComponentTooltip(String componentName) {
        return NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPane." + componentName + ".tooltip"); // NOI18N
    }

    private static String getLocalizedText(String id) {
        return NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPane." + id); // NOI18N
    }

    private void handleConnection() {
        SwingUtilities.invokeLater(() -> {
            String rawMessage;
            String formattedMessage;

            if (communicator != null) {
                communicator.disconnect();

                portName = communicator.getConfig().getPortName();

                setName(nbpio.serialmonitor.ui.Bundle.CTL_SerialMonitorDisplayPaneTopComponent() + " - " + (portName != null ? String.format("Connected to port: %s", portName) : "Not connected"));
                toggleComponents(true);

                rawMessage = getLocalizedText("connectedNotification");
                formattedMessage = MessageFormat.format(rawMessage, communicator.getConfig().getPortName());

                printNotificationLine(formattedMessage);
                revalidate();
            }
        });
    }

    private void disconnect() {
        if (communicator != null) {
            communicator.disconnect();

            String rawMessage = getLocalizedText("disconnectNotification");
            toggleComponents(false);

            portName = communicator.getConfig().getPortName();

            printNotificationLine(MessageFormat.format(rawMessage, portName));
            setName(nbpio.serialmonitor.ui.Bundle.CTL_SerialMonitorDisplayPaneTopComponent() + " - Not connected");
        }
    }

    public void reconnect() {
        if (communicator != null) {
            communicator.disconnect();

            String rawMessage = getLocalizedText("disconnectNotification");
            String formattedMessage = MessageFormat.format(rawMessage, communicator.getConfig().getPortName());

            printNotificationLine(formattedMessage);

            handleConnection();
        }
    }

    public boolean isCRSelected() {
        return crSwitch.isSelected();
    }

    public void setCRSelected(boolean selected) {
        crSwitch.setSelected(selected);
    }

    public boolean isLFSelected() {
        return lfSwitch.isSelected();
    }

    public void setLFSelected(boolean selected) {
        lfSwitch.setSelected(selected);
    }

    public void clear() {
        SwingUtilities.invokeLater(() -> textPane.setText(""));
    }

    private void sendMessage() {
        try {
            String message = inputField.getText();

            if (message != null && !message.isEmpty()) {
                inputField.setText("");

                byte[] messageBytes = message.getBytes(MESSAGE_CHARSET);
                OutputStream out = communicator.getOut();

                JOptionPane.showMessageDialog(null, message);
                JOptionPane.showMessageDialog(null, messageBytes);

                if (out != null) {
                    out.write(messageBytes);

                    if (isLFSelected()) {
                        out.write('\n');
                    }
                    if (isCRSelected()) {
                        out.write('\r');
                    }
                    out.flush();
                    printOutputLine(new String(messageBytes, MESSAGE_CHARSET));
                }
            }
        } catch (IOException | PureJavaIllegalStateException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void handleConfigure() {
        SwingUtilities.invokeLater(() -> {
            SerialMonitorConfigDialog serialMonitorConfigDialog = new SerialMonitorConfigDialog(configModel, (event) -> handleConnection());
            serialMonitorConfigDialog.setVisible(true);

            revalidate();
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sidePane = new javax.swing.JPanel();
        disconnectButton = new javax.swing.JButton();
        reconnectButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();
        configureButton = new javax.swing.JButton();
        topPane = new javax.swing.JPanel();
        inputField = new javax.swing.JTextField();
        sendButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        textPane = new javax.swing.JTextPane();

        setAlignmentX(0.0F);
        setAlignmentY(0.0F);

        sidePane.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 0, 1, new java.awt.Color(102, 102, 102)));

        disconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nbpio/serialmonitor/disconnect.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(disconnectButton, org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.disconnectButton.text")); // NOI18N
        disconnectButton.setEnabled(false);

        reconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nbpio/serialmonitor/reconnect.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(reconnectButton, org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.reconnectButton.text")); // NOI18N
        reconnectButton.setEnabled(false);

        clearButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nbpio/serialmonitor/clear.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(clearButton, org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.clearButton.text")); // NOI18N

        configureButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/nbpio/serialmonitor/settings.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(configureButton, org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.configureButton.text")); // NOI18N

        javax.swing.GroupLayout sidePaneLayout = new javax.swing.GroupLayout(sidePane);
        sidePane.setLayout(sidePaneLayout);
        sidePaneLayout.setHorizontalGroup(
            sidePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(disconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
            .addComponent(reconnectButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(clearButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(configureButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sidePaneLayout.setVerticalGroup(
            sidePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePaneLayout.createSequentialGroup()
                .addComponent(configureButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(disconnectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reconnectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearButton)
                .addContainerGap(154, Short.MAX_VALUE))
        );

        inputField.setColumns(40);
        inputField.setText(org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.inputField.text")); // NOI18N
        inputField.setEnabled(false);

        org.openide.awt.Mnemonics.setLocalizedText(sendButton, org.openide.util.NbBundle.getMessage(SerialMonitorDisplayPaneTopComponent.class, "SerialMonitorDisplayPaneTopComponent.sendButton.text")); // NOI18N
        sendButton.setEnabled(false);

        javax.swing.GroupLayout topPaneLayout = new javax.swing.GroupLayout(topPane);
        topPane.setLayout(topPaneLayout);
        topPaneLayout.setHorizontalGroup(
            topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPaneLayout.createSequentialGroup()
                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sendButton)
                .addGap(0, 20, Short.MAX_VALUE))
        );
        topPaneLayout.setVerticalGroup(
            topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(topPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(topPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(inputField)
                    .addComponent(sendButton))
                .addContainerGap())
        );

        textPane.setEditable(false);
        scrollPane.setViewportView(textPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(sidePane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrollPane)
                    .addComponent(topPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sidePane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(topPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearButton;
    private javax.swing.JButton configureButton;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JTextField inputField;
    private javax.swing.JButton reconnectButton;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton sendButton;
    private javax.swing.JPanel sidePane;
    private javax.swing.JTextPane textPane;
    private javax.swing.JPanel topPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
}
