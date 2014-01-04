package agents;

import definitions.Citizenship;
import messages.JobAdd;

public class Worker
{
    public Citizenship citizenship;
    private Newspaper newspaper;

    public void fire()
    {
        status = Status.unemployed;
        employer = -1;
    }

    enum Status {employed, unemployed};

    private int employer;
    public double wage;
    public double productivity;
    private Status status = Status.unemployed;



    public Worker(Citizenship citizenship, Newspaper newspaper,double wage, double productivity)
    {
        this.citizenship = citizenship;
        this.newspaper = newspaper;
        this.wage = wage;
        this.productivity = productivity;
    }

    public void apply()
    {
        if (status == Status.unemployed)
        {
            JobAdd job_add =  newspaper.get_add();
            job_add.firm.add(this);
        }
    }

    public void employ(Firm firm)
    {
        employer = firm.id;
        status = Status.employed;
    }
}