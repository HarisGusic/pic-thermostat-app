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
        });
        Communication.status = Communication.TEMP_TX_REQUEST;
        Communication.out.write((byte) Communication.TEMP_TX_REQUEST);
        Communication.usedPort.notifyOnDataAvailable(true);
    }

}
