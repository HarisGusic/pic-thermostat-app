package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class Controller {

    public BorderPane mainLayout;

    public VBox navbar;
    public ToggleGroup navbarToggle;

    // Navbar buttons
    public ToggleButton btnHome;
    public ToggleButton btnReadPrograms;
    public ToggleButton btnEditPrograms;

    // Content containers
    public VBox contentHome;
    public VBox contentReadPrograms;
    public VBox contentEditPrograms;

    @FXML
    private HomeController contentHomeController;
    @FXML
    private ReadProgramsController contentReadProgramsController;
    @FXML
    private EditProgramsController contentEditProgramsController;

    public Controller() {

    }

    @FXML
    public void initialize() {
        // Make all the Navbar buttons behave as RadioButtons
        for (var button : navbarToggle.getToggles())
            ((ToggleButton) button).addEventFilter(MOUSE_PRESSED, e -> {
                if (button.isSelected())
                    e.consume();
            });

        btnHome.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentHomeController.setActive(newVal);
            mainLayout.setCenter(contentHome);
        });
        btnReadPrograms.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentReadProgramsController.setActive(newVal);
            mainLayout.setCenter(contentReadPrograms);
        });
        btnEditPrograms.selectedProperty().addListener((obs, oldVal, newVal) -> {
            contentEditProgramsController.setActive(newVal);
            mainLayout.setCenter(contentEditPrograms);
        });
    }
}