package org.cardioart.dataturbine;

import com.rbnb.sapi.SAPIException;
import org.apache.commons.cli.*;
import org.cardioart.dataturbine.impl.CSVGenerator;

import java.io.IOException;
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
        int indexColumn = 0;
        double samplingRate = 360;
        double refreshRate = 60;
        String dataturbineHost = "http://127.0.0.1:3333/";
        String dataturbineChannel = "Android-123";
        String dataturbineUser = "";
        String dataturbinePassword = "";

        //Logger
        Logger logger = Logger.getLogger("databridge");
        LogManager.getLogManager().readConfiguration(MainThread.class.getResourceAsStream("/logging.properties"));

        //create the command line parser
        CommandLineParser parser = new BasicParser();

        //create the options
        Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("samplingrate")
                .hasArg()
                .withDescription("Sampling Rate each record in CSV file (unit:Hz)")
                .withArgName("HZ")
                .create("s"));
        options.addOption(OptionBuilder.withLongOpt("column")
                .hasArg()
                .withDescription("Selected Column in CSV file (start from 0)")
                .withArgName("N")
                .create("c"));
        options.addOption(OptionBuilder.withLongOpt("dataturbine")
                .hasArg()
                .withDescription("Set Dataturbine RBNB's host (ip and port)")
                .withArgName("host:port")
                .create("h"));
        options.addOption(OptionBuilder.withLongOpt("refreshrate")
                .hasArg()
                .withDescription("Set Refresh Rate (60Hz for human eye)")
                .withArgName("HZ")
                .create("r"));
        try {
            CommandLine commandLine = parser.parse(options, args);
            String[] commandLineArgs = commandLine.getArgs();

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

            if (commandLine.hasOption("column")) {
                indexColumn = Integer.parseInt(commandLine.getOptionValue("column"));
                logger.info("Use only " + indexColumn + " Column");
            }

            if (commandLine.hasOption("samplingrate")) {
                samplingRate = Integer.parseInt(commandLine.getOptionValue("samplingrate"));
                logger.info("Sampling Rate: " + samplingRate + " Hz");
            }
            if (commandLine.hasOption("refreshrate")) {
                refreshRate = Integer.parseInt(commandLine.getOptionValue("refreshrate"));
                logger.info("Refresh Rate: " + refreshRate + " Hz");
            }

            if (commandLineArgs.length == 0) {
                logger.info("NO File Input");
                System.exit(-1);
            }

            //Load data from input file
            String filename = commandLineArgs[0];
            SequenceGenerator csvgenerator;
            try {
                csvgenerator = new CSVGenerator(filename, indexColumn, samplingRate);
                logger.info("Load " + csvgenerator.getLength() + " samples");
                //Init DataTurbine Thread
                final DataTurbineSourceThread dataTurbineThread = new DataTurbineSourceThread(dataturbineHost, dataturbineChannel, csvgenerator, samplingRate, refreshRate);

                //Hook JVM Shutdown
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        dataTurbineThread.cancel();
                    }
                });

                //Start dataturbine thread
                dataTurbineThread.start();
            } catch (IOException ignore) {
                logger.info("Not found file: " + System.getProperty("user.dir") + "\\" + filename);
                System.exit(-1);
            } catch (SAPIException e) {
                logger.info(e.getMessage());
                System.exit(-1);
            }
        } catch (ParseException exp) {
            System.out.println(exp.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("dataturbinegen", options);
        }
    }
}
