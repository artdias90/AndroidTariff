package com.oneunit.test.cj2;

/**
 * Created by Bishal on 11/24/2015.
 */

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;

import java.util.Calendar;
import java.util.Date;

public class ComparisonFragment extends Fragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.comparison_fragment,container,false);
        GraphView comparisonGraph;


        try {
            Config config = new Config(getActivity().getApplicationContext());
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();
            double[][] measuredData = TrafficInfoManager.getDataPerMonth(getActivity().getApplicationContext(), currentDate);
            int totalMonthData = 0;
            for (int j = 0; j<measuredData[0].length; j++) {
                totalMonthData += ((int)measuredData[0][j] + (int)measuredData[1][j]);
            }
            double userPricing = Double.valueOf(config.getUserConfig("price"));
            double userPackage = Double.valueOf(config.getUserConfig("volume_data"));
            double userPricePerBit = userPricing / userPackage;
            int totalMonthCost = 0;
            if(userPackage > totalMonthData) {
                totalMonthCost = (int)(userPricing);
            } else {
                totalMonthCost = (int)(userPricePerBit * totalMonthData);
            }
            TextView textViewToChange = (TextView) view.findViewById(R.id.comparison_price_per_bit_value);
            textViewToChange.append(userPricePerBit +" ");


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
}
