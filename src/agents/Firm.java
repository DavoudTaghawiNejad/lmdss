package agents;
import messages.*;
import tools.*;
import definitions.Citizenship;
import tools.Policy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.Math.*;






public class Firm {
    public final int id;
    private final Rnd rnd;
    private final Newspaper newspaper_saudis;
    private final Newspaper newspaper_expats;
    private final double fixed_cost;
    private int visa_length;
    private Map<Integer, Group> visa_stack = new HashMap<Integer, Group>();
    private double net_worth;
    private double profit;
    private double price = 300;
    private double demand = 1;
    private double market_price = 300;
    private double planned_production = 400;
    private double distributed_profits;
    private List<WorkerRecord> applications;
    private Group staff = new Group(this);
    private boolean no_fake_probation = true;
    private Auctioneer auctioneer;
    private AtomicInteger day;
    private int distance_to_cut_off;
    private int before_saudis;
    private int before_expats;
    private double price_step_increase;
    private double price_step_decrease;
    private double planned_production_step_decrease;
    private double planned_production_step_increase;
    private final double minimum_mark_up;
    private final double days_pay_must_be_available;
    private double required_roi;
    private double percent_distribute;
    private double min_net_worth;
    private double production_function_exponent;
    
    public double stats_increase_price = 0;
    public int stats_num_applications;
    public double stats_accepted_wage_expats;
    public double stats_accepted_wage_saudis;
    public int stats_new_hires_saudi;
    public int stats_new_hires_expat;
    public double stats_decrease_price_bounded;
    public double stats_offer_wage_expats;
    public double stats_offer_wage_saudis;
    public int stats_net_hires = 0;
    public int stats_this_round_hire = 0;
    public int stats_this_round_fire = 0;
    private final double wage_step_saudis;
    private final double wage_step_expats;
    private int no_staff = 0;
    private int time_idle;
    private double[] quotas;

    public Firm(
            int id,
            long seed,
            ArrayList<WorkerRecord> post_box_applications,
            Newspaper newspaper_saudis,
            Newspaper newspaper_expats,
            Auctioneer auctioneer,
            AtomicInteger day,
            Assumptions assumptions,
            Policy initial_policy,
            double net_worth
    )
    {
        this.id = id;
        this.applications = post_box_applications;
        this.newspaper_saudis = newspaper_saudis;
        this.newspaper_expats = newspaper_expats;
        this.auctioneer = auctioneer;
        this.wage_step_expats = assumptions.wage_step_expat;
        this.wage_step_saudis = assumptions.wage_step_saudi;
        this.day = day;
        this.price_step_increase = assumptions.price_step_increase;
        this.price_step_decrease = assumptions.price_step_decrease;
        this.rnd = new Rnd(seed);
        this.visa_length = initial_policy.visa_length;
        this.planned_production_step_decrease = assumptions.planned_production_step_decrease;
        this.planned_production_step_increase = assumptions.planned_production_step_increase;
        this.minimum_mark_up = assumptions.minimum_mark_up;
        this.days_pay_must_be_available = assumptions.days_pay_must_be_available;
        this.net_worth = net_worth;
        this.min_net_worth = net_worth;
        this.required_roi = assumptions.required_roi;
        this.percent_distribute = assumptions.percent_distribute;
        this.production_function_exponent = assumptions.production_function_exponent;
        this.fixed_cost = assumptions.fixed_cost;
        this.time_idle = assumptions.time_idle;
        this.quotas = initial_policy.getQuotas();
    }

    /**
     * Price, and production target setting. Firms planned production and prices are set adaptively. Planned production
     * is adaptively increased, when there is excess demand, with regard to the planning - decreased in the opposite
     * case. Prices are modified, whenever the firm doesn’t find workers to meet its production target. (which feeds
     * back to the observed demand).
     *
     * When observed demand exceeds/falls short of planned production, planned production is increased/decreased.
     *
     * When a firm's actual production exceeds their planned production, by more than the productivity of the average
     * worker in that firm, the firm decrease it's price. But never below it's marginal costs. If the production falls
     * short by more than the average productivity of a worker, the price is increased. The increase/decrease of the
     * price is a uniform random percentage. The mean is a sector parameter.
     */
    public void set_prices_and_planned_production()
    {
        if (staff.getProductivity() > planned_production + average_productivity())
        {
                decrease_price_bounded_by_margine();
        }
        else if (staff.getProductivity() < planned_production - average_productivity())
        {
                increase_price();
        }

        if (demand > planned_production)
        {
            increase_planned_production_bounded_by_demand();
        }
        else
        {
            decrease_planned_production_bounded_by_demand();
        }
    }


