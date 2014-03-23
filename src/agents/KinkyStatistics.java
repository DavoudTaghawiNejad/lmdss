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
            if (size < 250)
            {
                quota = quotas[2];
            }
            if (size < 1897)
            {
                quota = quotas[3];
            }
            else
            {
                quota = quotas[4];
            }
            firm.setKinky(quota);
        }
    }
    public void after() {
        for (Firm firm: firms)
        {
            bin_count.put(firm.getDistance_to_cut_off(), 0);
            bin_change_saudis.put(firm.getDistance_to_cut_off(), 0);
        }
        for (Firm firm: firms)
        {
            int delta_saudis = firm.getBefore_saudis() - firm.staff.getSaudis();
            int distance_to_cut_off = firm.getDistance_to_cut_off();
            bin_change_saudis.put(distance_to_cut_off, bin_change_saudis.get(distance_to_cut_off) + delta_saudis);
            bin_count.put(distance_to_cut_off, bin_count.get(distance_to_cut_off) + 1);
        }
    }

    public JSONObject json()
    {
        JSONObject nitaqat_calibration_statistics = new JSONObject();
        Set<Integer> keys = bin_change_saudis.keySet();
        for (Integer  key: keys) {
            nitaqat_calibration_statistics.put(key, bin_change_saudis.get(key) / bin_count.get(key));
        }
        return nitaqat_calibration_statistics;
    }
}
