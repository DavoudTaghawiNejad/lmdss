import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import agents.*;
import com.sun.java.swing.plaf.gtk.resources.gtk_sv;
import definitions.Citizenship;
import definitions.WorkerStatistics;
import tools.DBConnection;
import tools.WorkerRecord;

public class Simulation
{
    private static double sauditization_percentage;
    private static List<Worker> workers;
    private static Auctioneer auctioneer;
    private static int setup_workers;
    private static int setup_firms;
    private static int setup_period;
    private static int num_firms;
    private static Random seed_generator;
    private static int simulation_length;
    private static int policy_change_time;
	private static List<List<WorkerRecord>> apply_to_firm;
	private static List<Firm> firms;
	private static Newspaper newspaper_saudi;
	private static Newspaper newspaper_expat;
    private static AtomicInteger day = new AtomicInteger();
    private static double wage_std;
    private static double reservation_wage_saudi;
    private static double reservation_wage_expat;
    private static DBConnection db_connection;


    public Simulation()
    {
        num_firms = 100;
        final double sauditization_percentage = 0;
        final int num_saudis = 3800;
        final int num_expats = 7000;
        final double productivity_mean_saudi = 6854.24 / 30;
        final double productivity_mean_expat = 6854.24 / 30;
        reservation_wage_saudi = 3137.39 / 30;
        reservation_wage_expat = 0;
        final double expat_minimum_wage = 0;
        final double saudi_minimum_wage = 0;
        final double expat_tax_percentage = 0;
        final double expat_tax_per_head = 0;
        final double reapplication_probability = 0.03 / 356;
        final double saudi_tax_percentage = 0;
        final double saudi_tax_per_head = 0;

        setup_period = 500;
        simulation_length = 2000;
        policy_change_time = 1500;
        wage_std = 3137.39 / 30;
        setup_workers = (int) Math.ceil((double)(num_expats + num_saudis) / setup_period);
        setup_firms = (int) Math.ceil((double) num_firms / setup_period);

        //final long seed = (new Random().nextLong());
        final long seed = 5302877246224082029L;
        System.out.println(seed);
        seed_generator = new Random(seed);
        db_connection = new DBConnection(seed);

        auctioneer = new Auctioneer(0.5, 1000000000);

        newspaper_saudi = new Newspaper(seed_generator.nextLong());
        newspaper_expat = new Newspaper(seed_generator.nextLong());

        workers = new ArrayList<Worker>();

        Random rnd = new Random(seed_generator.nextLong());

        create_workers(num_saudis, num_expats, productivity_mean_saudi, productivity_mean_expat, expat_minimum_wage,
                saudi_minimum_wage, saudi_tax_percentage, expat_tax_percentage, saudi_tax_per_head, expat_tax_per_head,
                reapplication_probability, 0, reservation_wage_saudi, reservation_wage_expat, rnd
        );
        Collections.shuffle(workers, new Random(seed_generator.nextLong()));

        apply_to_firm = new ArrayList<List<WorkerRecord>>();

        firms = new ArrayList<Firm>();
    }

    private static void create_workers(double num_saudis, double num_expats, double productivity_mean_saudi, double productivity_mean_expat,
                                       double expat_minimum_wage, double saudi_minimum_wage, double saudi_tax_percentage, double expat_tax_percentage,
                                       double saudi_tax_per_head, double expat_tax_per_head, double reapplication_probability_saudi, double reapplication_probability_expat, double reservation_wage_saudi, double reservation_wage_expat, Random rnd)
    {
        _create_workers(Citizenship.SAUDI, num_saudis, reservation_wage_saudi, productivity_mean_saudi, saudi_minimum_wage, saudi_tax_percentage, saudi_tax_per_head, reapplication_probability_saudi, rnd);
        _create_workers(Citizenship.EXPAT, num_expats, reservation_wage_expat, productivity_mean_expat, expat_minimum_wage, expat_tax_percentage, expat_tax_per_head, reapplication_probability_expat, rnd);

    }

    private static void _create_workers(Citizenship citizenship, double number, double reservation_wage, double productivity_mean, double minimum_wage, double tax_percentage, double tax_per_head,
                                        double reapplication_probability, Random rnd)
    {
        for (int i = 0; i < number; i++)
        {
            workers.add(
                    new Worker(
                            seed_generator.nextLong(),
                            citizenship,
                            newspaper_saudi,
                            rnd.nextGaussian() * reservation_wage + reservation_wage,
                            rnd.nextGaussian() * productivity_mean + productivity_mean,
                            minimum_wage,
                            tax_percentage,
                            tax_per_head,
                            reapplication_probability,
                            auctioneer                                                  
                     )
            );
        }
    }

    private static void create_firms(int number)
    {
        final int last_id = firms.size();
        for (int i = 0; i < number; i++)
        {
            ArrayList<WorkerRecord> applications = new ArrayList<WorkerRecord>();
            apply_to_firm.add(applications);
            firms.add(
                    new Firm(
                            last_id + i,
                            seed_generator.nextLong(),
                            applications,
                            newspaper_saudi,
                            newspaper_expat,
                            auctioneer,
                            sauditization_percentage,
                            day,
                            wage_std
                    )
            );
        }
    }

    public static void run() {

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
            for (Firm firm : firms)
            {
                firm.advertise();
            }
            newspaper_saudi.calculate_average_wage_offer();
            newspaper_expat.calculate_average_wage_offer();
            int i = 0;
            for (Worker worker : workers)
            {
                worker.apply(iday);
                i++;
                if (i > setup_workers * iday)
                {
                    break;
                }
            }
            for (Firm firm : firms)
            {
                firm.hiring();
            }
            for (Firm firm : firms)
            {
                firm.produce();
            }
            for (Firm firm : firms)
            {
                firm.post_offer();
            }
            auctioneer.compute_market();
            for (Firm firm : firms)
            {
                firm.sell();
            }
            for (Firm firm : firms)
            {
                firm.pay_wage();
            }
            for (Firm firm : firms)
            {
                firm.distribute_profits();
            }
            for (Firm firm : firms)
            {
                //firm.firing();
            }
            for (Firm firm : firms)
            {
                firm.set_prices_demand();
            }
            for (int h = firms.size() - 1; h >= 0; h--)
            {
                if (firms.get(h).out_of_business())
                {
                    System.out.println();
                    System.out.println(firms.get(h));
                    firms.remove(h);
                }
            }
            statistics(iday);

            if (iday == policy_change_time)
            {
                WorkerStatistics.net_contribution(workers, auctioneer.market_price, "before_policy");
                auctioneer.income *= 2;


            }
        }
        WorkerStatistics.net_contribution(workers, auctioneer.market_price, "final");
        db_connection.close();
    }

    private static void statistics(int iday) {

        if (
            iday >= 500
            && iday % 20 == 0
           )
        {
            db_connection.write_aggregate_firm_statistics(firms, iday);
            db_connection.write_firm_statistics(firms, iday);

        }
        if (iday == policy_change_time - 1)
        {
            WorkerStatistics.net_contribution(workers, auctioneer.market_price, "before_policy_change");
        }
    }
}
