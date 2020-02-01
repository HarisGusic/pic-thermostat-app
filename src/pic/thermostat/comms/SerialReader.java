package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.sun.jdi.AbsentInformationException;
import javafx.application.Platform;
import pic.thermostat.HomeModel;
import pic.thermostat.data.Time;

import static pic.thermostat.comms.Communication.activePort;

public class SerialReader {

    public static void initialize() {
        activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                if (Communication.status == Communication.REQUEST_RX_TEMP)
                    onTemperatureDataAvailable();
                else if (Communication.status == Communication.REQUEST_RX_TIME)
                    onTimeDataAvailable();
                //Remove excess buffer content
                while (activePort.bytesAvailable() > 0) {
                    byte[] b = new byte[1];
                    activePort.readBytes(b, 1);
                    System.out.println("Ate: " + b[0]);
                }
            }
        });
    }

    public static void readTemperature() throws Exception {
        if (Communication.status != 0)
            return;
        Communication.status = Communication.REQUEST_RX_TEMP;
        SerialReader.initialize();
        activePort.writeBytes(new byte[]{Communication.REQUEST_RX_TEMP}, 1);
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
        Communication.status = 0;
    }

    public static void readProgram() throws Exception {
        if (Communication.status != 0)
            return;
    }

    public static void readTime() throws Exception {
        if (Communication.status != 0)
            return;
        Communication.status = Communication.REQUEST_RX_TIME;
        SerialReader.initialize();
        activePort.writeBytes(new byte[]{Communication.REQUEST_RX_TIME}, 1);
    }

    private static void onTimeDataAvailable() {
        byte[] data = new byte[3];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        System.out.println(String.format("%x %x %x", data[0], data[1], data[2]));
        try {
            Time time = new Time();
            time.deserialize(data);
            Platform.runLater(() -> HomeModel.setDeviceTime(time));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
        Communication.status = 0;
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

}
