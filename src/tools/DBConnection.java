package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import agents.Firm;

public class DBConnection {
	
	//database connection setup
	Connection connect = null;
	Connection SQLiteConnection = null;
	Statement statement = null;
	PreparedStatement firmPreparedStatement = null;
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
	
	
	
	
	
	public void SQLiteStatistics(List<Firm> firms, long experamentID,int day, int num_firms){
		//reset the statistics for each cycle
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
        
        
        //generating the current iteration (
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
	
	
	public DBConnection (){
		setupSQLite();  

	}
	
	public void setupMySQL(){
		// This will load the MySQL driver, each DB has its own driver
		
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		      // Setup the connection with the DB
		      connect = DriverManager
		          .getConnection("jdbc:mysql://localhost/saudifirms?"
		              + "user=root&password=4569515"
		              + "&rewriteBatchedStatements=true" );

		      //connect.setAutoCommit(false);
		      //this prepares a query to insert a firm intothe DB
		      String query = "INSERT INTO `saudiFirms`.`firms` VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			   firmPreparedStatement = connect.prepareStatement(query);
		}
		catch (Exception e)
		{
    		System.err.println("Error: " + e.getMessage());
    		e.printStackTrace();		
		}		
	}
	
	// this function only prepar a query, you must call bulckInsert() to execute query, this is done for performance enhancements.
	public void insertFirms(List<Firm> firms, long experamentID,int day)
	{

		try
		{
			for (Firm firm: firms)
	        {
				firmPreparedStatement.setLong(1, experamentID);
				firmPreparedStatement.setInt(2, day);
				firmPreparedStatement.setDouble(3, firm.wage_bill);
				firmPreparedStatement.setInt(4, firm.num_expats);
				firmPreparedStatement.setInt(5, firm.num_saudis);
				firmPreparedStatement.setInt(6, firm.id);
				firmPreparedStatement.setDouble(7, firm.net_worth);
				firmPreparedStatement.setDouble(8, firm.profit);
				firmPreparedStatement.setDouble(9, firm.price);
				firmPreparedStatement.setDouble(10, firm.demand);
				firmPreparedStatement.setDouble(11, firm.market_price);
				firmPreparedStatement.setDouble(12, firm.production);
				firmPreparedStatement.setDouble(13, firm.planned_production);
				firmPreparedStatement.setDouble(14, firm.offer_wage_saudis);
				firmPreparedStatement.setDouble(15, firm.offer_wage_expats);
				firmPreparedStatement.setDouble(16, firm.distributed_profits);
				firmPreparedStatement.setDouble(17, firm.wage_saudis);
				firmPreparedStatement.setDouble(18, firm.wage_expats);
				
				firmPreparedStatement.addBatch();             
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
			firmPreparedStatement.executeBatch();
			firmPreparedStatement.clearBatch(); 			
		}
		catch (Exception e)
		{
    		System.err.println("Error: " + e.getMessage());
    		e.printStackTrace();		
    	}
		
	}

	public void setupSQLite() {
		Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      SQLiteConnection = DriverManager.getConnection("jdbc:sqlite:test.db");
	      SQLiteConnection.setAutoCommit(false);
	      
	      // create the individual firms' database table
	      stmt = SQLiteConnection.createStatement();
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
		   firmPreparedStatement = SQLiteConnection.prepareStatement(sql);

	      
	      
	      
	      //create the aggrigate firms' data table
	      stmt = SQLiteConnection.createStatement();
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
	      SQLite_firmStatisticsPreparedStatement = SQLiteConnection.prepareStatement(sql);

	      
	      
	      
	      stmt.close();
	      
	    } catch ( Exception e ) {
		      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
		      e.printStackTrace();
		      System.exit(0);
		    }

	      
	}
	
	public void SQLite(List<Firm> firms, long experamentID,int day) {
	    try {
		   for (Firm firm: firms)
	        {
				firmPreparedStatement.setLong(1, experamentID);
				firmPreparedStatement.setInt(2, day);
				firmPreparedStatement.setDouble(3, firm.wage_bill);
				firmPreparedStatement.setInt(4, firm.num_expats);
				firmPreparedStatement.setInt(5, firm.num_saudis);
				firmPreparedStatement.setInt(6, firm.id);
				firmPreparedStatement.setDouble(7, firm.net_worth);
				firmPreparedStatement.setDouble(8, firm.profit);
				firmPreparedStatement.setDouble(9, firm.price);
				firmPreparedStatement.setDouble(10, firm.demand);
				firmPreparedStatement.setDouble(11, firm.market_price);
				firmPreparedStatement.setDouble(12, firm.production);
				firmPreparedStatement.setDouble(13, firm.planned_production);
				firmPreparedStatement.setDouble(14, firm.offer_wage_saudis);
				firmPreparedStatement.setDouble(15, firm.offer_wage_expats);
				firmPreparedStatement.setDouble(16, firm.distributed_profits);
				firmPreparedStatement.setDouble(17, firm.wage_saudis);
				firmPreparedStatement.setDouble(18, firm.wage_expats);
				
				firmPreparedStatement.addBatch();
	        }

			firmPreparedStatement.executeBatch();
			firmPreparedStatement.clearBatch();

			
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }


	}
	
	public void closeSQLite(){
		try {
			

	   SQLite_firmStatisticsPreparedStatement.executeBatch();
	   SQLite_firmStatisticsPreparedStatement.clearBatch();

			
		SQLiteConnection.commit();
		SQLiteConnection.close();
	      
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }

	}
}
