package tools;

import agents.Firm;
import agents.Worker;
import definitions.Citizenship;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class Group<E> extends ArrayList<E> {
    private double productivity = 0;
    private double wage = 0;
    private int saudis = 0;
    private int expats = 0;
    protected Firm employer;


    protected double wage(Worker worker)
    {
        throw new NotImplementedException();
    }

    public Group(Group<E> staff, Group<Worker> can_be_fired, Firm employer)
    {
        super(staff);
        super.removeAll(can_be_fired);
        this.employer = employer;
        productivity = staff.getProductivity() - can_be_fired.getProductivity();
        wage = staff.getWage() - can_be_fired.getWage();
        saudis = staff.getSaudis() - can_be_fired.getSaudis();
        expats = staff.getExpats() - can_be_fired.getExpats();
    }

    public int getSaudis() {
        return saudis;
    }


    public int getExpats() {
        return expats;
    }

    public double getProductivity() {
        return productivity;
    }

    public double getWage() {
        return wage;
    }

    public Group(int initialCapacity, Firm employer) {
        super(initialCapacity);
        this.employer = employer;
    }

    public Group(Firm employer) {
        super();
        this.employer = employer;
    }

    public Group(Group<E> team, Firm employer) {
        super(team);
        this.employer = employer;
        productivity = team.getProductivity();
        wage = team.getWage();
        saudis = getSaudis();
        expats = getExpats();
    }

    public Group(List<E> team, Firm employer) {
        super(team);
        this.employer = employer;

        for(Worker worker: ((List<Worker>)team))
        {
            productivity += worker.getProductivity();
            wage += wage(worker);
            if (worker.citizenship == Citizenship.SAUDI) {
                saudis++;
            } else {
                expats++;
            }
        }
    }

    public boolean addAll(Group to_add) {
        productivity += to_add.getProductivity();
        wage += to_add.getWage();
        saudis += to_add.getSaudis();
        expats += to_add.getExpats();
        return super.addAll((List<E>)to_add);
    }
    @Override
    public boolean add(E worker)
    {
        Worker w = (Worker)worker;
        if (w.isEmployee(employer))
        {
            wage += w.getWage();
        } else {
            wage += w.getAdvertisedWage();
        }
        productivity += ((Worker)w).getProductivity();
        if (((Worker)w).citizenship == Citizenship.SAUDI) {
            saudis++;
        } else {
            expats++;
        }
        return super.add((E)w);
    }

    public boolean remove(Object worker)
    {
        Worker w = (Worker)worker;
        wage -= wage(w);
        productivity -= w.getProductivity();
        if (w.citizenship == Citizenship.SAUDI) {
            saudis--;
        } else {
            expats--;
        }
        return super.remove(w);
    }

    public boolean removeAll(Group<Worker> to_remove)
    {
        wage -= to_remove.getWage();
        productivity -= to_remove.getProductivity();
        saudis -= getSaudis();
        expats -= getExpats();
        return super.removeAll(to_remove);
    }

    public Group()
    {
        throw new NotImplementedException();
    }


    @Override
    public boolean removeAll(Collection<?> c)
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
