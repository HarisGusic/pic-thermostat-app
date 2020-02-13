package pic.thermostat;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pic.thermostat.comms.Communication;
import pic.thermostat.comms.SerialReader;
import pic.thermostat.comms.SerialWriter;
import pic.thermostat.data.Data;
import pic.thermostat.data.Time;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeController extends ContentController {

    public static LocalDateTime currentTime = LocalDateTime.of(2020, 2, 3, 0, 0);
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
    public Button btnDownloadTime;
    private PauseTransition timeUpdater;

    public HomeController() {
    }

    @FXML
    public void initialize() {
        invalidateCurrentProgram();

        btnDownloadTime.addEventHandler(MouseEvent.MOUSE_ENTERED,
                e -> btnDownloadTime.getGraphic().setStyle("-fx-fill: -accent;"));
        btnDownloadTime.addEventHandler(MouseEvent.MOUSE_EXITED, e -> btnDownloadTime.getGraphic().setStyle("-fx-fill: -text;"));

        /*
         * Add listeners to properties
         */

        HomeModel.deviceTimeProperty().addListener((obs, oldVal, newVal) -> {
            // Whenever the time changes, change the textual representation as well
            HomeModel.setDisplayDeviceTime(newVal.toString());
        });
        HomeModel.temperatureProperty().addListener((obs, oldVal, newVal) -> {
            // Whenever the temperature changes, change the textual representation as well
            HomeModel.setDisplayTemperature(Data.getTemperatureText());
            if (HomeModel.getCurrentProgram() != null)
                updateTemperatureBar();
        });
        HomeModel.currentProgramProperty().addListener((obs, oldVal, newVal) -> {
            invalidateCurrentProgram();
        });

        // Bind controls to their respective model properties
        labelDeviceTime.textProperty().bind(HomeModel.displayDeviceTimeProperty());
        fldHomeTemp.textProperty().bind(HomeModel.displayTemperatureProperty());
        btnDownloadTime.setOnAction(e -> {
            SerialWriter.sendTime(new Time((byte) ((currentTime.getDayOfWeek().getValue() + 6) % 7), (short) (currentTime.getHour() * 60 + currentTime.getMinute())));
            SerialReader.readTime();
        });

        // Periodically update the home screen GUI
        timeUpdater = new PauseTransition(Duration.seconds(0.1));
        timeUpdater.setOnFinished(e -> {
            updateClock();
            timeUpdater.playFromStart();
        });
        setActive(true);
    }

    /*
     * Refresh the system clock
     */
    private void updateClock() {
        currentTime = currentTime.plusSeconds(6); // FIXME only when debugging
        // currentTime = LocalDateTime.now(); // TODO change after debugging
        String hms = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        labelTime.setText(
                currentTime.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
                        + ", "
                        + hms
        );
    }

    private void updateTemperatureBar() {
        barTemperature.setProgress((float) (HomeModel.getTemperature() - HomeModel.getCurrentProgram().min) / (HomeModel.getCurrentProgram().max - HomeModel.getCurrentProgram().min));
        if (barTemperature.getProgress() < 0)
            barTemperature.setProgress(0);
    }

    public void setActive(boolean active) {
        if (active) {
            // Initiate periodic data acquisition
            Communication.timer = new Timer();
            Communication.timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Communication.update();
                }
            }, 0, 1000);
        } else {
            Communication.timer.cancel();
            Communication.timer = null;
        }
        contentHome.setVisible(active);

        if (active)
            timeUpdater.play();
        else
            timeUpdater.pause();
    }

    private void invalidateCurrentProgram() {
        fldHomeMin.setDisable(HomeModel.getCurrentProgram() == null);
        fldHomeMax.setDisable(HomeModel.getCurrentProgram() == null);
        fldTimeOn.setDisable(HomeModel.getCurrentProgram() == null);
        fldTimeOff.setDisable(HomeModel.getCurrentProgram() == null);
        barTemperature.setDisable(HomeModel.getCurrentProgram() == null);
        if (HomeModel.getCurrentProgram() != null) {
            // Whenever the current program changes, change the content of Min, Max, Start time and End time
            fldHomeMin.setText(Data.getTemperatureText(HomeModel.getCurrentProgram().min));
            fldHomeMax.setText(Data.getTemperatureText(HomeModel.getCurrentProgram().max));
            updateTemperatureBar();
            fldTimeOn.setText(new Time(HomeModel.getCurrentProgram().start.day, HomeModel.getCurrentProgram().start.timeOfDay).toString());
            fldTimeOff.setText(new Time(HomeModel.getCurrentProgram().end.day, HomeModel.getCurrentProgram().end.timeOfDay).toString());
        }
    }
}
