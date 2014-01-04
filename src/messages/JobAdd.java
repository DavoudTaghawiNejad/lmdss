package messages;
import agents.Worker;

import java.util.List;

public final class JobAdd{
    public final List<Worker> firm;
    public final double wage;

    public JobAdd(List<Worker> firm, double wage) {
    	this.firm = firm;
    	this.wage = wage;
    	assert wage > 0 || wage == -1: firm;
    }
} 
