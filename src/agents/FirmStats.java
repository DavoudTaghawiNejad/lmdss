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
    
    public FirmStats()
    {
        System.out.print("time, ");
        System.out.print("employment_saudis, ");
        System.out.print("employment_expats, ");
        System.out.print("wage_bill, ");
        System.out.print("net_worth, ");
        System.out.print("profit, ");
        System.out.print("price, ");
        System.out.print("demand, ");
        System.out.print("production, ");
        System.out.print("planned_production, ");
        System.out.print("offer_wage_saudi, ");
        System.out.print("offer_wage_expats, ");
        System.out.print("distributed_profits, ");
        System.out.print("wage_saudis, ");
        System.out.print("wage_expats, ");
        System.out.println("");

    }  
    public void printcsv()
    {
        System.out.print(this.num_saudis);
        System.out.print(", ");
        System.out.print(this.num_expats);
        System.out.print(", ");
        System.out.print(this.wage_bill);
        System.out.print(", ");
        System.out.print(this.net_worth);
        System.out.print(", ");
        System.out.print(this.profit);
        System.out.print(", ");
        System.out.print(this.price);
        System.out.print(", ");
        System.out.print(this.demand);
        System.out.print(", ");
        System.out.print(this.production);
        System.out.print(", ");
        System.out.print(this.planned_production);
        System.out.print(", ");
        System.out.print(this.offer_wage_saudis);
        System.out.print(", ");
        System.out.print(this.offer_wage_expats);
        System.out.print(", ");
        System.out.print(this.distributed_profits);
        System.out.print(", ");
        System.out.print(this.wage_saudis);
        System.out.print(", ");
        System.out.print(this.wage_expats);

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
    }
}