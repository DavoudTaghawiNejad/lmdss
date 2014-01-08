package agents;

import definitions.Citizenship;
import definitions.Status;
import messages.JobAdd;

public class Worker
{
    public Citizenship citizenship;
    private Newspaper newspaper;
    public double wage;
    public double satisficing_wage;
    public double productivity;
    public double wage_floor;
    public Status status = Status.unemployed;
    private Firm employer = null;
    private JobAdd job_add;
    public Auctioneer auctioneer;
    public Worker(Citizenship citizenship, Newspaper newspaper,double satisficing_wage, double productivity, double expat_minimum_wage, double saudi_minimum_wage, double expat_tax_percentage, double expat_tax_per_head, Auctioneer auctioneer)
    {
        this.citizenship = citizenship;
        this.newspaper = newspaper;
        this.auctioneer = auctioneer;
        this.satisficing_wage = satisficing_wage;
        this.productivity = productivity;
        re_calculate_wage(expat_minimum_wage ,saudi_minimum_wage, expat_tax_percentage, expat_tax_per_head);
    }
    
    
    public void re_calculate_wage (double expat_minimum_wage, double saudi_minimum_wage, double expat_tax_percentage, double expat_tax_per_head)
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
    public void fire()
    {
        status = Status.unemployed;
        employer = null;
    }

    public void apply()
    {
    	if (status == Status.unemployed)
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
    	else if (status == Status.employed && citizenship == Citizenship.SAUDI)
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

    public void employ(Firm firm)
    {
    	if (employer == null)
    	{
    		employer = firm;
    		status = Status.employed;
    		wage = job_add.wage;
    	}
    	else
    	{
    		//System.out.println("Worker got a better offer...Nationality = "+citizenship+" old wage = "+wage+" new wage = "+job_add.wage);
    		employer.quitWorker(this);
    		employer = firm;
    		status = Status.employed;
    		wage = job_add.wage;
    	}
    }
    
}