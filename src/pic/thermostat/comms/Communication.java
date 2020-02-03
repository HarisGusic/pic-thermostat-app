package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import pic.thermostat.HomeModel;

import java.util.*;

public class Communication {

    public static final char
            REQUEST_CONNECTION = '#',
            REQUEST_RX_TEMP = '$',
            REQUEST_RX_TIME = 'T',
            REQUEST_RX_CURRENT_PROGRAM = '&',
            REQUEST_RX_PROGRAMS = '>',
            REQUEST_RX_N_PROGRAMS = 'N',
            REQUEST_TX_TIME = 't',
            REQUEST_TX_PROGRAM = 'p',
            REQUEST_TX_PROGRAMS = '<',
            REQUEST_TX_N_PROGRAMS = 'n';

    public static final int TIMEOUT = 100;

    public static final boolean OPERATION_READ = true, OPERATION_WRITE = false;
    public static volatile boolean connected = false;
    static volatile char status;
    static volatile boolean operationType = OPERATION_READ;
    static SerialPort activePort;
    volatile static LinkedList<Character> readQueue = new LinkedList<>();
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
        activePort = ports.get(0);
        activePort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, TIMEOUT, 0);
        activePort.openPort();

        establishConnection();
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
        Communication.status = 0;
    }

    public static void release() {
        if (activePort != null)
            activePort.closePort();
        if (timer != null)
            timer.cancel();
    }

    public static void registerTimeout() {
        connected = false;
        readQueue.addFirst(status);
        HomeModel.notifyCommTimeout();
        if (status == 0)
            processReadQueue();
    }

    public static void onOperationFinished() {
        Communication.status = 0;
        SerialWriter.processWriteQueue();
        if (status == 0)
            processReadQueue();
    }

    static void processReadQueue() {
        if (readQueue.isEmpty())
            return;
        Communication.status = readQueue.getFirst();
        readQueue.remove();
        SerialReader.initialize();
        activePort.writeBytes(new byte[]{(byte) Communication.status}, 1);
    }

}