    public void new_policy(
            HashMap<String,
                    Double> before_policy,
            HashMap<String, Double> after_policy,
            double[] quotas
    )
    {
        staff.recalculate_wage(before_policy, after_policy);
        this.quotas = quotas;
        visa_length = (int)Math.ceil(after_policy.get("visa_length"));
    }

    private void decrease_price_bounded_by_margine()
    {
        double before = price;
        price = Math.max(
                    ((staff.getWage() / staff.getProductivity()) * minimum_mark_up),
                price * (1 - rnd.uniform(price_step_decrease))
        );
        stats_decrease_price_bounded = price - before;
    }

    /**
     * Decreases planned_production by a random number; never below demand.
     */
    private void decrease_planned_production_bounded_by_demand()

    {
        planned_production = Math.max(demand, planned_production * (1 - rnd.uniform(planned_production_step_decrease)));
    }

    private void increase_price()
    {
        double before = price;
        price = price * (1 + rnd.uniform(price_step_increase));
        stats_increase_price = price - before;
    }

    private void increase_planned_production_bounded_by_demand() {
        planned_production = Math.min(demand, planned_production
                * (1 + rnd.uniform(planned_production_step_increase)));
    }

    /**
     * The advertised wage for Saudis and non Saudis is the firm's average wage in the respective category plus
     * a random gaussian variable. The standard deviation is a model parameter.
     */
    public void advertise()
    {        
        int num_visas_expiering_today = 0;
        int iday = day.get();
        try {
            num_visas_expiering_today = visa_stack.get(iday).size();
        } catch (Exception e) {
            num_visas_expiering_today = 0;
        }
        if (planned_production > staff.getProductivity()
            || num_visas_expiering_today > 0
           )
        {
            double add_wage_saudis;
            double add_wage_expats;

            if (staff.getWage_saudis() != 0 &&
                staff.getSaudis() != 0)
            {
                add_wage_saudis = staff.getWage_saudis() / staff.getSaudis() * (1 + rnd.nextGaussian(wage_step_saudis));
                if (add_wage_saudis <= 0)
                {
                    add_wage_saudis = newspaper_saudis.getAverage_wage_offer() * (1 + rnd.nextGaussian(wage_step_saudis));
                }
            }
            else
            {
                add_wage_saudis = newspaper_saudis.getAverage_wage_offer();
            }
            if (staff.getWage_expats() != 0 &&
                    staff.getExpats() != 0)
            {
                add_wage_expats = staff.getWage_expats() / staff.getExpats() * (1 + rnd.nextGaussian(wage_step_expats));
                if (add_wage_expats <= 0)
                {
                    add_wage_expats = newspaper_expats.getAverage_wage_offer() * (1 + rnd.nextGaussian(wage_step_expats));
                }
            }
            else
            {
             add_wage_expats = newspaper_expats.getAverage_wage_offer();
            }
            if (add_wage_saudis < 0)
            {
                add_wage_saudis = newspaper_saudis.getAverage_wage_offer();
            }
            if (add_wage_expats < 0)
            {
                add_wage_expats= newspaper_expats.getAverage_wage_offer();
            }


            newspaper_saudis.place_add(new JobAdd(applications, add_wage_saudis));
            newspaper_expats.place_add(new JobAdd(applications, add_wage_expats));
            stats_offer_wage_saudis = add_wage_saudis;
            stats_offer_wage_expats = add_wage_expats;
        }
    }

