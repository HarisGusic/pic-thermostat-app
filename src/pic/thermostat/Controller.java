package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import pic.thermostat.comms.SerialReader;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class Controller {

    public BorderPane mainLayout;

    public VBox navbar;
    public ToggleGroup navbarToggle;

    // Navbar buttons
    public ToggleButton btnHome;
    public ToggleButton btnPrograms;
    public ToggleButton btnSettings;

    // Content containers
    public VBox contentHome;
    public VBox contentPrograms;
    public VBox contentSettings;

    @FXML
    private HomeController contentHomeController;
    @FXML
    private ProgramsController contentProgramsController;

    @FXML
    private SettingsController contentSettingsController;

    public Controller() {

    }

    @FXML
    public void initialize() {
        // Make all the Navbar buttons behave as RadioButtons
        for (var button : navbarToggle.getToggles())
            ((ToggleButton) button).addEventFilter(MOUSE_PRESSED, e -> {
                if (button.isSelected())
                    e.consume();
                SerialReader.dropAll();
            });

        btnHome.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentHomeController.setActive(newVal);
            mainLayout.setCenter(contentHome);
        });
        btnPrograms.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentProgramsController.setActive(newVal);
            mainLayout.setCenter(contentPrograms);
        });
        btnSettings.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentSettingsController.setActive(newVal);
            mainLayout.setCenter(contentSettings);
        });
    }
}