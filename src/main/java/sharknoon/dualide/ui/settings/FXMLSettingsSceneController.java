package sharknoon.dualide.ui.settings;


import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.controlsfx.control.PropertySheet;

/**
 * FXML Controller class
 *
 * @author Josua Frank
 */
public class FXMLSettingsSceneController implements Initializable {

    @FXML
    PropertySheet propertySheet;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        propertySheet.getItems().add(new PropertySheet.Item() {
            @Override
            public Class<?> getType() {
                return String.class;
            }

            @Override
            public String getCategory() {
                return "Text";
            }

            @Override
            public String getName() {
                return "Textname";
            }

            @Override
            public String getDescription() {
                return "description bla 123";
            }

            String value = "42";
            
            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public void setValue(Object value) {
                this.value = value.toString();
            }

            @Override
            public Optional<ObservableValue<? extends Object>> getObservableValue() {
                return Optional.empty();
            }
        });
    }    
    
}
