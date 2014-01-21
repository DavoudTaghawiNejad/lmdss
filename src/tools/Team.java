package tools;

import agents.Firm;
import agents.Worker;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Collection;

public class Team<E> extends Group<E> {

    public Team(Group<E> staff, Firm employer) {
        super(staff, employer);
    }

    protected double wage(Worker worker)
    {
        return worker.getAdvertisedWage();
    }
    public Team(Firm employer) {
        super(employer);
    }

    @Override
    public boolean remove(Object worker)
    {
        Worker w = (Worker)worker;
        assert (!w.isEmployee(super.employer));
        return super.remove(w);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new NotImplementedException();
    }
}