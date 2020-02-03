package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.sun.jdi.AbsentInformationException;
import javafx.application.Platform;
import pic.thermostat.HomeModel;
import pic.thermostat.data.Program;
import pic.thermostat.data.Time;

import static pic.thermostat.comms.Communication.*;

public class SerialReader {

    public static void initialize() {
        //Remove excess buffer content
        clearBuffer();
        activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                switch (status) {
                    case Communication.REQUEST_RX_TEMP:
                        onTemperatureDataAvailable();
                        break;
                    case Communication.REQUEST_RX_TIME:
                        onTimeDataAvailable();
                        break;
                    case Communication.REQUEST_RX_CURRENT_PROGRAM:
                        onCurrentProgramDataAvailable();
                        break;
                }
                //Remove excess buffer content
                clearBuffer();
                Communication.onOperationFinished();
            }
        });
    }

    public static void readTemperature() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_TEMP)))
            readQueue.add(Communication.REQUEST_RX_TEMP);
    }

    private static void onTemperatureDataAvailable() {
        byte[] data = new byte[2];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        System.out.println(String.format("%x %x", data[0], data[1]));
        try {
            HomeModel.setTemperature(HomeModel.deserializeTemperature(data));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    public static void readTime() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_TIME)))
            readQueue.add(Communication.REQUEST_RX_TIME);
    }

    private static void onTimeDataAvailable() {
        byte[] data = new byte[Time.DATA_SIZE];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        System.out.println(String.format("%x %x %x", data[0], data[1], data[2])); //TODO remove
        try {
            Time time = new Time();
            time.deserialize(data);
            Platform.runLater(() -> HomeModel.setDeviceTime(time));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    public static void readCurrentProgram() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_CURRENT_PROGRAM)))
            readQueue.add(Communication.REQUEST_RX_CURRENT_PROGRAM);
    }

    private static void onCurrentProgramDataAvailable() {
        byte[] data = new byte[Program.DATA_SIZE];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        try {
            Program prog = new Program();
            prog.deserialize(data);
            Platform.runLater(() -> HomeModel.setCurrentProgram(prog));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read data from the serial port and store it in {@code destination}.
     *
     * @return Whether {@code destination.length} bytes have been read
     * without triggering a timeout.
     */
    private static boolean readWithTimeout(byte[] destination) {
        long time = System.currentTimeMillis();
        activePort.readBytes(destination, destination.length);
        return System.currentTimeMillis() - time < Communication.TIMEOUT;
    }

    static void clearBuffer() {
        int available = activePort.bytesAvailable();
        activePort.readBytes(new byte[available], available);
    }

}
