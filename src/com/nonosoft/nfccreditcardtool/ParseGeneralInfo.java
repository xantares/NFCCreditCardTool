package com.nonosoft.nfccreditcardtool;

import java.math.BigInteger;
import java.util.Arrays;

import android.util.Log;

public class ParseGeneralInfo
{
    String cardholdername = "";
    String pan = "";
    String expirydate = "";

    public static String toHex(byte[] bytes)
    {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public int byteValue(byte b)
    {
        int v = (int) b;
        if(v < 0) v = v + 256;
        return(v);
    }

    public static String bytesToString(byte bt[], int start, int length)
    {
        int i;
        String s = "";
        for(i = start; i < Math.min(start + length, bt.length); i++)
        {
            s = s + (char) bt[i];
        }
        return(s);
    }

    ParseGeneralInfo(byte[] data)
    {
        int i;

        // CH Name
        for(i = 0; i < data.length - 1; i++)
        {
            if((data[i] == 0x5f) && (data[i + 1] == 0x20))
            {
                int chlg = (int) data[i + 2];
                this.cardholdername = bytesToString(data, i + 3, chlg);
            }
        }

        // PAN & Expiry
        this.pan = new String();
        for(i = 0; i < data.length - 2; i++)
        {
//             if((data[i] == 0x4d || data[i] == (byte) 0x9c) && (data[i + 1] == 0x57))
            if((data[i] == 0x5a) && (data[i + 1] == 0x08))
            {
                //int panlg = (int) data[i+2];
                this.pan = toHex(Arrays.copyOfRange(data, i + 2, i + 10));
                if(false)
                {
                    this.pan = this.pan.substring(0, 4) + " **** **** " + this.pan.substring(12, 16);
                }
//                 int e = (byteValue(data[i + 13]) + (byteValue(data[i + 12]) << 8) + (byteValue(data[i + 11]) << 16)) >> 4;
//                 byte[] emonth = {(byte) (e & 255)};
//                 byte[] eyear = {(byte) ((e >> 8) & 255)};
//                 this.expirydate = toHex(emonth) + "/20" + toHex(eyear);
            }
        }
        for(i = 0; i < data.length - 3; i++)
        {
            if((data[i] == 0x5f) && (data[i + 1] == 0x24) && (data[i + 2] == 0x03))
                this.expirydate = "20"+toHex(Arrays.copyOfRange(data, i + 3, i + 4)) + "-" + toHex(Arrays.copyOfRange(data, i + 4, i + 5)) + "-" + toHex(Arrays.copyOfRange(data, i + 5, i + 6));
        }
    }
}
