package org.cardioart.databridge.packet;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.junit.*;

/**
 * Created by jirawat on 05/11/2014.
 * Test ADCPacket
 */
public class ADCPacketTest {

    @Test public void testInitValue() {
        ADCPacket packet = new ADCPacket(100);
        assertEquals(packet.getValue(), 100);

        long time = System.currentTimeMillis();
        packet = new ADCPacket(time, 105);
        assertEquals(packet.getValue(), 105);
        assertEquals(packet.getTime(), time);
    }

    @Test public void testInitTime() {
        ADCPacket packetOne = new ADCPacket(100);
        //Delay for milliseconds
        for (int i=0; i<100000; i++);
        ADCPacket packetTwo = new ADCPacket(101);
        assertTrue(packetOne.getTime() < packetTwo.getTime());
    }
}
