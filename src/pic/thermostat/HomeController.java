package pic.thermostat;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pic.thermostat.data.Time;

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
    public Label labelDeviceTime;
    public ProgressBar barTemperature;
    private PauseTransition timeUpdater;

    public HomeController() {
    }

    @FXML
    public void initialize() {
        /*
         * Add listeners to properties
         */
        HomeModel.deviceTimeProperty().addListener((obs, oldVal, newVal) -> {
            // Whenever the time changes, change the textual representation as well
            HomeModel.setDisplayDeviceTime(newVal.toString());
        });
        HomeModel.temperatureProperty().addListener((obs, oldVal, newVal) -> {
            // Whenever the temperature changes, change the textual representation as well
            HomeModel.setDisplayTemperature(HomeModel.getTextualTemperature(HomeModel.getTemperature()));
            if (HomeModel.getCurrentProgram() != null)
                updateTemperatureBar();
        });
        HomeModel.currentProgramProperty().addListener((obs, oldVal, newVal) -> {
            // Whenever the current program changes, change the content of Min, Max, Start time and End time
            fldHomeMin.setText(HomeModel.getTextualTemperature(newVal.min));
            fldHomeMax.setText(HomeModel.getTextualTemperature(newVal.max));
            updateTemperatureBar();
            fldTimeOn.setText(new Time(newVal.startDay, newVal.on).toString());
            fldTimeOff.setText(new Time(newVal.endDay, newVal.off).toString());
        });

        // Bind controls to their respective model properties
        labelDeviceTime.textProperty().bind(HomeModel.displayDeviceTimeProperty());
        fldHomeTemp.textProperty().bind(HomeModel.displayTemperatureProperty());

        // Periodically update the home screen GUI
        timeUpdater = new PauseTransition(Duration.seconds(0.1));
        timeUpdater.setOnFinished(e -> {
            updateClock();
            timeUpdater.playFromStart();
        });
        timeUpdater.play();
    }

    /*
     * Refresh the system clock
     */
    private void updateClock() {
        LocalDateTime dateTime = LocalDateTime.now();
        String hms = dateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        labelTime.setText(
                dateTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + ", "
                        + hms
        );
    }

    private void updateTemperatureBar() {
        barTemperature.setProgress((float) (HomeModel.getTemperature() - HomeModel.getCurrentProgram().min) / (HomeModel.getCurrentProgram().max - HomeModel.getCurrentProgram().min));
    }

    public void setActive(boolean active) {
        contentHome.setVisible(active);
        if (active)
            timeUpdater.play();
        else
            timeUpdater.pause();
    }
}
