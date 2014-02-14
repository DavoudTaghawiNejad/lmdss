package agents;


import messages.*;
import tools.*;
import definitions.Citizenship;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import static java.lang.Math.*;






public class Firm {
    private final Rnd rnd;
    private final Newspaper newspaper_saudis;
    private final Newspaper newspaper_expats;
    private Map<Integer, Group> visastack = new HashMap<Integer, Group>();

    public int id;
    public double net_worth = 1000;
    public double profit;
    public double price = 300;
    public double demand = 1;
    public double market_price = 300;
    public double planned_production = 400;
    public double distributed_profits;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    private List<Worker> applications;
    public int this_round_hire = 0;
    public int this_round_fire = 0;
    public Group staff = new Group(this);

    private double parameter_planned_production = 0.1;

    private double parameter_price = 2.0 / 356.0;

    public boolean no_fake_probation;
    private double sauditization_percentage;
    private Auctioneer auctioneer;
    private AtomicInteger day;
    public int net_hires = 0;
    public double stats_increase_price = 0;
    public int num_applications;
    public double stats_accepted_wage_expats;
    public double stats_accepted_wage_saudis;
    public int stats_new_hires_saudi;
    public int stats_new_hires_expat;
    public double stats_decrease_price_bounded;
    public double stats_offer_wage_expats;
    public double stats_offer_wage_saudis;

    public void setSauditization_percentage(double sauditization_percentage) {
        this.sauditization_percentage = sauditization_percentage;
    }

    /**
     * adapts prices if production is not in line with planned_production
     * adapts planned_production, when demand is changed
     */
    public void set_prices_demand()
    {
        if (staff.getProductivity() > planned_production + average_productivity())
        {
                decrease_price_bounded();
        }
        else if (staff.getProductivity() < planned_production - average_productivity())
        {
                increase_price();
        }

        if (demand > planned_production)
        {
            increase_planned_production();
        }
        else
        {
            decrease_planned_production_bounded();
        }
    }


    private void decrease_price_bounded()
    {
        double before = price;
        double rand;
        rand = rnd.uniform(parameter_price);
        if (price * (1 - rand) > (staff.getWage() / staff.getProductivity()) * 1.1) {
            price = price * (1 - rand);
        }
        stats_decrease_price_bounded = price - before;

    }

    private void decrease_planned_production() {

        planned_production = max(demand, planned_production
                * (1 - rnd.uniform(parameter_planned_production)));

     }

    /**
     * Decreases planned_production by a random number; never below demand.
     * There is no decrease is planned_production, when pp is close to actual production.
     * (2 times the average production)
     */
    private void decrease_planned_production_bounded() {
        double before  = planned_production;
        planned_production = max(demand, planned_production
                * (1 - rnd.uniform(parameter_planned_production)));

        if (planned_production < staff.getProductivity() && planned_production > staff.getProductivity() - 2 * average_productivity())
        {
            planned_production = min(before, staff.getProductivity());
        }
     }

    private void increase_price()
    {
        double before = price;
        price = price * (1 + rnd.uniform(parameter_price));
        stats_increase_price = price - before;
    }

    private void increase_planned_production() {
        planned_production = Math.min(demand, planned_production
                * (1 + rnd.uniform(parameter_planned_production)));
    }

    /**
     * If production should be expanded or people can be fired, Advertisments are set with
     * the wage is the average wage of workers plus a random term.
     */
    public void advertise()
    {
        if (planned_production > staff.getProductivity()
            || visastack.getOrDefault(day.get(), new Group(this)).size() > 0
           )
        {
            double add_wage_saudis;
            double add_wage_expats;

            if (wage_saudis != 0 &&
                staff.getSaudis() != 0)
            {
                do {
                    add_wage_saudis = wage_saudis / staff.getSaudis() * (1 + rnd.nextGaussian());
                } while (add_wage_saudis <= 0);
            }
            {
                add_wage_saudis = 10;
                //TODO set to average wage
            }
            if (wage_expats != 0 &&
                    staff.getExpats() != 0)
            {
                do {
                    add_wage_expats = wage_expats / staff.getExpats() * (1 + rnd.nextGaussian());
                } while (add_wage_expats <= 0);
            }
            else
            {
             add_wage_expats = 10;
            }

            newspaper_saudis.place_add(new JobAdd(applications, add_wage_saudis));
            newspaper_expats.place_add(new JobAdd(applications, add_wage_expats));
            stats_offer_wage_saudis = add_wage_saudis;
            stats_offer_wage_expats = add_wage_expats;
        }
    }
    public void hiring_()
    {
        num_applications = applications.size();
        Group can_be_fired = new Group(this);
        if (visastack.get(day.get()) != null)
        {
            for (WorkerRecord worker: visastack.remove(day.get()).getWorker_list())
            {
                if (staff.contains(worker))
                {
                    can_be_fired.add(worker);
                }
            }
        }
        Group team = new Group(staff, this);
        team.removeAll(can_be_fired);
        ArrayList<WorkerRecord> to_consider = new ArrayList<WorkerRecord>(WorkerArray.convert(applications, day.get()));
        WorkerRecord best;
        while (planned_production > h_produce(team, 0) && to_consider.size() > 0)
        {
            best = pop_best(team, to_consider);
            if (net_benefit(team, best) > 0)
            {
                team.add(best);
            }
        }
        net_hires = hire_or_fire_staff(team, can_be_fired);
    }

