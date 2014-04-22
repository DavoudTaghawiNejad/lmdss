package tools;

import org.json.simple.JSONObject;

import java.util.HashMap;


public class ReadParameter extends JSONObject
{
    HashMap<String, Boolean> readed =  new HashMap<String, Boolean>();
    
    public ReadParameter(JSONObject parameter)
    {        
        super(parameter);
        for (Object key: parameter.keySet())
        {
            readed.put((String) key, false);
        }
    }

    @Override
    public Object get(Object key)
    {
        readed.put((String) key, true);
        return super.get(key);
    }

    public Number getNumber(Object key)
    {
        Number value = (Number) this.get(key);
        if (value == null)
        {
            throw new NullPointerException("'" + key + "' not in json");
        }
        return value;
    }

    public double getTimeDouble(Object key)
    {
        Double value;
        if (super.keySet().contains(key + "_pday"))
        {
            value = (this.getNumber(key + "_pday")).doubleValue();
        }
        else if (super.keySet().contains(key + "_pmonth"))
        {
            value = (this.getNumber(key + "_pmonth")).doubleValue() / 30.4375;
        }
        else if (super.keySet().contains(key + "_pyear"))
        {
            value = (this.getNumber(key + "_pyear")).doubleValue() / 365.2;
        }
        else
        {
            throw new NullPointerException("'" + key + "_p*'(*=day/month/year) not in json");
        }
        assert value != null;
        return value;
    }

    public double getDirectionalTV(Object key, String direction)
    {
        double value;
        try
        {
            value = this.getTimeDouble(key + "_change");
        } catch (NullPointerException e)
        {
            try
            {
                value = this.getTimeDouble(key + "_" + direction);
            } catch (NullPointerException ee)
            {
                throw ee;
            }
        }
        return value;
    }

    public void warn_keys_not_read()
    {
        for (String key: readed.keySet())
        {
            if (readed.get(key) == false)
            {
                System.out.println("WARNING '" + key + "' has not been read. Possibly because they have been redundant");
            }
        }
    }
}
