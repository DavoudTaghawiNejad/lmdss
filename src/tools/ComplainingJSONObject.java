package tools;

import org.json.simple.JSONObject;

public class ComplainingJSONObject extends JSONObject
{
    public ComplainingJSONObject(JSONObject parameter)
    {
        super(parameter);
    }

    public Number getNumber(Object key)
    {
        Number value = (Number) super.get(key);
        if (value == null)
        {
            System.out.print("\"");
            System.out.print(key);
            System.out.println("\" not in json:");
            System.out.println(this.toString());
            throw new NullPointerException();
        }
        return value;
    }
}
