package foodlogapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author austin
 */
public class MainController implements Initializable {

    private FoodLogDb fldb;
    private int intake, aBmr;
    @FXML private TextField age, height, weight, newQty;
    @FXML private Label bmr, welcome, tip;
    @FXML private Label protein, fat, carb, fiber, sugar, calcium, vit_c, vit_d, s_fat, cholesterol, potassium, sodium, totalCal, iron;
    @FXML private TableColumn date, food, qty;
    @FXML private TableView dailyLogTable;
    @FXML private PieChart intake_chart;
    @FXML private ChoiceBox foodMenu, logMenu, activity;
    @FXML private MenuBar mainMenuBar;
    private DailyLog[] dlArr;
    private String dateToday, logDate;
    private double totalProtein, totalFat, totalCarb, totalFiber, totalSugar, totalCalcium;
    private double totalIron, totalPotassium, totalSodium, totalVitC, totalVitD, totalSFat, totalCholesterol;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        dateToday = ts.toString().split(" ")[0];
        fldb = new FoodLogDb();
        initUser();
        initFoodMenu();
        initLogs();
        initPieChart();
        initLogMenu();
        initActivityMenu();
        initTip();
    }
    
    private void initTip()
    {
        tip.setText(getTipString());
    }
    
    private void initActivityMenu()
    {
        activity.getItems().clear();
        for (String a : FL_Session.ACTIVITY_LEVELS) {
            activity.getItems().add(a);
        }
        activity.getSelectionModel().select(FL_Session.activity);
    }
    
    private void initLogMenu()
    {
        logMenu.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue ov, Number value, Number new_value) {
                populateLogTable(logMenu.getItems().get(new_value.intValue()).toString());
            }
        });
    }
    
    private void initFoodMenu()
    {
        foodMenu.getItems().clear();
        String[] foods = getFoodsFromDb();
        for (String f : foods) {
            foodMenu.getItems().add(f);
        }
    }
    
    private String[] getFoodsFromDb()
    {
        String[] retFoods = {};
        try {
            ResultSet rs = fldb.selectAll("Food");
            rs.last();
            int n = rs.getRow();
            retFoods = new String[n];
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {
                retFoods[i] = rs.getString("name");
                i++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return retFoods;
    }
    
    private void initUser()
    {
        age.setText(FL_Session.age + "");
        height.setText(FL_Session.height + "");
        weight.setText(FL_Session.weight + "");
        bmr.setText(getBmrString());
        welcome.setText("Welcome, " + FL_Session.name);
    }
    
    private void calcAdjustedBmr()
    {
        double aMult =  FL_Session.ACTIVITY_MULTIPLIERS[FL_Session.activity];
        aBmr = (int) (aMult * FL_Session.bmr);
    }
    
    private void initPieChart()
    {
        initTotals();
        int displayIntake = intake;
        calcAdjustedBmr();
        if (intake > aBmr) {
            displayIntake = aBmr;
        }
        String consumed = intake / aBmr > 1 ? "Over-consumed" : "Consumed";
        String remaining = intake / aBmr > 1 ? "" : "Remaining";
        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                new PieChart.Data(consumed, displayIntake),
                new PieChart.Data(remaining, aBmr - displayIntake));
        intake_chart.setData(pieChartData);
    }
    
    private void initTotals()
    {
        intake = 0;
        totalProtein = totalFat = totalCarb = totalFiber = totalSugar = totalCalcium = totalIron = 0;
        totalSodium = totalVitC = totalVitD = totalSFat = totalPotassium = totalCholesterol = 0;
        for (Object d : dailyLogTable.getItems()) {
            DailyLog dl = (DailyLog) d;
            if (logDate == null) {
                logDate = dateToday;
            }
            if (dl.getDate().equals(logDate)) {
                intake += dl.getCal() * dl.getQty();
                totalProtein += dl.getProtein() * dl.getQty();
                totalFat += dl.getFat() * dl.getQty();
                totalCarb += dl.getCarb() * dl.getQty();
                totalFiber += dl.getFiber() * dl.getQty();
                totalSugar += dl.getSugar() * dl.getQty();
                totalCalcium += dl.getCalcium() * dl.getQty();
                totalIron += dl.getIron() * dl.getQty();
                totalSodium += dl.getSodium() * dl.getQty();
                totalVitC += dl.getVitC() * dl.getQty();
                totalVitD += dl.getVitD() * dl.getQty();
                totalSFat += dl.getSFat() * dl.getQty();
                totalPotassium += dl.getPotassium() * dl.getQty();
                totalCholesterol += dl.getCholesterol() * dl.getQty();
            }
        }
        updateTotalLabels();
    }
    
    private void updateTotalLabels()
    {
        DecimalFormat df = new DecimalFormat("#.00");
        protein.setText(df.format(totalProtein) + "g");
        fat.setText(df.format(totalFat) + "g");
        carb.setText(df.format(totalCarb) + "g");
        fiber.setText(df.format(totalFiber) + "g");
        sugar.setText(df.format(totalSugar) + "g");
        calcium.setText(df.format(totalCalcium) + "mg");
        vit_c.setText(df.format(totalVitC) + "mg");
        vit_d.setText(df.format(totalVitD) + "IU");
        s_fat.setText(df.format(totalSFat) + "g");
        cholesterol.setText(df.format(totalCholesterol) + "mg");
        potassium.setText(df.format(totalPotassium) + "mg");
        sodium.setText(df.format(totalSodium) + "mg");
        iron.setText(df.format(totalIron) + "mg");
        totalCal.setText(intake + "");
    }
    
    private void initLogs()
    {
        initDailyLogs();
        initCols();
    }
    
    private void initDailyLogs()
    {
        try {
            String where = "WHERE user_id=" + FL_Session.user_id;
            String[] cols = {"created_at", "qty", "food_id"};
            ResultSet rs = fldb.selectFrom("DailyLog", cols, where);
            
            rs.last();
            int n = rs.getRow();
            dlArr = new DailyLog[n];
            rs.beforeFirst();
            int i = 0;
            while (rs.next()) {
                String tDate = rs.getString("created_at").split(" ")[0];
                DailyLog dl = new DailyLog(tDate, 
                            rs.getInt("food_id"), 
                            rs.getInt("qty"));
                dlArr[i] = dl;
                if (!logMenu.getItems().contains(tDate)) {
                    logMenu.getItems().add(tDate);
                }
                i++;
            }
            populateLogTable(dateToday);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    
    private void updateLogTable(ActionEvent event)
    {
        populateLogTable(logMenu.getSelectionModel().getSelectedItem().toString());
    }
    
    private void populateLogTable(String lDate)
    {
        logDate = lDate;
        dailyLogTable.getItems().clear();
        for (DailyLog dl : dlArr) {
            if (dl.getDate().equals(logDate)) {
                dailyLogTable.getItems().add(dl);
            } 
        }
        initTotals();
        reloadUser();
    }
    
    private void initCols()
    {
        date.setCellValueFactory(new Callback<CellDataFeatures<DailyLog, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(CellDataFeatures<DailyLog, String> dl) {
                return new ReadOnlyObjectWrapper(dl.getValue().getDate());
            }
         });
        food.setCellValueFactory(new Callback<CellDataFeatures<DailyLog, String>, ObservableValue<String>>() {
            @Override public ObservableValue<String> call(CellDataFeatures<DailyLog, String> dl) {
                return new ReadOnlyObjectWrapper(dl.getValue().getName());
            }
         });
        qty.setCellValueFactory(new Callback<CellDataFeatures<DailyLog, Integer>, ObservableValue<Integer>>() {
            @Override public ObservableValue<Integer> call(CellDataFeatures<DailyLog, Integer> dl) {
                return new ReadOnlyObjectWrapper(dl.getValue().getQty());
            }
         });
        
    }
    
    private String generateTipString(Double total, String nutrient)
    {
        String generatedTip;
        String niceName = nutrient;
        //For some of the awkward names, switch to nicenames
        switch (nutrient) {
            case "carb":
                niceName = "carbohydrates";
                break;
            case "vit_c":
                niceName = "vitamin C";
                break;
            case "vit_d":
                niceName = "vitamin D";
                break;
            case "s_fat":
                niceName = "saturated fat";
                break;
        }
        double multiplier = aBmr / FL_Session.RECOMMENDED_DV.get("cal"); //adjusted bmr over recommended daily value to get proportional multiplier
        if (total <= FL_Session.RECOMMENDED_DV.get(nutrient) * multiplier * .5) {
            generatedTip = "Eat more " + niceName + "!";
        } else if (total > FL_Session.RECOMMENDED_DV.get(nutrient) * multiplier * .9) {
            generatedTip = "You've consumed a sufficient amount of " + niceName + ".";
        } else {
            generatedTip = "Don't forget to eat some " + niceName + " before you go to bed.";
        }
        return generatedTip;
    }
    
    //Generate tips based on recommended daily values
    private String getTipString()
    {
        String genTip;
        Random rand = new Random(System.currentTimeMillis());
        switch (rand.nextInt(13)) {
            case 0:
                if (intake > aBmr * 0.9) {
                    genTip = "You have consumed 90% of your recommended " + aBmr + " cal";
                } else {
                    genTip = "You have consumed " + intake + "/" + aBmr + " cal";
                }
                break;
            case 1:
                genTip = generateTipString(totalProtein, "protein");
                break;
            case 2:
                genTip = generateTipString(totalFat, "fat");
                break;
            case 3:
                genTip = generateTipString(totalCarb, "carb");
                break;
            case 4:
                genTip = generateTipString(totalFiber, "fiber");
                break;
            case 5:
                genTip = generateTipString(totalSugar, "sugar");
                break;
            case 6:
                genTip = generateTipString(totalCalcium, "calcium");
                break;
            case 7:
                genTip = generateTipString(totalIron, "iron");
                break;
            case 8:
                genTip = generateTipString(totalSodium, "sodium");
                break;
            case 9:
                genTip = generateTipString(totalVitC, "vit_c");
                break;
            case 10:
                genTip = generateTipString(totalVitD, "vit_d");
                break;
            case 11:
                genTip = generateTipString(totalSFat, "s_fat");
                break;
            case 12:
                genTip = generateTipString(totalPotassium, "potassium");
                break;
            case 13:
                genTip = generateTipString(totalCholesterol, "cholesterol");
                break;
            default:
                genTip = "You have consumed " + intake + " out of recommended " + aBmr;
        }
        return genTip;
    }
    
    private String getBmrString()
    {
        calculateBmr();
        
        return aBmr + " cal";
    }
    
    private void calculateBmr()
    {
        double cBmr;
        //do calculations
        if (FL_Session.gender.equals("m")) {
            cBmr = 66;
            cBmr += FL_Session.height * 12.7  + 
                    FL_Session.weight * 6.23 - 
                    FL_Session.age * 6.8;
        } else {
            cBmr = 655;
            cBmr += FL_Session.height * 4.7  + 
                    FL_Session.weight * 4.35 - 
                    FL_Session.age * 4.7;
        }
        FL_Session.bmr = (int) cBmr;
        calcAdjustedBmr();
    }
    
    @FXML private void addFoodToLog(ActionEvent event)
    {
        String nFood = foodMenu.getSelectionModel().getSelectedItem().toString();
        int nQty = Integer.parseInt(newQty.getText());
        int food_id = getFoodId(nFood);
        if (food_id > 0) {
            addFood(food_id, nQty);
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            dailyLogTable.getItems().add(new DailyLog(ts.toString(), 
                                                food_id, 
                                                nQty));
            initPieChart();
        }
    }
    
    private int getFoodId(String nFood)
    {
        int retId = -1;
        try {
            String[] cols = {"id"};
            String clause = "WHERE name='" + nFood + "'";
            ResultSet rs = fldb.selectFrom("Food", cols, clause);
            if (rs.next()) {
                retId = rs.getInt("id");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return retId;
    }
    
    private void addFood(int fid, int q)
    {
        try {
            String[] cols = {"user_id", "food_id", "qty"};
            String[] vals = {FL_Session.user_id + "", fid + "", q + ""};
            String[] types = {"int", "int", "int"};
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            String tDate = ts.toString().split(" ")[0];
            ResultSet rs = fldb.selectFrom("DailyLog", cols, "WHERE food_id=" + fid + " AND created_at LIKE('%" + tDate + "%')");
            if (rs.next()) {
                //if the food_id is found for this date already, update the quantity
                int quant = rs.getInt("qty");
                quant += q;
                String[] upCols = {"qty"};
                String[] upVals = {quant + ""};
                String[] upTypes = {"string"};
                fldb.updateEntry("DailyLog", upCols, upVals, upTypes, " WHERE food_id=" + fid);
            } else {
                //otherwise insert the new entry
                fldb.insertEntry("DailyLog", cols, vals, types);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML private void showAddFood(ActionEvent event) throws IOException
    {
        Parent main = FXMLLoader.load(getClass().getResource("NewFood.fxml"));
        Scene scene = new Scene(main);
        Stage appStage = (Stage) mainMenuBar.getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
    
    @FXML private void handleLogout(ActionEvent event) throws IOException
    {
        Parent main = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene scene = new Scene(main);
        Stage appStage = (Stage) mainMenuBar.getScene().getWindow();
        appStage.setScene(scene);
        appStage.show();
    }
    
    @FXML private void refreshLogs(ActionEvent event)
    {
        initDailyLogs();
    }
    
    @FXML private void deleteUser(ActionEvent event) throws IOException
    {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you with to permanently delete this user account and all associated information?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (fldb.deleteWhere("DailyLog", "WHERE user_id=" + FL_Session.user_id) <= 0) {
                    System.out.println("Error deleting associated data.");
                }
                fldb.deleteWhere("User", "WHERE id=" + FL_Session.user_id);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } 
        }
        handleLogout(event);
    }
    
    @FXML private void updateUser(ActionEvent event)
    {
        try {
            String[] upCols = {"age", "height", "weight", "activity"};
            String[] upVals = {age.getText(), height.getText(), weight.getText(), activity.getSelectionModel().getSelectedIndex() + ""};
            String[] upTypes = {"int", "int", "int", "int"};
            fldb.updateEntry("User", upCols, upVals, upTypes, " WHERE id=" + FL_Session.user_id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        reloadUser();
    }
    
    private void reloadUser()
    {
        boolean isAuthorized = false;
        try {
            String where = "WHERE id='" + FL_Session.user_id + "'";
            String[] cols = new String[0]; //will select all
            ResultSet rs = fldb.selectFrom("User", cols, where);
            if (rs.next()) {
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
        initUser();
        initActivityMenu();
        initPieChart();
        initTip();
    }
    
    @FXML private void deleteSelectedLog(ActionEvent event)
    {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you with to permanently delete this log information?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                int sLogId = ((DailyLog) dailyLogTable.getSelectionModel().getSelectedItem()).getFoodId();
                String sLogDate = ((DailyLog) dailyLogTable.getSelectionModel().getSelectedItem()).getDate();
                if (fldb.deleteWhere("DailyLog", "WHERE food_id=" + sLogId + " AND user_id=" + FL_Session.user_id + " AND created_at LIKE('%" + sLogDate + "%')") <= 0) {
                    System.out.println("Error while deleting DailyLog information.");
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } 
        }
    }
    
    @FXML private void deleteSelectedFood(ActionEvent event)
    {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you with to permanently delete this food?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                String nFood = foodMenu.getSelectionModel().getSelectedItem().toString();
                int sFoodId = getFoodId(nFood);
                if (sFoodId > 0) {
                    if (fldb.deleteWhere("DailyLog", "WHERE food_id=" + sFoodId) <= 0) {
                        System.out.println("Unable to delete associated data.");
                    }
                    if (fldb.deleteWhere("DailyValue", "WHERE food_id=" + sFoodId) <= 0) {
                        System.out.println("Unable to delete associated data.");
                    }
                    fldb.deleteWhere("Food", "WHERE id=" + sFoodId);
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } 
        }
        initFoodMenu();
    }
}
