


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import agents.Auctioneer;
import agents.Firm;
import agents.Newspaper;
import agents.Worker;
import definitions.Citizenship;
import definitions.Status;


public class main
{

	private static long initialTime;
	private static double sauditization_percentage;
	private static List<List<Worker>> apply_to_firm;
	private static List<Firm> firms;
	private static Newspaper newspaper_saudi;
	private static Newspaper newspaper_expat;
	private static List<Worker> workers;
	private static Auctioneer auctioneer;

    public static void initialisation()
    {

		initialTime = System.currentTimeMillis();
        final int num_firms = 100;
        final double sauditization_percentage = 0;
        final int num_saudis = 3800;
        final int num_expats = 7000;
        final double productivity_mean_saudi = 6854.24 / 30;
        final double wage_mean_saudi = 3137.39 / 30;
        final double productivity_mean_expat = 6854.24 / 30;
        final double wage_mean_expat = 764.77 / 30;
        final double expat_minimum_wage = 0;
        final double saudi_minimum_wage = 0;
        final double expat_tax_percentage = 0;
        final double expat_tax_per_head = 0;

        Random seed_generator = new Random();

        auctioneer = new Auctioneer();

        newspaper_saudi = new Newspaper(seed_generator.nextLong());
        newspaper_expat = new Newspaper(seed_generator.nextLong());

        workers = new ArrayList<Worker>();

        Random rnd = new Random(seed_generator.nextLong());
        for (int i = 0; i < num_saudis; i++)
        {
            workers.add(
                    new Worker(
                            Citizenship.SAUDI,
                            newspaper_saudi,
                            rnd.nextGaussian() * wage_mean_saudi + wage_mean_saudi,
                            rnd.nextGaussian() * productivity_mean_saudi + productivity_mean_saudi,
                            expat_minimum_wage,
                            saudi_minimum_wage,
                            expat_tax_percentage,
                            expat_tax_per_head,
                            auctioneer
                     )
            );
        }
        for (int i = 0; i < num_expats; i++)
        {
            workers.add(
                    new Worker(
                            Citizenship.EXPAT,
                            newspaper_saudi,
                            rnd.nextGaussian() * wage_mean_expat + wage_mean_saudi,
                            rnd.nextGaussian() * productivity_mean_expat + productivity_mean_expat,
                            expat_minimum_wage,
                            saudi_minimum_wage,
                            expat_tax_percentage,
                            expat_tax_per_head,
                            auctioneer
                            
                     )
            );
        }

        apply_to_firm = new ArrayList<List<Worker>>();

        firms = new ArrayList<Firm>();


        for (int i = 0; i < num_firms; i++)
        {
            ArrayList<Worker> applications = new ArrayList<Worker>();
            apply_to_firm.add(applications);
            firms.add(
                    new Firm(
                            i,
                            seed_generator.nextLong(),
                            applications,
                            newspaper_saudi,
                            newspaper_expat,
                            auctioneer,
                            sauditization_percentage)
            );
        }
    }

    public static void run()
    {
        for (int day = 0; day < 1000; day++)
        {
            for (Firm firm: firms)
            {
                firm.set_prices_demand();
            }
            newspaper_saudi.clear_job_ads();
            newspaper_expat.clear_job_ads();
            for (Firm firm: firms)
            {
                firm.advertise();
            }
            for (Worker worker: workers)
            {
                worker.apply();
            }
            for (Firm firm: firms)
            {
                firm.hire();
            }
            for (Firm firm: firms)
            {
                firm.produce();
            }
            for (Firm firm: firms)
            {
                firm.post_offer();
            }
            auctioneer.compute_market();
            for (Firm firm: firms)
            {
                firm.sell();
            }
            for (Firm firm: firms)
            {
                firm.pay_wage();
            }
            for (Firm firm: firms)
            {
                firm.distribute_profits();
            }
            for (Firm firm: firms)
            {
                firm.fire();
            }
            if (day % 10 == 0)
            {
                int employed = 0;

                System.out.print(" day" + day);
            	System.out.print(" time "+ (System.currentTimeMillis() - initialTime));
            	System.out.print(" employed ");


                for (Worker w: workers)
                {
                    if (w.isEmployed())
                    {
                    employed++;
                    }
                }
               System.out.println(employed);

            }
        }
    }

    public static void main(String [] args)
    {
        initialisation();
        run();
        System.out.print("end");
    }
}
