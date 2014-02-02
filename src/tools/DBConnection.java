package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import agents.Firm;

public class DBConnection {
	
	//database connection setup
	Connection MySQL_connection = null;
	Connection SQLite_Connection = null;
	Statement statement = null;
	PreparedStatement MySQL_firmPreparedStatement = null;
	PreparedStatement SQLite_firmPreparedStatement = null;
	PreparedStatement SQLite_firmStatisticsPreparedStatement = null;
	String sql = null;
	
	
	
	// aggrigate parameters to be stored in each iteration (day)
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
	
	
	
	
	
	public DBConnection (){
		SQLite_setup();  

	}
	
	public void SQLite_setup() {
		Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      SQLite_Connection = DriverManager.getConnection("jdbc:sqlite:lmdss.db");
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

	      
	      
	      
	      //create the aggrigate firms' data table
	      stmt = SQLite_Connection.createStatement();
	      sql = "CREATE TABLE IF NOT EXISTS firm_statistics ("+
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
	    		  "net_hires  int(11) DEFAULT NULL,"+
	    		  "num_firms int(11) DEFAULT NULL"+
	    		")";
	      stmt.executeUpdate(sql);
	      
	      sql = "INSERT INTO firm_statistics VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	      SQLite_firmStatisticsPreparedStatement = SQLite_Connection.prepareStatement(sql);

	      
	      
	      
	      stmt.close();
	      
	    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      e.printStackTrace();
		      System.exit(0);
		    }

	      
	}
	
	public void SQLite_insertFirms(List<Firm> firms, long experamentID,int day) {
	    try {
		   for (Firm firm: firms)
	        {
				SQLite_firmPreparedStatement.setLong(1, experamentID);
				SQLite_firmPreparedStatement.setInt(2, day);
				SQLite_firmPreparedStatement.setDouble(3, firm.wage_bill);
				SQLite_firmPreparedStatement.setInt(4, firm.num_expats);
				SQLite_firmPreparedStatement.setInt(5, firm.num_saudis);
				SQLite_firmPreparedStatement.setInt(6, firm.id);
				SQLite_firmPreparedStatement.setDouble(7, firm.net_worth);
				SQLite_firmPreparedStatement.setDouble(8, firm.profit);
				SQLite_firmPreparedStatement.setDouble(9, firm.price);
				SQLite_firmPreparedStatement.setDouble(10, firm.demand);
				SQLite_firmPreparedStatement.setDouble(11, firm.market_price);
				SQLite_firmPreparedStatement.setDouble(12, firm.production);
				SQLite_firmPreparedStatement.setDouble(13, firm.planned_production);
				SQLite_firmPreparedStatement.setDouble(14, firm.offer_wage_saudis);
				SQLite_firmPreparedStatement.setDouble(15, firm.offer_wage_expats);
				SQLite_firmPreparedStatement.setDouble(16, firm.distributed_profits);
				SQLite_firmPreparedStatement.setDouble(17, firm.wage_saudis);
				SQLite_firmPreparedStatement.setDouble(18, firm.wage_expats);
				
				SQLite_firmPreparedStatement.addBatch();
	        }

			SQLite_firmPreparedStatement.executeBatch();
			SQLite_firmPreparedStatement.clearBatch();

			
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }


	}
	
	
	
	public void SQLite_FirmStatistics(List<Firm> firms, long experamentID,int day, int num_firms){
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
        this.num_firms = num_firms;
        
        
        //generating the statistics of the current iteration (Day..) by aggregating statistics from all firms in that iteration
        for (Firm firm: firms)
        {
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
        
        //inserting the current iteration (Day..)'s syayistics into the database
	    try {
			   SQLite_firmStatisticsPreparedStatement.setLong(1, experamentID);
			   SQLite_firmStatisticsPreparedStatement.setInt(2, day);
			   SQLite_firmStatisticsPreparedStatement.setInt(3, this.num_saudis );
			   SQLite_firmStatisticsPreparedStatement.setInt(4, this.num_expats );
			   SQLite_firmStatisticsPreparedStatement.setDouble(5, this.wage_bill );
			   SQLite_firmStatisticsPreparedStatement.setDouble(6, this.net_worth );
			   SQLite_firmStatisticsPreparedStatement.setDouble(7, this.profit );
			   SQLite_firmStatisticsPreparedStatement.setDouble(8, this.price );
			   SQLite_firmStatisticsPreparedStatement.setDouble(9, this.demand );
			   SQLite_firmStatisticsPreparedStatement.setDouble(10, this.production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(11, this.planned_production );
			   SQLite_firmStatisticsPreparedStatement.setDouble(12, this.offer_wage_saudis );
			   SQLite_firmStatisticsPreparedStatement.setDouble(13, this.offer_wage_expats );
			   SQLite_firmStatisticsPreparedStatement.setDouble(14, this.distributed_profits );
			   SQLite_firmStatisticsPreparedStatement.setDouble(15, this.wage_saudis );
			   SQLite_firmStatisticsPreparedStatement.setDouble(16, this.wage_expats );
			   SQLite_firmStatisticsPreparedStatement.setInt(17, this.net_hires );
			   SQLite_firmStatisticsPreparedStatement.setInt(18, this.num_firms);
			   
			   
			   SQLite_firmStatisticsPreparedStatement.addBatch();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      e.printStackTrace();
	      System.exit(0);
	    }

        
        
	}
	

	
	public void SQLite_close(){
		try {
			

	   SQLite_firmStatisticsPreparedStatement.executeBatch();
	   SQLite_firmStatisticsPreparedStatement.clearBatch();

			
		SQLite_Connection.commit();
		SQLite_Connection.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }

	}
	
	
	
	

	public void MySQL_setup(){
		// This will load the MySQL driver, each DB has its own driver
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      MySQL_connection = DriverManager
		          .getConnection("jdbc:mysql://localhost/saudifirms?"
		              + "user=root&password=4569515"
		              + "&rewriteBatchedStatements=true" );

		      //connect.setAutoCommit(false);
		      //this prepares a query to insert a firm intothe DB
		      String query = "INSERT INTO `saudiFirms`.`firms` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			   MySQL_firmPreparedStatement = MySQL_connection.prepareStatement(query);
		}
		catch (Exception e)
		{
    		System.err.println("Error: " + e.getMessage());
    		e.printStackTrace();		
		}		
	}
	
	public void MySQL_insertFirms(List<Firm> firms, long experamentID,int day)
	{

		try
		{
			for (Firm firm: firms)
	        {
				MySQL_firmPreparedStatement.setLong(1, experamentID);
				MySQL_firmPreparedStatement.setInt(2, day);
				MySQL_firmPreparedStatement.setDouble(3, firm.wage_bill);
				MySQL_firmPreparedStatement.setInt(4, firm.num_expats);
				MySQL_firmPreparedStatement.setInt(5, firm.num_saudis);
				MySQL_firmPreparedStatement.setInt(6, firm.id);
				MySQL_firmPreparedStatement.setDouble(7, firm.net_worth);
				MySQL_firmPreparedStatement.setDouble(8, firm.profit);
				MySQL_firmPreparedStatement.setDouble(9, firm.price);
				MySQL_firmPreparedStatement.setDouble(10, firm.demand);
				MySQL_firmPreparedStatement.setDouble(11, firm.market_price);
				MySQL_firmPreparedStatement.setDouble(12, firm.production);
				MySQL_firmPreparedStatement.setDouble(13, firm.planned_production);
				MySQL_firmPreparedStatement.setDouble(14, firm.offer_wage_saudis);
				MySQL_firmPreparedStatement.setDouble(15, firm.offer_wage_expats);
				MySQL_firmPreparedStatement.setDouble(16, firm.distributed_profits);
				MySQL_firmPreparedStatement.setDouble(17, firm.wage_saudis);
				MySQL_firmPreparedStatement.setDouble(18, firm.wage_expats);
				
				MySQL_firmPreparedStatement.addBatch();             
	        }

		}
		catch (Exception e)
		{
    		System.err.println("Error: " + e.getMessage());
    		e.printStackTrace();		
    	}
	}
	
	public void executeBatch () {
		
		try{
			MySQL_firmPreparedStatement.executeBatch();
			MySQL_firmPreparedStatement.clearBatch(); 			
		}
		catch (Exception e)
		{
    		System.err.println("Error: " + e.getMessage());
    		e.printStackTrace();		
    	}
		
	}

	
	
	
}
