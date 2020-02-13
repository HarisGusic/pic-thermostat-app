package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Holds program data in the same format as the struct Program
 * on the microcontroller. This class implements serialization
 * and deserialization.
 */
public class Program implements Serializable, Comparable<Program> {

    public static final int DATA_SIZE = 10;
    public Time start = new Time(), end = new Time();
    public short min, max;

    public Program() {

    }

    /**
     * Create a new Program object from the data in {@code rawData}.
     */
    public static Program deserialize(byte[] rawData) throws AbsentInformationException {
        if (rawData.length < DATA_SIZE)
            throw new AbsentInformationException("Byte data is incomplete");
        Program program = new Program();
        program.start = Time.deserialize(Arrays.copyOfRange(rawData, 0, 3));
        program.end = Time.deserialize(Arrays.copyOfRange(rawData, 3, 6));
        program.min = Data.deserializeShort(Arrays.copyOfRange(rawData, 6, 8));
        program.max = Data.deserializeShort(Arrays.copyOfRange(rawData, 8, 10));
        return program;
    }

    public void copy(Program prog) {
        start.day = prog.start.day;
        end.day = prog.end.day;
        start.timeOfDay = prog.start.timeOfDay;
        end.timeOfDay = prog.end.timeOfDay;
        min = prog.min;
        max = prog.max;
    }

    /**
     * Convert this object into byte data suitable
     * for serial transmission.
     */
    public byte[] serialize() {
        byte[][] data = {
                start.serialize(),
                end.serialize(),
                Data.serializeShort(min),
                Data.serializeShort(max)
        };
        byte[] returnData = new byte[DATA_SIZE];
        int k = 0;
        for (int i = 0; i < data.length; ++i)
            for (int j = 0; j < data[i].length; ++j)
                returnData[k++] = data[i][j];
        return returnData;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        Program program = (Program) o;
        return min == program.min && max == program.max && start.equals(program.start) && end.equals(program.end);
    }

    @Override
    public int hashCode() {
        int result = start.hashCode();
        result = 31 * result + end.hashCode();
        result = 31 * result + (int) min;
        result = 31 * result + (int) max;
        return result;
    }

    @Override
    public Object clone() {
        Program prog = new Program();
        prog.start = (Time) start.clone();
        prog.end = (Time) end.clone();
        prog.min = min;
        prog.max = max;
        return prog;
    }

    @Override
    public int compareTo(Program p) {
        return 2 * start.compareTo(p.start) + end.compareTo(p.end);
    }
}