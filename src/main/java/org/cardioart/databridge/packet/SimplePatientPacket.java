package org.cardioart.databridge.packet;

/**
 * Created by jirawat on 10/11/2014.
 */
public abstract class SimplePatientPacket {
    protected String patientId;
    protected int dataType;

    public SimplePatientPacket(String patientId) {
        this.patientId = patientId;
    }

    public SimplePatientPacket(String patientId, int dataType) {
        this.patientId = patientId;
        this.dataType = dataType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    public void setDataType(int value) {
        this.dataType = value;
    }
    public int getDataType() {
        return dataType;
    }
}
