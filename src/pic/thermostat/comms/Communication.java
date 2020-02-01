package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;

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

    public static volatile char status;
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
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
        activePort.openPort();

        // Establish connection
        activePort.writeBytes(new byte[]{REQUEST_CONNECTION}, 1);
        SerialReader.initialize();

        // Initiate periodic data acquisition
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (timerPaused)
                    return;
                try {
                    SerialReader.readTemperature();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000);
    }

    public static void release() {
        if (activePort != null)
            activePort.closePort();
        if (timer != null)
            timer.cancel();
    }

}