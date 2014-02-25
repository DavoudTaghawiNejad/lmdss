package agents;


import com.cedarsoftware.util.io.JsonWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.List;

public class CalibrationStatistics
{

    private int num_saudis = 0;
    private int num_expats = 0;
    private double wage_bill = 0;
    private double net_worth = 0;
    private double profit = 0;
    private double price = 0;
    private double demand = 0;
    private double production = 0;
    private double planned_production = 0;
    private double distributed_profits = 0;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    private int staff = 0;
    private int num_firms = 0;

    public CalibrationStatistics(List<Firm> firms)
    {
        update(firms);
    }

    private void update(List<Firm> firms) {
        for (Firm firm: firms)
        {
            num_firms++;
            num_saudis += firm.staff.getSaudis();
            num_expats += firm.staff.getExpats();
            wage_bill += firm.staff.getWage();
            net_worth += firm.net_worth;
            profit += firm.profit;
            price += firm.price * firm.demand;
            demand += firm.demand;
            production += firm.staff.getProductivity();
            planned_production += firm.planned_production;
            distributed_profits += firm.distributed_profits;
            wage_saudis += firm.staff.getWage_saudis();
            wage_expats += firm.staff.getWage_expats();
            staff += firm.staff.size();
        }
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
}
