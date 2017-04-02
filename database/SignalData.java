package database;

import java.util.ArrayList;

import exception.NoDataException;

public class SignalData {

    private String signalType;
    private String signalName;
    private String signalGroup;
    private double max, min;
    private String units;
    private ArrayList<Double> doubleBuff;
    private double[] data;

    public SignalData(String[] s) {
        this.signalType = s[0];
        this.signalName = s[1];
        this.signalGroup = s[2];
        this.max = Double.parseDouble(s[3]);
        this.min = Double.parseDouble(s[4]);
        this.units = s[5];
        doubleBuff = new ArrayList<Double>();
    }

    public double[] queryArray(int index, int num) throws NoDataException {
        if (this.data.length == 0) {
            throw new NoDataException();
        }
        double[] queryAns = new double[num];
        System.arraycopy(this.data, index, queryAns, 0, num);
        return queryAns;
    }

    public void postProgress() {
        int len = this.doubleBuff.size();
        this.data = new double[len];
        for (int k = 0; k < len; k++) {
            this.data[k] = this.doubleBuff.get(k);
        }
        this.doubleBuff.clear();
        this.doubleBuff = null;
    }

    public void add(double record) {
        this.doubleBuff.add(record);
    }

    public int size() {
        return this.data.length;
    }

    @Override
    public String toString() {
        return "Signal Tpye: " + this.signalType + ";\n" + "Signal Name: "
                + this.signalName + ";\n" + "Signal Group: " + this.signalGroup
                + ";\n" + "Number of data: " + this.size() + "\n"
                + "Maximum value: " + this.max + "\n" + "Minimum value "
                + this.min + "\n" + "Units: " + this.units;
    }
}
