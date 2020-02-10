package pic.thermostat;

import com.fazecast.jSerialComm.SerialPort;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pic.thermostat.comms.Communication;

import java.util.Comparator;
import java.util.List;

public class SettingsModel {

    private ObservableList<SerialPort> ports = FXCollections.observableArrayList();
    private SimpleObjectProperty<SerialPort> selectedPort;

    public SettingsModel() {
    }

    public ObservableList<SerialPort> getPorts() {
        return ports;
    }

    public void setPorts(ObservableList<SerialPort> ports) {
        this.ports = ports;
    }

    public SerialPort getSelectedPort() {
        return selectedPort.get();
    }

    public void setSelectedPort(SerialPort selectedPort) {
        this.selectedPort.set(selectedPort);
    }

    public SimpleObjectProperty<SerialPort> selectedPortProperty() {
        return selectedPort;
    }

    public void refresh() {
        List<SerialPort> p = Communication.getUnusedSerialPorts();
        if (Communication.getActivePort() != null)
            p.add(Communication.getActivePort());
        p.sort(Comparator.comparing(SerialPort::toString));
        ports.setAll(p);
        selectedPort = new SimpleObjectProperty<>(Communication.getActivePort());
    }
}
