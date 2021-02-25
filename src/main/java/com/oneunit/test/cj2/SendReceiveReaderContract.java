package com.oneunit.test.cj2;

import android.provider.BaseColumns;

/**
 * Created by artdias90 on 23/05/2016.
 */
public class SendReceiveReaderContract {
    public static abstract class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "SendReceiveReader";
        public static final String COLUMN_NAME_TIMESTAMP = "cTimeStamp";
        public static final String COLUMN_NAME_MOBILE_RX = "cVolumeNetworkRX";
        public static final String COLUMN_NAME_MOBILE_TX = "cVolumeNetworkTX";
        public static final String COLUMN_NAME_PREVIOUS_MOBILE_RX= "cVolumePreviousNetworkRX";
        public static final String COLUMN_NAME_PREVIOUS_MOBILE_TX= "cVolumePreviousNetworkTX";
        public static final String TEXT_TYPE = " TEXT";
        public static final String FLOAT_TYPE = " REAL";
        public static final String TIMESTAMP_TYPE = " TİMESTAMP"; //YYYY-MM-DD-HH-mm
        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                        FeedEntry.COLUMN_NAME_TIMESTAMP + " " + FeedEntry.FLOAT_TYPE +"  PRIMARY KEY, " +
                        FeedEntry.COLUMN_NAME_MOBILE_RX + " " + FeedEntry.FLOAT_TYPE + ", " +
                        FeedEntry.COLUMN_NAME_MOBILE_TX+ " " + FeedEntry.FLOAT_TYPE + ", " +
                        FeedEntry.COLUMN_NAME_PREVIOUS_MOBILE_RX+ " " + FeedEntry.FLOAT_TYPE + ", " +
                        FeedEntry.COLUMN_NAME_PREVIOUS_MOBILE_TX+ " " + FeedEntry.FLOAT_TYPE +
                        " );";
        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    }
}
