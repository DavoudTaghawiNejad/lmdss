package agents;

import definitions.Citizenship;
import messages.JobAdd;

public class Worker
{
    public Citizenship citizenship;
    private Newspaper newspaper;
    private double wage;
    private double satisficing_wage;
    private double productivity;
    private double wage_floor;
    private Firm employer = null;
    private JobAdd job_add;
    private Auctioneer auctioneer;


    public double getWage() {
        assert wage > -0.01: wage;
        return wage;
    }

    public double getAdvertisedWage() {
        assert job_add.getWage() > -0.01: job_add.getWage();
        return job_add.getWage();
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
        wage = - Double.POSITIVE_INFINITY;
    }

    public void apply()
    {
    	if (!this.isEmployed())
        {
    		job_add = newspaper.get_add();

            if (
            		job_add.getFirm() != null  // no adds in the newspaper
            		&&
            		(job_add.getWage()/auctioneer.market_price) > wage_floor //adjust wage floor to inflation..
                )
            {
                job_add.getFirm().add(this);
            }
        }
    	else if (this.isEmployed() && citizenship == Citizenship.SAUDI)
    	{
    		job_add = newspaper.get_add();
            if (
            		job_add.getFirm() != null // no adds in the newspaper
            		&&
            		job_add.getWage() > wage
            		&&
            		job_add.getFirm() != employer
                )
            {
                job_add.getFirm().add(this);
	        }
        }
        assert job_add.getWage() > -2: job_add.getWage();
    }

    public void sendEmploy(Firm firm)
    {
    	if (employer == null)
    	{
    		employer = firm;
    		wage = job_add.getWage();
    	}
    	else
    	{
            employer.sendQuit(this);
            employer = firm;
            wage = job_add.getWage();
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