package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ReadProgramsController extends ContentController {

    public VBox contentReadPrograms;

    public ReadProgramsController() {
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void setActive(boolean active) {
        contentReadPrograms.setVisible(active);
    }
}