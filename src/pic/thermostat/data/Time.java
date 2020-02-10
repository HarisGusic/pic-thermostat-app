package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;

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
     * Create a new Time object from the data in {@code rawData}.
     */
    public static Time deserialize(byte[] rawData) throws AbsentInformationException {
        if (rawData.length < DATA_SIZE)
            throw new AbsentInformationException("Byte data is incomplete");
        byte day = rawData[0];
        short timeOfDay = Data.deserializeShort(Arrays.copyOfRange(rawData, 1, 3));
        return new Time(day, timeOfDay);
    }

    public static Time parseTime(String s) throws ParseException {
        //TODO deprecated
        Date date = new SimpleDateFormat("EEE, HH:mm").parse(s);
        Time time = new Time();
        time.day = (byte) ((date.getDay() + 6) % 7);
        time.timeOfDay = (short) (date.getHours() * 60 + date.getMinutes());
        return time;
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

    /**
     * Convert this object into byte data suitable
     * for serial transmission.
     */
    public byte[] serialize() {
        byte[] timeOfDay = Data.serializeShort(this.timeOfDay);
        return new byte[]{
                day,
                timeOfDay[0],
                timeOfDay[1]
        };
    }
}
