package com.oneunit.test.cj2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.oneunit.test.cj2.UI.Constants;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Elena on 24/11/2015.
 */
public class TrafficInfoManager {

     public static double[][] getDataPerDay (Context context, Date dayInfo){
         double[][] consumptionPerDay = new double[2][Constants.DATA_PER_DAY];
         int i = 0;

         Calendar cal = Calendar.getInstance();
         cal.setTimeInMillis(dayInfo.getTime());
         cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
         cal.set(Calendar.MINUTE, 0); // set minutes to zero
         cal.set(Calendar.SECOND, 0); //set seconds to zero
         float dateStart = cal.getTimeInMillis();
         cal.set(Calendar.HOUR_OF_DAY, 23); //set hours to 23
         cal.set(Calendar.MINUTE, 59); // set minutes to 59
         cal.set(Calendar.SECOND, 59); //set seconds to 59
         float dateStop = cal.getTimeInMillis();

         int j=0;
         for (j = 0; j < Constants.DATA_PER_DAY; j++) {
             consumptionPerDay[0][j] = 0;
             consumptionPerDay[1][j] = 0;
         }

             SendReceiveReaderDbHelper dbAccess = new SendReceiveReaderDbHelper(context);
             SQLiteDatabase dbSource = dbAccess.getReadableDatabase();
             // read the previous value from the database
             String selectQuery = "SELECT  * FROM " + SendReceiveReaderContract.FeedEntry.TABLE_NAME + " WHERE " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP +
                     " >= " + dateStart + " AND " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " <= " +  dateStop + " ;";
             Cursor cursor = dbSource.rawQuery(selectQuery, null);

         int dateColumnIndex = cursor.getColumnIndex(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP);
         int valueIndexNetworkRX = cursor.getColumnIndex(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_RX);
         int valueIndexNetworkTX = cursor.getColumnIndex(SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_TX);

         if(cursor.moveToFirst()) {
             do {
                 cal.setTimeInMillis(cursor.getLong(dateColumnIndex));
                 j = cal.get(Calendar.HOUR_OF_DAY);
                 consumptionPerDay[0][j] = cursor.getDouble(valueIndexNetworkRX); // display in MB
                 consumptionPerDay[1][j] = cursor.getDouble(valueIndexNetworkTX); // display in MB
                 } while (cursor.moveToNext());
             }
         return consumptionPerDay;
    }

    public static double[][] getDataPerWeek (Context context, Date dayInfo) {
        double[][] consumptionPerWeek = new double[2][Constants.DATA_PER_WEEK];
        int i = 0;

        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(dayInfo.getTime());
        calStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calStart.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        calStart.set(Calendar.MINUTE, 0); // set minutes to zero
        calStart.set(Calendar.SECOND, 0); //set seconds to zero
        float dateStart = calStart.getTimeInMillis();
        Calendar calStop = Calendar.getInstance();
        calStop.setTimeInMillis(dayInfo.getTime());
        calStop.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        calStop.set(Calendar.HOUR_OF_DAY, 23); //set hours to 23
        calStop.set(Calendar.MINUTE, 59); // set minutes to 59
        calStop.set(Calendar.SECOND, 59); //set seconds to 59
        float dateStop = calStop.getTimeInMillis();

        SendReceiveReaderDbHelper dbAccess = new SendReceiveReaderDbHelper(context);
        SQLiteDatabase dbSource = dbAccess.getReadableDatabase();
        // read the previous value from the database

        for (i = 0; i < Constants.DATA_PER_WEEK; i++){
            String selectQuery = "SELECT SUM(" + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_RX + ") FROM " + SendReceiveReaderContract.FeedEntry.TABLE_NAME +
                    " WHERE " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " >= " + dateStart + " AND " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP +
                    " <= " +  dateStop + " ;";
            Cursor cursor = dbSource.rawQuery(selectQuery, null);

            cursor.moveToFirst();
            consumptionPerWeek[0][i] = cursor.getDouble(0);

            selectQuery = "SELECT SUM(" + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_TX + ") FROM " + SendReceiveReaderContract.FeedEntry.TABLE_NAME +
                    " WHERE " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " >= " + dateStart + " AND " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP +
                    " <= " +  dateStop + " ;";
            cursor = dbSource.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            consumptionPerWeek[1][i] = cursor.getDouble(0);

            calStart.add(Calendar.DATE, 1);
            dateStart = calStart.getTimeInMillis();
            calStop.add(Calendar.DATE, 1);
            dateStop = calStop.getTimeInMillis();
        }
        return consumptionPerWeek;
    }


    // retutotal daily usage in one month rangeday.
    // args: context, current date
    public static double[][] getDataPerMonth (Context context, Date dayInfo) {
        double[][] consumptionPerMonth = new double[2][Constants.DATA_PER_MONTH];
        int i = 0;

        Calendar calStart = Calendar.getInstance();
        calStart.setTimeInMillis(dayInfo.getTime());
        calStart.set(Calendar.DAY_OF_MONTH, 1);
        calStart.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        calStart.set(Calendar.MINUTE, 0); // set minutes to zero
        calStart.set(Calendar.SECOND, 0); //set seconds to zero
        float dateStart = calStart.getTimeInMillis();
        Calendar calStop = Calendar.getInstance();
        calStop.setTimeInMillis(dayInfo.getTime());
        calStop.set(Calendar.DAY_OF_MONTH, 1);
        calStop.set(Calendar.HOUR_OF_DAY, 23); //set hours to 23
        calStop.set(Calendar.MINUTE, 59); // set minutes to 59
        calStop.set(Calendar.SECOND, 59); //set seconds to 59
        float dateStop = calStop.getTimeInMillis();

        SendReceiveReaderDbHelper dbAccess = new SendReceiveReaderDbHelper(context);
        SQLiteDatabase dbSource = dbAccess.getReadableDatabase();
        // read the previous value from the database

        for (i = 0; i < Constants.DATA_PER_MONTH; i++) {
            String selectQuery = "SELECT SUM(" + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_RX + ") FROM " + SendReceiveReaderContract.FeedEntry.TABLE_NAME +
                    " WHERE " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " >= " + dateStart + " AND " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP +
                    " <= " +  dateStop + " ;";
            Cursor cursor = dbSource.rawQuery(selectQuery, null);

            cursor.moveToFirst();
            consumptionPerMonth[0][i] = cursor.getDouble(0);

            selectQuery = "SELECT SUM(" + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_MOBILE_TX + ") FROM " + SendReceiveReaderContract.FeedEntry.TABLE_NAME +
                    " WHERE " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP + " >= " + dateStart + " AND " + SendReceiveReaderContract.FeedEntry.COLUMN_NAME_TIMESTAMP +
                    " <= " +  dateStop + " ;";
            cursor = dbSource.rawQuery(selectQuery, null);
            cursor.moveToFirst();
            consumptionPerMonth[1][i] = cursor.getDouble(0);
            // increase the day of the month by one and recalculate milliseconds(0);

            calStart.add(Calendar.DAY_OF_MONTH, 1);
            dateStart = calStart.getTimeInMillis();
            calStop.add(Calendar.DAY_OF_MONTH, 1);
            dateStop = calStop.getTimeInMillis();
        }
        return consumptionPerMonth;
    }
}