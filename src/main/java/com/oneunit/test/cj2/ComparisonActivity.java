package com.oneunit.test.cj2;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by artdias90 on 04.04.2016.
 */

public class ComparisonActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comparison_fragment);


        try {
            Config config = new Config(getApplicationContext());
            Calendar c = Calendar.getInstance();
            Date currentDate = c.getTime();
            double[][] measuredData = TrafficInfoManager.getDataPerMonth(this, currentDate);
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
            TextView textViewToChange = (TextView) findViewById(R.id.comparison_price_per_bit_value);
            textViewToChange.append("Currently you pay FUCK");


        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
