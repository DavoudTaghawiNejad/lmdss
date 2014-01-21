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
    private Map<Integer, Staff<Worker>> visastack = new HashMap<Integer, Staff<Worker>>();

    public int id;
    public double net_worth;
    public double profit;
    public double price = 3;
    public double demand = 1;
    public double market_price = 3;
    public double planned_production = 400;
    public double offer_wage_saudis = 10;
    public double offer_wage_expats = 10;
    public double distributed_profits;
    public double wage_saudis = 0;
    public double wage_expats = 0;

    private List<Worker> applications;
    public Staff<Worker> staff = new Staff<Worker>(this);

    private double parameter_planned_production = 400;

    private double parameter_price = 0.025;
    private double parameter_wage = 0.1;
    private double parameter_price_if_wage_is_altered = 0.1;
    private double parameter_planned_production_if_wage_is_altered = 0.1;
    private double parameter_price_if_firing_is_impossible = 0.1;
    private double parameter_planned_production_if_firing_is_impossible = 0.1;
    public boolean no_fake_probation;

    private double sauditization_percentage;
    private Auctioneer auctioneer;
    public int net_hires = 0;
    private AtomicInteger day;

    public void setSauditization_percentage(double sauditization_percentage) {
        this.sauditization_percentage = sauditization_percentage;
    }


    public void set_prices_demand() {
        if (demand > planned_production) {
            if (price > market_price) {
                planned_production = Math.min(demand, planned_production
                        * (1 + rnd.uniform(parameter_planned_production)));
            } else if (demand > staff.getProductivity()) {
                price = price * (1 + rnd.uniform(parameter_price));
            }
        } else if (demand < planned_production) {
            if (price <= market_price)
                planned_production = max(demand, planned_production
                        * (1 - rnd.uniform(parameter_planned_production)));
            else {
                if (demand < staff.getProductivity()) {
                    double rand;
                    rand = rnd.uniform(parameter_price);
                    if (price * (1 - rand) > (staff.getWage() / staff.getProductivity()) * 1.1) {
                        price = price * (1 - rand);
                    } else {
                        planned_production = Math.max(demand, planned_production
                                * (1 - rnd.uniform(parameter_planned_production)));
                    }
                }
            }
        }
    }

    public void advertise()
    {
        if (planned_production > staff.getProductivity())
        {
            newspaper_saudis.place_add(new JobAdd(applications, offer_wage_saudis));
            newspaper_expats.place_add(new JobAdd(applications, offer_wage_expats));
        }
    }

    public void hiring()
    {
        Staff<Worker> can_be_fired;
        can_be_fired = visastack.remove(day.get());
        if (can_be_fired == null)
        {
            can_be_fired = new Staff<Worker>(this);
        }

        if (planned_production > staff.getProductivity() && applications.size() == 0) {
            if (offer_wage_saudis <= newspaper_saudis.getAverage_wage_offer())
            {
                offer_wage_saudis = offer_wage_saudis * (1 + rnd.uniform(parameter_wage));
            }
            if (offer_wage_expats <= newspaper_expats.getAverage_wage_offer())
            {
                offer_wage_expats = offer_wage_expats * (1 + rnd.uniform(parameter_wage));

            }
            price = price * (1 + rnd.uniform(parameter_price_if_wage_is_altered));
            planned_production = max(staff.getProductivity(), planned_production
                    * (1 - rnd.uniform(parameter_planned_production_if_wage_is_altered)));
        }

        if (staff.getProductivity() - average_productivity() > planned_production
                && can_be_fired.size() == 0)
        {
            price = price * (1 - rnd.uniform(parameter_price_if_firing_is_impossible));
            planned_production = min(demand, planned_production
                    * (1 + rnd.uniform(parameter_planned_production_if_firing_is_impossible)));
        }

        if (applications.size() > 0 || can_be_fired.size() > 0) {
            ArrayList<Worker> to_consider = new ArrayList<Worker>(applications);
            to_consider.addAll(can_be_fired);
            ArrayList<Worker> set_aside = new ArrayList<Worker>();
            Team team = new Team(staff, this);
            team.removeAll(can_be_fired);
            Team potential_team = new Team(team, this);
            Worker best = null;
            Worker best_aside = null;
            Worker best_apps = null;
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
                ArrayList<Worker> force_keeps = new ArrayList<Worker>(
                        can_be_fired);
                force_keeps.removeAll(team);
                while (force_keeps.size() > 0
                        && !is_admissible(team)) {
                    best = pop_best(team, force_keeps);
                    if (best.citizenship == Citizenship.SAUDI) {
                        team.add(best);
                    }
                }
            }
            net_hires = hire_or_fire_staff(team, can_be_fired);

            if (planned_production > h_produce(staff, 0)
                    + average_productivity()
                    && net_hires <= 0 && applications.size() > 0) {
                if (set_aside.size() == 0) {
                    if (offer_wage_saudis >= newspaper_saudis.getAverage_wage_offer()) {
                        offer_wage_saudis = offer_wage_saudis
                                * (1 - rnd.uniform(parameter_wage));
                    }
                    if (offer_wage_expats >= newspaper_expats.getAverage_wage_offer()) {
                        offer_wage_expats = offer_wage_expats
                                * (1 - rnd.uniform(parameter_wage));
                    }
                } else {
                    if (count_citizenship(applications, Citizenship.SAUDI) == 0) {
                        if (offer_wage_saudis <= newspaper_saudis.getAverage_wage_offer()) {
                            offer_wage_saudis = offer_wage_saudis * (1 + rnd.uniform(parameter_wage));
                        }
                    }
                    if (count_citizenship(applications, Citizenship.EXPAT) == 0) {
                        if (offer_wage_saudis >= newspaper_saudis.getAverage_wage_offer()) {
                            offer_wage_saudis = offer_wage_saudis * (1 - rnd.uniform(parameter_wage));
                        }
                    }
                }
                price = price * (1 + rnd.uniform(parameter_price_if_wage_is_altered));
            }
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

            ArrayList<Worker> to_consider = new ArrayList<Worker>(staff);

            ArrayList<Worker> set_aside = new ArrayList<Worker>();
            Team team = new Team(this);

            Team potential_team = new Team(this);

            Worker best = null;
            Worker best_aside = null;
            Worker best_apps = null;
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
            List<Worker> blue_list = new ArrayList<Worker>(staff);
            blue_list.removeAll(team);
            fire_staff(blue_list);
        }
    }

    double h_produce(Group team, double additional) {

        double p = additional;
        p += team.getProductivity();
        return Math.pow(p, 1);

    }

    void fire_staff(List<Worker> layoffs) {

        for (Worker worker : layoffs) {
            assert staff.contains(worker);
            fire(worker);
            net_hires--;
        }
    }

    Worker pop_best(Team team, List<Worker> to_evaluate) {

        Worker best = get_best(team, to_evaluate);
        to_evaluate.remove(best);
        return best;
    }

    Worker get_best(Team team, List<Worker> to_evaluate) {

        double max = 0;
        double current;
        if (to_evaluate.size() == 0)
            return null;
        Worker best = to_evaluate.get(0);
        for (Worker worker : to_evaluate) {
            current = net_benefit(team, worker);
            if (current > max) {
                best = worker;
                max = current;
            }
        }
        return best;

    }

    boolean is_admissible(Team team, Worker to_evaluate) {

        double saudi = team.getSaudis();
        double expat = team.getExpats();
        if (to_evaluate.citizenship == Citizenship.SAUDI)
            saudi++;
        else
            expat++;
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }

    boolean is_admissible(Team team) {
        double saudi = team.getSaudis();
        double expat = team.getExpats();
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }


    double net_benefit(Team team, Worker worker)
    {
        return price
                * (min(planned_production, h_produce(team, worker.getProductivity())) - min(
                planned_production, h_produce(team, 0))) - worker.getAdvertisedWage();        //return price * worker.getProductivity()- worker.job_add.wage;

    }

    boolean net_benefit(Team team, Team potential_team) {

        return price
                * (min(planned_production, h_produce(potential_team, 0)) - min(
                planned_production, h_produce(team, 0))) > potential_team.getWage()
                - team.getWage();
    }

    int count_citizenship(List<Worker> team, Citizenship citizenship) {

        int counter = 0;
        for (Worker worker : team) {
            if (worker.citizenship == citizenship) {
                counter += 1;
            }
        }
        return counter;

    }

    void hire(Worker worker)
    {
        
        staff.add(worker);
        if (worker.citizenship == Citizenship.SAUDI) {
            wage_saudis += worker.getAdvertisedWage();
        } else {
            wage_expats += worker.getAdvertisedWage();
            addVisa(worker);
        }
        worker.sendEmploy(this);
        
    }

    void fire(Worker worker)
    {
        
        disemploy(worker);
        worker.sendFire();
        
    }

    void disemploy(Worker worker)
    {
        
        staff.remove(worker);

        if (worker.citizenship == Citizenship.SAUDI) {
            wage_saudis -= worker.getWage();
        } else {
            wage_expats -= worker.getWage();
        }
        
    }

    public void sendQuit(Worker worker)
    {
        disemploy(worker);
    }


    int hire_or_fire_staff(Team<Worker> team, ArrayList<Worker> can_be_fired) {

        int initial_staff = staff.size();


        for (Worker worker : team) {
            if (staff.contains(worker)) {
                can_be_fired.remove(worker);
            } else {
                applications.remove(worker);
                hire(worker);
            }
        }

        applications.clear();

        for (Worker worker : can_be_fired) {
            fire(worker);
        }
        can_be_fired.clear();

        return staff.size() - initial_staff;
    }

    double average_productivity() {

        double productivity = 0;

        for (Worker w : staff) {
            productivity += w.getProductivity();
        }
        return productivity / staff.size();
    }

    public Firm(
            int id,
            long seed,
            List<Worker> post_box_applications,
            Newspaper newspaper_saudis,
            Newspaper newspaper_expats,
            Auctioneer auctioneer,
            double sauditization_percentage,
            AtomicInteger day)
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

        return (net_worth < 0);
    }

    private void addVisa(Worker worker)
    {
        final int visa_length = 365;
        Integer visa_date = day.get() + visa_length;
        Staff<Worker> day_list = visastack.get(visa_date);
        if (day_list == null)
        {
            day_list = new Staff<Worker>(this);
            visastack.put(visa_date, day_list);
        }
        day_list.add(worker);
    }
}