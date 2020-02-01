package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class SerialWriter {

    public static void initialize() {
        Communication.activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                Communication.status = 0;
            }
        });
    }


}
