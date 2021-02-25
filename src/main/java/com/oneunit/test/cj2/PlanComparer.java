package com.oneunit.test.cj2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Created by Abbas on 11/25/2015.
 */
public class PlanComparer {

    private Context context;
    private String[] defaultPlan;
    private BufferedReader bufferedReader;
    private Config config;
    private TariffHandler tariffHandler;
    private SimpleDateFormat dateFormat;

    public PlanComparer(Context context) throws IOException{
        this.context = context;
        this.config = new Config(context);
        this.dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        this.defaultPlan = new String[5];
        for(int i = 0; i < this.defaultPlan.length; i++){
            this.defaultPlan[i] = this.config.getValues()[i+2];
        }
        this.tariffHandler = new TariffHandler(context);
    }

    public String[] getBestTariffPlan(String tariffName){
        String result[] = null;
        int counter = 0;
        for(counter = 0; counter < this.tariffHandler.getTariffNames().length; counter++){
            if(tariffName.equals(this.tariffHandler.getTariffNames()[counter])){
                break;
            }
        }
        int bestPlanCounter = counter;

        for(int i = 0; i < this.tariffHandler.getTariffCostPerKBs().length; i++){
            if(Float.valueOf(tariffHandler.getTariffCostPerKBs()[bestPlanCounter]) < Float.valueOf(tariffHandler.getTariffCostPerKBs()[i])){
                bestPlanCounter = i;
            }
        }
        return this.tariffHandler.getTariff(bestPlanCounter);
    }

   /* public boolean comparePlans(String[] myPlan, String[] plan){
        Calendar calendar = Calendar.getInstance();
        calendar.getTime();
        double dataPerDay[][] = TrafficInfoManager.getDataPerDay(this.context, calendar.getTime());

    }*/


    public String[] getDefaultPlan(){
        return this.defaultPlan;
    }

    public String[][] getTenBestTariffPlans(int usedMonthly){
        String result[][] = new String[9][3];
        String names[] = this.tariffHandler.getTariffNames();
        String speeds[] = this.tariffHandler.getTariffSpeeds();

        String limits[] = new String[this.tariffHandler.getTariffLimits().length];
        float pricePerMbs[] = new float[this.tariffHandler.getTariffCostPerKBs().length];
        String pricePerMbsAfterLimit[] = new String[this.tariffHandler.getTariffPricesExtra().length];

        float prices[] = new float[names.length];
        for(int i = 0; i < limits.length; i++){
            limits[i] = this.tariffHandler.getTariffLimits()[i];
            pricePerMbs[i] = Float.valueOf(this.tariffHandler.getTariffCostPerKBs()[i]);
            pricePerMbsAfterLimit[i] = this.tariffHandler.getTariffPricesExtra()[i];

            int limitsArr[][] = new int[limits.length][limits[i].split("!").length];
            float pricesArr[][] = new float[limits.length][pricePerMbsAfterLimit[i].split("!").length];

            for(int k = 0; k < limitsArr[i].length; k++){
                limitsArr[i][k] = Integer.valueOf(limits[i].split("!")[k]);
                pricesArr[i][k] = Float.valueOf(pricePerMbsAfterLimit[i].split("!")[k]);
            }


            float totalSum = 0f;
            for(int j = 0; j < limitsArr[i].length; j++){

                if(usedMonthly > limitsArr[i][j] && j != limitsArr[i].length - 1){
                    totalSum+=((limitsArr[i][j+1] - limitsArr[i][j])*pricesArr[i][j]);
                }
                else if (usedMonthly > limitsArr[i][j]){
                    totalSum+= ((usedMonthly - limitsArr[i][j])*pricesArr[i][j]);
                }
                else {
                    totalSum = limitsArr[i][0] * pricePerMbs[i];
                }
            }
            if(usedMonthly> limitsArr[i][0]){
                totalSum += limitsArr[i][0]*pricePerMbs[i];
            }

            prices[i] = totalSum;

        }
        float pricesCopy[] = prices;
        int pricesCopyCounter = 0;
        Arrays.sort(prices);

        for(int i = 0; i< pricesCopy.length; i++){
            if(pricesCopy[pricesCopyCounter] == pricesCopy[i]){
                String temp = names[pricesCopyCounter];
                names[pricesCopyCounter] = names[i];
                names[i] = temp;

                String tempSpeed = speeds[pricesCopyCounter];
                speeds[pricesCopyCounter] = speeds[i];
                speeds[i] = tempSpeed;
            }
        }
        int length = 0;
        if(names.length < result.length){
            length = names.length;
        }
        else{
            length = result.length;
        }

        for(int i = 0 ; i< length; i++){
            result[i][0] = names[i];
            result[i][1] = String.format("%.2f",prices[i]) + "";
            result[i][2] = speeds[i];
        }
        return result;
    }

    public String getMyMonthlyPrice(int usedData) throws IOException{
        String limit = this.config.getValues()[Config.MY_PLAN_LIMIT_INDEX];
        float pricePerMb = Float.valueOf(this.config.getValues()[Config.MY_PLAN_PRICE_PER_MB_INDEX]);
        String pricePerMbAfterLimit = this.config.getValues()[Config.MY_PLAN_PRICE_AFTER_LIMIT_PER_MB_INDEX];

        int limits[] = new int[limit.split("!").length];
        float prices[] = new float[pricePerMbAfterLimit.split("!").length];
        for(int k = 0; k < limits.length; k++){
            limits[k] = Integer.valueOf(limit.split("!")[k]);
            prices[k] = Float.valueOf(pricePerMbAfterLimit.split("!")[k]);
        }
        float totalSum = 0f;
        for(int i = 0; i < limits.length; i++){
            if(usedData > limits[i] && i != limits.length - 1){
                totalSum+=((limits[i+1] - limits[i])*prices[i]);
            }
            else if (usedData > limits[i]){
                totalSum+= ((usedData - limits[i])*prices[i]);
            }
            else {
                float sum = limits[0] * pricePerMb;
                return String.format("%.2f", sum );
            }
        }
        if(totalSum>0.1f){
            totalSum += limits[0]*pricePerMb;
        }
        return String.format("%.2f", totalSum);
    }
}
