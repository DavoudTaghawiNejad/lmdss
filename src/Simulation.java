
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import agents.*;
import definitions.Citizenship;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import tools.*;

public class Simulation
{
    private final List<Worker> workers;
    private final Auctioneer auctioneer;
    private final int setup_workers;
    private final int setup_firms;
    private final Random seed_generator;
    private final List<List<WorkerRecord>> apply_to_firm;
    private final List<Firm> firms;
    private final Newspaper newspaper_saudi;
    private final Newspaper newspaper_expat;
    private final AtomicInteger day = new AtomicInteger();
    private final DBConnection db_connection;
    private final ArrayList<Firm> firms_reserve = new ArrayList<Firm>();
    private final tools.Assumptions assumptions;
    private final boolean time_series;
    private final boolean panel_data;
    private final String sha;
    private CalibrationStatistics after_policy_calibration_statistics;
    private CalibrationStatistics before_policy_calibration_statistics;
    private tools.Policy after_policy;
    private tools.Policy before_policy;
    private KinkyStatistics before_after;

    public Simulation(String options, JSONObject dictionary) throws Exception
    {
        if (!dictionary.keySet().contains("assumptions"))
            throw new Exception("no assumptions in json");
        if (!dictionary.keySet().contains("before_policy"))
            throw new Exception("no before_policy in json");
        if (!dictionary.keySet().contains("after_policy"))
            throw new Exception("no after_policy in json");
        try {
            assumptions = new tools.Assumptions((JSONObject) dictionary.get("assumptions"));
            before_policy = new tools.Policy((JSONObject) dictionary.get("before_policy"));
            after_policy = new tools.Policy((JSONObject) dictionary.get("after_policy"));
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


        int num_expats = assumptions.num_expats;
        int num_saudis = assumptions.num_saudis;
        setup_workers = (int) Math.ceil((double) (num_expats + num_saudis) / assumptions.setup_period_1);
        setup_firms = (int) Math.ceil((double) assumptions.num_firms / assumptions.setup_period_1);

        long seed = assumptions.seed;
        seed_generator = new Random(seed);
        Random rnd = new Random(seed_generator.nextLong());
        sha = assumptions.sha();
        if (time_series || panel_data)
        {
            db_connection = new DBConnection(sha);
        }
        else
            db_connection = null;

        auctioneer = new Auctioneer(assumptions.love_for_variety, assumptions.sector_spending);

        newspaper_saudi = new Newspaper(seed_generator.nextLong());
        newspaper_expat = new Newspaper(seed_generator.nextLong());

        workers = new ArrayList<Worker>();


        create_workers(
                num_saudis,
                num_expats,
                assumptions.productivity_mean_saudi,
                assumptions.productivity_mean_expat,
                before_policy.expat_minimum_wage,
                before_policy.saudi_minimum_wage,
                before_policy.saudi_tax_percentage,
                before_policy.expat_tax_percentage,
                before_policy.saudi_tax_per_head,
                before_policy.expat_tax_per_head,
                assumptions.reservation_wage_mean_saudi,
                assumptions.reservation_wage_mean_expat,
                assumptions.reservation_wage_std_saudi,
                assumptions.reservation_wage_std_expat,
                assumptions.productivity_std_saudi,
                assumptions.productivity_std_expat,
                assumptions.reapplication_probability_saudi,
                assumptions.reapplication_probability_expat,
                rnd
        );

        apply_to_firm = new ArrayList<List<WorkerRecord>>();
        firms = new ArrayList<Firm>();
        create_firms(assumptions.num_firms, assumptions, rnd);
    }

    private void create_workers(
            double num_saudis, double num_expats,
            double productivity_mean_saudi, double productivity_mean_expat,
            double expat_minimum_wage, double saudi_minimum_wage,
            double saudi_tax_percentage, double expat_tax_percentage,
            double saudi_tax_per_head, double expat_tax_per_head,
            double reservation_wage_mean_saudi, double reservation_wage_mean_expat,
            double reservation_wage_std_saudi, double reservation_wage_std_expat,
            double productivity_std_saudi, double productivity_std_expat,
            double reapplication_probability_saudi, double reapplication_probability_expat,
            Random rnd
    )
    {
        _create_workers(
            Citizenship.SAUDI,
            num_saudis,
            reservation_wage_mean_saudi, reservation_wage_std_saudi,
            productivity_mean_saudi, productivity_std_saudi,
            reapplication_probability_saudi,
            saudi_minimum_wage,
            saudi_tax_percentage,
            saudi_tax_per_head,
            rnd
        );
        _create_workers(
            Citizenship.EXPAT,
            num_expats,
            reservation_wage_mean_expat, reservation_wage_std_expat,
            productivity_mean_expat, productivity_std_expat,
            reapplication_probability_expat,
            expat_minimum_wage,
            expat_tax_percentage,
            expat_tax_per_head,
            rnd
        );
        Collections.shuffle(workers, new Random(seed_generator.nextLong()));
    }

    private void _create_workers(
            Citizenship citizenship,
            double number,
            double reservation_wage, double reservation_wage_std,
            double productivity_mean, double productivity_std,
            double reapplication_probability,
            double minimum_wage,
            double tax_percentage,
            double tax_per_head,
            Random rnd
    )
    {
        for (int i = 0; i < number; i++)
        {
            workers.add(
                    new Worker(
                            seed_generator.nextLong(),
                            citizenship,
                            newspaper_saudi,
                            rnd.nextGaussian() * reservation_wage_std + reservation_wage,
                            rnd.nextGaussian() * productivity_std + productivity_mean,
                            minimum_wage,
                            tax_percentage,
                            tax_per_head,
                            reapplication_probability,
                            auctioneer
                    )
            );
        }
    }

    private void create_firms(int number, tools.Assumptions assumptions, Random rnd)
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
                            day,
                            assumptions,
                            before_policy,
                            Math.max(rnd.nextGaussian() * assumptions.initial_net_worth_std + assumptions.initial_net_worth_mean, 0)
                    )
            );
        }
    }

    public final JSONObject run() throws IOException, ParseException
    {
        after_policy_calibration_statistics = new CalibrationStatistics(firms);
        before_policy_calibration_statistics = new CalibrationStatistics(firms);
        before_after = new KinkyStatistics(firms);


        int simulation_length = assumptions.simulation_length;
        int policy_change_time = assumptions.policy_change_time;

        JSONObject output = new JSONObject();
        output.put("parameter", assumptions.toJson());

        for (int iday = 0; iday < simulation_length; iday++)
        {
            day.set(iday);
            auctioneer.new_round();
            if (iday < assumptions.setup_period_1)
            {
                firms.addAll(firms_reserve.subList(0, Math.min(setup_firms, firms_reserve.size())));
                firms_reserve.subList(0, Math.min(setup_firms, firms_reserve.size())).clear();
                auctioneer.income = assumptions.sector_spending * ((double)iday / (double) assumptions.setup_period_1);
            }
                                    newspaper_saudi.clear_job_ads();
                                    newspaper_expat.clear_job_ads();
            for (Firm firm : firms) firm.advertise();
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
            for (Firm firm : firms) firm.hiring();
            for (Firm firm : firms) firm.produce();
            for (Firm firm : firms) firm.post_offer();
                                    auctioneer.compute_market();
            for (Firm firm : firms) firm.sell();
            for (Firm firm : firms) firm.pay_wage();
            for (Firm firm : firms) firm.add_or_distribute_profits();
            //firm.firing();
            for (Firm firm : firms) firm.set_prices_and_planned_production();

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

        if (db_connection != null)
        {
            db_connection.close();
        }
        output.put("before_policy", before_policy_calibration_statistics.json());
        output.put("after_policy", after_policy_calibration_statistics.json());
        output.put("k_saudis", before_after.saudis());
        output.put("k_expats", before_after.expats());
        output.put("k_sauditization", before_after.sauditization());
        output.put("display_start_day", assumptions.setup_period_1 + assumptions.setup_period_2);
        return output;
    }



    private void statistics(int iday, int policy_change_time, int simulation_length)
    {


        if(time_series)
        {
            db_connection.write_aggregate_firm_statistics(firms, iday);
        }
        if(panel_data)
        {
            db_connection.write_firm_statistics(firms, iday);
        }

        if (iday ==  policy_change_time - 1)
        {
            before_after.before();
        }
        if (iday ==  simulation_length - 1)
        {
            before_after.after();
        }
        if (iday >  policy_change_time - 200 && iday < policy_change_time)
        {
            before_policy_calibration_statistics.update();
        }
        if (iday >  simulation_length - 200)
        {
            after_policy_calibration_statistics.update();
        }
    }

    public String getSha()
    {
        return sha;
    }

}
