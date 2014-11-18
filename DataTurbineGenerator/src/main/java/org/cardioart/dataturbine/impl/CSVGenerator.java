package org.cardioart.dataturbine.impl;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.cardioart.dataturbine.SequenceGenerator;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * Created by jirawat on 12/11/2014.
 */
@ThreadSafe
public class CSVGenerator implements SequenceGenerator {
    private final int lengthList;
    private final int indexColumn;
    private final double samplingRate;
    private final double secondPerSample;
    private double timestamp;

    @GuardedBy("synchonize this")
    private int currentPosition = 0;

    private final ArrayList<Integer> arrayList;

    public CSVGenerator(String filename) throws IOException {
        this(filename, 0, 360, System.currentTimeMillis());
    }
    public CSVGenerator(String filename, int indexColumn) throws IOException {
        this(filename, indexColumn, 360, System.currentTimeMillis());
    }
    public CSVGenerator(String filename, int indexColumn, double samplingRate) throws IOException {
        this(filename, indexColumn, samplingRate, System.currentTimeMillis());
    }
    public CSVGenerator(String filename, int indexColumn, double samplingRate, double timestamp) throws IOException {
        final Reader reader = new FileReader(filename);
        final CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader());
        try {
            this.indexColumn = indexColumn;
            this.arrayList = new ArrayList<Integer>();
            for(final CSVRecord record: csvParser) {
                this.arrayList.add(Integer.parseInt(record.get(indexColumn)));
            }
            this.lengthList = arrayList.size();
            this.samplingRate = samplingRate;
            this.secondPerSample = 1000.0/samplingRate;
            this.timestamp = timestamp;
        } finally {
            csvParser.close();
            reader.close();
        }
    }
    @Override
    public int getData() {
        int data;
        synchronized (this) {
            data = arrayList.get(currentPosition);
            currentPosition++;
            if (currentPosition >= lengthList) {
                currentPosition = 0;
            }
        }
        return data;
    }

    @Override
    public int getLength() {
        return lengthList;
    }

    @Override
    public synchronized double getTimeMillis() {
        this.timestamp += secondPerSample;
        return timestamp;
    }

    public int getData(int position) {
        return arrayList.get(position % lengthList);
    }

    public long getIndexColumn() {
        return indexColumn;
    }
}
