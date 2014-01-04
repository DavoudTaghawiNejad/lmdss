package agents;

import messages.JobAdd;

import java.util.ArrayList;
import java.util.Random;


public class Newspaper {
	private double average_wage_offer;

    private Random rand;

	public java.util.List<JobAdd> job_adds = new ArrayList<JobAdd>();

    JobAdd get_add() {

		JobAdd job_add = null;
		if (job_adds.size() == 0) {
            System.out.println("0 job adds");
			return new JobAdd(null, -1);
		}
		double total_wage = 0;
		double choice = 0;
		for (JobAdd c : job_adds)
        {
			total_wage += c.wage;
		}
		choice = rand.nextDouble() * total_wage;

        for (JobAdd c : job_adds)
        {
			choice -= c.wage;
			if (choice <= 0) {
                job_add = c;
				break;
			}
		}
        return job_add;

	}

	void calculate_average_wage_offer() {

		double wage_offer = 0;
        for (JobAdd c : job_adds)
        {
			wage_offer += c.wage;
		}
		average_wage_offer = wage_offer / job_adds.size();
	}


	public void place_add(JobAdd job_add) {
			job_adds.add(job_add);
			assert job_add.wage > 0 : job_add.wage;
	}
	
	public Newspaper(long seed) {
        rand = new Random(seed);
	}

    public double getAverage_wage_offer() {
        return average_wage_offer;
    }

    public void clear_job_ads() {
        job_adds.clear();
    }
}
