package foodlogapplication;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author austin
 */
public class NewFoodController implements Initializable {

    @FXML private TextField name;
    @FXML private TextField protein;
    @FXML private TextField fat;
    @FXML private TextField carb;
    @FXML private TextField fiber;
    @FXML private TextField sugar;
    @FXML private TextField calcium;
    @FXML private TextField iron;
    @FXML private TextField portion_size;
    @FXML private TextField potassium;
    @FXML private TextField sodium;
    @FXML private TextField vit_c;
    @FXML private TextField vit_d;
    @FXML private TextField s_fat;
    @FXML private TextField cholesterol;
    @FXML private TextField cal;
    private FoodLogDb fldb;
    private int food_id;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fldb = new FoodLogDb();
    }
    
    @FXML private void addNewFood(ActionEvent event) throws IOException
    {
        if (validate()) {
            insertFoodToDb();
            Parent main = FXMLLoader.load(getClass().getResource("Main.fxml"));
            Scene scene = new Scene(main);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setScene(scene);
            appStage.show();
        }
    }
    
    @FXML private void showMain(ActionEvent event) throws IOException
    {
        insertFoodToDb();
        Parent main = FXMLLoader.load(getClass().getResource("Main.fxml"));
        Scene scene = new Scene(main);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
    
    @FXML private void showUsdaLink(ActionEvent event) throws URISyntaxException, IOException
    {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("https://ndb.nal.usda.gov/ndb/"));
        }
    }
    
    private boolean validate()
    {
        return true;
    }
    
    private void insertFoodToDb()
    {
        try {
            String[] cols = {"name", "portion_size"};
            String[] vals = {name.getText(), portion_size.getText()};
            String[] types = {"string", "double"};
            food_id = fldb.insertEntry("Food", cols, vals, types);
            insertFoodValues();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void insertFoodValues()
    {
        try {
            String[] cols = {"food_id", "protein", "fat", "carb", "fiber", "sugar", "calcium", "iron", "potassium", "sodium", 
                            "vit_c", "vit_d", "s_fat", "cholesterol", "cal"};
            String[] vals = {food_id + "", protein.getText(), fat.getText(), carb.getText(), fiber.getText(), sugar.getText(),  calcium.getText(), 
                            iron.getText(), potassium.getText(), sodium.getText(), vit_c.getText(), 
                            vit_d.getText(), s_fat.getText(), cholesterol.getText(), cal.getText()};
            String[] types = {"int", "double", "double", "double", "double", "double", "double", "double", "double",
                            "double", "double", "double", "double", "double", "double"};
            fldb.insertEntry("DailyValue", cols, vals, types);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
