package messages;
import agents.Worker;

import java.util.List;

public final class JobAdd{
    private final List<Worker> firm;
    private final double wage;

    public JobAdd(List<Worker> firm, double wage) {
    	this.firm = firm;
    	this.wage = wage;
    	assert wage > 0 || wage == -1: firm;
    }

    public double getWage() {
        return wage;
    }

    public List<Worker> getFirm() {
        return firm;
    }
}
