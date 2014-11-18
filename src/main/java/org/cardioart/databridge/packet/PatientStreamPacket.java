package org.cardioart.databridge.packet;

/**
 * Created by jirawat on 13/11/2014.
 */
public class PatientStreamPacket extends SimplePatientPacket {

    private double[] timeSec;
    private int[] data;

    public PatientStreamPacket(final String patientId, final int dataType) {
        super(patientId, dataType);
    }
    public PatientStreamPacket(final String patientId, final int dataType, final double[] time, final int[] data) {
        super(patientId, dataType);
        this.timeSec = time;
        this.data = data;
    }

    public double[] getTimeSec() {
        return timeSec;
    }

    public int[] getData() {
        return data;
    }

    public void setTimeSec(double[] timeMillis) {
        this.timeSec = timeMillis;
    }

    public void setData(int[] data) {
        this.data = data;
    }
}
