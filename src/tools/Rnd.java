package tools;

import java.util.Random;

/**
 * Created by davoud on 1/3/14.
 */
public class Rnd extends Random
{
    public Rnd(long seed)
    {
        super(seed);
    }

    public Rnd() {
        assert false: "all random numbers must be seeded by the generator_seed!";
    }

    public double uniform(double stretch)
    {
        return super.nextDouble() * stretch;
    }
}
