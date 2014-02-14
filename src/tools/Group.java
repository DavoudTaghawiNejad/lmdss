package tools;

import agents.Firm;
import definitions.Citizenship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Group 
{
    private double productivity = 0;
    private double wage = 0;
    private double wage_saudis = 0;
    private double wage_expats = 0;
    private int saudis = 0;
    private int expats = 0;
    protected ArrayList<WorkerRecord> worker_list;
    protected Firm employer;

    public ArrayList<WorkerRecord> getWorker_list() {
        return worker_list;
    }

    public Group(Group staff, Group can_be_fired, Firm employer)
    {
        worker_list = new ArrayList<WorkerRecord>(staff.getWorker_list());
        worker_list.removeAll(can_be_fired.getWorker_list());
        this.employer = employer;
        productivity = staff.getProductivity() - can_be_fired.getProductivity();
        wage = staff.getWage() - can_be_fired.getWage();
        wage_saudis = staff.getWage_saudis() - can_be_fired.getWage_saudis();
        wage_expats = staff.getWage_expats() - can_be_fired.getWage_expats();

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

    public Group(int initialCapacity, Firm employer) {
        worker_list = new ArrayList<WorkerRecord>(initialCapacity);
        this.employer = employer;
    }

    public Group(Firm employer) {
        worker_list = new ArrayList<WorkerRecord>();
        this.employer = employer;
    }

    public Group(Group team, Firm employer) {
        worker_list = new ArrayList<WorkerRecord>(team.getWorker_list());
        this.employer = employer;
        productivity = team.getProductivity();
        wage = team.getWage();
        wage_saudis = team.getWage_saudis();
        wage_expats = team.getWage_expats();
        saudis = getSaudis();
        expats = getExpats();
    }

    public Group(List<WorkerRecord> team, Firm employer) {
        worker_list = new ArrayList<WorkerRecord>(team);
        this.employer = employer;

        for(WorkerRecord worker: (team))
        {
            productivity += worker.getProductivity();
            wage += worker.getWage();
            if (worker.getCitizenship() == Citizenship.SAUDI) {
                saudis++;
                wage_saudis += worker.getWage();
            } else {
                expats++;
                wage_expats += worker.getWage();
            }
        }
    }

    public boolean addAll(Group to_add) {
        productivity += to_add.getProductivity();
        wage += to_add.getWage();
        wage_saudis += to_add.getWage_saudis();
        wage_expats += to_add.getWage_expats();
        saudis += to_add.getSaudis();
        expats += to_add.getExpats();
        return worker_list.addAll(to_add.getWorker_list());
    }

    public boolean add(WorkerRecord worker)
    {
        wage += worker.getWage();
        productivity += worker.getProductivity();
        if (worker.getCitizenship() == Citizenship.SAUDI) {
            saudis++;
            wage_saudis += worker.getWage();
        } else {
            expats++;
            wage_expats += worker.getWage();
        }
        return worker_list.add(worker);
    }

    public boolean remove(WorkerRecord worker)
    {
        wage -= worker.getWage();
        productivity -= worker.getProductivity();
        if (worker.getCitizenship() == Citizenship.SAUDI) {
            saudis--;
            wage_saudis -= worker.getWage();
        } else {
            expats--;
            wage_expats -= worker.getWage();
        }
        return worker_list.remove(worker);
    }

    public boolean removeAll(Group to_remove)
    {
        wage -= to_remove.getWage();
        wage_saudis -= to_remove.getWage_saudis();
        wage_expats -= to_remove.getWage_expats();
        productivity -= to_remove.getProductivity();
        saudis -= getSaudis();
        expats -= getExpats();
        return worker_list.removeAll(to_remove.getWorker_list());
    }

    public int size()
    {
        return worker_list.size();
    }

    public boolean contains(WorkerRecord worker)
    {
        return worker_list.contains(worker);
    }

    public int count(WorkerRecord worker)
    {
        return Collections.frequency(worker_list, worker);
    }

    public void clear()
    {
        worker_list.clear();
    }

    public double getWage_saudis()
    {
        return wage_saudis;
    }

    public double getWage_expats()
    {
        return wage_expats;
    }

    public double getWage() {
        return wage;
    }
}
