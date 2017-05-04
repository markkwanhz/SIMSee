package util.database;

import java.util.ArrayList;

public class PropertyBase extends ArrayList<SortedList> {
    /**
     * PropertyBase[0]: Sorted by Type PropertyBase[1]: Sorted by Group
     * 
     * The format of s: Type Name Group Max Min Units sortLists: query signal
     * name using type & group
     */
    private static final long serialVersionUID = 1L;
    public static final String TYPE = "type";
    public static final String GROUP = "group";

    public PropertyBase() {
        SortedList sl = new SortedList();
        this.add(sl);
        sl = new SortedList();
        this.add(sl);
    }

    public void registerInfo(String[] s) {
        registerSignal(s[0], s[1], PropertyBase.TYPE); // "Type"
        registerSignal(s[2], s[1], PropertyBase.GROUP); // "Group"
    }

    public void registerSignal(String s1, String name, String key) {
        int index = key.equals(TYPE)? 0:1;
        SortedList temp = this.get(index);
        temp.registerSignal(s1, name);
    }

    public String listTypes(int index) {
        return this.get(index).toString();
    }

    public String listNames(int index, String type) {
        return this.get(index).toString(type);
    }
}
