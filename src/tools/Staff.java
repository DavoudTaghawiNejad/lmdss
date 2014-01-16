package tools;

import agents.Worker;

public class Staff<E> extends Group<E> {
    private double wage(Worker worker)
    {
        return worker.getAdvertisedWage();
    }

}