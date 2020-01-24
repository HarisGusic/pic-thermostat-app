package pic.thermostat.data;

public class Data {

    private volatile static short temperature;
    private volatile static byte startDay, endDay;
    private volatile static short on, off;
    private volatile static short min, max;
    private volatile static Program[] programs;

    public static short getTemperatureRaw() {
        return temperature;
    }

    public static void setTemperatureRaw(short temp) {
        temperature = temp;
    }

    public static float getTemperature() {
        return (float) (temperature * 5.0 / 1024);
    }

    public static void setTemperature(float temp) {
        //TODO temperature = temp;
    }

}
