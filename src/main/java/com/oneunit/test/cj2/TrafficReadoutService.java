package com.oneunit.test.cj2;

/**
 * Created by Elena on 02/11/2015.
 */
import android.app.*;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.*;
import android.support.v4.app.NotificationCompat;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TrafficReadoutService extends Service {
    private boolean running;
    private QueueRequestHandler serverAccess;
    private final String mobileTag = new String("TrafficStatsNetwork");
    private final String mobileRXTag = new String("TrafficStatsNetworkRX");
    private final String mobileTXTag = new String("TrafficStatsNetworkTX");
    private final String wirelessTag = new String("TrafficStatsWifi");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        this.running = false;
    }

    @Override
    public void onDestroy(){
        this.running = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long mbDivider = 1000 * 1000;
        this.running = true;
        // collect consumption information
        TrafficStatsFile trStFile = new TrafficStatsFile(); // access to system files
        long totalBytesMobile = (trStFile.getMobileRxBytes() + trStFile.getMobileTxBytes()) / mbDivider;
        long totalBytesWifi = (trStFile.getWifiRxBytes() + trStFile.getWifiTxBytes()) / mbDivider;

        DailyFeedReaderDbHelper dbAccess = new DailyFeedReaderDbHelper(TrafficReadoutService.this);
        NetworkAnalysisDbHelper dbAnalysis = new NetworkAnalysisDbHelper(TrafficReadoutService.this);
        SendReceiveReaderDbHelper sendReceiveReaderDbHelper = new SendReceiveReaderDbHelper(TrafficReadoutService.this);
        serverAccess = new QueueRequestHandler(this); // remote server access
        double previousDataNetwork, previousDataWifi;
        long previousDataNetworkRX, previousDataNetworkTX;

        // read the previous value of the TrafficStats readout
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        previousDataNetwork = preferences.getFloat(mobileTag, (float)totalBytesMobile);
        previousDataWifi = preferences.getFloat(wirelessTag, (float)totalBytesWifi);

        try{
            previousDataNetworkRX = preferences.getLong(mobileRXTag, 0);
            previousDataNetworkTX = preferences.getLong(mobileTXTag, 0);
        } catch(ClassCastException c) {
            double temp1 = preferences.getFloat(mobileRXTag, 0);
            double temp2 = preferences.getFloat(mobileTXTag, 0);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(mobileRXTag);
            editor.remove(mobileTXTag);
            previousDataNetworkRX = (long)temp1;
            previousDataNetworkTX = (long)temp2;
        }


        SQLiteDatabase dbSourceWrite = dbAccess.getWritableDatabase();
        SQLiteDatabase dbSourceWrite2 = dbAnalysis.getWritableDatabase();
        SQLiteDatabase dbSourceWrite3 = sendReceiveReaderDbHelper.getWritableDatabase();


        // creating values to be written into the database1
        // timestamp + total consumption within an hour
        ContentValues values = new ContentValues();
        long timeNow = System.currentTimeMillis();
        values.put(DailyFeedReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP, timeNow);

        values.put(DailyFeedReaderContract.FeedEntry.COLUMN_NAME_VOLUME_NETWORK, totalBytesMobile - previousDataNetwork > 0?totalBytesMobile - previousDataNetwork: 0);
        values.put(DailyFeedReaderContract.FeedEntry.COLUMN_NAME_VOLUME_WIFI, totalBytesWifi - previousDataWifi> 0?totalBytesWifi - previousDataWifi: 0);


        // creating analysis logs to be written into the database2
        ContentValues values2 = new ContentValues();
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_TIMESTAMP, timeNow);
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_MOBILE_RX, trStFile.getMobileRxBytes());
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_MOBILE_TX, trStFile.getMobileTxBytes());
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_PREVIOUS_MOBILE, previousDataNetwork);
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_WIFI_RX, trStFile.getWifiRxBytes());
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_WIFI_TX, trStFile.getWifiTxBytes());
        values2.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_PREVIOUS_WIFI, previousDataWifi);



        //storing only transmitted and received mobile traffic
        ContentValues values3 = new ContentValues();
        values3.put(NetworkAnalysisDbContract.FeedEntry.COLUMN_NAME_TIMESTAMP, timeNow);
        values3.put(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_RX, trStFile.getMobileRxBytes() / mbDivider - previousDataNetworkRX > 0? trStFile.getMobileRxBytes() / mbDivider - previousDataNetworkRX : 0);
        values3.put(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_TX, trStFile.getMobileTxBytes() / mbDivider - previousDataNetworkTX > 0? trStFile.getMobileTxBytes() / mbDivider - previousDataNetworkTX : 0);
        values3.put(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_PREVIOUS_MOBILE_RX, previousDataNetworkRX);
        values3.put(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_PREVIOUS_MOBILE_TX, previousDataNetworkTX);



        long newRowId = dbSourceWrite.insert(DailyFeedReaderContract.FeedEntry.TABLE_NAME, "null", values); // insert the values

        long newRowId2 = dbSourceWrite2.insert(NetworkAnalysisDbContract.FeedEntry.TABLE_NAME, "null", values2); // insert the values2

        long newRowId3 = dbSourceWrite3.insert(SendReceiveReaderContract.FeedEntry.TABLE_NAME, "null", values3); // insert the values3

        serverAccess.requestQueue(Double.toString(totalBytesMobile - previousDataNetwork), dateFormat.format(new Date(timeNow))); // place the values onto the remote server

        // store the current readouts for next service launch
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(mobileTag, (float)totalBytesMobile);
        editor.putFloat(wirelessTag, (float)totalBytesWifi);
        editor.putLong(mobileRXTag, trStFile.getMobileRxBytes() > 0? (trStFile.getMobileRxBytes() / mbDivider) : 0);
        editor.putLong(mobileTXTag, trStFile.getMobileTxBytes() > 0? (trStFile.getMobileTxBytes() / mbDivider) : 0);
        editor.commit();




        if(trStFile.getMobileRxBytes() / mbDivider - previousDataNetworkRX > 0) {
            //Notification check
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Data Center")
                    .setContentText("Warning: You consumed "+ (trStFile.getMobileRxBytes() / mbDivider - previousDataNetworkRX)+"MB in last hour.");
            int notificationID = 911;
            Intent resultIntent = new Intent(this, MainActivity.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            mBuilder.setAutoCancel(true);
            mNotificationManager.notify(notificationID, mBuilder.build());
        }



        return START_NOT_STICKY;
    }
}
