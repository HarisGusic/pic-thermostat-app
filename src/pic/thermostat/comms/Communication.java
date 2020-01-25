package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;

import java.util.*;

public class Communication {

    public static final char
            CONNECTION_REQUEST = '#',
            TEMP_TX_REQUEST = '$',
            TIME_TX_REQUEST = 'T',
            TIME_RX_REQUEST = 't',
            PROGRAM_TX_REQUEST = '&',
            PROGRAMS_RX_REQUEST = '<',
            PROGRAMS_TX_REQUEST = '>';

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
        activePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 0, 0);

        // Establish connection
        activePort.writeBytes(new byte[]{CONNECTION_REQUEST}, 1);

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
        }, 0, 1000);
    }

    public static void release() {
        if (activePort != null)
            activePort.closePort();
        if (timer != null)
            timer.cancel();
    }

    /**
     * Called when a transmit/receive operation has finished.
     */
    static void onFinishTask(char task) {
        timerPaused = true;
        while (activePort.bytesAvailable() > 0) {
            byte[] b = new byte[1];
            activePort.readBytes(b, 1);
            System.out.println("Ate: " + b[0]);
        }
        timerPaused = false;
    }
}