package com.oneunit.test.cj2;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.oneunit.test.cj2.UI.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by artdias90 on 27.04.2016.
 */
public class UserStatsActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.user_stats_fragment);

            // fetch user package from config
            Config config = new Config(getApplicationContext());
            setUserData(config);
            setSMSData(config);
            setVoiceData(config);
            setPriceData(config);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setUserData(Config config) throws IOException {
        TextView textViewToChange = (TextView) findViewById(R.id.user_data_value);
        String userData = config.getUserConfig("volume_data");
        textViewToChange.append(userData + " GB");
    }

    private void setSMSData(Config config) throws IOException {
        TextView textViewToChange = (TextView) findViewById(R.id.user_sms_value);
        String userData = config.getUserConfig("sms");
        textViewToChange.append(userData + " SMS");
    }

    private void setVoiceData(Config config) throws IOException {
        TextView textViewToChange = (TextView) findViewById(R.id.user_voice_value);
        String userData = config.getUserConfig("voice");
        textViewToChange.append(userData + " Minute(s)");
    }

    private void setPriceData(Config config) throws IOException {
        TextView textViewToChange = (TextView) findViewById(R.id.user_price_value);
        String userData = config.getUserConfig("price");
        textViewToChange.append(userData + " Euro(s)");
    }

    /** show welcome dialog to enter usage data**/
    public void editUserData(View view) {
        WelcomeDialog welcomeDialog = new WelcomeDialog(this);
        welcomeDialog.show();
        TableLayout tableLayout = (TableLayout) findViewById(R.id.user_stats);
        tableLayout.invalidate();
        tableLayout.refreshDrawableState();
    }




    /** Called when the user clicks the Send button */
    public void exportDb(View view) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /** Called when the user clicks the export analysis button */
    public void exportAnalysis(View view) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            if (sd.canWrite()) {

                String currentDBPath = "//data//com.oneunit.test.cj2//databases//NetworkAnalysis.db";
                String backupDBPath = "analysisBackup.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(downloads, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                }

                currentDBPath = "//data//com.oneunit.test.cj2//databases//TariffUseDaily.db";
                backupDBPath = "myBackup.db";
                currentDB = new File(data, currentDBPath);
                backupDB = new File(downloads, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                }

                currentDBPath = "//data//com.oneunit.test.cj2//databases//SendReceiveReader.db";
                backupDBPath = "SendReceiveReader.db";
                currentDB = new File(data, currentDBPath);
                backupDB = new File(downloads, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(getBaseContext(), backupDB.toString(), Toast.LENGTH_LONG).show();
                }

            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }



    public void sendUsage(View view) {

        try {
            Config config = new Config(getApplicationContext());
            RequestQueue queue = MySingleton.getInstance(getApplicationContext()).getRequestQueue();

            String userId = config.getUserConfig("user_id");
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            for(int i = 1; i<=month; i++) {
                c.set(Calendar.MONTH, i-1);
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
                String url ="http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/addDataUsage.pl?userId="+userId
                        +"&date="+String.format("%02d", i)+'-'+year
                        +"&data="+totalMonthData;
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"PROBLEM",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });

                String url2 ="http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/addCost.pl?userId="+userId
                        +"&date="+String.format("%02d", i)+'-'+year
                        +"&cost="+totalMonthCost;
                StringRequest stringRequest2 = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(),"PROBLEM",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
                MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest2);
                c.add(Calendar.DATE, 1);
            }


        }  catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }


    }



}
