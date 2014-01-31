

import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import agents.*;
import definitions.Citizenship;
import definitions.WorkerStatistics;


public class Main
{
    private static double sauditization_percentage;
    private static List<Worker> workers;
    private static Auctioneer auctioneer;
    private static FirmStats statistics_firms;
    private static int setup_workers;
    private static int setup_firms;
    private static int setup_period;
    private static int num_firms;
    private static Random seed_generator;
    private static int simulation_length;
    private static int policy_change_time;
	private static List<List<Worker>> apply_to_firm;
	private static List<Firm> firms;
	private static Newspaper newspaper_saudi;
	private static Newspaper newspaper_expat;
    private static AtomicInteger day = new AtomicInteger();

    public static void initialisation()
    {
        num_firms = 100;
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
        final double reapplication_probability = 0.03 / 356;

        setup_period = 500;
        simulation_length = 1600;
        policy_change_time = 1100;
        setup_workers = (int) Math.ceil((double)(num_expats + num_saudis) / setup_period);
        setup_firms = (int) Math.ceil((double) num_firms / setup_period);

        //final long seed = (new Random().nextLong());
        final long seed = 5302877246224082029L;
        System.out.println(seed);
        seed_generator = new Random(seed);

        statistics_firms = new FirmStats(num_firms);

        auctioneer = new Auctioneer(0.5, 100000000);

        newspaper_saudi = new Newspaper(seed_generator.nextLong());
        newspaper_expat = new Newspaper(seed_generator.nextLong());

        workers = new ArrayList<Worker>();

        Random rnd = new Random(seed_generator.nextLong());

        for (int i = 0; i < num_saudis; i++)
        {
            workers.add(
                    new Worker(
                            seed_generator.nextLong(),
                            Citizenship.SAUDI,
                            newspaper_saudi,
                            rnd.nextGaussian() * wage_mean_saudi + wage_mean_saudi,
                            rnd.nextGaussian() * productivity_mean_saudi + productivity_mean_saudi,
                            expat_minimum_wage,
                            saudi_minimum_wage,
                            expat_tax_percentage,
                            expat_tax_per_head,
                            reapplication_probability, auctioneer
                     )
            );
        }
        for (int i = 0; i < num_expats; i++)
        {
            workers.add(
                    new Worker(
                            seed_generator.nextLong(),
                            Citizenship.EXPAT,
                            newspaper_saudi,
                            rnd.nextGaussian() * wage_mean_expat + wage_mean_expat,
                            rnd.nextGaussian() * productivity_mean_expat + productivity_mean_expat,
                            expat_minimum_wage,
                            saudi_minimum_wage,
                            expat_tax_percentage,
                            expat_tax_per_head,
                            reapplication_probability, auctioneer
                     )
            );
        }
        Collections.shuffle(workers, new Random(seed_generator.nextLong()));

        apply_to_firm = new ArrayList<List<Worker>>();

        firms = new ArrayList<Firm>();

    }

    private static void create_firms(int number)
    {
        for (int i = 0; i < number; i++)
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
                            sauditization_percentage,
                            day)
            );
        }
    }

    public static void run()
    {

        for (int iday = 0; iday < simulation_length; iday++)
        {
            day.set(iday);
            auctioneer.new_round();
            if (iday < setup_period)
            {
                create_firms(Math.min(setup_firms, num_firms - firms.size()));
            }
            newspaper_saudi.clear_job_ads();
            newspaper_expat.clear_job_ads();
            for (Firm firm: firms)
            {
                firm.advertise();
            }
            newspaper_saudi.calculate_average_wage_offer();
            newspaper_expat.calculate_average_wage_offer();
            int i = 0;
            for (Worker worker: workers)
            {
                worker.apply();
                i++;
                if (i > setup_workers * iday)
                {
                    break;
                }
            }
            for (Firm firm: firms)
            {
                firm.hiring();
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
                //firm.firing();
            }
            for (int h = firms.size()-1; h >= 0; h--) {
                if (firms.get(h).out_of_business())
                {
                    firms.remove(h);
                }
            }
            statistics(iday);

            if (iday == policy_change_time)
            {
                WorkerStatistics.net_contribution(workers, auctioneer.market_price, "before_policy");
                auctioneer.income *= 10;

            for (Firm firm: firms)
            {
                firm.set_prices_demand();
            }
        }
        WorkerStatistics.net_contribution(workers, auctioneer.market_price, "final");

    }

    private static void statistics(double iday) {
        if (
            iday >= 500
            && iday % 10 == 0
           )
        {
            System.out.print(iday);
            System.out.print("\t");
            updateFirmStatistics();
            System.out.println("");

        }
        if (iday == 1200)
        {
            WorkerStatistics.net_contribution(workers, auctioneer.market_price, "1200");
        }
    }


    private static void updateFirmStatistics() {
        statistics_firms.reset();
        for (Firm firm: firms)
        {
            statistics_firms.update(firm);
        }
        statistics_firms.printcsv();
    }

    public static void main(String [] args)
    {
        long started = System.currentTimeMillis();
        initialisation();
        run();
        System.out.print("end");
        System.out.print(System.currentTimeMillis() - started);

    }
}
