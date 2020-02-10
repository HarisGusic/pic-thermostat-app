package pic.thermostat;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import pic.thermostat.comms.Communication;


public class SettingsController extends ContentController {

    public VBox contentSettings;
    public ListView listPorts;
    public Button btnRefresh;

    private SettingsModel model;
    private ChangeListener<SerialPort> portChangeListener = (obs, oldVal, newVal) -> {
        model.setSelectedPort(newVal);
        Communication.setActivePort(newVal);
    };

    public SettingsController() {
        model = new SettingsModel();
    }

    @FXML
    public void initialize() {
        listPorts.setItems(model.getPorts());
        listPorts.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            if (Communication.isBusy())
                e.consume();
        });
        listPorts.getSelectionModel().selectedItemProperty().addListener(portChangeListener);

        btnRefresh.setOnAction(e -> refresh());
    }

    @Override
    public void setActive(boolean active) {
        contentSettings.setVisible(active);
        if (active)
            refresh();
    }

    public void refresh() {
        listPorts.getSelectionModel().selectedItemProperty().removeListener(portChangeListener);
        model.refresh();
        listPorts.getSelectionModel().select(model.getSelectedPort());
        listPorts.getSelectionModel().selectedItemProperty().addListener(portChangeListener);
    }

}