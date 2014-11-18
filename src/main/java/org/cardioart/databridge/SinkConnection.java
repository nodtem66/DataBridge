package org.cardioart.databridge;

/**
 * Created by jirawat on 05/11/2014.
 */
public abstract class SinkConnection<T> {
    public static final int CONNECTION_OPEN = 0;
    public static final int CONNECTION_WORKING = 1;
    public static final int CONNECTION_WAITING = 2;
    public static final int CONNECTION_CLOSE = 3;

    protected SinkEncoder sinkEncoder;
    protected int status = CONNECTION_CLOSE;
    protected long byteSend;

    public abstract void connect();
    public abstract void close();

    public void initialize() {
        try {
            connect();
            status = CONNECTION_OPEN;
        } catch (Exception exception) {
            exception.getStackTrace();
            status = CONNECTION_CLOSE;
        }
    }
    public void reconnect() {
        try {
            close();
            connect();
            status = CONNECTION_OPEN;
        } catch (Exception exception) {
            exception.getStackTrace();
        }
    }
    public void destroy() {
        close();
        status = CONNECTION_CLOSE;
    }
    public boolean put(T message) {
        status = CONNECTION_WORKING;
        boolean result = sinkEncoder.send(message);
        byteSend += 1;
        return result;
    }
    public void setSinkEncoder(SinkEncoder sinkEncoder) {
        this.sinkEncoder = sinkEncoder;
    }
    public long byteSend() {
        return byteSend();
    }
    public int  getStatus() {
        return status;
    }
    public SinkEncoder getSinkEncoder() {
        return this.sinkEncoder;
    }
}
