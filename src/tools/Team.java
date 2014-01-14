package tools;

import agents.Firm;
import agents.Worker;
import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by taghawi on 1/9/14.
 */
public class Team<E> extends ArrayList<E> {
    private double productivity = 0;
    private double wage = 0;
    private Firm employer;

    public double getProductivity() {
        return productivity;
    }

    public double getWage() {
        return wage;
    }

    public Team(int initialCapacity, Firm employer) {
        super(initialCapacity);
        this.employer = employer;
    }

    public Team(Firm employer) {
        super();
        this.employer = employer;
    }

    public Team(List<E> team, Firm employer) {
        super(team);
        this.employer = employer;

        for(Worker worker: ((List<Worker>)team))
        {
            productivity += worker.getProductivity();
            if (worker.isEmployee(employer))
            {
                wage += worker.getWage();
            } else {
                wage += worker.getAdvertisedWage();
            }
        }
    }

    public boolean addAll(Team to_add) {
            productivity += to_add.getProductivity();
            wage += to_add.getWage();
        return super.addAll((List<E>)to_add);
    }

    @Override
    public boolean add(E worker)
    {
        if (((Worker)worker).isEmployee(employer))
        {
            wage += ((Worker)worker).getWage();
        } else {
            wage += ((Worker)worker).getAdvertisedWage();
        }
        productivity += ((Worker)worker).getProductivity();
        return super.add(worker);
    }

    public boolean remove(Worker worker)
    {
        wage -= worker.getWage();
        productivity -= worker.getProductivity();
        return super.remove(worker);
    }

    public boolean removeAll(List<Worker> to_remove)
    {
        for (Worker worker: to_remove)
        {
            wage -= worker.getWage();
            productivity -= worker.getProductivity();
        }
        return super.removeAll(to_remove);
    }


    @Override
    public boolean removeAll(Collection<?> c)
    {
        throw new NotImplementedException();
    }
    //is there a command that forbids all non overridden functions?

    @Override
    public boolean remove(Object o)
    {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new NotImplementedException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new NotImplementedException();
    }
}
