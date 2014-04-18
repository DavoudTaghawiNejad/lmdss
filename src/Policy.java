import agents.Worker;
import com.cedarsoftware.util.io.JsonWriter;
import definitions.Citizenship;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tools.ComplainingJSONObject;
import tools.check_bounds;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import static tools.MyComparators.*;


class Policy
{
    public static final double MONTHLY_TO_DAILY = 30.41;
    public final double expat_minimum_wage;
    public final double saudi_minimum_wage;
    public final double expat_tax_percentage;
    public final double expat_tax_per_head;
    public final double saudi_tax_percentage;
    public final double saudi_tax_per_head;
    public final double sauditization_percentage;
    public final int visa_length;

    public HashMap<String, Double> dump_policy()
    {
        HashMap<String, Double> out = new HashMap<String, Double>();
        out.put("expat_minimum_wage", expat_minimum_wage);
        out.put("saudi_minimum_wage", saudi_minimum_wage);
        out.put("expat_tax_percentage", expat_tax_percentage);
        out.put("saudi_tax_percentage", saudi_tax_percentage);
        out.put("expat_tax_per_head", expat_tax_per_head);
        out.put("saudi_tax_per_head", saudi_tax_per_head);
        out.put("sauditization_percentage", sauditization_percentage);
        out.put("visa_length", (double) visa_length);
        return out;
    }

    public Policy()
    {
        expat_minimum_wage = 0;
        saudi_minimum_wage = 0;
        expat_tax_percentage = 0;
        expat_tax_per_head = 0;
        saudi_tax_percentage = 0;
        saudi_tax_per_head = 0;
        sauditization_percentage = 0;
        visa_length = 356;
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
        return ret;
    }


    public String sha() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        MessageDigest digest = null;
        byte[] hash = null;
        digest = MessageDigest.getInstance("SHA-256");
        hash = digest.digest(json().toString().getBytes("UTF-8"));
        return hash.toString();
    }

    public Policy(JSONObject parameter) throws Exception
    {
        ComplainingJSONObject parameters = new ComplainingJSONObject(parameter);
        expat_minimum_wage = parameters.getNumber("expat_minimum_wage").doubleValue();
        check_bounds.check_bound("expat_minimum_wage", expat_minimum_wage, 0, BIGGER_EQUAL);
        saudi_minimum_wage =  parameters.getNumber("saudi_minimum_wage").doubleValue();
        check_bounds.check_bound("saudi_minimum_wage", saudi_minimum_wage, 0, BIGGER_EQUAL);
        expat_tax_percentage =  parameters.getNumber("expat_tax_percentage").doubleValue();
        check_bounds.check_bound("expat_tax_percentage", expat_tax_percentage, 0, BIGGER_EQUAL);
        expat_tax_per_head =  parameters.getNumber("expat_tax_per_head").doubleValue();
        check_bounds.check_bound("expat_tax_per_head", expat_tax_per_head, Double.NEGATIVE_INFINITY, BIGGER);
        saudi_tax_percentage =  parameters.getNumber("saudi_tax_percentage").doubleValue();
        check_bounds.check_bound("saudi_tax_percentage", saudi_tax_percentage, 0, BIGGER_EQUAL);
        saudi_tax_per_head =  parameters.getNumber("saudi_tax_per_head").doubleValue();
        check_bounds.check_bound("saudi_tax_per_head", saudi_tax_per_head, Double.NEGATIVE_INFINITY, BIGGER);
        sauditization_percentage =  parameters.getNumber("sauditization_percentage").doubleValue();
        check_bounds.check_bounds("sauditization_percentage", sauditization_percentage, 0, 1, BIGGER_EQUAL, SMALLER_EQUAL);
        visa_length =  parameters.getNumber("visa_length").intValue();
        check_bounds.check_bound("visa_length", visa_length, 0, BIGGER);
    }

    public void change_policy_for_workers(List<Worker> workers)
    {
        for (Worker worker: workers)
        {
            if (worker.getCitizenship() == Citizenship.SAUDI)
            {
                worker.re_calculate_wage(saudi_minimum_wage);
            }
            else
            {
                worker.re_calculate_wage(expat_minimum_wage);
            }
        }
    }
}
