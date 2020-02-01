package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;

/**
 * Holds time data in the same format as the struct Time
 * on the microcontroller. This class implements serialization
 * and deserialization.
 */
public class Time implements Serializable {

    byte day;
    short timeOfDay;

    /**
     * Convert this object into byte data suitable
     * for serial transmission.
     */
    public byte[] serialize() {
        return new byte[]{
                day,
                (byte) (timeOfDay & 0xff),
                (byte) (timeOfDay >> 8)
        };
    }

    /**
     * Assign the attributes of this object
     * with the data from {@code rawData}.
     */
    public void deserialize(byte[] rawData) throws AbsentInformationException {
        if (rawData.length < 10)
            throw new AbsentInformationException("Byte data is incomplete");
        day = rawData[0];
        timeOfDay = (short) (rawData[1] + ((short) rawData[2] << 8));
    }
}
