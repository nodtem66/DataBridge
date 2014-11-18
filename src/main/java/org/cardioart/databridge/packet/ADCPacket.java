package org.cardioart.databridge.packet;

/**
 * Created by jirawat on 14/11/2014.
 */
public class ADCPacket extends SimplePatientPacket {
    private long time;
    private int value;

    public ADCPacket() {this(0);}
    public ADCPacket(int value) {this(System.currentTimeMillis(), value);}
    public ADCPacket(long time, int value) {this("", DataType.UNKNOWN); this.value = value; this.time = time;}
    public ADCPacket(String patientId, int dataType) {
        super(patientId, dataType);
    }

    public long getTime() {
        return time;
    }

    public void setTime() {
        this.time = System.currentTimeMillis();
    }
    public void setTime(long time) {
        this.time = time;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
