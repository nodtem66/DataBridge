package org.cardioart.databridge.source;

/**
 * Created by jirawat on 13/11/2014.
 */
public class ChannelDecoder {
    private String client;
    private String patientId;
    private String dataType;
    private String[] unknown;

    public ChannelDecoder(String channelName) {
        boolean isAbsolutePath = false;
        if (channelName.startsWith("/")) {
            channelName = channelName.substring(1);
            isAbsolutePath = true;
        }
        String[] split = channelName.split("/");
        if (split.length >= 1 && !isAbsolutePath) {
            this.client = split[0];
        }
        if (split.length >= 2) {
            this.patientId = split[1];
        }
        if (split.length >= 3) {
            this.dataType = split[2];
        }
        if (split.length >= 4) {
            this.unknown = new String[split.length-3];
            for(int i=3; i < split.length; i++) {
                if (!split[i].isEmpty()) {
                    this.unknown[i - 3] = split[i];
                }
            }
        }
    }

    public String getClient() {
        return client;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDataType() {
        return dataType;
    }

    public String[] getUnknown() {
        return unknown;
    }
}
