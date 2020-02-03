package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;

/**
 * Holds program data in the same format as the struct Program
 * on the microcontroller. This class implements serialization
 * and deserialization.
 */
public class Program implements Serializable {

    public Time start = new Time(), end = new Time();
    public short min, max;

    public static final int DATA_SIZE = 10;

    public Program() {

    }

    public Program(byte[] rawData) throws AbsentInformationException {
        deserialize(rawData);
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
        return new byte[]{
                start.day,
                (byte) (start.timeOfDay & 0xff),
                (byte) (start.timeOfDay >> 8),
                end.day,
                (byte) (end.timeOfDay & 0xff),
                (byte) (end.timeOfDay >> 8),
                (byte) (min & 0xff),
                (byte) (min >> 8),
                (byte) (max & 0xff),
                (byte) (max >> 8),
        };
    }

    /**
     * Assign the attributes of this object
     * with the data from {@code rawData}.
     */
    public void deserialize(byte[] rawData) throws AbsentInformationException {
        if (rawData.length < 10)
            throw new AbsentInformationException("Byte data is incomplete");
        start.day = rawData[0];
        start.timeOfDay = (short) (rawData[1] + ((short) rawData[2] << 8));
        end.day = rawData[3];
        end.timeOfDay = (short) (rawData[4] + ((short) rawData[5] << 8));
        min = (short) (rawData[6] + ((short) rawData[7] << 8));
        max = (short) (rawData[8] + ((short) rawData[9] << 8));
    }

    @Override
    public boolean equals(Object o) {
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
}