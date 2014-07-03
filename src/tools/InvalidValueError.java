package tools;



public class InvalidValueError extends Exception
{
    public InvalidValueError() {}

    public InvalidValueError(String Variable, String violation, int should, int is)
    {
        super("*** " + Variable + " " + Integer.toString(should)  + " " + violation + " " +  Integer.toString(is) + " ***");
    }

    public InvalidValueError(String Variable, String violation, double should, double is)
    {
        super("*** " + Variable + " " + Double.toString(should) + " " + violation + " " + Double.toString(is) + " ***");
    }
}
