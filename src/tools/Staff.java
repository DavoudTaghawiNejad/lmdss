package tools;

import agents.Firm;
import agents.Worker;


public class Staff extends Group {

    @Override
    protected double wage(Worker worker)
    {
        return worker.getWage();
    }

    public Staff(Firm employer) {
        super(employer);
    }

    public boolean remove(Worker worker)
    {
        assert (worker.isEmployee(super.employer)): worker_list.contains(worker);
        return super.remove(worker);
    }

    public void consistency()
    {
        double wage = 0;
        for (Worker worker: super.getWorker_list())
        {
            wage += worker.getWage();
        }
        assert (wage < super.getWage() + 0.000001 && wage > super.getWage() - 0.000001) : wage - super.getWage();
    }
}