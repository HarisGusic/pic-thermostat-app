package pic.thermostat;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import pic.thermostat.comms.SerialWriter;

import java.util.ArrayList;
import java.util.List;

public class ProgramsController extends ContentController {

    public VBox contentPrograms;
    public Accordion accordion;
    public Button btnRead;
    public Button btnSend;

    public ProgramsController() {

    }

    @FXML
    public void initialize() {
        ProgramsModel.controller = this;
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

    }

    @Override
    public void setActive(boolean active) {
        contentPrograms.setVisible(active);
    }

    void updateUI() {
        try {
            List<TitledPane> panes = new ArrayList<>(ProgramsModel.getPrograms().size());
            int i = 0;
            for (var program : ProgramsModel.getPrograms()) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/program.fxml"));
                loader.setController(new ProgramItemController(new ProgramItemModel(program, i)));
                panes.add(loader.load());
                ++i;
            }
            accordion.getPanes().setAll(panes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}