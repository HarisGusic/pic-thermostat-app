package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;

/**
 * Holds program data in the same format as the struct Program
 * on the microcontroller. This class implements serialization
 * and deserialization.
 */
public class Program implements Serializable {

    public byte startDay, endDay;
    public short on, off;
    public short min, max;

    public static final int DATA_SIZE = 10;

    public Program() {

    }

    public Program(byte[] rawData) throws AbsentInformationException {
        deserialize(rawData);
    }

    public void copy(Program prog) {
        startDay = prog.startDay;
        endDay = prog.endDay;
        on = prog.on;
        off = prog.off;
        min = prog.min;
        max = prog.max;
    }

    /**
     * Convert this object into byte data suitable
     * for serial transmission.
     */
    public byte[] serialize() {
        return new byte[]{
                startDay,
                endDay,
                (byte) (on & 0xff),
                (byte) (on >> 8),
                (byte) (off & 0xff),
                (byte) (off >> 8),
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
        startDay = rawData[0];
        endDay = rawData[1];
        on = (short) (rawData[2] + ((short) rawData[3] << 8));
        off = (short) (rawData[4] + ((short) rawData[5] << 8));
        min = (short) (rawData[6] + ((short) rawData[7] << 8));
        max = (short) (rawData[8] + ((short) rawData[9] << 8));
    }

}