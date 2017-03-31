package database;

import java.util.ArrayList;

import exception.NoDataException;

public class DataSection {
	private DataBase data;
	private PropertyBase property;
	private ArrayList<String> signalName;

	public DataSection() {
		data = new DataBase();
		property = new PropertyBase();
		signalName = new ArrayList<String>();
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

	public void setResolution(double r) {
		this.data.resolution = r;
	}

	public void registerData(int signalIndex, double signal) {
		this.data.registerData(signalName.get(signalIndex), signal);
	}
	
	public double[] queryData(double timeStart, int num, String signalName) 
			throws NoDataException{
		return this.data.queryData(timeStart, num, signalName);
	}

}
