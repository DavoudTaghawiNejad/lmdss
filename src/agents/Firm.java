package agents;


import java.util.ArrayList;
import java.util.List;


import definitions.Citizenship;

import static java.lang.Math.*;


import messages.*;
import tools.Rnd;

public class Firm {
    private final Rnd rnd;
    private final Newspaper newspaper_saudis;
    private final Newspaper newspaper_expats;

    public double wage_bill = 0;
    public int num_expats;
    public int num_saudis;
    public int id;
    public double net_worth;
    public double profit;
    public double price = 3;
    public double demand = 1;
    public double market_price = 3;
    public double production = 0;
    public double planned_production = 400;
    public double offer_wage_saudis = 10;
    public double offer_wage_expats = 10;
    public double distributed_profits;
    public double wage_saudis;
    public double wage_expats;
    private java.util.List<Worker> applications;
    public java.util.LinkedList<Worker> staff = new java.util.LinkedList<Worker>();
    private java.util.ArrayList<Worker> can_be_fired = new java.util.ArrayList<Worker>();
    private double parameter_planned_production = 400;

    private double parameter_price = 0.1;
    private double parameter_wage = 0.1;
    private double parameter_price_if_wage_is_altered = 0.1;
    private double parameter_planned_production_if_wage_is_altered = 0.1;
    private double parameter_price_if_fireing_is_impossible = 0.1;
    private double parameter_planned_production_if_fireing_is_impossible = 0.1;
    public boolean no_fake_probation;

    private double sauditization_percentage;
    private Auctioneer auctioneer;
    public int net_hires = 0;

    public void setSauditization_percentage(double sauditization_percentage) {
        this.sauditization_percentage = sauditization_percentage;
    }


