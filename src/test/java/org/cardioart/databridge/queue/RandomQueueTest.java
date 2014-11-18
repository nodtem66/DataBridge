package org.cardioart.databridge.queue;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.cardioart.databridge.packet.ADCPacket;
import org.junit.*;

/**
 * Created by jirawat on 05/11/2014.
 */
public class RandomQueueTest {
    RandomQueue<ADCPacket> randomQueue = new RandomQueue<ADCPacket>(ADCPacket.class);

    @Test public void testInsert() {
        ADCPacket packet = new ADCPacket();
        boolean result = false;
        try {
            randomQueue.put(packet);
            result = true;
        } catch (Exception ignore) {}
        assertTrue(result);
    }

    @Test public void testPop() {
        ADCPacket packet;
        boolean result = false;
        try {
            packet = randomQueue.take();
            result = true;
        } catch (Exception ignore) {}
        assertTrue(result);
    }
    @Test public void testSequencePop() {
        ADCPacket packet;
        long timeOne = 1, timeTwo = 0;
        boolean resultOne = false, resultTwo = false;
        try {
            packet = randomQueue.take();
            timeOne = packet.getTime();
            resultOne = true;

            //Delay for some milliseconds
            for(int i=0; i<50000; i++);

            packet = randomQueue.take();
            timeTwo = packet.getTime();
            resultTwo = true;
        } catch (Exception ignore) {}

        assertTrue(resultOne);
        assertTrue(resultTwo);
        assertTrue(timeOne <= timeTwo);
    }
}
