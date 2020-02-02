package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class SettingsController extends ContentController {

    public VBox contentSettings;

    public SettingsController() {
    }

    @FXML
    public void initialize() {

    }

    @Override
    public void setActive(boolean active) {
        contentSettings.setVisible(active);
    }

}