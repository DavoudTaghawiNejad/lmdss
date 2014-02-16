package tools;


import agents.Worker;

import java.util.ArrayList;
import java.util.List;

public class WorkerArray
{
    public static List<WorkerRecord> convert (List<Worker> workers, int today)
    {
        List<WorkerRecord> worker_records = new ArrayList<WorkerRecord>(workers.size());
        int i = 0;
        for (Worker worker: workers)
        {
            i++;
            worker_records.add(new WorkerRecord(worker, worker.getAdvertisedWage(), today));
        }
        return worker_records;
    }
}