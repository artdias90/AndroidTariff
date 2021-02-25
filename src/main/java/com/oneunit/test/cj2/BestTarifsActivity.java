package com.oneunit.test.cj2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.IOException;

/**
 * Created by Abbas on 12/15/2015.
 */
public class BestTarifsActivity extends Activity {

    private Config config;
    private PlanComparer planComparer;
    private TextView textViewNames[];
    private TextView textViewPrices[];
    private TextView textViewSpeeds[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.best_tarif);
        int myPrice;
        int usedData = 500;
        try {
            this.config = new Config(this);
            this.planComparer = new PlanComparer(this);
            ((TextView) findViewById(R.id.my_tariff_name_text_view)).setText("(my)" + config.getValues()[Config.MY_PLAN_NAME_INDEX]);
            ((TextView) findViewById(R.id.my_tariff_price_text_view)).setText(planComparer.getMyMonthlyPrice(usedData) + "$");
            ((TextView) findViewById(R.id.my_tariff_speed_text_view)).setText(config.getValues()[Config.MY_PLAN_SPEED_INDEX] + "Mb/s");
        }
        catch (IOException e){
            e.printStackTrace();
        }

        this.textViewNames = new TextView[9];
        this.textViewPrices = new TextView[9];
        this.textViewSpeeds = new TextView[9];

        this.textViewNames[0] = (TextView) findViewById(R.id.tarif_name_0);
        this.textViewNames[1] = (TextView) findViewById(R.id.tarif_name_1);
        this.textViewNames[2] = (TextView) findViewById(R.id.tarif_name_2);
        this.textViewNames[3] = (TextView) findViewById(R.id.tarif_name_3);
        this.textViewNames[4] = (TextView) findViewById(R.id.tarif_name_4);
        this.textViewNames[5] = (TextView) findViewById(R.id.tarif_name_5);
        this.textViewNames[6] = (TextView) findViewById(R.id.tarif_name_6);
        this.textViewNames[7] = (TextView) findViewById(R.id.tarif_name_7);
        this.textViewNames[8] = (TextView) findViewById(R.id.tarif_name_8);

        this.textViewPrices[0] = (TextView) findViewById(R.id.tarif_price_0);
        this.textViewPrices[1] = (TextView) findViewById(R.id.tarif_price_1);
        this.textViewPrices[2] = (TextView) findViewById(R.id.tarif_price_2);
        this.textViewPrices[3] = (TextView) findViewById(R.id.tarif_price_3);
        this.textViewPrices[4] = (TextView) findViewById(R.id.tarif_price_4);
        this.textViewPrices[5] = (TextView) findViewById(R.id.tarif_price_5);
        this.textViewPrices[6] = (TextView) findViewById(R.id.tarif_price_6);
        this.textViewPrices[7] = (TextView) findViewById(R.id.tarif_price_7);
        this.textViewPrices[8] = (TextView) findViewById(R.id.tarif_price_8);

        this.textViewSpeeds[0] = (TextView) findViewById(R.id.tarif_speed_0);
        this.textViewSpeeds[1] = (TextView) findViewById(R.id.tarif_speed_1);
        this.textViewSpeeds[2] = (TextView) findViewById(R.id.tarif_speed_2);
        this.textViewSpeeds[3] = (TextView) findViewById(R.id.tarif_speed_3);
        this.textViewSpeeds[4] = (TextView) findViewById(R.id.tarif_speed_4);
        this.textViewSpeeds[5] = (TextView) findViewById(R.id.tarif_speed_5);
        this.textViewSpeeds[6] = (TextView) findViewById(R.id.tarif_speed_6);
        this.textViewSpeeds[7] = (TextView) findViewById(R.id.tarif_speed_7);
        this.textViewSpeeds[8] = (TextView) findViewById(R.id.tarif_speed_8);

        String bestTariffs[][] = planComparer.getTenBestTariffPlans(usedData);
        for(int i = 0; i < bestTariffs.length; i++){
            textViewNames[i].setText(bestTariffs[i][0]);
            textViewPrices[i].setText(bestTariffs[i][1] + "$");
            textViewSpeeds[i].setText(bestTariffs[i][2] + "Mb/s");
        }

    }
}
