package com.qr.utils;

import com.constants.Constants;
import com.constants.ErrorCorrection;
import com.constants.Modes;

public class QRCapacity {

    public static int getVersion(int dataLength, ErrorCorrection errorCorrection, Modes mode)
    {
        int[][] table = switch (mode)
        {
            case NUMERIC -> Constants.NUMERIC;
            case ALPHANUMERIC -> Constants.ALPHANUMERIC;
            case BYTE -> Constants.BYTE;
        };

        int errorIndex = errorCorrection.ordinal(); //index of the enum value

        for(int version = 0; version < table.length; version++)
        {
            if(dataLength <= table[version][errorIndex])
            {
                return version + 1;
            }
        }
        return -1;
    }
}
