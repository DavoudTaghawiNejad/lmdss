package messages;
import agents.Worker;
import tools.WorkerRecord;

import java.util.List;

public final class JobAdd{
    private final List<WorkerRecord> firm;
    private final double wage;

    public JobAdd(List<WorkerRecord> firm, double wage) {
    	this.firm = firm;
    	this.wage = wage;
    	assert wage > 0 || wage == -1: firm;
    }

    public double getWage() {
        return wage;
    }

    public List<WorkerRecord> getFirm() {
        return firm;
    }
}
