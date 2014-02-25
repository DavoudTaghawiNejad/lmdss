import java.io.IOException;


public class Start
{
    public static void main(String [] args)
    {
        long started = System.currentTimeMillis();
        delete_database();
        Simulation simulation = new Simulation();
        simulation.run();
        dump_csv();
        System.out.print("end ");
        System.out.print((System.currentTimeMillis() - started) / 1000.0);
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
