package agents;

import definitions.Citizenship;
import definitions.Status;
import messages.JobAdd;

public class WorkerStats
{
    public Citizenship citizenship;
    private Newspaper newspaper;
    public double wage;
    public double productivity;
    public Status status = Status.unemployed;
    private int employer;
    private JobAdd job_add;

    public WorkerStats(Citizenship citizenship, Newspaper newspaper, double wage, double productivity)
    {
        this.citizenship = citizenship;
        this.newspaper = newspaper;
        this.wage = wage;
        this.productivity = productivity;
    }

    public void fire()
    {
        status = Status.unemployed;
        employer = -1;
    }

    public void apply()
    {
        if (status == Status.unemployed)
        {
            job_add = newspaper.get_add();
            if (job_add.firm != null)
            {
                job_add.firm.add(this);
            }
        }
    }

    public void employ(Firm firm)
    {
        employer = firm.id;
        status = Status.employed;
        wage = job_add.wage;
    }
}