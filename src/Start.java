import agents.CalibrationStatistics;
import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileReader;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import java.util.Arrays;
import java.util.List;


public class Start
{
    public static void main(String[] raw_args) throws Exception
    {
        List<String> args = Arrays.asList(raw_args);
        if (args.contains("--help") || args.contains("-h") || args.size() < 2 || args.size() > 3)
        {
            System.out.println("start t  for time series");
            System.out.println("start p  for panel data");
            System.out.println("start tp  for both");
            System.out.println("start tp {Assumptions} for both with parameters");
            System.out.println("start c {Assumptions} for calibration");
            System.out.println("start c filename.toJson for calibration");
            System.out.println();
            System.out.println("Parameters:");
            System.out.println("{");
            System.out.println("\"assumptions\":");
            System.out.print(new tools.Assumptions().toString());
            System.out.println(",\n\"before_policy\":");
            System.out.println(new tools.Policy().toString());
            System.out.println(",\"after_policy\":");
            System.out.print(new tools.Policy().toString());
            System.out.println("\n}");
            System.out.println("\n\nResults\n");
            System.out.println(new CalibrationStatistics("--help").json().toString());
            System.out.println();
            System.out.println("All parameters and results are monthly");
            return;
        }
        long started = System.currentTimeMillis();
        Simulation simulation;
        JSONObject parameters;
        if (args.get(1).startsWith("{"))
        {
            parameters = (JSONObject) JSONValue.parse(args.get(1));
        }
        else
        {
            File f = new File(args.get(1));
            if(f.exists() && !f.isDirectory())
            {
                JSONParser parser = new JSONParser();
                parameters = (JSONObject) parser.parse(new FileReader(args.get(1)));
            }
            else
            {
                throw new IllegalArgumentException(args.get(1) + "not a file or JSON string" );
            }
        }
        simulation = new Simulation(args.get(0), parameters);
        JSONObject simulation_output = simulation.run();
        simulation_output.put("run time", (System.currentTimeMillis() - started) / 1000.0);
        simulation_output.put("assumptions_hash", simulation.getSha());
        System.out.print(simulation_output.toJSONString());
    }
}

