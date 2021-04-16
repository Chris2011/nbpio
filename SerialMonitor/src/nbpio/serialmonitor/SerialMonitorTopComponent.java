package nbpio.serialmonitor;

import java.awt.BorderLayout;
import java.util.Properties;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import nbpio.serialmonitor.ui.configuration.SerialMonitorConfigDialog;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ConvertAsProperties(
    dtd = "-//nbpio.serialmonitor//SerialMonitor//EN",
    autostore = false
)
@TopComponent.Description(
    preferredID = "SerialMonitorTopComponent",
    iconBase = "nbpio/serialmonitor/serialPort.png",
    persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "output", openAtStartup = false)
@ActionID(category = "Tools", id = "nbpio.serialmonitor.SerialMonitorTopComponent")
@ActionReference(path = "Menu/Window/Tools", position = 950)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_SerialMonitorAction",
    preferredID = "SerialMonitorTopComponent"
)
@Messages({
    "CTL_SerialMonitorAction=Serial Monitor",
    "CTL_SerialMonitorTopComponent=Serial Monitor",
    "HINT_SerialMonitorTopComponent=This is a SerialMonitor window"
})
public final class SerialMonitorTopComponent extends TopComponent {
    private static final Logger LOGGER = Logger.getLogger(SerialMonitorTopComponent.class.getName());

    private SerialPortCommunicator communicator;
    private final SerialMonitorConfigModel configModel;
    private final SerialMonitorDisplayPaneOld serialMonitorDisplayPane;

    public SerialMonitorTopComponent() {
        initComponents();

        configModel = new DefaultSerialMonitorConfigModel();
        communicator = new SerialPortCommunicator(configModel.getCurrentConfig());

        handleConnection();

        System.out.println("dasdasdasdadsasdasdasdasdasdasdasda--------------------------------------------------------dsdssdsds");

        serialMonitorDisplayPane = new SerialMonitorDisplayPaneOld((event) -> handleConfigure());
        serialMonitorDisplayPane.connectToPort(communicator);

        add(serialMonitorDisplayPane);
    }

    private void initComponents() {
        setName(Bundle.CTL_SerialMonitorTopComponent());
        setToolTipText(Bundle.HINT_SerialMonitorTopComponent());
        setLayout(new BorderLayout());

//        communicator = new SerialPortCommunicator(configModel.getCurrentConfig());
//
//        add(new SerialMonitorDisplayPane(communicator, null));
//        SerialMonitorConfigDialog serialMonitorConfigDialog = new SerialMonitorConfigDialog();
//        serialMonitorConfigDialog.setVisible(true);
    }

    @Override
    public void componentOpened() {
        // ignore
    }

    @Override
    public void componentClosed() {
        if (communicator != null) {
            communicator.disconnect();
        }
    }

    void writeProperties(Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private void handleConnection() {
        if (communicator != null) {
            communicator.disconnect();
        }

        communicator = new SerialPortCommunicator(configModel.getCurrentConfig());

        SwingUtilities.invokeLater(() -> {
            final String portName = communicator.getConfig().getPortName();

            setName(Bundle.CTL_SerialMonitorTopComponent() + " - " + (portName != null ? String.format("Connected to port: %s", portName) : "Not connected"));

            revalidate();
        });
    }

    private void handleConfigure() {
        SwingUtilities.invokeLater(() -> {
            SerialMonitorConfigDialog serialMonitorConfigDialog = new SerialMonitorConfigDialog(configModel, (event) -> handleConnection());
            serialMonitorConfigDialog.setVisible(true);

            revalidate();
        });
    }
}
