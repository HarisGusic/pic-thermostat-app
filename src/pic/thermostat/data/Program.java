package pic.thermostat.data;

import java.io.*;

public class Program implements Serializable {
    byte startDay, endDay;
    short on, off;
    short min, max;

    public Program(byte[] rawData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream in = new ByteArrayInputStream(rawData);
        ObjectInputStream is = new ObjectInputStream(in);
        Program prog = (Program) is.readObject();
        copy(prog);
    }

    public void copy(Program prog) {
        startDay = prog.startDay;
        endDay = prog.endDay;
        on = prog.on;
        off = prog.off;
        min = prog.min;
        max = prog.max;
    }

    public byte[] serialize() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(this);
        return out.toByteArray();
    }

}