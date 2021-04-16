package nbpio.serialmonitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import purejavacomm.CommPortIdentifier;
import purejavacomm.NoSuchPortException;
import purejavacomm.PortInUseException;
import purejavacomm.SerialPort;
import purejavacomm.SerialPortEvent;
import purejavacomm.UnsupportedCommOperationException;

public class SerialPortCommunicator {
    private final SerialPortConfig config;
    private Consumer<Boolean> connectionHandler;
    private Consumer<InputStream> inputHandler;
    private SerialPort port;
    private InputStream in;
    private OutputStream out;

    public SerialPortCommunicator(SerialPortConfig config) {
        this.config = config;
    }

    public SerialPortConfig getConfig() {
        return config;
    }

    public void connect(Consumer<Boolean> connectionHandler, Consumer<InputStream> inputHandler) throws TooManyListenersException, UnsupportedCommOperationException, PortInUseException, NoSuchPortException, IOException {
        this.connectionHandler = connectionHandler;
        this.inputHandler = inputHandler;

        final String portName = config.getPortName();

        if (portName != null) {
            CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(portName);

            port = (SerialPort) portid.open(getClass().getName(), 1000);

            this.connectionHandler.accept(Boolean.FALSE);  // first connection

            setupPort();
        }
    }

    public InputStream getIn() {
        return in;
    }

    public OutputStream getOut() {
        return out;
    }

//    public void reconnect() throws NoSuchPortException, PortInUseException, IOException, UnsupportedCommOperationException, TooManyListenersException {
//        disconnect();
//
//        final String portName = config.getPortName();
//
//        if (portName != null) {
//            CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(portName);
//
//            port = (SerialPort) portid.open(getClass().getName(), 1000);
//
//            connectionHandler.accept(Boolean.TRUE);  // reconnection
//
//            setupPort();
//        }
//    }

    public void disconnect() {
        if (port != null) {
            port.close();
        }
    }

    public void startScanningForPort() {
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    CommPortIdentifier portid = CommPortIdentifier.getPortIdentifier(config.getPortName());
                    if (portid != null) {
                        port = (SerialPort) portid.open(getClass().getName(), 1000);
                        
                        connectionHandler.accept(Boolean.TRUE);  // reconnection
                        setupPort();

                        return;
                    }
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    return;
                } catch (NoSuchPortException ex) {
                    // ignore
                } catch (IOException | TooManyListenersException | PortInUseException | UnsupportedCommOperationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void setupPort() throws IOException, UnsupportedCommOperationException, TooManyListenersException {
        JOptionPane.showMessageDialog(null, "in setupPort");
        in = port.getInputStream();
        out = port.getOutputStream();
        port.notifyOnDataAvailable(true);
        port.notifyOnOutputEmpty(false);
        port.setFlowControlMode(config.getFlowControl());
        port.setSerialPortParams(config.getBaudRate(), config.getDataBits(), config.getStopBits(), config.getParity());
        port.addEventListener((SerialPortEvent event) -> {
            if (event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                inputHandler.accept(in);
            }
        });
    }
}