    public void hiring()
    {
        num_applications = applications.size();
        Group can_be_fired = new Group(this);
        if (visastack.get(day.get()) != null)
        {
            for (WorkerRecord worker: visastack.remove(day.get()).getWorker_list())
            {
                if (staff.contains(worker))
                {
                    can_be_fired.add(worker);
                }
            }
        }

        if (applications.size() > 0 || can_be_fired.size() > 0) {
            ArrayList<WorkerRecord> to_consider = new ArrayList<WorkerRecord>(WorkerArray.convert(applications, day.get()));
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
                    && team.getWage() * 30 < net_worth
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

            net_hires = hire_or_fire_staff(team, can_be_fired);
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

    public void distribute_profits() {
        distributed_profits = 0;
        net_worth += profit;
        if (profit < 0.1 * net_worth) {
            distributed_profits = net_worth - 0.9 * profit;
            if (net_worth - distributed_profits < 100) {
                distributed_profits = 0;
            }
            net_worth -= distributed_profits;
            if (net_worth == 0)
                net_worth = -1;
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
        return Math.pow(p, 1);

    }

    void fire_staff(List<WorkerRecord> layoffs) {

        for (WorkerRecord worker : layoffs) {
            assert staff.contains(worker);
            worker.getAddress().sendFire();
            net_hires--;
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
        if (to_evaluate.getCitizenship() == Citizenship.SAUDI)
            saudi++;
        else
            expat++;
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }

    boolean is_admissible(Group team) {
        double saudi = team.getSaudis();
        double expat = team.getExpats();
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
        if (worker.getCitizenship() == Citizenship.SAUDI) {
            wage_saudis += worker.getWage();
        } else {
            wage_expats += worker.getWage();
            addVisa(worker);
        }
        worker.getAddress().sendEmploy(this, worker);
        this_round_hire++;
    }

    void fire(WorkerRecord worker)
    {

        disemploy(worker);
        worker.getAddress().sendFire();
        this_round_fire++;
    }

    void disemploy(WorkerRecord worker)
    {

        staff.remove(worker);

        if (worker.getCitizenship() == Citizenship.SAUDI) {
            wage_saudis -= worker.getWage();
        } else {
            wage_expats -= worker.getWage();
        }

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
                addVisa(worker);
            } else {
                applications.remove(worker);
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

    public Firm(
            int id,
            long seed,
            List<Worker> post_box_applications,
            Newspaper newspaper_saudis,
            Newspaper newspaper_expats,
            Auctioneer auctioneer,
            double sauditization_percentage,
            AtomicInteger day, double wage_std)
    {
        this.id = id;
        this.applications = post_box_applications;
        this.newspaper_saudis = newspaper_saudis;
        this.newspaper_expats = newspaper_expats;
        this.auctioneer = auctioneer;
        this.sauditization_percentage = sauditization_percentage;
        this.day = day;
        this.rnd = new Rnd(seed);
    }



    public void send_market_price_individual_demand(double market_price, double demand)
    {
        this.market_price = market_price;
        this.demand = demand;
    }

    public boolean out_of_business()
    {
        if (net_worth < 0)
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
        final int visa_length = 365;
        Integer visa_date = day.get() + visa_length;
        Group day_list = visastack.get(visa_date);
        if (day_list == null)
        {
            day_list = new Group(this);
            visastack.put(visa_date, day_list);
        }
        day_list.add(worker);
    }

    public double getAvgWageSaudis()
    {
        return wage_saudis;
    }

    public double getAvgWageExpats()
    {
        return wage_expats;
    }
}
