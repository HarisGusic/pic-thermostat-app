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
            REQUEST_TX_TIME = 't',
            REQUEST_TX_PROGRAM = 'p',
            REQUEST_TX_PROGRAMS = '<';

    public static final int TIMEOUT = 100;

    public static volatile char status;
    public static volatile boolean connected = false;
    static SerialPort activePort;
    private static Timer timer;
    private static volatile boolean timerPaused = false;

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

    public static void initialize() throws Exception {
        System.out.println(Arrays.toString(SerialPort.getCommPorts()));
        List<SerialPort> ports = getUnusedSerialPorts();
        if (ports.isEmpty())
            return;
        activePort = ports.get(0);
        activePort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, TIMEOUT, 0);
        activePort.openPort();

        establishConnection();

        // Initiate periodic data acquisition
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                update();
            }
        }, 0, 1000);
    }

    private static void update() {
        if (timerPaused)
            return;
        if (!connected)
            establishConnection();
        else
            try {
                SerialReader.readTemperature();
                //SerialReader.readTime();
            } catch (Exception e) {
                e.printStackTrace();
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
        HomeModel.notifyCommTimeout();
    }

}