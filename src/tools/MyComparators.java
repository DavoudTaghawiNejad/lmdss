package tools;

public enum MyComparators
{
    SMALLER("<")
            {
                @Override
                public boolean violation(double x1, double x2)
                {
                    return x1 >= x2;
                }

                @Override
                public boolean apply(int x1, int x2)
                {
                    return x1 >= x2;
                }
            },
    BIGGER(">")
            {
                @Override
                public boolean violation(double x1, double x2)
                {
                    return x1 <= x2;
                }@Override
                 public boolean apply(int x1, int x2)
                {
                    return x1 <= x2;
                }
            },
    SMALLER_EQUAL("<=")
    {
        @Override
        public boolean violation(double x1, double x2)
        {
            return x1 > x2;
        }
        public boolean apply(int x1, int x2)
        {
            return x1 > x2;
        }
    },
    BIGGER_EQUAL(">=")
    {
        @Override
        public boolean violation(double x1, double x2)
        {
            return x1 < x2;
        }
        @Override
        public boolean apply(int x1, int x2)
        {
            return x1 < x2;
        }
    };
    private final String text;

    private MyComparators(String text)
    {
        this.text = text;
    }

    // Yes, enums *can* have abstract methods. This code compiles...
    public abstract boolean violation(double x1, double x2);
    public abstract boolean apply(int x1, int x2);
    @Override
    public String toString()
    {
        return text;
    }
}