    public void hiring()
    {
        stats_num_applications = applications.size();
        Group can_be_fired = new Group(this);
        if (visa_stack.get(day.get()) != null)
        {
            for (WorkerRecord worker: visa_stack.remove(day.get()).getWorker_list())
            {
                if (staff.contains(worker))
                {
                    can_be_fired.add(worker);
                }
            }
        }

        if (applications.size() > 0 || can_be_fired.size() > 0) {
            List<WorkerRecord> to_consider = applications;
            to_consider.addAll(can_be_fired.getWorker_list());
            ArrayList<WorkerRecord> set_aside = new ArrayList<WorkerRecord>();
            Group team = new Group(staff, this);
            team.removeAll(can_be_fired);
            Group potential_team = new Group(team, this);
            WorkerRecord best = null;
            WorkerRecord best_aside = null;
            WorkerRecord best_apps = null;
            int last_set_aside_size = to_consider.size();

            while (planned_production > h_produce(team, 0)
                    && team.getWage() * days_pay_must_be_available <= net_worth
                    ) {

                if ((to_consider.size() > 0) && (set_aside.size() == 0)) {
                    best = pop_best(team, to_consider);
                } else if ((to_consider.size() == 0)
                        && (set_aside.size() == 0))
                    break;
                else if ((to_consider.size() == 0)
                        && (set_aside.size() > 0)) {
                    if (set_aside.size() < last_set_aside_size) {
                        last_set_aside_size = set_aside.size();
                        to_consider.addAll(set_aside);
                        set_aside.clear();
                    } else {
                        break;
                    }
                } else if ((to_consider.size() > 0)
                        && (set_aside.size() > 0)) {
                    best_aside = get_best(team, set_aside);
                    best_apps = get_best(team, to_consider);
                    if (is_admissible(potential_team, best_aside)
                            && net_benefit(team, best_aside) > net_benefit(
                            team, best_apps)) {
                        best = best_aside;
                        set_aside.remove(best);
                    } else {
                        best = best_apps;
                        to_consider.remove(best);
                    }
                }

                if (!is_admissible(potential_team, best)) {
                    if (net_benefit(team, best) > 0) {
                        set_aside.add(best);
                    }
                    continue;
                }
                if ((is_admissible(team, best))
                        && (net_benefit(team, best) > 0)) {
                    team.add(best);
                    potential_team.add(best);
                } else {
                    potential_team.add(best);
                    if (net_benefit(team, potential_team)) {
                        team.addAll(potential_team);
                    }
                }
            }

            if ((no_fake_probation) && (team.size() > 0)) {
                ArrayList<WorkerRecord> force_keeps = new ArrayList<WorkerRecord>(can_be_fired.getWorker_list());
                force_keeps.removeAll(team.getWorker_list());
                while (force_keeps.size() > 0
                        && !is_admissible(team)) {
                    best = pop_best(team, force_keeps);
                    if (best.getCitizenship() == Citizenship.SAUDI) {
                        team.add(best);
                    }
                }
            }

            stats_net_hires = hire_or_fire_staff(team, can_be_fired);
        }
    }

    public void produce() {

    }

    public void post_offer()
    {
        auctioneer.make_final_good_offer(this, price, staff.getProductivity());
    }

    public void sell()
    {
        profit = min(demand, staff.getProductivity()) * price;
    }

    public void pay_wage() {
        profit -= staff.getWage();
    }

    public void add_or_distribute_profits() {
        distributed_profits = 0;
        net_worth += profit;
        if (profit < required_roi * net_worth) {
            distributed_profits = net_worth - percent_distribute * profit;
            if (net_worth - distributed_profits < min_net_worth) {
                distributed_profits = 0;
            }
            net_worth -= distributed_profits;
        }
    }

