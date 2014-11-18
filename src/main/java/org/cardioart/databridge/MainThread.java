package org.cardioart.databridge;

import com.rbnb.sapi.SAPIException;
import org.apache.commons.cli.*;
import org.cardioart.databridge.packet.PatientStreamPacket;
import org.cardioart.databridge.packet.SimplePatientPacket;
import org.cardioart.databridge.sink.InfluxDBThread;
import org.cardioart.databridge.source.DataTurbineSinkThread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by jirawat on 02/11/2014.
 */
public class MainThread {

    public static void main(String[] args) throws IOException {
        //Flag
        boolean isVerbose = false;

        //Parameters
        int corePoolsize = 1;
        int maximumPoolSize = Runtime.getRuntime().availableProcessors();
        int capacityQueue = 2048;
        String influxHost = "http://127.0.0.1:8086/";
        String influxUser = "root";
        String influxPassword = "root";
        String influxDatabase = "test1";
        String dataturbineHost = "http://127.0.0.1:3333/";
        String dataturbineChannel = "test1";
        String dataturbineUser = "";
        String dataturbinePassword = "";

        //Logger
        Logger logger = Logger.getLogger("databridge");
        LogManager.getLogManager().readConfiguration(MainThread.class.getResourceAsStream("/logging.properties"));

        //Queue
        BlockingQueue<PatientStreamPacket> blockingQueue = new LinkedBlockingDeque<PatientStreamPacket>();

        //create the command line parser
        CommandLineParser parser = new BasicParser();

        //create the options
        Options options = new Options();
        options.addOption("v", "verbose", false, "display output to stdout");
        options.addOption(OptionBuilder.withLongOpt("logfile")
                .hasArg()
                .withDescription("Set logfile to store stdout and stderr")
                .withArgName("file")
                .create("l"));
        options.addOption(OptionBuilder.withLongOpt("dataturbine")
                .hasArg()
                .withDescription("Set Dataturbine RBNB's host (ip and port)")
                .withArgName("user:pass@host:port#channel_name")
                .create("in"));
        options.addOption(OptionBuilder.withLongOpt("influxdb")
                .hasArg()
                .withDescription("Set InfluxDB interface (ip and port)")
                .withArgName("user:pass@http[s]://host:port#database_name")
                .create("out"));
        options.addOption(OptionBuilder.withLongOpt("thread")
                .hasArg()
                .withDescription("Set Maximum Thread")
                .withArgName("N")
                .create("t"));
        try {
            CommandLine commandLine = parser.parse(options, args);
            if (commandLine.hasOption("thread")) {
                corePoolsize = Integer.parseInt(commandLine.getOptionValue("thread"));
            }
            if (commandLine.hasOption("verbose")) {
                isVerbose = true;
                logger.info("maximum thread: " + corePoolsize);
                logger.info("Number of Runtime Processors: " + maximumPoolSize);
            }
            if (corePoolsize > maximumPoolSize) {
                corePoolsize = maximumPoolSize;
            }
            if (commandLine.hasOption("dataturbine")) {
                String bufferOption = commandLine.getOptionValue("dataturbine");
                String[] buffer = bufferOption.split("#");
                if (buffer.length == 2) {
                    dataturbineChannel = buffer[1];
                }
                buffer = buffer[0].split("@");
                if (buffer.length == 2) {
                    dataturbineHost = buffer[1];
                    buffer = buffer[0].split(":");
                    if (buffer.length == 2) {
                        dataturbineUser = buffer[0];
                        dataturbinePassword = buffer[1];
                    }
                } else {
                    dataturbineHost = buffer[0];
                }
                if (dataturbineUser.isEmpty() || dataturbinePassword.isEmpty()) {
                    logger.info("Dataturbine host:" + dataturbineHost +
                            " channel:" + dataturbineChannel);
                } else {
                    logger.info("Dataturbine host:" + dataturbineHost +
                            " channel:" + dataturbineChannel +
                            " user:" + dataturbineUser +
                            " password:" + dataturbinePassword);
                }
            }
            if (commandLine.hasOption("influxdb")) {
                String bufferOption = commandLine.getOptionValue("influxdb");
                String[] buffer = bufferOption.split("#");
                if (buffer.length == 2) {
                    influxDatabase = buffer[1];
                }
                buffer = buffer[0].split("@");
                if (buffer.length == 2) {
                    influxHost = buffer[1];
                    buffer = buffer[0].split(":");
                    if (buffer.length == 2) {
                        influxUser = buffer[0];
                        influxPassword = buffer[1];
                    }
                } else {
                    influxHost = buffer[0];
                }
                logger.info("InfluxDB host:" + influxHost +
                        " channel:" + influxDatabase +
                        " user:" + influxUser +
                        " password:" + influxPassword);
            }
            try {
                //Construct thread instance
                final DataTurbineSinkThread dataTurbineThread = new DataTurbineSinkThread(dataturbineHost, dataturbineChannel, blockingQueue);
                final ArrayList<InfluxDBThread> influxDBThreadPool = new ArrayList<InfluxDBThread>();
                final int threadSize = corePoolsize;
                //Start influxdb thread
                for(int i=0; i<corePoolsize; i++) {
                    influxDBThreadPool.add(new InfluxDBThread(influxHost, influxUser, influxPassword, influxDatabase, blockingQueue));
                    influxDBThreadPool.get(i).start();
                }

                //Hook JVM Shutdown
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        if (dataTurbineThread.isAlive()) {
                            dataTurbineThread.cancel();
                        }
                        for(int i=0; i< threadSize; i++) {
                            if (influxDBThreadPool.get(i).isAlive()) {
                                influxDBThreadPool.get(i).cancel();
                            }
                        }
                    }
                });

                //Start dataturbine thread
                dataTurbineThread.start();
            } catch (SAPIException ignore) {}
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("databridge", options);
        }
    }
}
