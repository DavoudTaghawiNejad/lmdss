package tools;

import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import static tools.MyComparators.*;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Random;


public class Assumptions
{
    public final long seed;
    public final int time_after_policy;
    public final int setup_period_1;
    public final int setup_period_2;
    public final int time_before_policy;
    public final int num_firms;
    public final int num_saudis;
    public final int num_expats;
    public final double productivity_mean_saudi;
    public final double productivity_mean_expat;
    public final double productivity_std_saudi;
    public final double productivity_std_expat;
    public final double sector_spending;
    public final double love_for_variety;
    public final double reservation_wage_mean_saudi;
    public final double reservation_wage_std_saudi;
    public final double reservation_wage_mean_expat;
    public final double reservation_wage_std_expat;
    public final double price_step_increase;
    public final double price_step_decrease;
    public final double planned_production_step_increase;
    public final double planned_production_step_decrease;
    public final double initial_net_worth_mean;
    public final double initial_net_worth_std;
    public final double reapplication_probability_saudi;
    public final double reapplication_probability_expat;
    public final double minimum_mark_up;
    public final double days_pay_must_be_available;
    public final double required_roi;
    public final double percent_distribute;
    public final double production_function_exponent;
    public final double initial_wage_offer_saudi;
    public final double initial_wage_offer_expat;
    public final double wage_step_saudi;
    public final double wage_step_expat;

    public Assumptions()
    {
        seed = 0L;
        time_after_policy = 2000;
        setup_period_1 = 1000;
        setup_period_2 = 1000;
        num_firms = 100;
        num_saudis = 3800;
        num_expats = 7000;
        productivity_mean_saudi = 6854.24;
        productivity_mean_expat = 6854.24;
        productivity_std_saudi = 1000;
        productivity_std_expat = 1000;
        reservation_wage_mean_saudi  = 3137.39;
        reservation_wage_mean_expat = 0;
        reservation_wage_std_expat = 1000;
        reservation_wage_std_saudi = 1000;
        sector_spending = 10000000000.0;
        love_for_variety = 0.5;
        time_before_policy = 1500;
        price_step_increase = 2.0 / 356.0;
        price_step_decrease = 2.0 / 356.0;
        initial_net_worth_mean = 10000;
        initial_net_worth_std = 1000;
        planned_production_step_increase = 0.1;
        planned_production_step_decrease = 0.1;
        reapplication_probability_saudi = 0.03 / 356;
        reapplication_probability_expat = 0;
        minimum_mark_up = 1.1;
        days_pay_must_be_available = 30;
        required_roi = 0.14 / 365;
        percent_distribute = 0.9;
        production_function_exponent = 1;
        initial_wage_offer_saudi = 5000;
        initial_wage_offer_expat = 500;
        wage_step_saudi = 2.0 / 365;
        wage_step_expat = 2.0 / 365;

    }

    public String toString()
    {
        try
        {
            return toJson().toString();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
            return "";
        }
    }

    public JSONObject toJson() throws IOException, ParseException
    {
        return (JSONObject) JSONValue.parse(JsonWriter.objectToJson(this));
    }


