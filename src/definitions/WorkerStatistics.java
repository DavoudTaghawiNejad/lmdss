package definitions;

import agents.Worker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class WorkerStatistics
{
    final static class Emp {
        private final double  profit;
        private final boolean employed;

        public Emp(Worker w, double price)
        {
            this.employed = w.isEmployed();
            if (this.employed)
            {
                this.profit = w.getProductivity() * price - w.getWagePrivate();
                //System.out.println(w.wage);
            } else {
                this.profit = w.getProductivity() * price - w.getMarket_price();
            }

        }
    }

    public static void net_contribution(List<Worker> workers, double price, String file_name)
    {
        ArrayList<Emp> saudi_profitability = new ArrayList<Emp>();
        ArrayList<Emp> expat_profitability = new ArrayList<Emp>();


        for (Worker w:workers)
        {

            if (w.getCitizenship() == Citizenship.SAUDI) {
                saudi_profitability.add(new Emp(w, price));
            }
            else {
                expat_profitability.add(new Emp(w, price));
            }
        }

        Collections.sort(saudi_profitability, new Comparator<Emp>() {
            public int compare(Emp wk1, Emp wk2) {
                return new Double(wk2.profit).compareTo(wk1.profit);
            }
        });

        Collections.sort(expat_profitability, new Comparator<Emp>() {
            public int compare(Emp wk1, Emp wk2) {
                return new Double(wk2.profit).compareTo(wk1.profit);
            }
        });
        double [] saudi_profitability_employed = new double [saudi_profitability.size()];
        double [] expat_profitability_employed = new double [expat_profitability.size()];

        double [] saudi_profitability_unemployed = new double [saudi_profitability.size()];
        double [] expat_profitability_unemployed = new double [expat_profitability.size()];

 

        double net;

        for (int i = 0; i < saudi_profitability.size(); i++)
        {
                net = saudi_profitability.get(i).profit;
                if  (saudi_profitability.get(i).employed)
                {
                    saudi_profitability_employed[i] = net;
                } else {
                    saudi_profitability_unemployed[i] = net;
                }
        }

        for (int i = 0; i < expat_profitability.size(); i++)
        {
            net = expat_profitability.get(i).profit;
            if  (expat_profitability.get(i).employed)
            {
                expat_profitability_employed[i] = net;
            } else {
                expat_profitability_unemployed[i] = net;
            }
        }

        try
        {
            PrintWriter writer = new PrintWriter(file_name + ".csv", "UTF-8");
            writer.print("\t");
            writer.print("saudis employed\t");
            writer.print("saudis unemployed\t");
            writer.print("expats employed\t");
            writer.print("expats unemployed\n");
            for (int i = 0; i < Math.max(saudi_profitability_employed.length, expat_profitability_employed.length) ; i++)
            {
                writer.print("\t");
                if (i < saudi_profitability.size())
                {
                    if (!(saudi_profitability_employed[i] == 0))
                    {
                        writer.print(saudi_profitability_employed[i]);
                    }
                    writer.print("\t");
                    if (!(saudi_profitability_unemployed[i] == 0))
                    {
                        writer.print(saudi_profitability_unemployed[i]);
                    }
                    writer.print("\t");
                } else {
                    writer.print("");
                    writer.print("\t");
                    writer.print("");
                    writer.print("\t");
                }
                if (i < expat_profitability.size())
                {
                    if (!(expat_profitability_employed[i] == 0))
                    {
                        writer.print(expat_profitability_employed[i]);
                    }
                    writer.print("\t");
                    if (!(expat_profitability_unemployed[i] == 0))
                    {
                        writer.print(expat_profitability_unemployed[i]);
                    }
                    writer.print("\n");
                } else {
                    writer.print("");
                    writer.print("\t");
                    writer.print("");
                    writer.print("\n");
                }
            }
            writer.close();
        } catch (IOException ex) {
            System.out.print("cant write workers stats");
        }
    }
}