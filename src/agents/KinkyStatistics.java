package agents;


import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class KinkyStatistics
{
    private List<Firm> firms;

    // distance bin distance_to_cut_off, sum of change of saudis
    HashMap<Integer, Integer> bin_change_saudis = new HashMap<Integer, Integer>();
    HashMap<Integer, Integer> bin_change_expats = new HashMap<Integer, Integer>();
    HashMap<Integer, Double> bin_change_Sauditization = new HashMap<Integer, Double>();
    // distance bin distance_to_cut_off, count
    HashMap<Integer, Integer> bin_count = new HashMap<Integer, Integer>();

    public KinkyStatistics(List<Firm> firms)
    {

        this.firms = firms;


    }

    public KinkyStatistics(String s)
    {
    }

    public void before()
    {
        for (Firm firm: firms)
        {
            double[] quotas;
            quotas = new double[]{0.0,
                0.0, // 1
                8 / 100,  // 2
                15 / 100, // 3
                20 / 100, // 4
                20 / 100 // 5
            };
            int size = firm.staff.getSaudis() + firm.staff.getExpats();
            double quota;
            if (size < 10)
            {
                quota = quotas[1];
            }
            else if (size < 50)
            {
                quota = quotas[2];
            }
            else if (size < 500)
            {
                quota = quotas[3];
            }
            else if (size < 3000)
            {
                quota = quotas[4];
            }
            else
            {
                quota = quotas[5];
            }
            firm.setKinky(quota);
        }
    }
    public void after() {
        for (Firm firm: firms)
        {
            bin_count.put(firm.getDistance_to_cut_off(), 0);
            bin_change_saudis.put(firm.getDistance_to_cut_off(), 0);
            bin_change_expats.put(firm.getDistance_to_cut_off(), 0);
            bin_change_Sauditization.put(firm.getDistance_to_cut_off(), 0.0);
        }
        for (Firm firm: firms)
        {
            int delta_saudis = firm.getBefore_saudis() - firm.staff.getSaudis();
            int delta_expats = firm.getBefore_expats() - firm.staff.getExpats();

            double b_sauditization = 0;
            double a_sauditization = 0;
            try {
                b_sauditization = firm.getBefore_saudis() / (firm.getBefore_saudis() + firm.getBefore_expats());
                a_sauditization = firm.staff.getSaudis() / (firm.staff.getSaudis()+ firm.staff.getExpats());
            } catch (Exception e) {
            }


            int distance_to_cut_off = firm.getDistance_to_cut_off();
            bin_change_saudis.put(distance_to_cut_off, bin_change_saudis.get(distance_to_cut_off) + delta_saudis);
            bin_change_expats.put(distance_to_cut_off, bin_change_expats.get(distance_to_cut_off) + delta_expats);
            bin_change_Sauditization.put(distance_to_cut_off, bin_change_Sauditization.get(distance_to_cut_off) + b_sauditization - a_sauditization);
            bin_count.put(distance_to_cut_off, bin_count.get(distance_to_cut_off) + 1);
        }
    }

    public JSONObject saudis()
    {
        JSONObject nitaqat_calibration_statistics = new JSONObject();
        Set<Integer> keys = bin_change_saudis.keySet();
        for (Integer  key: keys) {
            nitaqat_calibration_statistics.put(key, bin_change_saudis.get(key) / bin_count.get(key));
        }
        return nitaqat_calibration_statistics;
    }
    public JSONObject expats()
    {
        JSONObject nitaqat_calibration_statistics = new JSONObject();
        Set<Integer> keys = bin_change_expats.keySet();
        for (Integer  key: keys) {
            nitaqat_calibration_statistics.put(key, bin_change_expats.get(key) / bin_count.get(key));
        }
        return nitaqat_calibration_statistics;
    }
    public JSONObject sauditization()
    {
        JSONObject nitaqat_calibration_statistics = new JSONObject();
        Set<Integer> keys = bin_change_Sauditization.keySet();
        for (Integer  key: keys) {
            nitaqat_calibration_statistics.put(key, bin_change_Sauditization.get(key) / bin_count.get(key));
        }
        return nitaqat_calibration_statistics;
    }
}
