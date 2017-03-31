package database;

import java.util.ArrayList;

public class PropertyBase extends ArrayList<SortedList> {
	/**
	 * PropertyBase[0]: Sorted by Type
	 * PropertyBase[1]: Sorted by Group
	 * 
	 * The format of s: Type Name Group Max Min Units
	 * sortLists: query signal name using type & group
	 */
	private static final long serialVersionUID = 1L;
	public static final int TYPE = 0;
	public static final int GROUP = 1;
	
	public PropertyBase(){
		SortedList sl = new SortedList();
		this.add(sl);
		sl = new SortedList();
		this.add(sl);
	}

	public void registerInfo(String[] s){
		registerSignal(s[0], s[1], PropertyBase.TYPE); // "Type"
		registerSignal(s[2], s[1],PropertyBase.GROUP); // "Group"
	}
	
	public void registerSignal(String s1, String name, int index){
		SortedList temp = this.get(index);
		temp.registerSignal(s1, name);
	}
	public String listTypes(int index){
		return this.get(index).toString();
	}
	
	public String listNames(int index, String type){
		return this.get(index).toString(type);
	}
}
