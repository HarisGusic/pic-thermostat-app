package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class ProgramsController extends ContentController {

    public VBox contentPrograms;

    public ProgramsController() {
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void setActive(boolean active) {
        contentPrograms.setVisible(active);
    }
}