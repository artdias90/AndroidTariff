package com.oneunit.test.cj2;

/**
 * Created by Elena on 19/12/2015.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.net.TrafficStats;
import android.util.Log;

public class TrafficStatsFile {

    private static final String mobileRxFile_1 = "/sys/class/net/rmnet0/statistics/rx_bytes";
    private static final String mobileRxFile_2 = "/sys/class/net/ppp0/statistics/rx_bytes";

    private static final String mobileTxFile_1 = "/sys/class/net/rmnet0/statistics/tx_bytes";
    private static final String mobileTxFile_2 = "/sys/class/net/ppp0/statistics/tx_bytes";

    private static final String wifiRxFile = "/sys/class/net/wlan0/statistics/rx_bytes";
    private static final String wifiTxFile = "/sys/class/net/wlan0/statistics/tx_bytes";

    public long getMobileRxBytes() {
        return tryBoth(mobileRxFile_1, mobileRxFile_2);
    }

    public long getMobileTxBytes() {
        return tryBoth(mobileTxFile_1, mobileTxFile_2);

    }

    public long getWifiTxBytes() {
        return readNumber(wifiTxFile);
    }

    public long getWifiRxBytes() {
        return readNumber(wifiRxFile);
    }

    // check file 1 first, and if it returns value -1 (for not found), check file 2
    private static long tryBoth(String a, String b) {
        long num = readNumber(a);
        return num >= 0 ? num : readNumber(b);
    }

    // read the contents of the file to return the value stored as a long integer
    private static long readNumber(String filename) {
        try {
            RandomAccessFile f = new RandomAccessFile(filename, "r");
            try {
                String contents = f.readLine();
                if(!contents.isEmpty() && contents!=null) {
                    try {
                        return Long.parseLong(contents);
                    }
                    catch(NumberFormatException nfex) {
                    }
                }
                else {
                }
            }
            catch (FileNotFoundException fnfex) {
            }
            catch(IOException ioex) {
            }
        }catch(FileNotFoundException ffe){
        }
        return -1;
    }

}