    public void firing() {
        if ((profit < 0) && (staff.size() > 0)) {

            ArrayList<WorkerRecord> to_consider = new ArrayList<WorkerRecord>(staff.getWorker_list());

            ArrayList<WorkerRecord> set_aside = new ArrayList<WorkerRecord>();
            Group team = new Group(this);

            Group potential_team = new Group(this);

            WorkerRecord best = null;
            WorkerRecord best_aside = null;
            WorkerRecord best_apps = null;
            int last_set_aside_size = to_consider.size();

            while (planned_production > h_produce(team, 0)) {
                if ((to_consider.size() > 0) && (set_aside.size() == 0)) {
                    best = pop_best(team, to_consider);
                } else if ((to_consider.size() == 0)
                        && (set_aside.size() == 0))
                    break;
                else if ((to_consider.size() == 0)
                        && (set_aside.size() > 0)) {
                    if (set_aside.size() < last_set_aside_size) {
                        last_set_aside_size = set_aside.size();
                        to_consider.addAll(set_aside);
                        set_aside.clear();
                    } else {
                        break;
                    }
                } else if ((to_consider.size() > 0)
                        && (set_aside.size() > 0)) {
                    best_aside = get_best(team, set_aside);
                    best_apps = get_best(team, to_consider);
                    if (is_admissible(potential_team, best_aside)
                            && net_benefit(team, best_aside) > net_benefit(
                            team, best_apps)) {
                        best = best_aside;
                        set_aside.remove(best);
                    } else {
                        best = best_apps;
                        to_consider.remove(best);
                    }
                }

                if (!is_admissible(potential_team, best)) // check!!!
                {
                    set_aside.add(best);
                    continue;
                }
                if ((is_admissible(team, best))
                        && (net_benefit(team, best) > 0)) {
                    team.add(best);
                    potential_team.add(best);
                } else {
                    potential_team.add(best);
                    if (net_benefit(team, potential_team)) {
                        team.addAll(potential_team);
                    }
                }
            }
            List<WorkerRecord> blue_list = new ArrayList<WorkerRecord>(staff.getWorker_list());
            blue_list.removeAll(team.getWorker_list());
            fire_staff(blue_list);
        }
    }

    double h_produce(Group team, double additional) {

        double p = additional;
        p += team.getProductivity();
        return Math.pow(p, production_function_exponent);

    }

    void fire_staff(List<WorkerRecord> layoffs) {

        for (WorkerRecord worker : layoffs) {
            assert staff.contains(worker);
            worker.getAddress().sendFire();
            stats_net_hires--;
        }
    }

    WorkerRecord pop_best(Group team, List<WorkerRecord> to_evaluate) {

        WorkerRecord best = get_best(team, to_evaluate);
        to_evaluate.remove(best);
        return best;
    }

    WorkerRecord get_best(Group team, List<WorkerRecord> to_evaluate) {

        double max = 0;
        double current;
        if (to_evaluate.size() == 0)
            return null;
        WorkerRecord best = to_evaluate.get(0);
        for (WorkerRecord worker : to_evaluate) {
            current = net_benefit(team, worker);
            if (current > max) {
                best = worker;
                max = current;
            }
        }
        return best;

    }

    boolean is_admissible(Group team, WorkerRecord to_evaluate) {

        double saudi = team.getSaudis();
        double expat = team.getExpats();
        int size = staff.getSaudis() + staff.getExpats();
        double sauditization_percentage;
        if (size < 10)
        {
            sauditization_percentage = quotas[1];
        }
        else if (size < 50)
        {
            sauditization_percentage = quotas[2];
        }
        else if (size < 500)
        {
            sauditization_percentage = quotas[3];
        }
        else if (size < 3000)
        {
            sauditization_percentage = quotas[4];
        }
        else
        {
            sauditization_percentage = quotas[5];
        }
        if (to_evaluate.getCitizenship() == Citizenship.SAUDI)
            saudi++;
        else
            expat++;
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }


    boolean is_admissible(Group team) {
        double saudi = team.getSaudis();
        double expat = team.getExpats();
        int size = staff.getSaudis() + staff.getExpats();
        double sauditization_percentage;
        if (size < 10)
        {
            sauditization_percentage = quotas[1];
        }
        else if (size < 50)
        {
            sauditization_percentage = quotas[2];
        }
        else if (size < 500)
        {
            sauditization_percentage = quotas[3];
        }
        else if (size < 3000)
        {
            sauditization_percentage = quotas[4];
        }
        else
        {
            sauditization_percentage = quotas[5];
        }
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }


    double net_benefit(Group team, WorkerRecord worker)
    {
        return price
                * (min(planned_production, h_produce(team, worker.getProductivity())) - min(
                planned_production, h_produce(team, 0))) - worker.getWage();        //return price * worker.getProductivity()- worker.job_add.wage;

    }

