package tools;


public class check_bounds
{
    public static void check_bound(String variable, double is, double bound, MyComparators comp) throws InvalidValueError
    {
        if (comp.violation(is, bound))
        {
            throw new InvalidValueError(variable, comp.toString(), is, bound);
        }
    }

    public static void check_bound(String variable, int is, int bound, MyComparators comp) throws InvalidValueError
    {
        if (comp.violation(is, bound))
        {
            throw new InvalidValueError(variable, comp.toString(), is, bound);
        }
    }

    public static void check_bounds(String variable, double is, double lower, double upper, MyComparators comp_lower, MyComparators comp_upper) throws InvalidValueError
    {
        check_bound(variable, is, lower, comp_lower);
        check_bound(variable, is, upper, comp_upper);
    }

    public static void check_bounds(String variable, int is, int lower, int upper, MyComparators comp_lower, MyComparators comp_upper) throws InvalidValueError
    {
        check_bound(variable, is, lower, comp_lower);
        check_bound(variable, is, upper, comp_upper);
    }
}

