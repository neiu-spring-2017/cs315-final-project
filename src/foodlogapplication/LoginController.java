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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author austin
 */
public class LoginController implements Initializable {
    
    private FoodLogDb fldb;
    @FXML private TextField username;
    @FXML private TextField password;
    @FXML private Label errorLabel;
    @FXML private Button loginBtn;
    
    @Override public void initialize(URL url, ResourceBundle rb) {
        fldb = new FoodLogDb();
        loginBtn.setDefaultButton(true);
    } 
    
    @FXML private void handleSignup(ActionEvent event) throws IOException
    {
        Parent reg = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Scene scene = new Scene(reg);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
    
    @FXML private void handleLogin(ActionEvent event) throws IOException
    {
        if (authorize()) {
            Parent main = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(main);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
        } else {
            errorLabel.setText("Username/password combination mismatch.");
        }
    }
    
    private boolean authorize()
    {
        boolean isAuthorized = false;
        try {
            String where = "WHERE username='" + username.getText() + "'";
            String[] cols = new String[0]; //will select all
            ResultSet rs = fldb.selectFrom("User", cols, where);
            String db_pw = "";
            while (rs.next()) {
                db_pw = rs.getString("password");
                FL_Session.user_id = rs.getInt("id");
                FL_Session.height = rs.getInt("height");
                FL_Session.age = rs.getInt("age");
                FL_Session.weight = rs.getInt("weight");
                FL_Session.bmr = rs.getInt("bmr");
                FL_Session.gender = rs.getString("gender");
                FL_Session.name = rs.getString("name");
                FL_Session.activity = rs.getInt("activity");
            }
            if (password.getText().equals(db_pw)) {
                isAuthorized = true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return isAuthorized;
    }
    
}
