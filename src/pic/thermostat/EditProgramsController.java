package pic.thermostat;

import javafx.scene.layout.VBox;

public class EditProgramsController extends ContentController {

    public VBox contentEditPrograms;

    @Override
    public void setActive(boolean active) {
        contentEditPrograms.setVisible(active);
    }
}
