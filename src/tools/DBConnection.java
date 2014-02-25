package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import agents.Firm;



public class DBConnection 
{
	
	//database connection setup
	Connection SQLite_Connection = null;
	Statement statement = null;
	PreparedStatement SQLite_firmPreparedStatement = null;
	PreparedStatement SQLite_firmStatisticsPreparedStatement = null;
	String sql = null;

	// Aggregate parameters to be stored in each iteration (day)
    private long experiment_seed;
    
    
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


    public DBConnection(long experiment_id)
	{
        this.experiment_seed = experiment_id;
        SQLite_setup();
	}
	
	private void SQLite_setup()
	{
		Statement stmt = null;
	    try 
	    {
	      Class.forName("org.sqlite.JDBC");
	      SQLite_Connection = DriverManager.getConnection("jdbc:sqlite:lmdss.sqlite3");
	      SQLite_Connection.prepareStatement("PRAGMA synchronous=OFF;").execute();
	      SQLite_Connection.prepareStatement("PRAGMA journal_mode=OFF;").execute();
	      SQLite_Connection.prepareStatement("PRAGMA count_changes=OFF;").execute();
	      SQLite_Connection.prepareStatement("PRAGMA temp_store=OFF;").execute();
	      SQLite_Connection.prepareStatement("PRAGMA default_temp_store=OFF;").execute();
	      SQLite_Connection.setAutoCommit(false);
	      
	      // create the individual firms' database table
	      stmt = SQLite_Connection.createStatement();
	      String sql = "CREATE TABLE IF NOT EXISTS firms ("+
	    		  "experamentID bigint(20) DEFAULT NULL,"+
	    		  "day int(11) DEFAULT NULL,"+
	    		  "wage_bill double DEFAULT NULL,"+
	    		  "num_expats int(11) DEFAULT NULL,"+
	    		  "num_saudis int(11) DEFAULT NULL,"+
	    		  "id int(11) DEFAULT NULL,"+
	    		  "net_worth double DEFAULT NULL,"+
	    		  "profit double DEFAULT NULL,"+
	    		  "price double DEFAULT NULL,"+
	    		  "demand double DEFAULT NULL,"+
	    		  "market_price double DEFAULT NULL,"+
	    		  "production double DEFAULT NULL,"+
	    		  "planned_production double DEFAULT NULL,"+
	    		  "offer_wage_saudis double DEFAULT NULL,"+
	    		  "offer_wage_expats double DEFAULT NULL,"+
	    		  "distributed_profits double DEFAULT NULL,"+
	    		  "wage_saudis double DEFAULT NULL,"+
	    		  "wage_expats double DEFAULT NULL"+
	    		")";
	      stmt.executeUpdate(sql);
	      
	      sql = "INSERT INTO firms VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		  SQLite_firmPreparedStatement = SQLite_Connection.prepareStatement(sql);

	      //create the aggregate firms' data table
	      stmt = SQLite_Connection.createStatement();
	      sql = "CREATE TABLE IF NOT EXISTS firm_aggregate ("+
	    		  "experamentID bigint(20) DEFAULT NULL,"+
	    		  "day int(11) DEFAULT NULL,"+
	    		  "num_saudis int(11) DEFAULT NULL,"+
	    		  "num_expats  int(11) DEFAULT NULL,"+
	    		  "wage_bill  double DEFAULT NULL,"+
	    		  "net_worth  double DEFAULT NULL,"+
	    		  "profit  double DEFAULT NULL,"+
	    		  "price  double DEFAULT NULL,"+
	    		  "demand  double DEFAULT NULL,"+
	    		  "production  double DEFAULT NULL,"+
	    		  "planned_production  double DEFAULT NULL,"+
	    		  "offer_wage_saudis  double DEFAULT NULL,"+
	    		  "offer_wage_expats  double DEFAULT NULL,"+
	    		  "distributed_profits  double DEFAULT NULL,"+
	    		  "wage_saudis  double DEFAULT NULL,"+
	    		  "wage_expats  double DEFAULT NULL,"+
	    		  "staff int(11) DEFAULT NULL,"+
	    		  "num_firms int(11) DEFAULT NULL,"+
	    		  "hires int(11) DEFAULT NULL,"+
	    		  "fires int(11) DEFAULT NULL,"+
	    		  "applications int(11) DEFAULT NULL,"+
	    		  "decrease_price_bounded double DEFAULT NULL,"+
	    		  "decrease_prices_no_firing double DEFAULT NULL,"+
	    		  "increase_price double DEFAULT NULL,"+
	    		  "increase_price_wage_altered double DEFAULT NULL,"+
	    		  "accepted_wage_expats double DEFAULT NULL,"+
	    		  "accepted_wage_saudis double DEFAULT NULL"+
	    		")";
	      stmt.executeUpdate(sql);

	      sql = "INSERT INTO firm_statistics VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	      SQLite_firmStatisticsPreparedStatement = SQLite_Connection.prepareStatement(sql);

	      stmt.close();
	      
	    }
	    catch ( Exception e )
	    {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      e.printStackTrace();
		      System.exit(0);
		}
	}
	
