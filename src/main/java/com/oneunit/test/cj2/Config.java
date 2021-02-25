package com.oneunit.test.cj2;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Abbas on 11/24/2015.
 */
public class Config {
    private final String FILE = "config.txt";
    static final String CONFIG_FIRST_USE = "first_use";
    static final String CONFIG_SEND_DATA = "send_data";
    static final String CONFIG_USER_ID = "user_id";
    public static final int MY_PLAN_NAME_INDEX = 2;
    public static final int MY_PLAN_LIMIT_INDEX = 4;
    public static final int MY_PLAN_PRICE_PER_MB_INDEX = 3;
    public static final int MY_PLAN_SPEED_INDEX = 6;
    public static final int MY_PLAN_PRICE_AFTER_LIMIT_PER_MB_INDEX = 8;
    private BufferedReader reader;
    private BufferedWriter writer;

    private Context context;

    public Config(Context context) throws IOException {
        this.context = context;
    }

    public boolean isEmpty() {
        try {
            this.reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE)));
            this.reader.close();
            return false;
        } catch (IOException e) {
            try {
                this.writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
                this.writer.flush();
                this.writer.close();
                return true;
            } catch (IOException e1) {
                e.printStackTrace();
            }
        }
        return false;

    }

    public String getValue(String value) throws IOException {
        if (isEmpty()) {
            throw new IOException("File Doesn't Exist");
        }
        String strArr[] = this.getValues();
        switch (value) {
            case CONFIG_FIRST_USE:
                return strArr[0].split("=")[0].trim();
            case CONFIG_SEND_DATA:
                return strArr[1].split("=")[1].trim();
            default:
                throw new IOException();
        }
    }

    public String[] getValues() throws IOException {
        if (isEmpty()) {
            throw new IOException("File Doesn't Exist");
        }
        this.reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE)));
        String result[] = new String[0];
        String temp = "";
        while ((temp = this.reader.readLine()) != null) {
            String tempResult[] = result;
            result = new String[result.length + 1];
            for (int i = 0; i < tempResult.length; i++) {
                result[i] = tempResult[i];
            }
            result[result.length - 1] = temp;
        }
        this.reader.close();
        return result;
    }

    //return user tariff option.
    // accepts 'volume_data', 'voice', 'sms', 'user_id', 'price'
    public String getUserConfig(String param) throws IOException {
        //Pattern pattern = Pattern.compile( "\\(data_volume|phone|sms\\)");
        //Matcher matcher = pattern.matcher(param);
        if(isEmpty()){
            throw new IOException("File Doesn't Exist");
        }
        this.reader = new BufferedReader(new InputStreamReader(context.openFileInput(FILE)));
        String temp = "";
        while((temp = this.reader.readLine()) != null) {
            if(temp.indexOf(param) > -1) {
                return temp.split(param+" ")[1];
            }
        }
        this.reader.close();
        return null;
    }


    public void createConfigFile(ArrayList<String> userOpts) throws IOException {
        String userId = this.getUserConfig("user_id");
        this.writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
        this.writer.write(Config.CONFIG_FIRST_USE + "=1");
        this.writer.newLine();

        // send data to server
        this.writer.write(Config.CONFIG_SEND_DATA + "=1");

        // store the user input for data, phone and sms rates
        this.writer.newLine();
        this.writer.write("volume_data " + userOpts.get(0));
        this.writer.newLine();
        this.writer.write("voice " + userOpts.get(1));
        this.writer.newLine();
        this.writer.write("sms " + userOpts.get(2));
        this.writer.newLine();
        this.writer.write("price " + userOpts.get(3));
        if(userId != null) {
            this.writer.newLine();
            this.writer.write("user_id " + userId);
        } else {
            // request an ID
            String url ="http://r1482a-02.etech.haw-hamburg.de/~s16tarif/cgi-bin/newUser.pl";
            RequestQueue queue = MySingleton.getInstance(context).getRequestQueue();

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
                                writer.newLine();
                                writer.write("user_id " + response);
                                writer.flush();
                                writer.close();
                                Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                Toast.makeText(context,response,Toast.LENGTH_SHORT).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(context,error.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

            MySingleton.getInstance(context).addToRequestQueue(stringRequest);
        }
        this.writer.flush();
        this.writer.close();
    }

    public void setValue(String confType, int value) throws Exception {
        if(value != 0 | value != 1){
            throw new Exception("Value must be either 0 or 1");
        }
        switch (confType){
            case Config.CONFIG_FIRST_USE:
                String values[] = this.getValues();
                this.writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
                this.writer.write(Config.CONFIG_FIRST_USE + "=" + value);
                this.writer.write(values[1]);
                return;
            case Config.CONFIG_SEND_DATA:
                values = this.getValues();
                this.writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
                this.writer.write(values[0]);
                this.writer.write(Config.CONFIG_SEND_DATA + "=" + value);
                return;
            default:
                throw new Exception("Wrong Configuration Name");
        }
    }

    public void writeToFile(String str) throws IOException{
        this.writer = new BufferedWriter(new OutputStreamWriter(context.openFileOutput(FILE, Context.MODE_PRIVATE)));
        this.writer.write(str);
        this.writer.flush();
        this.writer.close();
    }


}
