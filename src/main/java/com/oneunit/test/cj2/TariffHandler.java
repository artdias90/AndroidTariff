package com.oneunit.test.cj2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Abbas on 11/24/2015.
 * Updated by Arthur on 08/04/2016
 * Tarif document model:
 * -Name
 * -Limit
 * -Cost per KBs
 * -Price
 * -Price Extra
 */
public class TariffHandler {
    private Context context;
    private static String FILE = "tariff_plans.txt";
    private String tariffNames[];
    private String tariffPrices[];
    private String tariffCostPerKBs[];
    private String tariffLimits[];
    private String tariffSpeeds[];
    private String tariffType[];
    private String tariffPricesExtra[];
    private String defaultPlan[];

    public TariffHandler(Context context) throws IOException{
        this.context = context;



        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.context.getResources().openRawResource(R.raw.tariff_plans)));

        int tariffAmont = Integer.valueOf(bufferedReader.readLine());
        this.tariffNames = new String[tariffAmont];
        this.tariffPrices = new String[tariffAmont];
        this.tariffCostPerKBs = new String[tariffAmont];
        this.tariffLimits = new String[tariffAmont];
        this.tariffSpeeds = new String[tariffAmont];
        this.tariffType = new String[tariffAmont];
        this.tariffPricesExtra = new String[tariffAmont];

        for(int i = 0; i < tariffAmont; i++){
            this.tariffNames[i] = bufferedReader.readLine();
            this.tariffLimits[i] = bufferedReader.readLine();
            this.tariffCostPerKBs[i] = bufferedReader.readLine();
            this.tariffPrices[i] = bufferedReader.readLine();
            this.tariffSpeeds[i] = bufferedReader.readLine();
            this.tariffType[i] = bufferedReader.readLine();
            this.tariffPricesExtra[i] = bufferedReader.readLine();
        }
        bufferedReader.close();
    }

    public String[] getTariffNames(){
        return this.tariffNames;
    }

    public String[] getTariffCostPerKBs() {
        return tariffCostPerKBs;
    }

    public String[] getTariffLimits() {
        return tariffLimits;
    }

    public String[] getTariffPrices() {
        return tariffPrices;
    }

    public String[] getTariffSpeeds() {
        return tariffSpeeds;
    }
    public String[] getTariffType(){
        return tariffType;
    }
    public String[] getTariffPricesExtra(){
        return tariffPricesExtra;
    }

    public String[] getTariff(int i){
        String[] result = new String[7];
        result[0] = this.tariffNames[i];
        result[1] = this.tariffCostPerKBs[i];
        result[2] = this.tariffLimits[i];
        result[3] = this.tariffPrices[i];
        result[4] = this.tariffSpeeds[i];
        result[5] = this.tariffType[i];
        result[6] = this.tariffPricesExtra[i];
        return result;
    }

    public String[] getDefaultPlan(){
        try {
            Config config = new Config(context);
            this.defaultPlan = new String[5];
            for (int i = 0; i < this.defaultPlan.length; i++) {
                this.defaultPlan[i] = config.getValues()[i + 2];
            }
        }
        catch (IOException e){

        }
        return defaultPlan;
    }
}
