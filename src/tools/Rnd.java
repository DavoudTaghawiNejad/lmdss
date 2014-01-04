package tools;

import java.util.Random;

/**
 * Created by davoud on 1/3/14.
 */
public class Rnd {
    private final Random generator;

    public Rnd(long seed)
    {
        generator = new Random(seed);
    }

    public double uniform(double stretch)
    {
        return generator.nextDouble() * stretch;
    }
}
