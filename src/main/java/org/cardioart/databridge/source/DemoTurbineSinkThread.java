package org.cardioart.databridge.source;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Sink;
import org.cardioart.databridge.packet.ADCPacket;
import org.cardioart.databridge.packet.DataType;
import org.cardioart.databridge.packet.SimplePatientPacket;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by jirawat on 12/11/2014.
 */
public class DemoTurbineSinkThread extends Thread {

    private final BlockingQueue<SimplePatientPacket> blockingQueue;
    private final Logger logger;
    private final String host;
    private final String database;
    private int channelIndex;

    public DemoTurbineSinkThread(String host, String database, BlockingQueue<SimplePatientPacket> blockingQueue)
            throws SAPIException {
        setName("Dataturbine");
        this.blockingQueue = blockingQueue;
        this.host = host;
        this.database = database;

        logger = Logger.getLogger("databridge");
        logger.info("START DATATURBINE THREAD ");
    }

    @Override
    public void run() {
        try {
            int i = 0;
            long packetTimeMillis = 1000/50;
            long startTimeMillis, endTimeMillis;
            while (! isInterrupted() ) {
                startTimeMillis = System.currentTimeMillis();
                for (int j=0; j<60; j++) {
                    ADCPacket packet = new ADCPacket("1", DataType.ECG_LEAD_I);
                    packet.setValue(generateValue(i));
                    packet.setTime();
                    blockingQueue.put(packet);
                    i++;
                }
                endTimeMillis = System.currentTimeMillis() - startTimeMillis;
                if (packetTimeMillis > endTimeMillis) {
                    sleep(packetTimeMillis - endTimeMillis);
                }
            }
        } catch (Exception exception) {
            logger.info("ERROR INSIDE DATATURBINE LOOP");
        } finally {
        }
    }

    public void cancel() {
        logger.info("END DATATURBINE THREAD");
        interrupt();
    }

    private synchronized int generateValue(int i) {
        double v = 50*(Math.sin(Math.PI/1000*i) + 1);
        return (int)(v);
    }
}
