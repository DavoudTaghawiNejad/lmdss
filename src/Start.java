import agents.CalibrationStatistics;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
        long started = System.currentTimeMillis();
        Simulation simulation;
        {
        	JSONParser parser = new JSONParser();
        	String jsonString = parser.parse(new FileReader(args.get(1))).toString();
            simulation = new Simulation(args.get(0), jsonString);
        }
        JSONObject simulation_output = simulation.run();
        simulation_output.put("run time", (System.currentTimeMillis() - started) / 1000.0);
        simulation_output.put("hash", simulation.getSha());
        System.out.print(simulation_output);
    }
}
