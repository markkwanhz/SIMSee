package util.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * PropertyBase[0]: Sorted by Type; PropertyBase[1]: Sorted by Group</p>
 * The format of s: Type Name Group Max Min Units </p>
 * sortLists: query signal name using type & group
 */
public class PropertyBase extends HashMap<String, SortedList> {
    private static final long serialVersionUID = 1L;
    public static final String TYPE = "type";
    public static final String GROUP = "group";

    public PropertyBase() {
        super();
    }

    public void registerInfo(String tag, String prop, String signalName) {
        SortedList target = get(tag);
        if(target==null){
            target = new SortedList();
            put(tag, target);
        }
        target.registerSignal(prop, signalName);
    }

    /**
     * list all available filter
     * @return
     */
    public String[] listTypes() {
        ArrayList<String> ans = new ArrayList<>();
        Iterator<Map.Entry<String, SortedList>> it = this.entrySet().iterator();
        Map.Entry<String, SortedList> entry;
        while(it.hasNext()){
            entry = it.next();
            entry.getValue().listType(entry.getKey(), ans);
        }
        return ans.toArray(new String[0]);
    }

    /**
     * list all data names under such filter
     * @param tag
     * @param type
     * @return
     */
    public String listNames(String tag, String type) {
        return this.get(tag).listNames(type);
    }
}
