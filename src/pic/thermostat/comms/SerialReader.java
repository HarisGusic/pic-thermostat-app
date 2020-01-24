package pic.thermostat.comms;

public class SerialReader {

    public static void readTemperature() throws Exception {
        if (Communication.status != 0)
            return;
        Communication.out.write((byte) Communication.TEMP_TX_REQUEST);
        Communication.status = Communication.TEMP_TX_REQUEST;


    }

}
