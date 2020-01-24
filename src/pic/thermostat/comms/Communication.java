package pic.thermostat.comms;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
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

    public static SerialPort usedPort = null;
    public static InputStream in;
    public static OutputStream out;
    public static volatile char status;
    private static Timer timer;

    public static Set<CommPortIdentifier> getAvailableSerialPorts() {
        Set<CommPortIdentifier> portsSet = new HashSet<CommPortIdentifier>();
        Enumeration ports = CommPortIdentifier.getPortIdentifiers();
        while (ports.hasMoreElements()) {
            CommPortIdentifier com = ((CommPortIdentifier) ports.nextElement());
            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL)
                portsSet.add(com);
        }
        return portsSet;
    }

    public static boolean isPortAvailable(CommPortIdentifier portId) {
        try {
            CommPort port = portId.open("CommUtil", 50);
            port.close();
            return true;
        } catch (PortInUseException e) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void initialize() throws Exception {
        var ports = getAvailableSerialPorts();
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
        out.write((byte) CONNECTION_REQUEST);

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
        if (usedPort != null)
            usedPort.close();
        if (timer != null)
            timer.cancel();
    }

}