    public String sha()
    {
        assert seed != 0L: "can not getSha, when seed is not set";
        MessageDigest digest = null;
        byte[] hash = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
            hash = digest.digest(toJson().toString().getBytes("UTF-8"));
        } catch (Exception e)
        {
            hash = "hashing failed".getBytes();
        }
        return hash.toString();
    }

    public Assumptions(JSONObject parameter) throws Exception
    {
        ReadParameter parameters = new ReadParameter(parameter);
        time_after_policy =  parameters.getNumber("time_after_policy").intValue();
        setup_period_1 =  parameters.getNumber("setup_period_1").intValue();
        setup_period_2 =  parameters.getNumber("setup_period_2").intValue();
        time_before_policy =  parameters.getNumber("time_before_policy").intValue();
        num_firms =  parameters.getNumber("num_firms").intValue();
        num_saudis =  parameters.getNumber("num_saudis").intValue();
        num_expats =  parameters.getNumber("num_expats").intValue();
        productivity_mean_saudi =  parameters.getTimeDouble("productivity_mean_saudi");
        productivity_mean_expat =  parameters.getTimeDouble("productivity_mean_expat");
        productivity_std_saudi =  parameters.getTimeDouble("productivity_std_saudi");
        productivity_std_expat =  parameters.getTimeDouble("productivity_std_expat");
        reapplication_probability_saudi =  parameters.getNumber("reapplication_probability_saudi").doubleValue();
        reapplication_probability_expat =  parameters.getNumber("reapplication_probability_expat").doubleValue();
        sector_spending =  parameters.getTimeDouble("sector_spending");
        love_for_variety =  parameters.getNumber("love_for_variety").doubleValue();
        reservation_wage_mean_saudi =  parameters.getNumber("reservation_wage_mean_saudi").doubleValue();
        reservation_wage_std_saudi =  parameters.getNumber("reservation_wage_std_saudi").doubleValue();
        reservation_wage_mean_expat =  parameters.getNumber("reservation_wage_mean_expat").doubleValue();
        reservation_wage_std_expat =  parameters.getNumber("reservation_wage_std_expat").doubleValue();
        price_step_increase = parameters.getDirectionalTV("price_step", "increase");
        price_step_decrease = parameters.getDirectionalTV("price_step", "decrease");
        planned_production_step_increase = parameters.getDirectionalTV("planned_production_step", "increase");
        planned_production_step_decrease = parameters.getDirectionalTV("planned_production_step", "decrease");
        initial_net_worth_mean = parameters.getNumber("initial_net_worth_mean").doubleValue();
        initial_net_worth_std = parameters.getNumber("initial_net_worth_std").doubleValue();
        minimum_mark_up = parameters.getNumber("minimum_mark_up").doubleValue();
        days_pay_must_be_available = parameters.getNumber("days_pay_must_be_available").intValue();
        required_roi = parameters.getTimeDouble("required_roi");
        percent_distribute = parameters.getTimeDouble("percent_distribute");
        production_function_exponent = parameters.getNumber("production_function_exponent").doubleValue();
        initial_wage_offer_saudi = parameters.getTimeDouble("initial_wage_offer_saudi");
        initial_wage_offer_expat = parameters.getTimeDouble("initial_wage_offer_expat");
        wage_step_saudi = parameters.getTimeDouble("wage_step_saudi");
        wage_step_expat = parameters.getTimeDouble("wage_step_expat");


        check_bounds.check_bound("time_after_policy", time_after_policy, 0, BIGGER);
        check_bounds.check_bound("setup_period_1", setup_period_1, 0, BIGGER);
        check_bounds.check_bound("setup_period_2", setup_period_2, 0, BIGGER);
        check_bounds.check_bound("time_before_policy", time_before_policy, -1, BIGGER_EQUAL);
        check_bounds.check_bound("num_firms", num_firms, 0, BIGGER);
        check_bounds.check_bound("num_saudis", num_saudis, 0, BIGGER_EQUAL);
        check_bounds.check_bound("num_expats", num_expats, 0, BIGGER_EQUAL);
        check_bounds.check_bound("num_saudis + num_expats", num_saudis + num_expats, 0, BIGGER);
        check_bounds.check_bound("productivity_mean_saudi", productivity_mean_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("productivity_mean_expat", productivity_mean_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bound("productivity_std_saudi", productivity_std_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("productivity_std_expat", productivity_std_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bound("productivity_mean_saudi + productivity_std_saudi", productivity_mean_saudi + productivity_std_saudi, 0, BIGGER);
        check_bounds.check_bound("productivity_mean_expat + productivity_std_expat", productivity_mean_expat + productivity_std_expat, 0, BIGGER);
        check_bounds.check_bound("reapplication_probability_saudi", reapplication_probability_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reapplication_probability_expat", reapplication_probability_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bound("sector_spending", sector_spending, 0, BIGGER);
        check_bounds.check_bounds("love_for_variety", love_for_variety, 0.0, 1.0, BIGGER, SMALLER);
        check_bounds.check_bound("reservation_wage_mean_saudi", reservation_wage_mean_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_std_saudi", reservation_wage_std_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_mean_expat", reservation_wage_mean_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_std_expat", reservation_wage_std_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bounds("price_step_increase", price_step_increase, 0, 1, BIGGER, SMALLER);
        check_bounds.check_bounds("price_step_decrease", price_step_decrease, 0, 1, BIGGER, SMALLER);
        check_bounds.check_bounds("planned_production_step_increase", planned_production_step_increase, 0, 1, BIGGER, SMALLER);
        check_bounds.check_bounds("planned_production_step_decrease", planned_production_step_decrease, 0, 1, BIGGER, SMALLER);
        check_bounds.check_bound("initial_net_worth_mean", initial_net_worth_mean, 0, BIGGER);
        check_bounds.check_bound("initial_net_worth_std", initial_net_worth_std, 0, BIGGER);
        //minimum_mark_up [-inf, inf]
        check_bounds.check_bound("days_pay_must_be_available", days_pay_must_be_available, 0, BIGGER);
        check_bounds.check_bound("required_roi", required_roi, 0, BIGGER_EQUAL);
        check_bounds.check_bounds("percent_distribute", percent_distribute, 0.0, 1.0 / 365, BIGGER_EQUAL, SMALLER);
        //production_function_exponent [-inf, inf]
        check_bounds.check_bound("initial_wage_offer_saudi", initial_wage_offer_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("initial_wage_offer_expat", initial_wage_offer_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bounds("wage_step_saudi", wage_step_saudi, 0, 1, BIGGER, SMALLER);
        check_bounds.check_bounds("wage_step_expat", wage_step_expat, 0, 1, BIGGER, SMALLER);
        long local_seed = 0;
        try
        {
            local_seed = parameters.getNumber("seed").longValue();
        } catch (NullPointerException e)
        {}
        if (local_seed == 0)
        {
            seed = new Random().nextLong();
        }
        else
        {
            seed = local_seed;
        }
        parameters.warn_keys_not_read();
    }
}
