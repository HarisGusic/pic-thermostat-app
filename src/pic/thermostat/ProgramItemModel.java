package pic.thermostat;

import javafx.beans.property.SimpleObjectProperty;
import pic.thermostat.data.Program;
import pic.thermostat.data.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProgramItemModel {

    SimpleObjectProperty<Program> program;

    public ProgramItemModel(Program prog) {
        program = new SimpleObjectProperty<>(prog);
    }

    public static Time parseTime(String s) throws ParseException {
        //TODO deprecated
        Date date = new SimpleDateFormat("EEE, HH:mm").parse(s);
        Time time = new Time();
        time.day = (byte) ((date.getDay() + 6) % 7);
        time.timeOfDay = (short) (date.getHours() * 60 + date.getMinutes());
        return time;
    }

    public static short parseTemperature(String s) {
        return HomeModel.getRawTemperature(Float.parseFloat(s.replace(" °C", "")));
    }

    public Program getProgram() {
        return program.get();
    }

    public void setProgram(Program program) {
        this.program.set(program);
    }

    public SimpleObjectProperty<Program> programProperty() {
        return program;
    }

}
