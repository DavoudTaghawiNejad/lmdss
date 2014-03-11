import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
* Created by taghawi on 2/25/14.
*/
class Parameters
{
    public static final double MONTHLY_TO_DAILY = 30.41;
    private final int num_saudis;
    private final int num_expats;
    private final double productivity_mean_saudi;
    private final double productivity_mean_expat;
    private final double expat_minimum_wage;
    private final double saudi_minimum_wage;
    private final double expat_tax_percentage;
    private final double expat_tax_per_head;
    private final double reapplication_probability;
    private final double saudi_tax_percentage;
    private final double saudi_tax_per_head;
    private final double sector_spending;
    private final double love_for_variety;
    private final int num_firms;
    private final int simulation_length;
    private final int policy_change_time;
    private final double wage_std;
    private final double reservation_wage_saudi;
    private final double reservation_wage_expat;
    private final double initial_sauditization_percentage;
    private final int visa_length;
    private long seed;

    public Parameters()
    {
        num_firms = 100;
        num_saudis = 3800;
        num_expats = 7000;
        productivity_mean_saudi = 6854.24;
        productivity_mean_expat = 6854.24;
        reservation_wage_saudi = 3137.39;
        reservation_wage_expat = 0;
        expat_minimum_wage = 0;
        saudi_minimum_wage = 0;
        expat_tax_percentage = 0;
        expat_tax_per_head = 0;
        reapplication_probability = 0.03 / 356;
        saudi_tax_percentage = 0;
        saudi_tax_per_head = 0;
        initial_sauditization_percentage = 0;
        sector_spending = 10000000000.0;
        love_for_variety = 0.5;
        simulation_length = 2000;
        policy_change_time = 1500;
        wage_std = 3137.39;
        visa_length = 356;
        seed = 0L;
    }

    public String toString()
    {
        return json().toString();
    }

    public JSONObject json()
    {
        JSONParser parser=new JSONParser();
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
        if (seed == 0L)
        {
            ret.remove("seed");
        }
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
            hash = digest.digest(json().toString().getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return hash.toString();
    }

    public static Parameters Parameters(String parameters)
    {
        Parameters new_class = null;
        try
        {
            new_class = (Parameters) JsonReader.jsonToJava(parameters);
        } catch (IOException e)
        {
            System.out.println("--");
            System.out.println(parameters);
            System.out.println("--");
            e.printStackTrace();
        }
        return new_class;
    }

    public int getNum_saudis()
    {
        return num_saudis;
    }

    public int getNum_expats()
    {
        return num_expats;
    }

    public double getProductivity_mean_saudi()
    {
        return productivity_mean_saudi / MONTHLY_TO_DAILY;
    }

    public double getProductivity_mean_expat()
    {
        return productivity_mean_expat / MONTHLY_TO_DAILY;
    }

    public double getExpat_minimum_wage()
    {
        return expat_minimum_wage / MONTHLY_TO_DAILY;
    }

    public double getSaudi_minimum_wage()
    {
        return saudi_minimum_wage / MONTHLY_TO_DAILY;
    }

    public double getExpat_tax_percentage()
    {
        return expat_tax_percentage / MONTHLY_TO_DAILY;
    }

    public double getExpat_tax_per_head()
    {
        return expat_tax_per_head / MONTHLY_TO_DAILY;
    }

    public double getReapplication_probability()
    {
        return reapplication_probability / MONTHLY_TO_DAILY;
    }

    public double getSaudi_tax_percentage()
    {
        return saudi_tax_percentage;
    }

    public double getSaudi_tax_per_head()
    {
        return saudi_tax_per_head;
    }

    public double getSector_spending()
    {
        return sector_spending / MONTHLY_TO_DAILY;
    }

    public double getLove_for_variety()
    {
        return love_for_variety;
    }

    public int getNum_firms()
    {
        return num_firms;
    }

    public int getPolicy_change_time()
    {
        return policy_change_time;
    }

    public double getWage_std()
    {
        return wage_std;
    }

    public double getReservation_wage_saudi()
    {
        return reservation_wage_saudi / MONTHLY_TO_DAILY;
    }

    public double getReservation_wage_expat()
    {
        return reservation_wage_expat / MONTHLY_TO_DAILY;
    }

    public double getInitial_sauditization_percentage()
    {
        return initial_sauditization_percentage;
    }

    public int getSimulation_length()
    {
        return simulation_length;
    }

    public long getSeed()
    {
        if (seed == 0L)
        {
            seed = (new Random().nextLong());
        }
        return seed;
    }

    public int getVisa_length()
    {
        return visa_length;
    }
}
