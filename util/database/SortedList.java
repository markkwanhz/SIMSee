package util.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SortedList extends HashMap<String, ArrayList<String>> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void registerSignal(String prop, String Signal) {
        ArrayList<String> temp = this.get(prop);
        if (temp == null) {
            temp = new ArrayList<String>();
            temp.add(Signal);
            this.put(prop, temp);
        } else {
            temp.add(Signal);
        }
    }

    public String listNames(String type) {
        String nameList = "";
        ArrayList<String> signal = this.get(type);
        if (signal != null) {
            nameList = signal.get(0);
            for (int k = 1; k < signal.size(); k++) {
                nameList = nameList + " " + signal.get(k);
            }
        }
        return nameList;
    }
    
    public void listType(String tag, ArrayList<String> target){
        Iterator<java.util.Map.Entry<String, ArrayList<String>>> it = this
                .entrySet().iterator();
        String prop;
        while(it.hasNext()){
            prop = it.next().getKey();
            target.add(tag + ':' +prop);
        }
    }
}
