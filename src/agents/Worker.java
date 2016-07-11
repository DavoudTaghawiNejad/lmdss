package agents;

import definitions.Citizenship;
import messages.JobAdd;
import tools.Rnd;
import tools.WorkerRecord;

public class Worker
{
    private Citizenship citizenship;
    private Newspaper newspaper;
    private double wage;
    private double satisficing_wage;
    private double productivity;
    private double wage_floor;
    private Firm employer = null;
    private JobAdd job_add;
    private Auctioneer auctioneer;
    private final Rnd rnd;
    private double reapplication_probability;
    private WorkerRecord worker_record;


    public Citizenship getCitizenship() {
        return citizenship;
    }

    public double getWagePrivate() {
        assert wage > -0.01: wage;
        return wage;
    }

    public double getAdvertisedWage() {
        assert job_add.getWage() > -0.01: job_add.getWage();
        return job_add.getWage();
    }

    public Worker(long seed, Citizenship citizenship, Newspaper newspaper, double reservation_wage, double productivity,
                  double minimum_wage, double tax_percentage,
                  double tax_per_head, double reapplication_probability, Auctioneer auctioneer)
    {
        this.reapplication_probability = reapplication_probability;
        this.rnd = new Rnd(seed);
        this.citizenship = citizenship;
        this.newspaper = newspaper;
        this.auctioneer = auctioneer;
        this.satisficing_wage = reservation_wage;
        this.productivity = productivity;
        re_calculate_wage(minimum_wage);
    }

    public boolean isEmployed () {
    	return employer != null;
    }

    public double getMarket_wage()
    {
        return newspaper.getAverage_wage_offer();
    }

    /**
     * calculate the wage floor, it depends on the workers' satisficing_wage, minimum wage, and taxation (if any..)
    **/
    public void re_calculate_wage (double minimum_wage)
    {

        wage_floor = Math.max(
                satisficing_wage,
                minimum_wage
        );

        if (isEmployed())
        {
            wage = Math.max(wage, wage_floor);
        }
    }
    public void sendFire()
      
    {
        employer = null;
        worker_record = null;
        wage = - Double.POSITIVE_INFINITY;
    }

    public void apply(int day)
    {
        if (!this.isEmployed())
        {
    		job_add = newspaper.get_add();

            if (
            		job_add != null  // no adds in the newspaper
            		&&
            		job_add.getWage() > wage_floor // adjust wage floor to inflation..
                )
            {
                job_add.getFirm().add(new WorkerRecord(this, job_add.getWage(), day));
            }
        }
    	else if (this.isEmployed() && citizenship == Citizenship.SAUDI)
    	{
            if (rnd.nextDouble() < reapplication_probability)
            {
                job_add = newspaper.get_add();
                if (
                        job_add != null // no adds in the newspaper
                        &&
                        job_add.getWage() > wage
                        &&
                        job_add.getFirm() != employer
                    )
                {
                    job_add.getFirm().add(new WorkerRecord(this, job_add.getWage(), day));
                }
            }
        }
    }

    public void sendEmploy(Firm firm, WorkerRecord new_worker_record)
    {
        if (employer != null)
        {
            employer.sendQuit(worker_record);
        }
        employer = firm;
        worker_record = new_worker_record;
        wage = job_add.getWage();
    }

    public double getProductivity() {
        return productivity;
    }

    public boolean isEmployee(Firm firm)
    {
        return employer == firm;
    }

    public void setWage(double wage)
    {
        this.wage = wage;
    }
}