package database;

import java.util.ArrayList;

import exception.NoDataException;

public class DataSection {
    private DataBase data;
    private PropertyBase property;
    private ArrayList<String> signalName;
    private ArrayList<Double> time;

    public DataSection() {
        data = new DataBase();
        property = new PropertyBase();
        signalName = new ArrayList<String>();
        time = new ArrayList<Double>();
    }

    public void registerInfo(String[] s) {
        this.property.registerInfo(s);
        this.data.registerSignalCol(s);
        this.signalName.add(s[1]);
    }

    public String listTypes(int index) {
        return this.property.listTypes(index);
    }

    public String listNames(int index, String type) {
        return this.property.listNames(index, type);
    }

    public void registerData(int signalIndex, double signal) {
        if (signalIndex == 0) {
            this.time.add(signal);
        } else {
            this.data.registerData(signalName.get(signalIndex - 1), signal);
        }
    }

    public double[] queryData(double timeStart, int num, String signalName)
            throws NoDataException {
        return this.data.queryData(timeStart, num, signalName);
    }

    public void postProgress() {
        this.data.postProgress();
        double tr = this.time.get(1) - this.time.get(0);
        this.data.resolution = tr;
    }

}
