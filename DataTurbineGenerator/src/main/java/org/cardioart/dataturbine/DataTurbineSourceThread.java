package org.cardioart.dataturbine;

import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Source;

import java.util.logging.Logger;

/**
 * Created by jirawat on 13/11/2014.
 */
public class DataTurbineSourceThread extends Thread {

    private final SequenceGenerator generator;
    private final String host;
    private final String channel;
    private final Source source;
    private final ChannelMap sMap;
    private final int channelIndex;
    private final int dataPerPacket;
    private final long packetTimeMillis;
    private final Logger logger;

    public DataTurbineSourceThread(String host,
                                   String channel,
                                   SequenceGenerator sequenceGenerator,
                                   double samplingRate,
                                   double refreshRate) throws SAPIException {
        setName("Dataturbine-thread-1");
        this.generator = sequenceGenerator;
        this.host = host;
        this.channel = channel;
        this.dataPerPacket = (int)(samplingRate / refreshRate);
        this.packetTimeMillis = (long)(1000 / refreshRate);

        this.source = new Source(4096, "none", 4096);
        source.CloseRBNBConnection();
        source.OpenRBNBConnection(host, channel);
        sMap = new ChannelMap();
        sMap.Add("112481230A/ECG_LEAD_I");
        channelIndex = sMap.GetIndex("112481230A/ECG_LEAD_I");
        sMap.PutUserInfo(channelIndex, "units=n, property=value");
        sMap.PutMime(channelIndex, "application/octet-stream");
        source.Register(sMap);

        logger = Logger.getLogger("databridge");
        logger.info("START DATATURBINE THREAD ");

    }
    public DataTurbineSourceThread(String host, String channel, SequenceGenerator sequenceGenerator) throws SAPIException {
        this(host, channel, sequenceGenerator, 360, 60);
    }
    @Override
    public void run() {
        long startTimeMillis, endTimeMillis;
        try {
            while (!isInterrupted()) {
                startTimeMillis = System.currentTimeMillis();
                double[] times = new double[dataPerPacket];
                int[] data = new int[dataPerPacket];
                for(int i=0; i< dataPerPacket; i++) {
                    times[i] = generator.getTimeMillis() / 1000;
                    data[i] = generator.getData();
                }
                sMap.PutTimes(times);
                sMap.PutDataAsInt32(channelIndex, data);
                synchronized (this) {
                    source.Flush(sMap);
                }
                endTimeMillis = System.currentTimeMillis() - startTimeMillis;
                if (endTimeMillis < packetTimeMillis) {
                    sleep(packetTimeMillis - endTimeMillis);
                }
            }
        } catch (SAPIException ignore) {} catch (InterruptedException ignore) {}
        finally {
            source.CloseRBNBConnection();
        }
    }
    public void cancel() {
        logger.info("STOP DATATURBINE");
        interrupt();
    }
}
