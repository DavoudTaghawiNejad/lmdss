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
			return null;

		}
		double total_wage = 0;
		double choice;
		for (JobAdd add : job_adds)
        {
			total_wage += add.getWage();
		}
		choice = rand.nextDouble() * total_wage;

        for (JobAdd add : job_adds)
        {
			choice -= add.getWage();
			if (choice <= 0) {
                job_add = add;
				break;
			}
		}
        assert job_add != null: total_wage;
        return job_add;
	}

	public void calculate_average_wage_offer() {

		double wage_offer = 0;
        for (JobAdd add : job_adds)
        {
			wage_offer += add.getWage();
		}
		average_wage_offer = wage_offer / job_adds.size();
	}


	public void place_add(JobAdd job_add) {
            assert job_add.getWage() > 0: job_add.getWage();
			job_adds.add(job_add);
			assert job_add.getWage() > 0 : job_add.getWage();
	}
	
	public Newspaper(long seed) {
        rand = new Random(seed);
	}

    public double getAverage_wage_offer() {
        return average_wage_offer;
    }

    public void clear_job_ads() {
        job_adds.clear();
        average_wage_offer = Double.NEGATIVE_INFINITY;
    }
}
