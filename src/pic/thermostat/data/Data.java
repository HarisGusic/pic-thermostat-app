package pic.thermostat.data;

import com.sun.jdi.AbsentInformationException;
import pic.thermostat.HomeModel;

import java.text.DecimalFormat;

public class Data {

    public static byte[] serializeShort(short value) {
        return new byte[]{(byte) value, (byte) (value >> 8)};
    }

    public static short deserializeShort(byte[] data) throws AbsentInformationException {
        if (data.length < 2)
            throw new AbsentInformationException("Temperature data is incomplete");
        return (short) ((int) data[0] & 0xff | (((int) data[1] & 0xff) << 8));
    }

    public static short getTemperature() {
        return (short) HomeModel.getTemperature();
    }

    public static void setTemperature(short rawValue) {
        HomeModel.setTemperature(rawValue);
    }

    public static float getRealTemperature() {
        return getRealTemperature((short) HomeModel.getTemperature());
    }

    public static void setRealTemperature(float realValue) {
        setTemperature(getRawTemperature(realValue));
    }

    public static float getRealTemperature(short rawValue) {
        // Replace the rule based on the characteristic of the temperature sensor you are using
        return 5.0f * rawValue / 1024.0f;
    }

    public static short getRawTemperature(float realValue) {
        // Replace the rule based on the characteristic of the temperature sensor you are using
        return (short) (realValue * 1024 / 5.0);
    }

    public static String getTemperatureText(float realValue) {
        return Float.valueOf(new DecimalFormat("#.0").format(realValue)) + " °C";
    }

    public static String getTemperatureText(short rawValue) {
        return getTemperatureText(getRealTemperature(rawValue));
    }

    public static String getTemperatureText() {
        return getTemperatureText(getRealTemperature());
    }
}
