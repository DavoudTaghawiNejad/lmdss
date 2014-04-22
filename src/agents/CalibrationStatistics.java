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
    private double net_worth = 0;
    private double profit = 0;
    private double price = 0;
    private double demand = 0;
    private double production = 0;
    private double distributed_profits = 0;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    private int num_firms = 0;
    private int samples = 0;
    private List<Firm> firms;

    public CalibrationStatistics(List<Firm> firms)
    {

        this.firms = firms;
    }

    public CalibrationStatistics(String s)
    {
        samples = 1;
    }

    private void convert_to_monthly()
    {
        profit *= 30.42;
        demand *= 30.42;
        production *= 30.42;
        distributed_profits *= 30.42;
        wage_saudis *= 30.42;
        wage_expats *= 30.42;
    }

    public CalibrationStatistics()
    {
    }

    public void update() {
        samples++;
        for (Firm firm: firms)
        {            
            num_firms++;
            num_saudis += firm.getStaff().getSaudis();
            num_expats += firm.getStaff().getExpats();
            net_worth += firm.getNet_worth();
            profit += firm.getProfit();
            price += firm.getPrice() * firm.getDemand();
            demand += firm.getDemand();
            production += firm.getStaff().getProductivity();
            distributed_profits += firm.getDistributed_profits();
            wage_saudis += firm.getStaff().getWage_saudis();
            wage_expats += firm.getStaff().getWage_expats();
        }
    }

    public JSONObject json()
    {
        convert_to_monthly();
        JSONObject calibration_statistics = new JSONObject();
        // calibration_statistics.put("samples", samples);
        calibration_statistics.put("num_saudis",num_saudis / samples);
        calibration_statistics.put("num_expats",num_expats / samples);
        calibration_statistics.put("net_worth",net_worth / samples);
        calibration_statistics.put("profit",profit / samples);
        calibration_statistics.put("price", price / samples / demand);
        calibration_statistics.put("demand", demand / samples);
        calibration_statistics.put("production", production / samples);
        calibration_statistics.put("distributed_profits",distributed_profits / samples);
        calibration_statistics.put("wage_saudis", wage_saudis / samples);
        calibration_statistics.put("wage_expats", wage_expats / samples);
        calibration_statistics.put("num_firms", num_firms / samples);
        return calibration_statistics;
    }
}
