package tools;

import agents.Firm;
import agents.Worker;


public class Staff<E> extends Group<E> {

    @Override
    protected double wage(Worker worker)
    {
        return worker.getWage();
    }

    public Staff(Firm employer) {
        super(employer);
    }

    @Override
    public boolean remove(Object worker)
    {
        Worker w = (Worker)worker;
        assert (w.isEmployee(super.employer));
        return super.remove(w);
    }
}