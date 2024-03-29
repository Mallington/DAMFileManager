package com.company.UI_tools;

import com.company.data.Job;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class JobCellFactory extends PropertyValueFactory<Job, String> {
    /**
     * Creates a default PropertyValueFactory to extract the value from a given
     * TableView row item reflectively, using the given property name.
     *
     * @param property The name of the property with which to attempt to
     *                 reflectively extract a corresponding value for in a given object.
     */
    public JobCellFactory(String property) {
        super(property);
    }

    @Override public ObservableValue<String> call(TableColumn.CellDataFeatures<Job,String> param) {

        return null;
    }

}
