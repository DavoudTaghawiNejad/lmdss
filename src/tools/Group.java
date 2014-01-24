package tools;

import agents.Firm;
import agents.Worker;
import definitions.Citizenship;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class Group 
{
    private double productivity = 0;
    private double wage = 0;
    private int saudis = 0;
    private int expats = 0;
    protected ArrayList<Worker> worker_list;
    protected Firm employer;

    public ArrayList<Worker> getWorker_list() {
        return worker_list;
    }

    protected double wage(Worker worker)
    {
        throw new NotImplementedException();
    }

    public Group(Group staff, Group can_be_fired, Firm employer)
    {
        worker_list = new ArrayList<Worker>(staff.getWorker_list());
        worker_list.removeAll(can_be_fired.getWorker_list());
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
        worker_list = new ArrayList<Worker>(initialCapacity);
        this.employer = employer;
    }

    public Group(Firm employer) {
        worker_list = new ArrayList<Worker>();
        this.employer = employer;
    }

    public Group(Group team, Firm employer) {
        worker_list = new ArrayList<Worker>(team.getWorker_list());
        this.employer = employer;
        productivity = team.getProductivity();
        wage = team.getWage();
        saudis = getSaudis();
        expats = getExpats();
    }

    public Group(List<Worker> team, Firm employer) {
        worker_list = new ArrayList<Worker>(team);
        this.employer = employer;

        for(Worker worker: (team))
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
        return worker_list.addAll((List<Worker>)to_add);
    }

    public boolean add(Worker worker)
    {
        if (worker.isEmployee(employer))
        {
            wage += worker.getWage();
        } else {
            wage += worker.getAdvertisedWage();
        }
        productivity += ((Worker) worker).getProductivity();
        if (((Worker) worker).citizenship == Citizenship.SAUDI) {
            saudis++;
        } else {
            expats++;
        }
        return worker_list.add(worker);
    }

    public boolean remove(Object worker)
    {
        wage -= wage((Worker)worker);
        productivity -= ((Worker)worker).getProductivity();
        if (((Worker)worker).citizenship == Citizenship.SAUDI) {
            saudis--;
        } else {
            expats--;
        }
        return worker_list.remove((Worker)worker);
    }

    public boolean removeAll(Group to_remove)
    {
        wage -= to_remove.getWage();
        productivity -= to_remove.getProductivity();
        saudis -= getSaudis();
        expats -= getExpats();
        return worker_list.removeAll(to_remove.getWorker_list());
    }

    public Group()
    {
        throw new NotImplementedException();
    }

    public int size()
    {
        return worker_list.size();
    }
    
    public boolean contains(Worker worker)
    {
        return worker_list.contains(worker);
    }

    public int count(Worker worker)
    {
        return Collections.frequency(worker_list, worker);
    }

    public void clear()
    {
        worker_list.clear();
    }

}
