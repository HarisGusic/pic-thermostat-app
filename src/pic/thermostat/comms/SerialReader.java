package pic.thermostat.comms;

import pic.thermostat.data.Data;

import java.io.IOException;

public class SerialReader {

    public static void readTemperature() throws Exception {
        if (Communication.status != 0)
            return;

        Communication.usedPort.addEventListener(e -> {
            byte[] buffer = new byte[2];
            try {
                for (int i = 0; i < 2; ++i)
                    buffer[i] = (byte) Communication.in.read();
                Data.setTemperatureRaw((short) (buffer[0] + ((short) buffer[1] << 8)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Communication.usedPort.removeEventListener();
            Communication.status = 0;
        });
        Communication.status = Communication.TEMP_TX_REQUEST;
        Communication.out.write((byte) Communication.TEMP_TX_REQUEST);
        Communication.usedPort.notifyOnDataAvailable(true);
    }

    public static void readProgram() throws Exception {
        if (Communication.status != 0)
            return;
        Communication.usedPort.addEventListener(e -> {
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
        Communication.usedPort.notifyOnDataAvailable(true);
    }

    public static void readTime() throws Exception {
        if (Communication.status != 0)
            return;
        Communication.usedPort.addEventListener(e -> {
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
        Communication.usedPort.notifyOnDataAvailable(true);
    }

}
