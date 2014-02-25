import org.json.simple.JSONObject;

import java.io.IOException;


public class Start
{
    public static void main(String [] args)
    {
        assert args.length <= 1: "should only contain one json parameter set";
        delete_database();
        long started = System.currentTimeMillis();
        Simulation simulation;
        if (args.length == 0)
        {
            simulation = new Simulation();
        }
        else
        {
            simulation = new Simulation(args[0]);
        }
        JSONObject simulation_output = simulation.run();
        dump_csv();
        simulation_output.put("run time", (System.currentTimeMillis() - started) / 1000.0);
        System.out.println();
        System.out.println(simulation_output);
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
    {
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
        }
    }
}
