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
	Statement statement = null;
	PreparedStatement firmPreparedStatement = null;
	
	
	public DBConnection (){
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
				System.out.println("Hola");
				System.out.println(firmPreparedStatement);

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

}
