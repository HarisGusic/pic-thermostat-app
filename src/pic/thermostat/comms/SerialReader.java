package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.sun.jdi.AbsentInformationException;
import pic.thermostat.HomeModel;

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
                if (Communication.status == Communication.REQUEST_RX_TEMP) {
                    onTemperatureDataAvailable();
                }
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
        activePort.writeBytes(new byte[]{Communication.REQUEST_RX_TEMP}, 1);
    }

    private static void onTemperatureDataAvailable() {
        byte[] data = new byte[2];
        activePort.readBytes(data, 2);
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
        /*Communication.usedPort.addEventListener(e -> {
            byte[] buffer = new byte[10];
            try {
                for (int i = 0; i < 10; ++i)
                    buffer[i] = (byte) Communication.in.read();
                //TODO Data.setTemperatureRaw((short) (buffer[0] + ((short) buffer[1] << 8)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Communication.usedPort.removeEventListener();
            Communication.status = 0;
        });
        Communication.status = Communication.PROGRAM_TX_REQUEST;
        Communication.out.write((byte) Communication.PROGRAM_TX_REQUEST);
        Communication.usedPort.notifyOnDataAvailable(true);*/
    }

    public static void readTime() throws Exception {
        if (Communication.status != 0)
            return;
        /*Communication.usedPort.addEventListener(e -> {
            byte[] buffer = new byte[2];
            try {
                for (int i = 0; i < 2; ++i)
                    buffer[i] = (byte) Communication.in.read();
                Data.setDeviceTime((short) (buffer[0] + ((short) buffer[1] << 8)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Communication.usedPort.removeEventListener();
            Communication.status = 0;
        });
        Communication.status = Communication.TIME_TX_REQUEST;
        Communication.out.write((byte) Communication.TIME_TX_REQUEST);
        Communication.usedPort.notifyOnDataAvailable(true);*/
    }

}
