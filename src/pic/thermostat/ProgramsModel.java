package pic.thermostat;

import com.sun.jdi.AbsentInformationException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pic.thermostat.comms.SerialReader;
import pic.thermostat.data.Program;

import java.util.List;

public class ProgramsModel {

    public static ObservableList<Program> programs = FXCollections.observableArrayList();
    static ProgramsController controller;

    static boolean hasChanged = true, shouldReload = false;

    static void initialize() {
        // This method fills the programs list with dummy programs
        try {
            byte[][] data = {{0, 0, 0, 0, 59, 0, 102, 2, (byte) 153, 3},    // Mon 00:00 - Mon 00:59, 3 - 4.5
                    {0, 60, 0, 0, 119, 0, (byte) 153, 1, (byte) 204, 2},    // Mon 01:00 - Mon 01:59, 2 - 3.5
                    {0, (byte) 120, 0, 0, 88, 2, 0, 0, (byte) 153, 1}       // Mon 02:00 - Mon 10:00, 0 - 2
            };
            programs.add(new Program().deserialize(data[0]));
            programs.add(new Program().deserialize(data[1]));
            programs.add(new Program().deserialize(data[2]));
        } catch (AbsentInformationException e) {
            e.printStackTrace();
        }
    }

    public static void populate() {
        SerialReader.readPrograms();
    }

    public static ObservableList<Program> getPrograms() {
        return programs;
    }

    public static void setPrograms(ObservableList<Program> progs) {
        programs = progs;
    }

    public static void reloadPrograms(List<Program> programs) {
        Platform.runLater(() -> {
                    ProgramsModel.getPrograms().setAll(FXCollections.observableArrayList(programs));
                    controller.updateUI();
                }
        );
    }

}
