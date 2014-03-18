

import agents.Firm;
import agents.Worker;
import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import definitions.Citizenship;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
* Created by taghawi on 2/25/14.
*/
class Policy
{
    public static final double MONTHLY_TO_DAILY = 30.41;




    private final double expat_minimum_wage;
    private final double saudi_minimum_wage;
    private final double expat_tax_percentage;
    private final double expat_tax_per_head;

    private final double saudi_tax_percentage;
    private final double saudi_tax_per_head;








    private final double sauditization_percentage;
    private final int visa_length;

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
        out.put("visa_length", (double)visa_length);
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


    public String sha()
    {

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

    public static Policy Policy(String parameters)
    {
        Policy new_class = null;
        try
        {
            new_class = (Policy) JsonReader.jsonToJava(parameters);
        } catch (IOException e)
        {
            System.out.println("--");
            System.out.println(parameters);
            System.out.println("--");
            e.printStackTrace();
        }
        return new_class;
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







    public double getSaudi_tax_percentage()
    {
        return saudi_tax_percentage;
    }

    public double getSaudi_tax_per_head()
    {
        return saudi_tax_per_head;
    }





































    public double getSauditization_percentage()
    {
        return sauditization_percentage;
    }















    public int getVisa_length()
    {
        return visa_length;
    }
}
