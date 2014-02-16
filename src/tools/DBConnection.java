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
    private int num_saudis = 0;
    private int num_expats = 0;
    private double wage_bill = 0;
    private double net_worth = 0;
    private double profit = 0;
    private double price = 0;
    private double demand = 0;
    private double production = 0;
    private double planned_production = 0;
    private double offer_wage_saudis = 0;
    private double offer_wage_expats = 0;
    private double distributed_profits = 0;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    private int net_hires = 0;
    private int num_firms;
    private long experiment_seed;


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
	    		  "getNum_saudis int(11) DEFAULT NULL,"+
	    		  "id int(11) DEFAULT NULL,"+
	    		  "net_worth double DEFAULT NULL,"+
	    		  "profit double DEFAULT NULL,"+
	    		  "price double DEFAULT NULL,"+
	    		  "demand double DEFAULT NULL,"+
	    		  "market_price double DEFAULT NULL,"+
	    		  "getProduction double DEFAULT NULL,"+
	    		  "planned_production double DEFAULT NULL,"+
	    		  "offer_wage_saudis double DEFAULT NULL,"+
	    		  "offer_wage_expats double DEFAULT NULL,"+
	    		  "distributed_profits double DEFAULT NULL,"+
	    		  "getWage_saudis double DEFAULT NULL,"+
	    		  "GetWage_expats double DEFAULT NULL"+
	    		")";
	      stmt.executeUpdate(sql);
	      
	      sql = "INSERT INTO firms VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		  SQLite_firmPreparedStatement = SQLite_Connection.prepareStatement(sql);

	      //create the aggrigate firms' data table
	      stmt = SQLite_Connection.createStatement();
	      sql = "CREATE TABLE IF NOT EXISTS firm_statistics ("+
	    		  "experamentID bigint(20) DEFAULT NULL,"+
	    		  "day int(11) DEFAULT NULL,"+
	    		  "getNum_saudis int(11) DEFAULT NULL,"+
	    		  "num_expats  int(11) DEFAULT NULL,"+
	    		  "wage_bill  double DEFAULT NULL,"+
	    		  "net_worth  double DEFAULT NULL,"+
	    		  "profit  double DEFAULT NULL,"+
	    		  "price  double DEFAULT NULL,"+
	    		  "demand  double DEFAULT NULL,"+
	    		  "getProduction  double DEFAULT NULL,"+
	    		  "planned_production  double DEFAULT NULL,"+
	    		  "offer_wage_saudis  double DEFAULT NULL,"+
	    		  "offer_wage_expats  double DEFAULT NULL,"+
	    		  "distributed_profits  double DEFAULT NULL,"+
	    		  "getWage_saudis  double DEFAULT NULL,"+
	    		  "GetWage_expats  double DEFAULT NULL,"+
	    		  "net_hires  int(11) DEFAULT NULL,"+
	    		  "num_firms int(11) DEFAULT NULL"+
	    		")";
	      stmt.executeUpdate(sql);
	      
	      sql = "INSERT INTO firm_statistics VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
        this.num_firms = firms.size();
        
        
        // generating the statistics of the current iteration (Day..) by aggregating statistics from all firms in that iteration
        for (Firm firm: firms)
        {
        	this.num_saudis += firm.getNum_saudis();
        	this.num_expats += firm.num_expats();
        	this.wage_bill += firm.wage_bill();
        	this.net_worth += firm.net_worth;
        	this.profit += firm.profit;
        	this.price += firm.price;
        	this.demand += firm.demand;
        	this.production += firm.getProduction();
        	this.planned_production += firm.planned_production;
        	this.offer_wage_saudis += firm.stats_offer_wage_saudis;
        	this.offer_wage_expats += firm.stats_offer_wage_expats;
        	this.distributed_profits = firm.distributed_profits;
        	this.wage_saudis += firm.getWage_saudis();
        	this.wage_expats += firm.GetWage_expats();
        	this.net_hires += firm.net_hires;
        	firm.net_hires = 0;
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
			   SQLite_firmStatisticsPreparedStatement.setDouble(8, this.price / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(9, this.demand );
			   SQLite_firmStatisticsPreparedStatement.setDouble(10, this.production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(11, this.planned_production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(12, this.offer_wage_saudis / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(13, this.offer_wage_expats / num_firms);
			   SQLite_firmStatisticsPreparedStatement.setDouble(14, this.distributed_profits );
			   SQLite_firmStatisticsPreparedStatement.setDouble(15, this.wage_saudis );
			   SQLite_firmStatisticsPreparedStatement.setDouble(16, this.wage_expats );
			   SQLite_firmStatisticsPreparedStatement.setInt(17, this.net_hires );
			   SQLite_firmStatisticsPreparedStatement.setInt(18, this.num_firms);
			   
			   
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
