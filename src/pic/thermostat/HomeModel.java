package pic.thermostat;

import com.sun.jdi.AbsentInformationException;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Alert;
import pic.thermostat.data.Data;
import pic.thermostat.data.Program;
import pic.thermostat.data.Time;

public class HomeModel {

    private static SimpleIntegerProperty temperature = new SimpleIntegerProperty(0);
    private static SimpleStringProperty displayTemperature = new SimpleStringProperty(Data.getTemperatureText((short) getTemperature()));
    private static SimpleObjectProperty<Program> currentProgram = new SimpleObjectProperty<>();
    private static SimpleObjectProperty<Time> deviceTime = new SimpleObjectProperty<>(new Time((byte) 0, (short) 0));
    private static SimpleStringProperty displayDeviceTime = new SimpleStringProperty(deviceTime.get().toString());

    public static short deserializeTemperature(byte[] data) throws AbsentInformationException {
        if (data.length < 2)
            throw new AbsentInformationException("Temperature data is incomplete");
        return (short) (((int) data[0] & 0xff) + (((int) data[1] & 0xff) << 8));
    }

    // Trivial methods

    public static void setDisplayTemperature(String displayTemperature) {
        HomeModel.displayTemperature.set(displayTemperature);
    }

    public static SimpleStringProperty displayTemperatureProperty() {
        return displayTemperature;
    }

    public static int getTemperature() {
        return temperature.get();
    }

    public static void setTemperature(int temp) {
        temperature.set(temp);
    }

    public static SimpleIntegerProperty temperatureProperty() {
        return temperature;
    }

    public static void setDeviceTime(Time time) {
        deviceTime.set(time);
    }

    public static SimpleObjectProperty<Time> deviceTimeProperty() {
        return deviceTime;
    }

    public static void setDisplayDeviceTime(String strTime) {
        displayDeviceTime.set(strTime);
    }

    public static SimpleStringProperty displayDeviceTimeProperty() {
        return displayDeviceTime;
    }

    public static Program getCurrentProgram() {
        return currentProgram.get();
    }

    public static void setCurrentProgram(Program prog) {
        currentProgram.set(prog);
    }

    public static SimpleObjectProperty<Program> currentProgramProperty() {
        return currentProgram;
    }

    public static void notifyCommTimeout() {
        Platform.runLater(() -> {
            new Alert(Alert.AlertType.ERROR, "Serial connection lost.").showAndWait();
        });
    }
}
