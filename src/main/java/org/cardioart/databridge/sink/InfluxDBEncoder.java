package org.cardioart.databridge.sink;

import org.cardioart.databridge.SinkEncoder;
import org.cardioart.databridge.packet.DataType;
import org.cardioart.databridge.packet.PatientStreamPacket;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Serie;

import java.util.concurrent.TimeUnit;

/**
 * Created by jirawat on 11/11/2014.
 */
public class InfluxDBEncoder<T extends PatientStreamPacket> implements SinkEncoder<T> {
    private final InfluxDB influxDB;
    private final String databaseName;

    public InfluxDBEncoder(InfluxDB influxDB, String databaseName) {
        this.influxDB = influxDB;
        this.databaseName = databaseName;
    }

    @Override
    public boolean send(T message) {
        String dataType = DataType.getString(message.getDataType());
        String serieName = String.format("test.patient-%s.%s", message.getPatientId(), dataType.toLowerCase());
        boolean result = false;
        double[] timeSecs = message.getTimeSec();
        int[] data = message.getData();
        Serie.Builder builder = new Serie.Builder(serieName);
        builder.columns("time", "value");
        for(int i=0, length = timeSecs.length; i < length; i++) {
            builder.values((long)(timeSecs[i]*1000), data[i]);
        }
        influxDB.write(databaseName, TimeUnit.MILLISECONDS, builder.build());
        return result;
    }
}
