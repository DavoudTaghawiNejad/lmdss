package tools;

import agents.Firm;
import agents.Worker;
import definitions.Citizenship;
import definitions.ContractStatus;

import java.util.HashMap;
import java.util.List;


public class WorkerRecord
{
    private double agreed_wage;
    private int start_date;
    private Worker worker;
    public int visa_expiration = 0;
    public int contract_end = 0;
    public ContractStatus contract_status;
    private double wage;


    public WorkerRecord(Worker worker, double agreed_wage, int start_date) {
        this.agreed_wage = agreed_wage;
        this.start_date = start_date;
        this.worker = worker;
        this.contract_status = ContractStatus.new_hire;
    }

    public double getWage() {
        return agreed_wage;
    }

    public int getStart_date() {
        return start_date;
    }

    public double getProductivity() {
        return worker.getProductivity();
    }

    public Citizenship getCitizenship() {
        return worker.getCitizenship();
    }

    public boolean isEmployee(Firm employer) {
        return worker.isEmployee(employer);
    }

    public Worker getAddress() {
        return worker;
    }

    public void setWage(double wage)
    {
        this.wage = wage;
        this.worker.setWage(wage);
    }
}

