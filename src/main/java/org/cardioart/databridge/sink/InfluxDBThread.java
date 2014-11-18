package org.cardioart.databridge.sink;

import org.cardioart.databridge.SinkConnection;
import org.cardioart.databridge.packet.PatientStreamPacket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by jirawat on 10/11/2014.
 */
public class InfluxDBThread extends Thread {
    private final SinkConnection<PatientStreamPacket> sinkConnection;
    private final Logger logger;
    private final BlockingQueue<PatientStreamPacket> blockingQueue;

    public InfluxDBThread(String host, String user, String password, String database, BlockingQueue<PatientStreamPacket> blockingQueue) {
        setName("InfluxDB");
        this.sinkConnection = new InfluxDBConnection<PatientStreamPacket>(host, user, password, database);
        this.blockingQueue = blockingQueue;
        this.logger = Logger.getLogger("databridge");
        logger.info("START " + getName());
    }

    @Override
    public void run() {
        try {
            while ( !isInterrupted() ) {
                while (sinkConnection.getStatus() == SinkConnection.CONNECTION_CLOSE) {
                    try {
                        sinkConnection.reconnect();
                    } catch (Exception exception) {
                        logger.info("RECONNECT influxdb " + exception.getMessage());
                        sleep(500);
                        continue;
                    }
                }
                PatientStreamPacket packet = blockingQueue.take();
                sinkConnection.put(packet);
            }
        } catch (Exception exception) {
            logger.info("ERROR INSIZE INFLUXDB");
        } finally {
            sinkConnection.destroy();
        }
    }

    public void cancel() {
        logger.info("END " + getName());
        interrupt();
    }
}
