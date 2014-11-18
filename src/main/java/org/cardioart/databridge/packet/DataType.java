package org.cardioart.databridge.packet;

/**
 * Created by jirawat on 10/11/2014.
 */
public class DataType {
    public static final int ECG_LEAD_I = 0;
    public static final int ECG_LEAD_II = 1;
    public static final int ECG_LEAD_III = 2;
    public static final int ECG_LEAD_AVR = 3;
    public static final int ECG_LEAD_AVL = 4;
    public static final int ECG_LEAD_AVF = 5;
    public static final int ECG_LEAD_V1 = 6;
    public static final int ECG_LEAD_V2 = 7;
    public static final int ECG_LEAD_V3 = 8;
    public static final int ECG_LEAD_V4 = 9;
    public static final int ECG_LEAD_V5 = 10;
    public static final int ECG_LEAD_V6 = 11;
    public static final int SPO2_PERCENT = 12;
    public static final int SPO2_VAL = 13;
    public static final int UNKNOWN = 100;

    public static final String getString(int dataType) {
        switch (dataType) {
            case ECG_LEAD_I:
                return "ECG_LEAD_I";
            case ECG_LEAD_II:
                return "ECG_LEAD_II";
            case ECG_LEAD_III:
                return "ECG_LEAD_III";
            case ECG_LEAD_AVF:
                return "ECG_LEAD_AVF";
            case ECG_LEAD_AVL:
                return "ECG_LEAD_AVL";
            case ECG_LEAD_AVR:
                return "ECG_LEAD_AVR";
            case ECG_LEAD_V1:
                return "ECG_LEAD_V1";
            case ECG_LEAD_V2:
                return "ECG_LEAD_V2";
            case ECG_LEAD_V3:
                return "ECG_LEAD_V3";
            case ECG_LEAD_V4:
                return "ECG_LEAD_V4";
            case ECG_LEAD_V5:
                return "ECG_LEAD_V5";
            case ECG_LEAD_V6:
                return "ECG_LEAD_V6";
            case SPO2_PERCENT:
                return "SPO2_PERCENT";
            case SPO2_VAL:
                return "SPO2_VAL";
            default:
        }
        return "UNKNOWN";
    }

    public static int getDataTypefromString(final String dataType) {
        if (dataType.equalsIgnoreCase("ECG_LEAD_I"))
            return ECG_LEAD_I;
        if (dataType.equalsIgnoreCase("ECG_LEAD_II"))
            return ECG_LEAD_II;
        if (dataType.equalsIgnoreCase("ECG_LEAD_III"))
            return ECG_LEAD_III;
        if (dataType.equalsIgnoreCase("ECG_LEAD_AVF"))
            return ECG_LEAD_AVF;
        if (dataType.equalsIgnoreCase("ECG_LEAD_AVL"))
            return ECG_LEAD_AVL;
        if (dataType.equalsIgnoreCase("ECG_LEAD_AVR"))
            return ECG_LEAD_AVR;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V1"))
            return ECG_LEAD_V1;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V2"))
            return ECG_LEAD_V2;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V3"))
            return ECG_LEAD_V3;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V4"))
            return ECG_LEAD_V4;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V5"))
            return ECG_LEAD_V5;
        if (dataType.equalsIgnoreCase("ECG_LEAD_V6"))
            return ECG_LEAD_V6;
        if (dataType.equalsIgnoreCase("SPO2_PERCENT"))
            return SPO2_PERCENT;
        if (dataType.equalsIgnoreCase("SPO2_VAL"))
            return SPO2_VAL;
        return UNKNOWN;
    }
}