	public void write_firm_statistics(List<Firm> firms, int day)
	{
	    try 
	    {
			for (Firm firm: firms)
	        {
				SQLite_firmPreparedStatement.setLong(1, experiment_seed);
				SQLite_firmPreparedStatement.setInt(2, day);
				SQLite_firmPreparedStatement.setDouble(3, firm.wage_bill());
				SQLite_firmPreparedStatement.setInt(4, firm.num_expats());
				SQLite_firmPreparedStatement.setInt(5, firm.getNum_saudis());
				SQLite_firmPreparedStatement.setInt(6, firm.id);
				SQLite_firmPreparedStatement.setDouble(7, firm.net_worth);
				SQLite_firmPreparedStatement.setDouble(8, firm.profit);
				SQLite_firmPreparedStatement.setDouble(9, firm.price);
				SQLite_firmPreparedStatement.setDouble(10, firm.demand);
				SQLite_firmPreparedStatement.setDouble(11, firm.market_price);
				SQLite_firmPreparedStatement.setDouble(12, firm.getProduction());
				SQLite_firmPreparedStatement.setDouble(13, firm.planned_production);
				SQLite_firmPreparedStatement.setDouble(14, firm.stats_offer_wage_saudis);
				SQLite_firmPreparedStatement.setDouble(15, firm.stats_offer_wage_expats);
				SQLite_firmPreparedStatement.setDouble(16, firm.distributed_profits);
				SQLite_firmPreparedStatement.setDouble(17, firm.getWage_saudis());
				SQLite_firmPreparedStatement.setDouble(18, firm.GetWage_expats());
							
				
				SQLite_firmPreparedStatement.addBatch();
	        }

			SQLite_firmPreparedStatement.executeBatch();
			SQLite_firmPreparedStatement.clearBatch();

	    }
	    catch ( Exception e )
	    {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	System.exit(0);
	    }
	}
	
		
	public void write_aggregate_firm_statistics(List<Firm> firms, int day)
	{
		//reset the statistics for each iteration (Day..)
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
        
        
        
        
        // generating the statistics of the current iteration (Day..) by aggregating statistics from all firms in that iteration
        for (Firm firm: firms)
        {
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
        
        // inserting the current iteration (Day..)'s statistics into the database
	    try
	    {
			   SQLite_firmStatisticsPreparedStatement.setLong(1, experiment_seed);
			   SQLite_firmStatisticsPreparedStatement.setInt(2, day);
			   SQLite_firmStatisticsPreparedStatement.setInt(3, this.num_saudis );
			   SQLite_firmStatisticsPreparedStatement.setInt(4, this.num_expats );
			   SQLite_firmStatisticsPreparedStatement.setDouble(5, this.wage_bill / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(6, this.net_worth / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(7, this.profit / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(8, this.price / demand);
			   SQLite_firmStatisticsPreparedStatement.setDouble(9, this.demand );
			   SQLite_firmStatisticsPreparedStatement.setDouble(10, this.production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(11, this.planned_production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(12, this.offer_wage_saudis / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(13, this.offer_wage_expats / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(14, this.distributed_profits / num_firms );
			   SQLite_firmStatisticsPreparedStatement.setDouble(15, this.wage_saudis / num_saudis );
			   SQLite_firmStatisticsPreparedStatement.setDouble(16, this.wage_expats / num_expats );
			   SQLite_firmStatisticsPreparedStatement.setInt(17, this.staff - this.last_staff );
			   SQLite_firmStatisticsPreparedStatement.setInt(18, this.num_firms);
			   SQLite_firmStatisticsPreparedStatement.setInt(19, this.hires);
			   SQLite_firmStatisticsPreparedStatement.setInt(20, this.fires);
			   SQLite_firmStatisticsPreparedStatement.setInt(21, this.num_applications);
			   SQLite_firmStatisticsPreparedStatement.setDouble(22, decrease_price_bounded);
			   SQLite_firmStatisticsPreparedStatement.setDouble(23, decrease_prices_no_firing);
				SQLite_firmStatisticsPreparedStatement.setDouble(24, increase_price);
				SQLite_firmStatisticsPreparedStatement.setDouble(25, increase_price_wage_altered);
				SQLite_firmStatisticsPreparedStatement.setDouble(26, accepted_wage_saudis / stats_new_hires_saudi);
				SQLite_firmStatisticsPreparedStatement.setDouble(27, accepted_wage_expats / stats_new_hires_expat);

				
			   SQLite_firmStatisticsPreparedStatement.addBatch();
	    }
	    catch ( Exception e )
	    {
	    	System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    	e.printStackTrace();
	    	System.exit(0);
		}
	}


	public void close()
	{
		try
		{
			SQLite_firmStatisticsPreparedStatement.executeBatch();
			SQLite_firmStatisticsPreparedStatement.clearBatch();
			
					
			SQLite_Connection.commit();
			SQLite_Connection.close();
	    }
		catch ( Exception e )
		{
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
	    }
	}
	
	}