    public void set_prices_demand() {
                /*
				 * if (profit_1 > profit) { price = price_1; } profit_1 =
				 * profit; price_1 = price;
				 */
        if (demand > planned_production) {
            if (price > market_price) {
                planned_production = Math.min(demand, planned_production
                        * (1 + rnd.uniform(parameter_planned_production)));
            } else if (demand > production) {
                price = price * (1 + rnd.uniform(parameter_price));
            }
        } else if (demand < planned_production) {
            if (price <= market_price)
                planned_production = max(demand, planned_production
                        * (1 - rnd.uniform(parameter_planned_production)));
            else {
                if (demand < production) {
                    double rand;
                    rand = rnd.uniform(parameter_price);
                    if (price * (1 - rand) > (wage_bill / production) * 1.1) {
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
        if (planned_production > production)
        {
			newspaper_saudis.place_add(new JobAdd(applications, offer_wage_saudis));
            newspaper_expats.place_add(new JobAdd(applications, offer_wage_expats));
        }
        //System.out.println(offer_wage_expats);
    }

    public void hiring()
    {
        if (planned_production > production)
        {

        }
        if (planned_production > production && applications.size() == 0) {
            if (offer_wage_saudis <= newspaper_saudis.getAverage_wage_offer())
            {
                offer_wage_saudis = offer_wage_saudis * (1 + rnd.uniform(parameter_wage));
            }
            if (offer_wage_expats <= newspaper_expats.getAverage_wage_offer())
            {
                offer_wage_expats = offer_wage_expats * (1 + rnd.uniform(parameter_wage));

            }
            price = price * (1 + rnd.uniform(parameter_price_if_wage_is_altered));
            planned_production = max(production, planned_production
                    * (1 - rnd.uniform(parameter_planned_production_if_wage_is_altered)));
        }

        if (production - average_productivity() > planned_production
                && can_be_fired.size() == 0)
        {
            price = price * (1 - rnd.uniform(parameter_price_if_fireing_is_impossible));
            planned_production = min(demand, planned_production
                    * (1 + rnd.uniform(parameter_planned_production_if_fireing_is_impossible)));
        }

        if (applications.size() > 0 || can_be_fired.size() > 0) {
            ArrayList<Worker> to_consider = new ArrayList<Worker>(
                    applications);
            to_consider.addAll(can_be_fired);
            ArrayList<Worker> set_aside = new ArrayList<Worker>();
            ArrayList<Worker> team = new ArrayList<Worker>(staff);
            team.removeAll(can_be_fired);
            ArrayList<Worker> potential_team = new ArrayList<Worker>(
                    team);
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
                            && worker_net_benefit(team, best_aside) > worker_net_benefit(
                            team, best_apps)) {
                        best = best_aside;
                        set_aside.remove(best);
                    } else {
                        best = best_apps;
                        to_consider.remove(best);
                    }
                }

                if (!is_admissible(potential_team, best)) {
                    if (worker_net_benefit(team, best) > 0) {
                        set_aside.add(best);
                    }
                    continue;
                }
                if ((is_admissible(team, best))
                        && (worker_net_benefit(team, best) > 0)) {
                    team.add(best);
                    potential_team.add(best);
                } else {
                    potential_team.add(best);
                    if (team_net_benefit(team, potential_team)) {
                        team.addAll(potential_team);
                    }
                }
            }

            if ((no_fake_probation) && (team.size() > 0)) {
                ArrayList<Worker> force_keeps = new ArrayList<Worker>(
                        can_be_fired);
                force_keeps.removeAll(team);
                while (force_keeps.size() > 0
                        && !is_team_admissible(team)) {
                    best = pop_best(team, force_keeps);
                    if (best.citizenship == Citizenship.SAUDI) {
                        team.add(best);
                    }
                }
            }
            net_hires = hire_or_fire_staff(team);

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
                    if (count_applicants(Citizenship.SAUDI) == 0) {
                        if (offer_wage_saudis <= newspaper_saudis.getAverage_wage_offer()) {
                            offer_wage_saudis = offer_wage_saudis * (1 + rnd.uniform(parameter_wage));
                        }
                    } else {
                        if (offer_wage_saudis >= newspaper_saudis.getAverage_wage_offer()) {
                            offer_wage_saudis = offer_wage_saudis * (1 - rnd.uniform(parameter_wage));
                        } // or foreigners
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
        auctioneer.make_final_good_offer(this, price, production);
    }

    public void sell()
    {
        profit = min(demand, production) * price;
    }

    public void pay_wage() {
        profit -= wage_bill;
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
            ArrayList<Worker> team = new ArrayList<Worker>();

            ArrayList<Worker> potential_team = new ArrayList<Worker>();

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
                            && worker_net_benefit(team, best_aside) > worker_net_benefit(
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
                        && (worker_net_benefit(team, best) > 0)) {
                    team.add(best);
                    potential_team.add(best);
                } else {
                    potential_team.add(best);
                    if (team_net_benefit(team, potential_team)) {
                        team.addAll(potential_team);
                    }
                }
            }
            List<Worker> blue_list = new ArrayList<Worker>(staff);
            blue_list.removeAll(team);
            fire_staff(blue_list);
        }
    }

    double h_produce(List<Worker> team, double additional) {

        double p = additional;
        for (Worker worker : team) {
            p += worker.productivity;
        }
        return Math.pow(p, 1);

    }

    void fire_staff(List<Worker> layoffs) {

        for (Worker worker : layoffs) {
            if (staff.contains(worker)) {
                fire(worker);
            }
        }
    }

    Worker pop_best(List<Worker> team, List<Worker> to_evaluate) {

        Worker best = get_best(team, to_evaluate);
        to_evaluate.remove(best);
        return best;

    }

    Worker get_best(List<Worker> team, List<Worker> to_evaluate) {

        double max = 0;
        double current;
        if (to_evaluate.size() == 0)
            return null;
        Worker best = to_evaluate.get(0);
        for (Worker w : to_evaluate) {
            current = worker_net_benefit(team, w);
            if (current > max) {
                best = w;
                max = current;
            }
        }
        return best;

    }

    boolean is_admissible(List<Worker> team, Worker to_evaluate) {

        double saudi = 0;
        double expat = 0;

        for (Worker w : team) {
            if (w.citizenship == Citizenship.SAUDI)
                saudi++;
            else
                expat++;
        }
        if (to_evaluate.citizenship == Citizenship.SAUDI)
            saudi++;
        else
            expat++;
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }

    double total_wage(List<Worker> team) {

        double total_wage = 0;

        for (Worker w : team) {
            total_wage += w.wage;
        }
        return total_wage;
    }

    double worker_net_benefit(List<Worker> team, Worker worker) {

        return price
                * (min(planned_production, h_produce(team, worker.productivity)) - min(
                planned_production, h_produce(team, 0))) - worker.wage;
    }


    boolean is_team_admissible(List<Worker> team) {

        double saudi = 0;
        double expat = 0;

        for (Worker w : team) {
            if (w.citizenship == Citizenship.SAUDI)
                saudi++;
            else
                expat++;
        }
        return (saudi / (saudi + expat) >= sauditization_percentage);

    }

    boolean team_net_benefit(List<Worker> team, List<Worker> potential_team) {

        return price
                * (min(planned_production, h_produce(potential_team, 0)) - min(
                planned_production, h_produce(team, 0))) > total_wage(potential_team)
                - total_wage(team);
    }

    int count_applicants(Citizenship citizenship) {

        int counter = 0;
        for (Worker w : staff) {
            if (w.citizenship == citizenship) {
                counter += 1;
            }
        }
        return counter;

    }

    void hire(Worker worker)
    {
        staff.add(worker);
        worker.employ(this);
        wage_bill += worker.wage;
        production += worker.productivity;

        if (worker.citizenship == Citizenship.SAUDI) {
            wage_saudis += worker.wage;
            num_saudis++;
        } else {
            wage_expats += worker.wage;
            num_expats++;
        }
    }

    void fire(Worker worker)
    {
        staff.remove(worker);
        worker.fire();
        wage_bill -= worker.wage;
        production -= worker.productivity;

        if (worker.citizenship == Citizenship.SAUDI) {
            wage_saudis -= worker.wage;
            num_saudis--;
        } else {
            wage_expats -= worker.wage;
            num_expats--;
        }
    }
    int hire_or_fire_staff(ArrayList<Worker> team) {

        int initial_staff = staff.size();


        for (Worker worker : team) {
            if (staff.contains(worker)) {
                can_be_fired.remove(worker);
            } else {
                applications.remove(worker);
                hire(worker);
            }
        }

        /*
        for (Worker worker : applications) {
            //send(new Rejection(this), worker);
        }
        */
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
            productivity += w.productivity;
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
            double sauditization_percentage
    )
    {
        this.id = id;
        this.applications = post_box_applications;
        this.newspaper_saudis = newspaper_saudis;
        this.newspaper_expats = newspaper_expats;
        this.auctioneer = auctioneer;
        this.sauditization_percentage = sauditization_percentage;
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
}