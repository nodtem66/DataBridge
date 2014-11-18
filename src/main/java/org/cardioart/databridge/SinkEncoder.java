package org.cardioart.databridge;

/**
 * Created by jirawat on 10/11/2014.
 */
public interface SinkEncoder<T> {
    public boolean send(T message);
}
