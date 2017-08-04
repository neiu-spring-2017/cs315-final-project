package foodlogapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author austin
 */
public class RegisterController implements Initializable {
    
    private FoodLogDb fldb;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private TextField password2;
    @FXML private TextField name;
    @FXML private TextField age;
    @FXML private TextField weight;
    @FXML private TextField height;
    @FXML private Label errorLabel;
    @FXML private ToggleButton gender;
    @FXML private ChoiceBox activity;
    @FXML private Button submitBtn;
        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fldb = new FoodLogDb();
        submitBtn.setDefaultButton(true);
        initActivityMenu();
    }    
    
    private void initActivityMenu()
    {
        for (String a : FL_Session.ACTIVITY_LEVELS) {
            activity.getItems().add(a);
        }
    }
    
    @FXML private void handleSubmit(ActionEvent event) throws IOException
    {
        if (validateInputs()) {
            insertNewUser();
            Parent blah = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(blah);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
        }
    }
    private boolean validateInputs()
    {
        boolean valid = false;
        if (password.getText().equals(password2.getText())) {
            valid = true;
        } else {
            errorLabel.setText("Passwords must match.");
        }
        return valid;
    }
    
    private void insertNewUser()
    {
        try {
            String genderVal = gender.isSelected() ? "f" : "m";
            String[] cols = {"name", "username", "password", "age", "weight", "height", "gender", "activity"};
            String[] vals = {name.getText(), username.getText(), password.getText(), age.getText(), weight.getText(), height.getText(), genderVal, activity.getSelectionModel().getSelectedIndex() + ""};
            String[] types = {"string", "string", "string", "int", "int", "int", "string", "int"};
            fldb.insertEntry("User", cols, vals, types);
            
            String where = "WHERE username='" + username.getText() + "'";
            String[] sCols = new String[0]; //will select all
            ResultSet rs = fldb.selectFrom("User", sCols, where);
            while (rs.next()) {
                FL_Session.user_id = rs.getInt("id");
                FL_Session.height = rs.getInt("height");
                FL_Session.age = rs.getInt("age");
                FL_Session.weight = rs.getInt("weight");
                FL_Session.bmr = rs.getInt("bmr");
                FL_Session.gender = rs.getString("gender");
                FL_Session.name = rs.getString("name");
                FL_Session.activity = rs.getInt("activity");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML private void updateGenderText(ActionEvent event)
    {
        if (gender.isSelected()) {
            gender.setText("Female");
        } else {
            gender.setText("Male");
        }
    }
    
}
