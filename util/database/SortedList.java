package util.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SortedList extends HashMap<String, ArrayList<String>> {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public void registerSignal(String Type, String Signal) {
        ArrayList<String> temp = this.get(Type);
        if (temp == null) {
            temp = new ArrayList<String>();
            temp.add(Signal);
            this.put(Type, temp);
        } else {
            temp.add(Signal);
        }
    }

    @Override
    public String toString() {
        String typeList = "";
        Iterator<java.util.Map.Entry<String, ArrayList<String>>> it = this
                .entrySet().iterator();
        while (it.hasNext()) {
            typeList = typeList + " " + it.next().getKey() ;
        }
        return typeList;
    }

    public String toString(String type) {
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
}
