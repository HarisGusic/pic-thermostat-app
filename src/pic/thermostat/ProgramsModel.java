package pic.thermostat;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pic.thermostat.comms.SerialReader;
import pic.thermostat.data.Program;

import java.util.List;

public class ProgramsModel {

    public static ObservableList<Program> programs = FXCollections.observableArrayList();
    static ProgramsController controller;

    static boolean hasChanged = false, shouldReload = false;

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
