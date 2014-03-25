package tools;

import agents.Firm;
import agents.Worker;
import definitions.Citizenship;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
        productivity = staff.getProductivity();
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
    
    public void recalculate_wage(HashMap<String, Double> before_policy, HashMap<String, Double> after_policy)
    {
        wage = 0;
        wage_saudis = 0;
        wage_expats = 0;

        double  after_expat_tax_percentage =  after_policy.get("expat_tax_percentage");
        double before_expat_tax_percentage = before_policy.get("expat_tax_percentage");
        double  after_saudi_tax_percentage = after_policy.get("saudi_tax_percentage");
        double before_saudi_tax_percentage = before_policy.get("saudi_tax_percentage");
        
        double after_expat_minimum_wage = after_policy.get("expat_minimum_wage");

        double after_saudi_minimum_wage = after_policy.get("saudi_minimum_wage");

        
        double after_expat_tax_per_head = after_policy.get("expat_tax_per_head");
        double before_expat_tax_per_head = before_policy.get("expat_tax_per_head");
        double after_saudi_tax_per_head = after_policy.get("saudi_tax_per_head");
        double before_saudi_tax_per_head = before_policy.get("saudi_tax_per_head");

        for (WorkerRecord worker: worker_list)
        {
            double before_tax_percent;
            double before_tax_per_head;
            double after_tax_percent;
            double after_tax_per_head;
            double minimum_wage;
            if (worker.getCitizenship() == Citizenship.SAUDI)
            {
                before_tax_percent = before_saudi_tax_percentage;
                before_tax_per_head = before_saudi_tax_per_head;
                after_tax_percent = after_saudi_tax_percentage;
                after_tax_per_head = after_saudi_tax_per_head;
                minimum_wage = after_saudi_minimum_wage;
            }
            else
            {
                before_tax_percent = before_expat_tax_percentage;
                before_tax_per_head = before_expat_tax_per_head; 
                after_tax_percent = after_expat_tax_percentage;
                after_tax_per_head = after_expat_tax_per_head;
                minimum_wage = after_expat_minimum_wage;
            }
            double netto_wage = worker.getWage() * (1 - before_tax_percent) - before_tax_per_head;
            double new_brutto = netto_wage * (1 + after_tax_percent) - after_tax_per_head;
            double new_wage = Math.max(new_brutto, minimum_wage);
            worker.setWage(new_wage);
            wage += new_wage;
            if (worker.getCitizenship() == Citizenship.SAUDI)
            {
                wage_saudis += new_wage;
            }
            else
            {
                wage_expats += new_wage;
            }
        }
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
