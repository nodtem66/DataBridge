package org.cardioart.databridge.sink;

import net.jcip.annotations.ThreadSafe;
import org.cardioart.databridge.SinkConnection;
import org.cardioart.databridge.SinkEncoder;
import org.cardioart.databridge.packet.ADCPacket;
import org.cardioart.databridge.packet.PatientStreamPacket;
import org.cardioart.databridge.packet.SimplePatientPacket;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Database;
import org.influxdb.dto.Serie;

import java.util.List;

/**
 * Created by jirawat on 10/11/2014.
 */
@ThreadSafe
public class InfluxDBConnection<T extends PatientStreamPacket> extends SinkConnection<T> {
    private InfluxDB influxDB;
    private final String host;
    private final String userName;
    private final String passWord;
    private final String databaseName;

    public InfluxDBConnection(String host, String userName, String passWord, String databaseName) {
        this.host = host;
        this.userName = userName;
        this.passWord = passWord;
        this.databaseName = databaseName;
        this.status = CONNECTION_CLOSE;
    }

    @Override
    public void connect() {
        influxDB = InfluxDBFactory.connect(host, userName, passWord);
        List<Database> databaseList = influxDB.describeDatabases();
        int databaseSize = databaseList.size();
        boolean hasTableName = false;

        for (int i = 0; i < databaseList.size(); i++) {
            if (databaseList.get(i).getName().equalsIgnoreCase(databaseName)) {
                hasTableName = true;
                break;
            }
        }
        if (!hasTableName) {
            this.influxDB.createDatabase(databaseName);
        }
        synchronized (this) {
            SinkEncoder<T> sinkEncoder1 = new InfluxDBEncoder<T>(influxDB, databaseName);
            setSinkEncoder(sinkEncoder1);
        }
    }

    @Override
    public void close() {
    }

    @Override
    public synchronized boolean put(T message) {
        return sinkEncoder.send(message);
    }
}