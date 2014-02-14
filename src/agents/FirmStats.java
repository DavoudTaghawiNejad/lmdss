package agents;


public class FirmStats {

    public int num_saudis = 0;
    public int num_expats = 0;
    public double wage_bill = 0;
    public double net_worth = 0;
    public double profit = 0;
    public double price = 0;
    public double demand = 0;
    public double production = 0;
    public double planned_production = 0;
    public double offer_wage_saudis = 0;
    public double offer_wage_expats = 0;
    public double distributed_profits = 0;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    public int staff = 0;
    public int num_firms;
    private int hires = 0;
    private int fires = 0;
    private int last_staff = 0;
    public double decrease_price_bounded = 0;
    public double decrease_prices_no_firing = 0;
    public double increase_price = 0;
    public double increase_price_wage_altered = 0;
    private int num_applications;
    private double accepted_wage_expats;
    private double accepted_wage_saudis;
    private int stats_new_hires_saudi;
    private int stats_new_hires_expat;

    public FirmStats(int num_firms) {
        System.out.print("time\t");
        System.out.print("num_firms\t");
        System.out.print("employment_saudis\t");
        System.out.print("employment_expats\t");
        System.out.print("wage_bill\t");
        System.out.print("net_worth\t");
        System.out.print("profit\t");
        System.out.print("price\t");
        System.out.print("demand\t");
        System.out.print("production_capacity\t");
        System.out.print("planned_production\t");
        System.out.print("offer_wage_saudi\t");
        System.out.print("offer_wage_expats\t");
        System.out.print("distributed_profits\t");
        System.out.print("wage_saudis\t");
        System.out.print("wage_expats\t");
        System.out.print("staff\t");
        System.out.print("hires\t");
        System.out.print("fires\t");
        System.out.print("applications\t");
        System.out.print("decrease_price_bounded\t");
        System.out.print("decrease_prices_no_firing\t");
        System.out.print("increase_price\t");
        System.out.print("increase_price_wage_altered\t");
        System.out.print("accepted_wage_saudis\t");
        System.out.print("accepted_wage_expats\t");
        System.out.println("");

    }  
    public double printcsv()
    {
        System.out.print(this.num_firms);
        System.out.print("\t");
        System.out.print(this.num_saudis);
        System.out.print("\t");
        System.out.print(this.num_expats);
        System.out.print("\t");
        System.out.print(this.wage_bill / num_firms);
        System.out.print("\t");
        System.out.print(this.net_worth / num_firms);
        System.out.print("\t");
        System.out.print(this.profit / num_firms);
        System.out.print("\t");
        System.out.print(this.price / demand);
        System.out.print("\t");
        System.out.print(this.demand);
        System.out.print("\t");
        System.out.print(this.production);
        System.out.print("\t");
        System.out.print(this.planned_production);
        System.out.print("\t");
        System.out.print(this.offer_wage_saudis / num_firms);
        System.out.print("\t");
        System.out.print(this.offer_wage_expats / num_firms);
        System.out.print("\t");
        System.out.print(this.distributed_profits / num_firms);
        System.out.print("\t");
        System.out.print(this.wage_saudis / num_saudis);
        System.out.print("\t");
        System.out.print(this.wage_expats / num_expats);
        System.out.print("\t");
        System.out.print(this.staff - this.last_staff);
        System.out.print("\t");
        System.out.print(this.hires);
        System.out.print("\t");
        System.out.print(this.fires);
        System.out.print("\t");
        System.out.print(this.num_applications);
        System.out.print("\t");
        System.out.print(decrease_price_bounded);
        System.out.print("\t");
        System.out.print(decrease_prices_no_firing);
        System.out.print("\t");
        System.out.print(increase_price);
        System.out.print("\t");
        System.out.print(increase_price_wage_altered);
        System.out.print("\t");
        System.out.print(accepted_wage_saudis / stats_new_hires_saudi);
        System.out.print("\t");
        System.out.print(accepted_wage_expats / stats_new_hires_expat);

        return profit / net_worth;
    }

    public void reset() {
        this.num_firms = 0;
        this.num_saudis = 0;
        this.num_expats = 0;
        this.wage_bill = 0;
        this.net_worth = 0;
        this.profit = 0;
        this.price = 0;
        this.demand = 0;
        this.production = 0;
        this.planned_production = 0;
        this.offer_wage_saudis = 0;
        this.offer_wage_expats = 0;
        this.distributed_profits = 0;
        this.wage_saudis = 0;
        this.wage_expats = 0;
        this.last_staff = this.staff;
        this.staff = 0;
        this.hires = 0;
        this.fires = 0;
        this.decrease_price_bounded = 0;
        this.decrease_prices_no_firing = 0;
        this.increase_price = 0;
        this.increase_price_wage_altered = 0;
        this.num_applications = 0;
        this.accepted_wage_saudis = 0;
        this.accepted_wage_expats = 0;
    }

    public void update(Firm firm) {
        this.num_firms++;
        this.num_saudis += firm.staff.getSaudis();
        this.num_expats += firm.staff.getExpats();
        this.wage_bill += firm.staff.getWage();
        this.net_worth += firm.net_worth;
        this.profit += firm.profit;
        this.price += firm.price * firm.demand;
        this.demand += firm.demand;
        this.production += firm.staff.getProductivity();
        this.planned_production += firm.planned_production;
        this.offer_wage_saudis += firm.stats_offer_wage_saudis;
        this.offer_wage_expats += firm.stats_offer_wage_expats;
        this.distributed_profits += firm.distributed_profits;
        this.wage_saudis += firm.staff.getWage_saudis();
        this.wage_expats += firm.staff.getWage_expats();
        this.staff += firm.staff.size();
        this.hires += firm.this_round_hire;
        this.fires -= firm.this_round_fire;
        this.num_applications += firm.num_applications;
        this.increase_price += firm.stats_increase_price;
        this.decrease_price_bounded += firm.stats_decrease_price_bounded;
        this.accepted_wage_saudis  += firm.stats_accepted_wage_saudis;
        this.accepted_wage_expats += firm.stats_accepted_wage_expats;
        this.stats_new_hires_saudi += firm.stats_new_hires_saudi;
        this.stats_new_hires_expat += firm.stats_new_hires_expat;

        firm.stats_new_hires_saudi = 0;
        firm.stats_new_hires_expat = 0;
        firm.stats_accepted_wage_saudis = 0;
        firm.stats_accepted_wage_expats = 0;
        firm.this_round_hire = 0;
        firm.this_round_fire = 0;
        firm.stats_increase_price= 0;
        firm.stats_decrease_price_bounded= 0;
    }

    public double getWage_saudis()
    {
        return wage_saudis;
    }

    public double getWage_expats()
    {
        return wage_expats;
    }
}