import agents.CalibrationStatistics;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class Start
{
    public static void main(String [] raw_args) throws Exception
    {
        List<String> args = Arrays.asList(raw_args);
        if (args.contains("--help") || args.contains("-h") || args.size() <2 || args.size() > 3)
        {
            System.out.println("start t  for time series");
            System.out.println("start p  for panel data");
            System.out.println("start tp  for both");
            System.out.println("start tp {Assumptions} for both with parameters");
            System.out.println("start c {Assumptions} for calibration");

            System.out.println("Parameters:");
            System.out.println("{");
            System.out.println("\"assumptions\":");
            System.out.print(new Assumptions().toString());
            System.out.println(",\n\"before_policy\":");
            System.out.println(new Policy().toString());
            System.out.println(",\"after_policy\":");
            System.out.print(new Policy().toString());
            System.out.println("\n}");
            System.out.println("\n\nResults\n");
            System.out.println(new CalibrationStatistics("--help").json().toString());
            System.out.println();
            System.out.println("All parameters and results are monthly");
            return;
        }
        delete_database();
        long started = System.currentTimeMillis();
        Simulation simulation;
        {
            simulation = new Simulation(args.get(0), args.get(1));
        }
        JSONObject simulation_output = simulation.run();
        dump_csv();
        simulation_output.put("run time", (System.currentTimeMillis() - started) / 1000.0);
        simulation_output.put("hash", simulation.getSha());
        System.out.print(simulation_output);
    }

    private static void delete_database()
    {
        try
        {
            Runtime.getRuntime().exec("rm lmdss.sqlite3");
        } catch (Exception ee)
        {
            System.out.println("Cannot run batch...");
        }
    }

    private static void dump_csv()
    {/*
        try
        {
            Runtime.getRuntime().exec("cmd /c start DumpCSV.bat");
        } catch (IOException e)
        {
            try
            {
                Runtime.getRuntime().exec("sh /home/taghawi/Dropbox/workspace/saudifirms/dump.sh");
            }
            catch (Exception ee)
            {
                System.out.println("Cannot run batch...");
            }
        }*/
    }
}
