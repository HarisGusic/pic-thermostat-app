package pic.thermostat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import pic.thermostat.comms.SerialWriter;
import pic.thermostat.data.Program;

import java.io.IOException;

public class ProgramsController extends ContentController {

    public VBox contentPrograms;
    public Accordion accordion;
    public Button btnRead;
    public Button btnSend;
    public Button btnAdd;
    public ScrollPane scrollPane;
    public Button btnSort;

    public ProgramsController() {

    }

    @FXML
    public void initialize() {
        ProgramsModel.controller = this;

        ProgramsModel.initialize();

        updateUI();

        btnRead.setOnAction(e -> {
            ProgramsModel.populate();
        });
        btnSend.setOnAction(e -> {
            if (ProgramsModel.hasChanged) {
                SerialWriter.sendPrograms(ProgramsModel.programs);
                ProgramsModel.hasChanged = false;
            } else
                new Alert(Alert.AlertType.INFORMATION, "No changes have been made").showAndWait();
        });
        btnAdd.setOnAction(e -> {
            Program newEntry = new Program();
            ProgramsModel.getPrograms().add(newEntry);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/program.fxml"));
            int index = ProgramsModel.getPrograms().size() - 1;
            loader.setController(new ProgramItemController(new ProgramItemModel(newEntry, index)));
            try {
                accordion.getPanes().add(index, loader.load());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            accordion.setExpandedPane(accordion.getPanes().get(index));
            ProgramsModel.hasChanged = true;
        });
        btnSort.setOnAction(e -> {
            ProgramsModel.getPrograms().sort(Program::compareTo);
            updateUI();
        });

    }

    @Override
    public void setActive(boolean active) {
        contentPrograms.setVisible(active);
    }

    void updateUI() {
        accordion.getPanes().clear();
        try {
            int i = 0;
            for (var program : ProgramsModel.getPrograms()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/program.fxml"));
                loader.setController(new ProgramItemController(new ProgramItemModel(program, i)));
                accordion.getPanes().add(loader.load());
                ++i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}