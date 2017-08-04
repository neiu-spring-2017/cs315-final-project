package foodlogapplication;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author austin
 */
public class DailyLog {
    private String date;
    private int food_id;
    private int qty;
    private FoodLogDb fldb;
    private String name;
    private int cal;
    private double protein;
    private double fat;
    private double carb;
    private double fiber;
    private double sugar;
    private double calcium;
    private double iron;
    private double potassium;
    private double sodium;
    private double vit_c;
    private double vit_d;
    private double s_fat;
    private double cholesterol;
    private String fullDate;
    
    
    public DailyLog(String date, int food_id, int qty) {
        this.fullDate = date;
        this.date = date.split(" ")[0];
        this.food_id = food_id;
        this.qty = qty;
        fldb = new FoodLogDb();
        initValues();
    }
    
    private void initValues()
    {
        try {
           ResultSet rs = fldb.selectView("FoodValues", this.food_id);
           while (rs.next()) {
               this.name = rs.getString("name");
               this.cal = rs.getInt("cal");
               this.protein = rs.getDouble("protein");
               this.fat = rs.getDouble("fat");
               this.carb = rs.getDouble("carb");
               this.fiber = rs.getDouble("fiber");
               this.sugar = rs.getDouble("sugar");
               this.calcium = rs.getDouble("calcium");
               this.iron = rs.getDouble("iron");
               this.potassium = rs.getDouble("potassium");
               this.sodium = rs.getDouble("sodium");
               this.vit_c = rs.getDouble("vit_c");
               this.vit_d = rs.getDouble("vit_d");
               this.s_fat = rs.getDouble("s_fat");
               this.cholesterol = rs.getDouble("cholesterol");
           }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String getDate() { return this.date; }
    public int getQty() { return this.qty; }
    public int getFoodId() { return this.food_id; }
    public String getName() { return this.name; }
    public int getCal() { return this.cal; }
    public double getProtein() { return this.protein; }
    public double getFat() { return this.fat; }
    public double getCarb() { return this.carb; }
    public double getFiber() { return this.fiber; }
    public double getSugar() { return this.sugar; }
    public double getCalcium() { return this.calcium; }
    public double getVitC() { return this.vit_c; }
    public double getVitD() { return this.vit_d; }
    public double getSFat() { return this.s_fat; }
    public double getCholesterol() { return this.cholesterol; }
    public double getPotassium() { return this.potassium; }
    public double getSodium() { return this.sodium; }
    public double getIron() { return this.iron; }
    public String getFullDate() { return this.fullDate; }
}
