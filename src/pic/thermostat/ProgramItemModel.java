package pic.thermostat;

import javafx.beans.property.SimpleObjectProperty;
import pic.thermostat.data.Data;
import pic.thermostat.data.Program;

public class ProgramItemModel {

    SimpleObjectProperty<Program> program;

    int index;

    public ProgramItemModel(Program prog, int index) {
        program = new SimpleObjectProperty<>(prog);
        this.index = index;
    }

    public static short parseTemperature(String s) {
        return Data.getRawTemperature(Float.parseFloat(s.replace(" °C", "")));
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
