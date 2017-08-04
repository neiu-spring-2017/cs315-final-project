package foodlogapplication;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author austin
 */
public class FL_Session {
    public static int user_id;
    public static int age;
    public static String name;
    public static int height;
    public static int weight;
    public static int bmr;
    public static String gender;
    public static int activity;
    public static final String[] ACTIVITY_LEVELS = {"sedentary", "light", "moderate", "high", "extreme"};
    public static final double[] ACTIVITY_MULTIPLIERS = {1.2, 1.375, 1.55, 1.725, 1.9};         //Benedict Harris activity multipliers
    public static final Map<String, Integer> RECOMMENDED_DV;
    static {
        RECOMMENDED_DV = new HashMap<>();
        RECOMMENDED_DV.put("cal", 2000);
        RECOMMENDED_DV.put("protein", 50);
        RECOMMENDED_DV.put("fat", 65);
        RECOMMENDED_DV.put("carb", 300);
        RECOMMENDED_DV.put("fiber", 25);
        RECOMMENDED_DV.put("sugar", 0);
        RECOMMENDED_DV.put("calcium", 1000);
        RECOMMENDED_DV.put("iron", 18);
        RECOMMENDED_DV.put("potassium", 3500);
        RECOMMENDED_DV.put("sodium", 2400);
        RECOMMENDED_DV.put("vit_c", 60);
        RECOMMENDED_DV.put("vit_d", 400);
        RECOMMENDED_DV.put("s_fat", 20);
        RECOMMENDED_DV.put("cholesterol", 300);
    }
}
