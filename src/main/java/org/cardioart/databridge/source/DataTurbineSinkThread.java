package org.cardioart.databridge.source;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Sink;
import org.cardioart.databridge.packet.DataType;
import org.cardioart.databridge.packet.PatientStreamPacket;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * Created by jirawat on 12/11/2014.
 */
public class DataTurbineSinkThread extends Thread {

    private final BlockingQueue<PatientStreamPacket> blockingQueue;
    private final Logger logger;
    private final String host;
    private final String database;
    private int channelIndex;
    private ChannelMap channelMap;
    private final Sink sink;
    private boolean isConnected = false;

    public DataTurbineSinkThread(String host, String database, BlockingQueue<PatientStreamPacket> blockingQueue)
            throws SAPIException {
        setName("Dataturbine");
        this.blockingQueue = blockingQueue;
        this.host = host;
        this.database = database;

        this.sink = new Sink();

        logger = Logger.getLogger("databridge");
        logger.info("START DATATURBINE THREAD ");
    }

    @Override
    public void run() {
        try {
            String[] nameChannels = new String[0];
            ArrayList<ChannelDecoder> channelDecoders = new ArrayList<ChannelDecoder>();;
            int[] indexChannels = new int[0];
            int lengthChannel = 0;
            boolean isFirstTime = true;

            while (! isInterrupted() ) {
                if (!isConnected) {
                    try {
                        sink.CloseRBNBConnection();
                        sink.OpenRBNBConnection(host, database);
                        this.channelMap = new ChannelMap();
                        channelMap.Add("*/...");
                        sink.Subscribe(channelMap);
                        isConnected = true;
                        logger.info("Client: " + sink.GetClientName() + " Server:" + sink.GetServerName());
                    } catch (Exception exception) {
                        logger.info("RECONNECT DATATURBINE");
                        sink.CloseRBNBConnection();
                        sleep(500);
                        continue;
                    }
                }
                try {
                    ChannelMap cMap = sink.Fetch(1000);
                    if (isFirstTime) {
                        nameChannels = cMap.GetChannelList();
                        lengthChannel = nameChannels.length;
                        indexChannels = new int[lengthChannel];
                        for (int i = 0; i < lengthChannel; i++) {
                            if (!nameChannels[i].startsWith("_")) {
                                channelDecoders.add(new ChannelDecoder(nameChannels[i]));
                                indexChannels[i] = cMap.GetIndex(nameChannels[i]);
                                logger.info("CH: " + nameChannels[i] + " " + indexChannels[i]);
                            }
                        }
                        isFirstTime = false;
                    }
                    if (cMap.NumberOfChannels() > 0 && channelDecoders.size() > 0) {
                        lengthChannel = nameChannels.length;
                        for (int i = 0; i < lengthChannel; i++) {
                            int typeId = cMap.GetType(indexChannels[i]);
                            if (typeId == ChannelMap.TYPE_INT32) {
                                int[] data = cMap.GetDataAsInt32(indexChannels[i]);
                                double[] time = cMap.GetTimes(indexChannels[i]);
                                if (data.length > 0 && time.length > 0) {

                                    int dataType = DataType.getDataTypefromString(channelDecoders.get(i).getDataType());
                                    String patientId = channelDecoders.get(i).getPatientId();

                                    PatientStreamPacket packet = new PatientStreamPacket(patientId, dataType);
                                    packet.setTimeSec(time);
                                    packet.setData(data);

                                    blockingQueue.put(packet);
                                }
                            }
                        }
                    } else {
                        isFirstTime = true;
                    }
                } catch (Exception exception) {
                    logger.info("ERROR INSIDE DATATURBINE LOOP ");
                    logger.info(exception.getMessage());
                    exception.printStackTrace();
                }
            }
        } catch (Exception ignore) {} finally {
            sink.CloseRBNBConnection();
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
