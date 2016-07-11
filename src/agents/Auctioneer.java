package agents;
import messages.FirmQuantity;


public class Auctioneer {
	public double love_for_variety;
    public double q;
	public double total_value_sold;
	public double income;
    private final double demand_multiplier;  # income and demand demand_multiplier are redundant!!!
    public double market_price;
	public double total_deliverable_demand;
	public double total_capacity;
	public double average_price;
	public double total_demand;

	public java.util.LinkedHashMap<Firm, Double> prices = new java.util.LinkedHashMap<Firm, Double>();
	public java.util.LinkedHashMap<Firm, Double> quantities = new java.util.LinkedHashMap<Firm, Double>();
	public java.util.ArrayList<FirmQuantity> demand_list = new java.util.ArrayList<FirmQuantity>();

    public Auctioneer(double love_for_variety, double income, double demand_multiplier) {
        this.love_for_variety = love_for_variety;
        this.income = income;
        this.demand_multiplier = demand_multiplier;
    }

    public void compute_market() {
		calc_q();
		double demand = 0;
		double deliverable_demand = 0;
		double capacity = 0;
		total_capacity = 0;
		double weighted_prices = 0;
		double price;
		total_deliverable_demand = 0;
		average_price = 0;
		demand_list.clear();
		total_value_sold = 0;
		total_demand = 0;

		for (Firm firm : prices.keySet()) {
			price = prices.get(firm);
			capacity = quantities.get(firm);
			demand = demand_schedule(price);
			demand_list.add(new FirmQuantity(firm, demand));
			deliverable_demand = Math.min(capacity, demand);
			weighted_prices += deliverable_demand * price;
			average_price += price;
			total_deliverable_demand += deliverable_demand;
			total_capacity += capacity;
			total_demand += demand;
			total_value_sold += price * deliverable_demand;
		}
		average_price = average_price / prices.size();

		if (!Double.isNaN(weighted_prices / total_deliverable_demand)) {
			market_price = weighted_prices / total_deliverable_demand;
		} else {
			market_price = 0;
			for (Firm firm : prices.keySet()) {
				if (prices.get(firm) > market_price)
					market_price = prices.get(firm);
			}
		}

		for (FirmQuantity item : demand_list)
        {
            item.firm.send_market_price_individual_demand(market_price, item.quantity);
		}

		prices.clear();
		quantities.clear();
    }

	public void new_round() {
		prices.clear();
		quantities.clear();
		demand_list.clear();
	}

	double demand_schedule(double pi)
    {
		return demand_multiplier * (income / q) * Math.pow((q / pi), (1 / (1 - love_for_variety)));
		 # income and demand demand_multiplier are redundant!!!
	}

	void calc_q() {

		q = 0;
		for (Firm c : prices.keySet()) {
			q += Math.pow(prices.get(c), (love_for_variety / (love_for_variety - 1)));
		}
		q = Math.pow(q, ((love_for_variety - 1) / love_for_variety));

	}

	public void make_final_good_offer(Firm firm, double price, double quantity) {
		prices.put(firm, price);
		quantities.put(firm, quantity);
	}

}
