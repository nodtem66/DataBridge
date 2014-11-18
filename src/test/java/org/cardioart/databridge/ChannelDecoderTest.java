package org.cardioart.databridge;

import org.cardioart.databridge.source.ChannelDecoder;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jirawat on 13/11/2014.
 */
public class ChannelDecoderTest {
    @Test public void testOneSlash() {
        ChannelDecoder result = new ChannelDecoder("/test");
        Assert.assertNull(result.getClient());
        Assert.assertNull(result.getDataType());
        Assert.assertNull(result.getPatientId());
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testOneSlash2() {
        ChannelDecoder result = new ChannelDecoder("test/");
        Assert.assertEquals(result.getClient(), "test");
        Assert.assertNull(result.getDataType());
        Assert.assertNull(result.getPatientId());
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testOneSlash3() {
        ChannelDecoder result = new ChannelDecoder("client/id");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertNull(result.getDataType());
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testTwoSlash() {
        ChannelDecoder result = new ChannelDecoder("/test/");
        Assert.assertNull(result.getClient());
        Assert.assertNull(result.getDataType());
        Assert.assertNull(result.getPatientId());
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testTwoSlash2() {
        ChannelDecoder result = new ChannelDecoder("/server/client");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertNull(result.getDataType());
        Assert.assertNull(result.getPatientId());
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testTwoSlash3() {
        ChannelDecoder result = new ChannelDecoder("client/id/");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertNull(result.getDataType());
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testTwoSlash4() {
        ChannelDecoder result = new ChannelDecoder("client/id/ecg");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertEquals(result.getDataType(), "ecg");
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testThreeSlash() {
        ChannelDecoder result = new ChannelDecoder("/server/client/id");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertNull(result.getDataType());
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testThreeSlash2() {
        ChannelDecoder result = new ChannelDecoder("client/id/ecg/");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertEquals(result.getDataType(), "ecg");
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertNull(result.getUnknown());
    }
    @Test public void testThreeSlash3() {
        ChannelDecoder result = new ChannelDecoder("client/id/ecg/test1");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertEquals(result.getDataType(), "ecg");
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertEquals(result.getUnknown().length, 1);
        Assert.assertEquals(result.getUnknown()[0], "test1");
    }
    @Test public void testThreeSlash4() {
        ChannelDecoder result = new ChannelDecoder("client/id/ecg/test1/test2");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertEquals(result.getDataType(), "ecg");
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertEquals(result.getUnknown().length, 2);
        Assert.assertEquals(result.getUnknown()[0], "test1");
        Assert.assertEquals(result.getUnknown()[1], "test2");
    }
    @Test public void testThreeSlash5() {
        ChannelDecoder result = new ChannelDecoder("client/id/ecg/test1/test2/test3/");
        Assert.assertEquals(result.getClient(), "client");
        Assert.assertEquals(result.getDataType(), "ecg");
        Assert.assertEquals(result.getPatientId(), "id");
        Assert.assertEquals(result.getUnknown().length, 3);
        Assert.assertEquals(result.getUnknown()[0], "test1");
        Assert.assertEquals(result.getUnknown()[1], "test2");
        Assert.assertEquals(result.getUnknown()[2], "test3");
    }
}
