package org.cardioart.databridge;
import static org.junit.Assert.*;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Database;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Serie;
import org.junit.*;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by jirawat on 05/11/2014.
 */
public class InfuxDBTest {
    private InfluxDB influxDB;
    private static final String host = "http://192.168.59.103:8086";
    private static final String userName = "root";
    private static final String passWord = "root";

    @Test public void testConnection() {
        boolean result = false;
        try {
            influxDB = InfluxDBFactory.connect(host, userName, passWord);
            List<Database> databaseList = influxDB.describeDatabases();
            int databaseSize = databaseList.size();
            boolean hasTableName = false;
            for(int i=0; i<databaseList.size(); i++) {
                if (databaseList.get(i).getName().equalsIgnoreCase("test1")) {
                    hasTableName = true;
                    break;
                }
            }
            if (!hasTableName) {
                this.influxDB.createDatabase("test1");
            }
            result = true;
        } catch (Exception ignore) {}

        assertTrue(result);
    }
    @Test public void testSeries() {
        boolean result = false;
        try {
            influxDB = InfluxDBFactory.connect(host, userName, passWord);
            Serie serie = new Serie.Builder("serie.test.1")
                    .columns("time", "sequence_number","value")
                    .values(System.currentTimeMillis(),1, 100)
                    .values(System.currentTimeMillis(),2, 200)
                    .build();
            influxDB.write("test1", TimeUnit.MILLISECONDS, serie);
            result = true;
        } catch (Exception ignore) {}
        assertTrue(result);
    }
}
