package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Holds time data in the same format as the struct Time
 * on the microcontroller. This class implements serialization
 * and deserialization.
 */
public class Time implements Serializable {

    public byte day = 0;
    // In this version: the number of minutes that have passed today
    public short timeOfDay = 0;

    public static final int DATA_SIZE = 3;

    public Time() {
    }

    public Time(byte day, short timeOfDay) {
        this.day = day;
        this.timeOfDay = timeOfDay;
    }

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
        if (rawData.length < 3)
            throw new AbsentInformationException("Byte data is incomplete");
        day = rawData[0];
        timeOfDay = (short) (rawData[1] + ((short) rawData[2] << 8));
    }

    /**
     * @return The time in the format "Day, HH:mm".
     * <br>
     * Example: "Wed, 04:05"
     */
    @Override
    public String toString() {
        // NOTE: Should always reflect the format of struct Time on the microcontroller.
        return new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"}[day % 7] + ", "
                + LocalTime.of(timeOfDay / 60 % 24, timeOfDay % 60).format(DateTimeFormatter.ofPattern("HH:mm")); //FIXME only for debugging
    }

    @Override
    public boolean equals(Object o) {
        Time t = ((Time) o);
        return day == t.day && timeOfDay == t.timeOfDay;
    }

    @Override
    public int hashCode() {
        return day + timeOfDay * 256;
    }

    @Override
    public Object clone() {
        return new Time(day, timeOfDay);
    }

    public byte getDay() {
        return day;
    }

    public void setDay(byte day) {
        this.day = day;
    }

    public short getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(short timeOfDay) {
        this.timeOfDay = timeOfDay;
    }
}
