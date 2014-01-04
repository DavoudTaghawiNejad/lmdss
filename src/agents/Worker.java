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

    public int employer;
    public double wage;
    public double productivity;
    public Status status = Status.unemployed;

    public Worker(Citizenship citizenship, Newspaper newspaper) {
        this.citizenship = citizenship;
        this.newspaper = newspaper;
    }

    public void apply()
    {
        if (status == Status.unemployed)
        {
            JobAdd job_add =  newspaper.get_add();
            job_add.firm.add(this);
        }
    }


}