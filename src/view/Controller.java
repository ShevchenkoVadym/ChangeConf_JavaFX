package view;

import core.Main;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.ConfValue;
import org.controlsfx.dialog.Dialogs;

public class Controller {

    @FXML
    private TextField filterField;
    @FXML
    private TableView<ConfValue> table;
    @FXML
    private TableColumn<ConfValue, String> sort;
    @FXML
    private TableColumn<ConfValue, String> title;
    @FXML
    private TableColumn<ConfValue, String> value;

    private Main mainApp;

    public Controller() {
    }

    @FXML
    private void initialize() {
        sort.setCellValueFactory(cellData -> cellData.getValue().sortProperty());
        title.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        value.setCellValueFactory(cellData -> cellData.getValue().valueProperty());




    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        ObservableList data = mainApp.getData();
        FilteredList<ConfValue> filteredData = new FilteredList<>(data, p -> true);
        // 2. Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(confValue -> {
                // If filter text is empty, display all persons.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compare first name and last name of every person with filter text.
                String lowerCaseFilter = newValue.toLowerCase();
                if (confValue.getSort().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                } else if (confValue.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true; // Filter matches first name.
                }
                return false; // Does not match.
            });
        });

        SortedList<ConfValue> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);
    }

    @FXML
    private void handleDeleteMetal() {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            table.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Dialogs.create()
                    .title("No Selection")
                    .masthead("No row selected")
                    .message("Please select a row in the table.")
                    .showWarning();
        }
    }

    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new person.
     */
    @FXML
    private void handleNewMetal() {
        ConfValue tempMetal = new ConfValue();
        boolean okClicked = mainApp.showMetalEditDialog(tempMetal);
        if (okClicked) {
            mainApp.getData().add(tempMetal);
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditMetal() {
        ConfValue selectedMetal = table.getSelectionModel().getSelectedItem();
        if (selectedMetal != null) {
            mainApp.showMetalEditDialog(selectedMetal);
        } else {
            // Nothing selected.
            Dialogs.create()
                    .title("No Selection")
                    .masthead("No row selected")
                    .message("Please select a row in the table.")
                    .showWarning();
        }
    }

}
