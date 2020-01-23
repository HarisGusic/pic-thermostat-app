package pic.thermostat;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class HomeController extends ContentController {

    public VBox contentHome;
    public Label labelTime;
    public GridPane temperatureGroup;
    public TextField fldHomeTemp;
    public TextField fldHomeMin;
    public TextField fldHomeMax;
    public TextField fldTimeOn;
    public TextField fldTimeOff;
    private PauseTransition timeUpdater;

    public HomeController() {
    }

    @FXML
    public void initialize() {
        // Periodically update the home screen GUI
        timeUpdater = new PauseTransition(Duration.seconds(0.1));
        timeUpdater.setOnFinished(e -> {
            updateTime();
            timeUpdater.playFromStart();
        });
        timeUpdater.play();
    }

    /*
     * Refresh the system clock
     */
    private void updateTime() {
        LocalDateTime dateTime = LocalDateTime.now();
        String hms = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        labelTime.setText(
                dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + ", "
                        + hms
        );
    }

    public void setActive(boolean active) {
        contentHome.setVisible(active);
        if (active)
            timeUpdater.play();
        else
            timeUpdater.pause();
    }
}
