package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import static javafx.scene.input.MouseEvent.MOUSE_PRESSED;

public class Controller {

    public TextField fldHomeMin;
    public TextField fldHomeTemp;
    public TextField fldHomeMax;
    public VBox contentHome;
    public BorderPane mainLayout;
    public VBox navbar;
    public ToggleGroup navbarToggle;
    public Label labelTime;
    public GridPane temperatureGroup;
    public TextField fldTimeOn;
    public TextField fldTimeOff;

    public Controller() {

    }

    @FXML
    public void initialize() {
        fldHomeMax.prefColumnCountProperty().bind(fldHomeMax.textProperty().length());

        // Make all the Navbar buttons behave as RadioButtons
        for (var button : navbarToggle.getToggles())
            ((ToggleButton) button).addEventFilter(MOUSE_PRESSED, e -> {
                if (button.isSelected())
                    e.consume();
            });
    }
}