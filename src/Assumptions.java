import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tools.ComplainingJSONObject;
import tools.check_bounds;
import static tools.MyComparators.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


class Assumptions
{
    public static final double MONTHLY_TO_DAILY = 30.41;
    public final long seed;
    public final int simulation_length;
    public final int policy_change_time;
    public final int num_firms;
    public final int num_saudis;
    public final int num_expats;
    public final double productivity_mean_saudi;
    public final double productivity_mean_expat;
    public final double productivity_std_saudi;
    public final double productivity_std_expat;
    public final double reapplication_probability;
    public final double sector_spending;
    public final double love_for_variety;
    public final double reservation_wage_mean_saudi;
    public final double reservation_wage_std_saudi;
    public final double reservation_wage_mean_expat;
    public final double reservation_wage_std_expat;

    public Assumptions()
    {
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
        reapplication_probability = 0.03 / 356;
        sector_spending = 10000000000.0;
        love_for_variety = 0.5;
        simulation_length = 2000;
        policy_change_time = 1500;
        seed = 0L;
    }

    public String toString()
    {
        return toJson().toString();
    }

    public JSONObject toJson()
    {
        JSONParser parser = new JSONParser();
        JSONObject ret = null;

        try
        {

            ret = (JSONObject) parser.parse(JsonWriter.objectToJson(this));
        } catch (ParseException e)
        {
            e.printStackTrace();
        } catch (IOException ee)
        {
            ee.printStackTrace();
        }
        assert (seed != 0L);
        return ret;
    }


    public String sha()
    {
        assert seed != 0L: "can not getSha, when seed is not set";
        MessageDigest digest = null;
        byte[] hash = null;
        try
        {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        try
        {
            hash = digest.digest(toJson().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return hash.toString();
    }

    public Assumptions(JSONObject parameter) throws Exception
    {
        ComplainingJSONObject parameters = new ComplainingJSONObject(parameter);
        simulation_length =  parameters.getNumber("simulation_length").intValue();
        policy_change_time =  parameters.getNumber("policy_change_time").intValue();
        num_firms =  parameters.getNumber("num_firms").intValue();
        num_saudis =  parameters.getNumber("num_saudis").intValue();
        num_expats =  parameters.getNumber("num_expats").intValue();
        productivity_mean_saudi =  parameters.getNumber("productivity_mean_saudi").doubleValue();
        productivity_mean_expat =  parameters.getNumber("productivity_mean_expat").doubleValue();
        productivity_std_saudi =  parameters.getNumber("productivity_std_saudi").doubleValue();
        productivity_std_expat =  parameters.getNumber("productivity_std_expat").doubleValue();
        reapplication_probability =  parameters.getNumber("reapplication_probability").doubleValue();
        sector_spending =  parameters.getNumber("sector_spending").doubleValue();
        love_for_variety =  parameters.getNumber("love_for_variety").doubleValue();
        reservation_wage_mean_saudi =  parameters.getNumber("reservation_wage_mean_saudi").doubleValue();
        reservation_wage_std_saudi =  parameters.getNumber("reservation_wage_std_saudi").doubleValue();
        reservation_wage_mean_expat =  parameters.getNumber("reservation_wage_mean_expat").doubleValue();
        reservation_wage_std_expat =  parameters.getNumber("reservation_wage_std_expat").doubleValue();
        check_bounds.check_bound("simulation_length", simulation_length, 0, BIGGER);
        check_bounds.check_bound("policy_change_time", policy_change_time, -1, BIGGER_EQUAL);
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
        check_bounds.check_bound("reapplication_probability", reapplication_probability, 0, BIGGER_EQUAL);
        check_bounds.check_bound("sector_spending", sector_spending, 0, BIGGER);
        check_bounds.check_bounds("love_for_variety", love_for_variety, 0.0, 1.0, BIGGER, SMALLER);
        check_bounds.check_bound("reservation_wage_mean_saudi", reservation_wage_mean_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_std_saudi", reservation_wage_std_saudi, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_mean_expat", reservation_wage_mean_expat, 0, BIGGER_EQUAL);
        check_bounds.check_bound("reservation_wage_std_expat", reservation_wage_std_expat, 0, BIGGER_EQUAL);

        long local_seed = 0;
        try
        {
            local_seed = ((Number) parameters.get("seed")).longValue();
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
    }
}