    boolean net_benefit(Group team, Group potential_team) {

        return price
                * (min(planned_production, h_produce(potential_team, 0)) - min(
                planned_production, h_produce(team, 0))) > potential_team.getWage()
                - team.getWage();
    }
    int count_citizenship(List<Worker> team, Citizenship citizenship) {

        int counter = 0;
        for (Worker worker : team) {
            if (worker.getCitizenship() == citizenship) {
                counter += 1;
            }
        }
        return counter;

    }

    void hire(WorkerRecord worker)
    {

        staff.add(worker);
        worker.getAddress().sendEmploy(this, worker);
        stats_this_round_hire++;
    }

    void fire(WorkerRecord worker)
    {

        disemploy(worker);
        worker.getAddress().sendFire();
        stats_this_round_fire++;
    }

    void disemploy(WorkerRecord worker)
    {
        staff.remove(worker);
    }


    public void sendQuit(WorkerRecord worker)
    {
        disemploy(worker);
    }

    int hire_or_fire_staff(Group team, Group can_be_fired) {

        int initial_staff = staff.size();
        for (WorkerRecord worker : team.getWorker_list()) {
            if (staff.contains(worker)) {
                can_be_fired.remove(worker);
                if (worker.getCitizenship() == Citizenship.EXPAT) {
                    addVisa(worker);
                }
            } else {
                hire(worker);
                update_wage(worker.getCitizenship(), worker.getWage());
            }
        }
        applications.clear();

        for (WorkerRecord worker : can_be_fired.getWorker_list()) {

            fire(worker);
        }
        can_be_fired.clear();

        return staff.size() - initial_staff;
    }

    private void update_wage(Citizenship citizenship, double wage)
    {
        if (citizenship == Citizenship.SAUDI)
        {
            stats_accepted_wage_saudis += wage;
            stats_new_hires_saudi++;
        }
        else
        {
            stats_accepted_wage_expats += wage;
            stats_new_hires_expat++;
        }
    }

    double average_productivity()
    {
        return staff.getProductivity() / staff.size();
    }



    public void send_market_price_individual_demand(double market_price, double demand)
    {
        this.market_price = market_price;
        this.demand = demand;
    }

    public boolean out_of_business()
    {
        if (staff.size() == 0)
        {
            no_staff += 1;
        } else
        {
            no_staff = 0;
        }
        if (net_worth <= 0 || (no_staff == time_idle && rnd.uniform(1) < 0.25))
        {
                fire_staff(staff.getWorker_list());
                return true;
        }
        else
        {
            return false;
        }
    }

    private void addVisa(WorkerRecord worker)
    {
        Integer visa_date = day.get() + visa_length;
        Group day_list = visa_stack.get(visa_date);
        if (day_list == null)
        {
            day_list = new Group(this);
            visa_stack.put(visa_date, day_list);
        }
        day_list.add(worker);
    }

    public double wage_bill()
    {
        return staff.getWage();
    }

    public int num_expats()
    {
        return staff.getExpats();
    }

    public int getNum_saudis()
    {
        return staff.getSaudis();
    }

    public double getProduction()
    {
        return staff.getProductivity();
    }

    public double getWage_saudis()
    {
        assert staff.getWage_expats() == staff.getWage_expats();
        return staff.getWage_saudis();
    }

    public double getWage_expats()
    {
        assert staff.getWage_saudis() == staff.getWage_saudis();
        return staff.getWage_expats();
    }

    public void setKinky(double quota) {

        int saudis = staff.getSaudis();
        distance_to_cut_off = (int) Math.ceil((staff.getExpats() + saudis) * quota - saudis);
        before_saudis = saudis;
        before_expats = staff.getSaudis();
    }

    public int getDistance_to_cut_off() {
        return distance_to_cut_off;
    }

    public int getBefore_saudis() {
        return before_saudis;
    }

    public int getBefore_expats() {
        return before_expats;
    }

    public Group getStaff()
    {
        return staff;
    }

    public double getNet_worth()
    {
        return net_worth;
    }

    public double getPrice()
    {
        return price;
    }

    public double getDemand()
    {
        return demand;
    }

    public double getDistributed_profits()
    {
        return distributed_profits;
    }

    public double getProfit()
    {
        return profit;
    }

    public double getPlanned_production()
    {
        return planned_production;
    }

    public double getMarket_price()
    {
        return market_price;
    }

    public void fix_costs()
    {
        net_worth -= fixed_cost;
    }
}
