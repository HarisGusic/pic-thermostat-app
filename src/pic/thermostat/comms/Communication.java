package pic.thermostat.comms;

import java.util.Timer;
import java.util.TimerTask;

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
    private static Timer timer;

    public static void initialize() throws Exception {
        /*var ports = getAvailableSerialPorts();
        for (var portId : ports) {
            System.out.println(portId.getName());
            if (isPortAvailable(portId))
                usedPort = portId.open("PIC Thermostat", 2000);
        }
        if (usedPort == null) {
            System.exit(0);
            return;
        }
        usedPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
        in = usedPort.getInputStream();
        out = usedPort.getOutputStream();
        out.write((byte) CONNECTION_REQUEST);*/

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    SerialReader.readTemperature();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);
    }

    public static void release() {
        //TODO
        if (timer != null)
            timer.cancel();
    }

}