package pic.thermostat;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import pic.thermostat.data.Program;

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
        btnRead.setOnAction(e -> {
            ProgramsModel.populate();
        });
        btnSend.setOnAction(e -> {
            if (ProgramsModel.hasChanged) {
                //TODO send via SerialWriter
                ProgramsModel.hasChanged = false;
            } else
                new Alert(Alert.AlertType.INFORMATION, "No changes have been made").showAndWait();
        });

        ProgramsModel.getPrograms().addListener((ListChangeListener<Program>) c -> {
            try {
                List<TitledPane> panes = new ArrayList<>(ProgramsModel.getPrograms().size());
                for (var program : ProgramsModel.getPrograms()) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/program.fxml"));
                    loader.setController(new ProgramItemController(new ProgramItemModel(program)));
                    panes.add(loader.load());
                }
                accordion.getPanes().setAll(panes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setActive(boolean active) {
        contentPrograms.setVisible(active);
    }
}