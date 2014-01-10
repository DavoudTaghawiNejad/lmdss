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
    public double wage_saudis = 0;
    public double wage_expats = 0;
    public int net_hires = 0;
    public int num_firms;

    public FirmStats(int num_firms) {
        this.num_firms = num_firms;
        System.out.print("time\t");
        System.out.print("employment_saudis\t");
        System.out.print("employment_expats\t");
        System.out.print("wage_bill\t");
        System.out.print("net_worth\t");
        System.out.print("profit\t");
        System.out.print("price\t");
        System.out.print("demand\t");
        System.out.print("production\t");
        System.out.print("planned_production\t");
        System.out.print("offer_wage_saudi\t");
        System.out.print("offer_wage_expats\t");
        System.out.print("distributed_profits\t");
        System.out.print("wage_saudis\t");
        System.out.print("wage_expats\t");
        System.out.print("net_hires\t");
        System.out.println("");

    }  
    public void printcsv()
    {
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
        System.out.print(this.price / num_firms);
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
        System.out.print(this.wage_saudis);
        System.out.print("\t");
        System.out.print(this.wage_expats);
        System.out.print("\t");
        System.out.print(this.net_hires);

    }

    public void reset() {
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
        this.net_hires = 0;
    }

    public void update(Firm firm) {
        this.num_saudis += firm.num_saudis;
        this.num_expats += firm.num_expats;
        this.wage_bill += firm.wage_bill;
        this.net_worth += firm.net_worth;
        this.profit += firm.profit;
        this.price += firm.price;
        this.demand += firm.demand;
        this.production += firm.production;
        this.planned_production += firm.planned_production;
        this.offer_wage_saudis += firm.offer_wage_saudis;
        this.offer_wage_expats += firm.offer_wage_expats;
        this.distributed_profits = firm.distributed_profits;
        this.wage_saudis += firm.wage_saudis;
        this.wage_expats += firm.wage_expats;
        this.net_hires += firm.net_hires;
        firm.net_hires = 0;
    }
}