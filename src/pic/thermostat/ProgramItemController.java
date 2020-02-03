package pic.thermostat;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import pic.thermostat.data.Program;

import java.text.ParseException;

public class ProgramItemController {

    public TextField fldEnd;
    public TextField fldStart;
    public TextField fldMin;
    public TextField fldMax;
    public Button btnEdit;
    public AnchorPane content;
    public TitledPane title;

    private boolean isEditButton = true;

    private ProgramItemModel model;

    public ProgramItemController(ProgramItemModel model) {
        this.model = model;
    }

    @FXML
    public void initialize() {

        updateUI();

        btnEdit.setOnAction(e -> {
            if (isEditButton) {
                for (var child : content.getChildren())
                    if (child instanceof TextField)
                        ((TextField) child).setEditable(true);
                isEditButton = false;
                btnEdit.setText("Save");
            } else {
                for (var child : content.getChildren())
                    if (child instanceof TextField)
                        ((TextField) child).setEditable(false);
                isEditButton = true;
                btnEdit.setText("Edit");
                save();
            }
        });

        model.programProperty().addListener((obs, oldVal, newVal) -> {
            title.setText(newVal.start.toString() + " - " + newVal.end.toString());
        });
    }

    private void save() {
        try {
            Program program = new Program();
            program.start = ProgramItemModel.parseTime(fldStart.getText());
            program.end = ProgramItemModel.parseTime(fldEnd.getText());
            program.min = ProgramItemModel.parseTemperature(fldMin.getText());
            program.max = ProgramItemModel.parseTemperature(fldMax.getText());
            if (!model.getProgram().equals(program))
                ProgramsModel.hasChanged = true;
            model.setProgram(program);
        } catch (ParseException e) {
            //TODO notify the user
            e.printStackTrace();
        }
    }

    private void updateUI() {
        fldStart.setText(model.getProgram().start.toString());
        fldEnd.setText(model.getProgram().end.toString());
        title.setText(model.getProgram().start.toString() + " - " + model.getProgram().end.toString());
        fldMin.setText(HomeModel.getTextualTemperature(model.getProgram().min));
        fldMax.setText(HomeModel.getTextualTemperature(model.getProgram().max));
    }
}
