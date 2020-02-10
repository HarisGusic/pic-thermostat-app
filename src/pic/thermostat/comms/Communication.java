package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class Communication {

    public static final char
            REQUEST_CONNECTION = '#',
            REQUEST_RX_TEMP = '$',
            REQUEST_RX_TIME = 'T',
            REQUEST_RX_CURRENT_PROGRAM = '&',
            REQUEST_RX_PROGRAMS = '>',
            REQUEST_RX_N_PROGRAMS = 'N',
            REQUEST_RX_ISNULL = '?',
            REQUEST_TX_TIME = 't',
            REQUEST_TX_PROGRAM = 'p',
            REQUEST_TX_PROGRAMS = '<',
            REQUEST_TX_N_PROGRAMS = 'n';

    public static final int TIMEOUT = 100;

    public static volatile boolean connected = false;
    static volatile char status;
    static SerialPort activePort;
    public static Timer timer;
    private static volatile boolean timerPaused = false;
    private static int prescaler = 0;

    /**
     * Find the serial port with a microcontroller connected to it.
     *
     * @return The SerialPort object associated with the port that has been found.
     */
    public static SerialPort detectDevice() {
        //TODO implement
        SerialPort[] comPorts = SerialPort.getCommPorts();
        for (var port : comPorts) {
            if (port.openPort()) {

            }
        }
        return null;
    }

    /**
     * @return All serial ports that are not used by other programs.
     */
    public static List<SerialPort> getUnusedSerialPorts() {
        List<SerialPort> found = new ArrayList<>();
        SerialPort[] comPorts = SerialPort.getCommPorts();
        for (var port : comPorts) {
            if (port.openPort()) {
                found.add(port);
                port.closePort();
            }
        }
        return found;
    }

    public static void initialize() {
        System.out.println(Arrays.toString(SerialPort.getCommPorts()));
        List<SerialPort> ports = getUnusedSerialPorts();
        if (ports.isEmpty())
            return;
        setActivePort(ports.get(0));
    }

    public static SerialPort getActivePort() {
        return activePort;
    }

    public static void update() {
        if (activePort == null)
            return;
        ++prescaler;
        if (timerPaused)
            return;
        if (!connected)
            establishConnection();
        else {
            if ((prescaler % 10) == 9)
                SerialReader.readTime();
            else if ((prescaler % 5) == 4)
                SerialReader.readCurrentProgram();
            else
                SerialReader.readTemperature();

            prescaler %= 10;
        }
    }

    public static void establishConnection() {
        activePort.writeBytes(new byte[]{REQUEST_CONNECTION}, 1);
        // TODO implement check
        connected = true;
        status = 0;
        SerialWriter.processWriteQueue();
        if (status == 0)
            SerialReader.processReadQueue();
    }

    public static void release() {
        if (activePort != null)
            activePort.closePort();
        if (timer != null)
            timer.cancel();
    }

    public static void onOperationFinished() {
        Communication.status = 0;
        SerialWriter.processWriteQueue();
        if (status == 0)
            SerialReader.processReadQueue();
    }

    public static void setActivePort(SerialPort port) {
        if (activePort != null)
            activePort.closePort();

        activePort = port;

        if (port == null)
            return;

        activePort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, TIMEOUT, 0);
        activePort.openPort();

        establishConnection();
    }

    public static boolean isBusy() {
        return status != 0;
    }
}