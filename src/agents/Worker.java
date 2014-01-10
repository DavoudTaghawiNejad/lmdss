package agents;

import definitions.Citizenship;
import messages.JobAdd;

public class Worker
{
    public Citizenship citizenship;
    private Newspaper newspaper;
    public double wage;
    private double satisficing_wage;
    private double productivity;
    private double wage_floor;
    private Firm employer = null;
    public JobAdd job_add;
    public Auctioneer auctioneer;
    public double getWage() {
        return wage;
    }

    public double getSatisficing_wage() {
        return satisficing_wage;
    }
    public Worker(Citizenship citizenship, Newspaper newspaper,double satisficing_wage, double productivity, double expat_minimum_wage, double saudi_minimum_wage, double expat_tax_percentage, double expat_tax_per_head, Auctioneer auctioneer)
    {
        this.citizenship = citizenship;
        this.newspaper = newspaper;
        this.auctioneer = auctioneer;
        this.satisficing_wage = satisficing_wage;
        this.productivity = productivity;
        re_calculate_wage(expat_minimum_wage ,saudi_minimum_wage, expat_tax_percentage, expat_tax_per_head);
    }
    
    public boolean isEmployed () {
    	return employer != null;
    }

    public double getMarket_price()
    {
        return newspaper.getAverage_wage_offer();
    }
    
    
    public void re_calculate_wage (double expat_minimum_wage, double saudi_minimum_wage, double expat_tax_percentage, double expat_tax_per_head)
      //calculate the wage floor, it depends on the workers' satisficing_wage, minimum wage, and taxation (if any..)
    {
    	if (citizenship == Citizenship.EXPAT)
    	{
    		wage_floor = Math.max(
    				satisficing_wage,
    				expat_minimum_wage
    		);
    		wage *= (1 + expat_tax_percentage);
    		wage += expat_tax_per_head;
    	} else if (citizenship == Citizenship.SAUDI)
    	{
    		wage_floor = Math.max(
    				satisficing_wage,
    				saudi_minimum_wage
    				
    		);
    	} 		
    }
    public void sendFire()
      
    {
        employer = null;
        wage = Double.POSITIVE_INFINITY;
    }

    public void apply()
    {
    	if (!this.isEmployed())
        {
    		job_add = newspaper.get_add();
            if (
            		job_add.firm != null
            		&&
            		(job_add.wage/auctioneer.market_price) > wage_floor //adjust wage floor to inflation..
                )
            {
                job_add.firm.add(this);
            }
        }
    	else if (this.isEmployed() && citizenship == Citizenship.SAUDI)
    	{
    		job_add = newspaper.get_add();
            if (
            		job_add.firm != null
            		&&
            		job_add.wage > wage
            		&&
            		job_add.firm != employer
                )
            {
                job_add.firm.add(this);
	        }
        }
    }

    public void sendEmploy(Firm firm)
    {
    	if (employer == null)
    	{
    		employer = firm;
    		wage = job_add.wage;
    	}
    	else
    	{
    		//System.out.println("Worker got a better offer...Nationality = "+citizenship+" old wage = "+wage+" new wage = "+job_add.wage);
    		employer.sendQuit(this);
    		employer = firm;
    		wage = job_add.wage;
    	}
    }

    public double getProductivity() {
        return productivity;
    }

    public boolean isEmployee(Firm firm)
    {
        return employer == firm;
    }
}