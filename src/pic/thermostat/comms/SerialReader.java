package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.sun.jdi.AbsentInformationException;
import javafx.application.Platform;
import pic.thermostat.HomeModel;
import pic.thermostat.ProgramsModel;
import pic.thermostat.data.Program;
import pic.thermostat.data.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static pic.thermostat.comms.Communication.*;

public class SerialReader {

    private static int programSize = 0;

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
                    case REQUEST_RX_TEMP:
                        onTemperatureDataAvailable();
                        break;
                    case REQUEST_RX_TIME:
                        onTimeDataAvailable();
                        break;
                    case REQUEST_RX_CURRENT_PROGRAM:
                        onCurrentProgramDataAvailable();
                        break;
                    case REQUEST_RX_N_PROGRAMS:
                        onProgramSizeAvailable();
                        break;
                    case REQUEST_RX_PROGRAMS:
                        onProgramsAvailable();
                        break;
                }
            }
        });
    }

    public static void readTemperature() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_TEMP)))
            readQueue.add(Communication.REQUEST_RX_TEMP);
        if (status == 0)
            SerialWriter.processWriteQueue();
        if (status == 0)
            Communication.processReadQueue();
    }

    private static void onTemperatureDataAvailable() {
        byte[] data = new byte[2];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        System.out.println(String.format("%x %x", data[0], data[1]));
        Platform.runLater(() -> {
            try {
                HomeModel.setTemperature(HomeModel.deserializeTemperature(data));
            } catch (AbsentInformationException e) {
                e.printStackTrace();
            }
        });
        //Remove excess buffer content
        clearBuffer();
        Communication.onOperationFinished();
    }

    public static void readTime() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_TIME)))
            readQueue.add(Communication.REQUEST_RX_TIME);
        if (status == 0)
            SerialWriter.processWriteQueue();
        if (status == 0)
            Communication.processReadQueue();
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
        //Remove excess buffer content
        clearBuffer();
        Communication.onOperationFinished();
    }

    public static void readCurrentProgram() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_CURRENT_PROGRAM)))
            readQueue.add(Communication.REQUEST_RX_CURRENT_PROGRAM);
        if (status == 0)
            SerialWriter.processWriteQueue();
        if (status == 0)
            Communication.processReadQueue();
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
        //Remove excess buffer content
        clearBuffer();
        Communication.onOperationFinished();
    }

    public static void readPrograms() {
        if (readQueue.stream().noneMatch(c -> c.equals(Communication.REQUEST_RX_N_PROGRAMS)))
            readQueue.add(Communication.REQUEST_RX_N_PROGRAMS);
        if (status == 0)
            SerialWriter.processWriteQueue();
        if (status == 0)
            Communication.processReadQueue();
    }

    private static void onProgramSizeAvailable() {
        status = REQUEST_RX_PROGRAMS;
        byte[] size = new byte[1];
        activePort.readBytes(size, 1);
        programSize = 0 * (size[0]) + 2; //FIXME Only for debugging purposes
        // Initiate transmission
        activePort.writeBytes(new byte[]{REQUEST_RX_PROGRAMS}, 1);
    }

    private static void onProgramsAvailable() {
        byte[] data = new byte[programSize * Program.DATA_SIZE];
        if (!readWithTimeout(data)) {
            Communication.registerTimeout();
            return;
        }
        List<Program> programs = new ArrayList<>(programSize);
        try {
            for (int i = 0; i < programSize; ++i) {
                Program program = new Program();
                program.deserialize(Arrays.copyOfRange(data, Program.DATA_SIZE * i, Program.DATA_SIZE * (i + 1)));
                programs.add(program);
            }
            ProgramsModel.reloadPrograms(programs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Remove excess buffer content
        clearBuffer();
        Communication.onOperationFinished();
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
