import agents.CalibrationStatistics;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.zeromq.ZMQ;
import tools.InvalidValueError;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;


public class Start
{
    public static void main(String[] raw_args) throws Exception
    {
        List<String> args = Arrays.asList(raw_args);
        if (args.contains("--help") || args.contains("-h") || args.size() < 1 || args.size() > 4)
        {
            print_help();
            return;
        }
        Simulation simulation;

        if (args.get(0).contains("z"))
        {
            simulation_via_zmq(args, true);
        }
        else
        {
            JSONObject parameters = null;
            if (args.get(1).startsWith("{"))
            {
                parameters = (JSONObject) JSONValue.parse(args.get(1));
            }
            else
            {
                parameters = simulation_from_file(args);
            }
            JSONObject simulation_output = run_simulation(args, parameters, false);
            System.out.print(simulation_output.toJSONString());
        }

    }

    private static void print_help()
    {
        System.out.println();
        System.out.println("java -Djava.library.path=/usr/local/lib -jar saudifirms.jar ...");
        System.out.println("... t   \t-\tfor time series");
        System.out.println("... p   \t-\tfor panel data");
        System.out.println("... tp  \t-\tfor both");
        System.out.println("... tp {Assumptions}  \t-\tfor both with parameters");
        System.out.println("... c {Assumptions}   \t-\tfor calibration");
        System.out.println("... c filename.toJson \t-\tfor calibration");
        System.out.println("... ztpc                                \t-\tfor ZeroMQ remote controlling");
        System.out.println("... ztpc 5557 5558 5559 tcp://localhost:\t-\tfor ZeroMQ remote controlling with custom address");
        System.out.println("... ztpc task result kill address       \t-\tfor ZeroMQ remote controlling");
        System.out.println("... zatpc ...      \t-\tfor ZeroMQ remote controlling, does not shut down amazon instance");
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
    }

    private static JSONObject simulation_from_file(List<String> args) throws IOException, ParseException
    {
        JSONObject parameters;File f = new File(args.get(1));
        if(f.exists() && !f.isDirectory())
        {
            JSONParser parser = new JSONParser();
            parameters = (JSONObject) parser.parse(new FileReader(args.get(1)));
        }
        else
        {
            throw new IllegalArgumentException(args.get(1) + "not a file or JSON string" );
        }
        return parameters;
    }

    private static void simulation_via_zmq(List<String> args, boolean print_round) throws UnsupportedEncodingException {
        System.out.println("simulation via zmq 0.1");
        int address_task;
        int address_result;
        int address_kill;
        String address_prefix;
        try
        {
            address_task = Integer.getInteger(args.get(1));
            address_result = Integer.getInteger(args.get(2));
            address_kill = Integer.getInteger(args.get(3));
        } catch (Exception ArrayIndexOutOfBoundsException)
        {
            address_task = 5557;
            address_result = 5558;
            address_kill = 5559;
        }
        try
        {
            address_prefix = args.get(4);
        } catch (Exception ArrayIndexOutOfBoundsException)
        {
            address_prefix = "tcp://localhost:";
        }

        ZMQ.Context context = ZMQ.context(1);

        ZMQ.Socket receiver = context.socket(ZMQ.PULL);
        receiver.connect(address_prefix + String.valueOf(address_task));

        ZMQ.Socket sender = context.socket(ZMQ.PUSH);
        sender.connect(address_prefix + String.valueOf(address_result));

        ZMQ.Socket controller = context.socket(ZMQ.SUB);
        controller.connect(address_prefix + String.valueOf(address_kill));
        controller.subscribe("".getBytes());

        ZMQ.Poller poller = new ZMQ.Poller(2);
        final int RECEIVER = 0;
        poller.register(receiver, ZMQ.Poller.POLLIN);
        final int CONTROLLER = 1;
        poller.register(controller, ZMQ.Poller.POLLIN);

        while (true) {

            System.out.println("Receiving");
            poller.poll();  //we can put a time out for shutdown of instance
            if (poller.pollin(RECEIVER)) {
                byte[] messageb = receiver.recv();
                String output_string;
                String message;
                JSONObject parameters = null;
                try
                {
                    message = new String(messageb, "UTF-8");
                } catch (Exception e)
                {
                    output_string = e.toString() + "                                                    ***" + messageb + "***";
                    sender.send(output_string, 0);
                    continue;
                }
                try
                {
                    parameters = (JSONObject) JSONValue.parse(message);
                } catch (Exception e)
                {
                    output_string = e.toString() + "/n'" + message + "'";
                    sender.send(output_string, 0);
                    continue;
                }
                System.out.println("Received and Working:");
                //System.out.println(JsonWriter.formatJson(parameters.toString()));
                JSONObject simulation_output = null;
                try {
                    simulation_output = run_simulation(args, parameters, print_round);
                    output_string = simulation_output.toJSONString();
                } catch (Exception e) {
                    output_string = e.toString();
                }
                System.out.println("Worked and Send:");
                //System.out.println(JsonWriter.formatJson(simulation_output.toString()));
                sender.send(output_string, 0);
            }
            //  Any waiting controller command acts as 'KILL'
            if (poller.pollin(CONTROLLER)) {
                break; // Exit loop
            }

        }
        // Finished
        receiver.close();
        sender.close();
        controller.close();
        //context.term();                         Context.term() hangs, try enabling in future versions of jzmq
        if (!(args.get(0).contains("a")))
        {
            //amazon.shutdown_vm()
        }
    }


    private static JSONObject run_simulation(List<String> args, JSONObject parameters, boolean print_round) throws SQLException, InvalidValueError, ClassNotFoundException, IOException, ParseException {
        Simulation simulation;
        long started = System.currentTimeMillis();
        simulation = new Simulation(args.get(0), parameters, print_round);
        JSONObject simulation_output = simulation.run();
        JSONObject output = new JSONObject();
        output.put("result", simulation_output);
        output.put("run time", (System.currentTimeMillis() - started) / 1000.0);
        output.put("hash", parameters.get("hash"));
        output.put("parameters", parameters);
        return output;
    }
}

