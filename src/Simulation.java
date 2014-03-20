
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import agents.*;
import definitions.Citizenship;
import definitions.WorkerStatistics;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import tools.DBConnection;
import tools.WorkerRecord;

public class Simulation
{
    private final List<Worker> workers;
    private final Auctioneer auctioneer;
    private final int setup_workers;
    private final int setup_firms;
    private final int setup_period;
    private final Random seed_generator;
    private final List<List<WorkerRecord>> apply_to_firm;
    private final List<Firm> firms;
    private final Newspaper newspaper_saudi;
    private final Newspaper newspaper_expat;
    private final AtomicInteger day = new AtomicInteger();
    private final DBConnection db_connection;
    private final ArrayList<Firm> firms_reserve = new ArrayList<Firm>();
    private final Assumptions assumptions;
    private final boolean time_series;
    private final boolean panel_data;
    private final String sha;
    private CalibrationStatistics after_policy_calibration_statistics;
    private CalibrationStatistics before_policy_calibration_statistics;
    private Policy after_policy;
    private Policy before_policy;

    public Simulation(String options, String parameters) throws Exception {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(parameters);
        JSONObject dictionary = (JSONObject) obj;

        try {
            assumptions = Assumptions.Assumptions(dictionary.get("assumptions").toString());
            before_policy = Policy.Policy(dictionary.get("before_policy").toString());
            after_policy = Policy.Policy(dictionary.get("after_policy").toString());
        } catch (Exception e) {
            System.out.println(dictionary.toString());
            e.printStackTrace();
            throw new Exception(e);
        }

        if (options.contains("t"))
        {
            time_series = true;
        }
        else
            time_series = false;
        if (options.contains("p"))
        {
            panel_data = true;
        }
        else
            panel_data = false;


        int num_expats = assumptions.getNum_expats();
        int num_saudis = assumptions.getNum_saudis();

        setup_period = 500;
        setup_workers = (int) Math.ceil((double) (num_expats + num_saudis) / setup_period);
        setup_firms = (int) Math.ceil((double) assumptions.getNum_firms() / setup_period);

        long seed = assumptions.getSeed();
        seed_generator = new Random(seed);
        Random rnd = new Random(seed_generator.nextLong());
        sha = assumptions.sha();
        if (time_series || panel_data)
        {
            db_connection = new DBConnection(sha);
        }
        else
            db_connection = null;

        auctioneer = new Auctioneer(assumptions.getLove_for_variety(), assumptions.getSector_spending());

        newspaper_saudi = new Newspaper(seed_generator.nextLong());
        newspaper_expat = new Newspaper(seed_generator.nextLong());

        workers = new ArrayList<Worker>();


        create_workers(num_saudis, num_expats, assumptions.getProductivity_mean_saudi(), assumptions.getProductivity_mean_expat(), before_policy.getExpat_minimum_wage(),
                before_policy.getSaudi_minimum_wage(), before_policy.getSaudi_tax_percentage(), before_policy.getExpat_tax_percentage(), before_policy.getSaudi_tax_per_head(), before_policy.getExpat_tax_per_head(),
                assumptions.getReapplication_probability(), 0, assumptions.getReservation_wage_saudi(), assumptions.getReservation_wage_expat(), rnd
        );

        apply_to_firm = new ArrayList<List<WorkerRecord>>();
        firms = new ArrayList<Firm>();
        create_firms(assumptions.getNum_firms(), before_policy.getSauditization_percentage(), assumptions.getWage_std(), before_policy.getVisa_length());
    }

    private final void create_workers(double num_saudis, double num_expats, double productivity_mean_saudi, double productivity_mean_expat,
                                      double expat_minimum_wage, double saudi_minimum_wage, double saudi_tax_percentage, double expat_tax_percentage,
                                      double saudi_tax_per_head, double expat_tax_per_head, double reapplication_probability_saudi,
                                      double reapplication_probability_expat, double reservation_wage_saudi, double reservation_wage_expat, Random rnd
    )
    {
        _create_workers(Citizenship.SAUDI, num_saudis, reservation_wage_saudi, productivity_mean_saudi, saudi_minimum_wage, saudi_tax_percentage, saudi_tax_per_head, reapplication_probability_saudi, rnd);
        _create_workers(Citizenship.EXPAT, num_expats, reservation_wage_expat, productivity_mean_expat, expat_minimum_wage, expat_tax_percentage, expat_tax_per_head, reapplication_probability_expat, rnd);
        Collections.shuffle(workers, new Random(seed_generator.nextLong()));
    }

    private final void _create_workers(Citizenship citizenship, double number, double reservation_wage, double productivity_mean, double minimum_wage, double tax_percentage, double tax_per_head,
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

    private final void create_firms(int number, double initial_sauditization_percentage, double wage_std, int visa_length)
    {
        final int last_id = 0;
        for (int i = 0; i < number; i++)
        {
            ArrayList<WorkerRecord> applications = new ArrayList<WorkerRecord>();
            apply_to_firm.add(applications);
            firms_reserve.add(
                    new Firm(
                            last_id + i,
                            seed_generator.nextLong(),
                            applications,
                            newspaper_saudi,
                            newspaper_expat,
                            auctioneer,
                            initial_sauditization_percentage,
                            visa_length,
                            day,
                            wage_std
                    )
            );
        }
    }

    public final JSONObject run()
    {
        after_policy_calibration_statistics = new CalibrationStatistics(firms);
        before_policy_calibration_statistics = new CalibrationStatistics(firms);


        int simulation_length = assumptions.getSimulation_length();
        int policy_change_time = assumptions.getPolicy_change_time();

        JSONObject output = new JSONObject();
        output.put("parameter", assumptions.json());

        for (int iday = 0; iday < simulation_length; iday++)
        {
            day.set(iday);
            auctioneer.new_round();
            if (iday < setup_period)
            {
                firms.addAll(firms_reserve.subList(0, Math.min(setup_firms, firms_reserve.size())));
                firms_reserve.subList(0, Math.min(setup_firms, firms_reserve.size())).clear();
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

                    firms.remove(h);
                }
            }
            statistics(iday, policy_change_time, simulation_length);

            if (iday == policy_change_time)
            {
                after_policy.change_policy_for_workers(workers);
                for (Firm firm : firms)
                {
                    firm.set_new_policy(before_policy.dump_policy(), after_policy.dump_policy());
                }
            }
        }
        WorkerStatistics.net_contribution(workers, auctioneer.market_price, "final");
        if (db_connection != null)
        {
            db_connection.close();
        }
        output.put("before_policy", after_policy_calibration_statistics.json());
        output.put("after_policy", after_policy_calibration_statistics.json());
        return output;
    }



    private final void statistics(int iday, int policy_change_time, int simulation_length)
    {


        if(time_series)
        {
            db_connection.write_aggregate_firm_statistics(firms, iday);
        }
        if(panel_data)
        {
            db_connection.write_firm_statistics(firms, iday);
        }

        if (iday >  policy_change_time - 200 && iday < policy_change_time)
        {
            before_policy_calibration_statistics.update();
        }
        if (iday >  simulation_length - 200)
        {
            after_policy_calibration_statistics.update();
        }

        //if (iday == policy_change_time - 1)
        //{
        //    WorkerStatistics.net_contribution(workers, auctioneer.market_price, "before_policy_change");
        //}
    }

    public String getSha()
    {
        return sha;
    }

}
