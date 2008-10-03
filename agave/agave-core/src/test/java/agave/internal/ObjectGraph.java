package agave.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectGraph {
    
    private String cat = "";
    private List<String> names = new ArrayList<String>();
    private List<String> nickNames = new ArrayList<String>();
    private Map<String, String> moodIndicators = new HashMap<String, String>();
    private NestedObject nested = new NestedObject();
    
    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getCat() {
        return cat;
    }
    
    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }
    
    public void addToNames(String name) {
        names.add(name);
    }
    
    public void setNickNames(List<String> nickNames) {
        this.nickNames = nickNames;
    }

    public List<String> getNickNames() {
        return nickNames;
    }
    
    public void insertInNickNames(int index, String nickName) {
        nickNames.add(index, nickName);
    }
    
    public void setMoodIndicators(Map<String, String> moodIndicators) {
        this.moodIndicators = moodIndicators;
    }

    public Map<String, String> getMoodIndicators() {
        return moodIndicators;
    }
    
    public void putInMoodIndicators(String indicator, String mood) {
        moodIndicators.put(indicator, mood);
    }
    
    public void setNested(NestedObject nested) {
        this.nested = nested;
    }

    public NestedObject getNested() {
        return nested;
    }
    
}
