package database;

import java.util.HashMap;

import exception.NoDataException;

public class DataBase extends HashMap<String, SignalData>{
	/**
	 * The format of s: Type Name Group Max Min Units
	 * sortLists: query signal name using type & group
	 */
	private static final long serialVersionUID = 1L;
	private HashMap<String,SortedList> sortedLists = new HashMap<String,SortedList>();
	private double resolution;
	
	public void registerInfo(String[] s){
		registerSignal(s[0], s[1], "type");
		registerSignal(s[2], s[1],"group");
		registerSignalCol(s);
	}
	
	public void registerSignal(String s1, String name, String tag){
		SortedList temp = this.sortedLists.get(tag);
		if(temp == null){
			temp = new SortedList();
			temp.registerSignal(s1, name);
			this.sortedLists.put(tag, temp);
		}else{
			temp.registerSignal(s1, name);
		}
	}
	
	public void registerSignalCol(String[] s){
		this.put(s[1], new SignalData(s));
	}
	
	public void setResolution(double r){
		this.resolution = r;
	}
	
	public void registerData(String signalName, double signal){
		this.get(signalName).add(signal);
	}
	
	public double[] queryData(double timeStart, int num, String signalName) 
			throws NoDataException{
		int start = (int) (timeStart / resolution);
		double[] ans = this.get(signalName).queryArray(start, num);
		return ans;
	}
	
	public String listTypes(String tag){
		return this.sortedLists.get(tag).toString();
	}
	
	public String listNames(String tag, String type){
		return this.sortedLists.get(tag).toString(type);
	}
}
