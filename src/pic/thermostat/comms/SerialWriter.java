package pic.thermostat.comms;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import pic.thermostat.data.Program;
import pic.thermostat.data.Time;

import java.util.LinkedList;
import java.util.List;

import static pic.thermostat.ProgramsModel.programs;
import static pic.thermostat.comms.Communication.*;

public class SerialWriter {

    volatile static LinkedList<Character> writeQueue = new LinkedList<>();
    private static Time timeToSend;
    private static List<Program> programsToSend;

    public static void initialize() {
        Communication.activePort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_WRITTEN;
            }

            @Override
            public void serialEvent(SerialPortEvent serialPortEvent) {
                status = 0;
            }
        });
    }

    public static void sendTime(Time time) {
        if (writeQueue.stream().noneMatch(c -> c.equals(REQUEST_TX_TIME)))
            writeQueue.add(REQUEST_TX_TIME);
        timeToSend = (Time) time.clone();
    }

    private static void onSendTime() {
        activePort.writeBytes(timeToSend.serialize(), Time.DATA_SIZE);
        onOperationFinished();
    }

    public static void sendPrograms(List<Program> programs) {
        if (writeQueue.stream().noneMatch(c -> c.equals(REQUEST_TX_PROGRAMS)))
            writeQueue.add(REQUEST_TX_PROGRAMS);
        programsToSend = List.copyOf(programs);
        if (status == 0)
            processWriteQueue();
    }

    private static void onSendPrograms() {
        for (var prog : programs)
            activePort.writeBytes(prog.serialize(), Program.DATA_SIZE);
        onOperationFinished();
    }

    static void processWriteQueue() {
        if (writeQueue.isEmpty())
            return;
        activePort.removeDataListener();
        status = writeQueue.getFirst();
        writeQueue.removeFirst();
        activePort.writeBytes(new byte[]{(byte) status}, 1);
        switch (status) {
            case REQUEST_TX_TIME:
                onSendTime();
                break;
            case REQUEST_TX_PROGRAMS:
                onSendPrograms();
                break;
        }
    }

}
