package database;

import java.util.ArrayList;

import exception.NoDataException;

public class SignalData extends ArrayList<Double>{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String signalType;
	private String signalName;
	private String signalGroup;
	private double max,min;
	private String units;
	
	public SignalData(String[] s){
		this.signalType = s[0];
		this.signalName = s[1];
		this.signalGroup = s[2];
		this.max = Double.parseDouble(s[3]);
		this.min = Double.parseDouble(s[4]);
		this.units = s[5];		
	}
	
	public double[] queryArray(int index, int num) throws NoDataException{
		if(this.size()==0){
			throw new NoDataException();
		}
		double[] queryAns = new double[num];
		System.arraycopy(this, index, queryAns, 0, num);
		return queryAns;
	}
	
	public void postProgress(){
		this.trimToSize();
	}
	
	@Override
	public String toString() {
		return "Signal Tpye: " + this.signalType + ";\n" +
				"Signal Name: " + this.signalName + ";\n" +
				"Signal Group: " + this.signalGroup + ";\n" +
				"Number of data: " + this.size() +"\n" +
				"Maximum value: " + this.max + "\n" +
				"Minimum value " + this.min + "\n" +
				"Units: " + this.units;
	}
}
