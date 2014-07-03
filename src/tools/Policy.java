package tools;

import agents.Worker;
import com.cedarsoftware.util.io.JsonWriter;
import definitions.Citizenship;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import static tools.MyComparators.*;


public class Policy
{
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

    public Policy(JSONObject parameter, String section) throws Exception
    {
        ReadParameter parameters = new ReadParameter(parameter);
        saudi_minimum_wage =  parameters.getTimeDouble("saudi_minimum_wage");
        expat_minimum_wage = parameters.getTimeDouble("expat_minimum_wage");
        saudi_tax_percentage =  parameters.getNumber("saudi_tax_percentage").doubleValue();
        expat_tax_percentage =  parameters.getNumber("expat_tax_percentage").doubleValue();
        saudi_tax_per_head =  parameters.getTimeDouble("saudi_tax_per_head");
        expat_tax_per_head =  parameters.getTimeDouble("expat_tax_per_head");
        sauditization_percentage =  parameters.getNumber("sauditization_percentage").doubleValue();
        visa_length =  parameters.getNumber("visa_length").intValue();
        check_bounds.check_bound("saudi_minimum_wage", saudi_minimum_wage, 0, BIGGER_EQUAL);
        check_bounds.check_bound("expat_minimum_wage", expat_minimum_wage, 0, BIGGER_EQUAL);
        check_bounds.check_bound("expat_tax_percentage", expat_tax_percentage, 0, BIGGER_EQUAL);
        check_bounds.check_bound("saudi_tax_percentage", saudi_tax_percentage, 0, BIGGER_EQUAL);
        check_bounds.check_bound("saudi_tax_per_head", saudi_tax_per_head, Double.NEGATIVE_INFINITY, BIGGER);
        check_bounds.check_bound("expat_tax_per_head", expat_tax_per_head, Double.NEGATIVE_INFINITY, BIGGER);
        check_bounds.check_bound("visa_length", visa_length, 0, BIGGER);
        check_bounds.check_bounds("sauditization_percentage", sauditization_percentage, 0, 1, BIGGER_EQUAL, SMALLER_EQUAL);
        parameters.warn_keys_not_read(section);
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
