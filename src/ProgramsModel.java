import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pic.thermostat.data.Program;

public class ProgramsModel {

    private ObservableList<Program> loadedPrograms = FXCollections.observableArrayList(),
            localPrograms = FXCollections.observableArrayList();

}
