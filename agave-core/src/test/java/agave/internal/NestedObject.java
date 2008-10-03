package agave.internal;

import agave.ConvertWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NestedObject {

    private String cat = "";
    private Integer age;
    private double weight;
    private String convertMe;
    private List<String> names = new ArrayList<String>();
    private List<String> nickNames = new ArrayList<String>();
    private Map<String, String> moodIndicators = new HashMap<String, String>();
    
    public void setCat(String cat) {
        this.cat = cat;
    }

    public String getCat() {
        return cat;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }
    
    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
    
    public void setConvertMe(@ConvertWith(BooyakaConverter.class) String convertMe) {
        this.convertMe = convertMe;
    }

    public String getConvertMe() {
        return convertMe;
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
    
}
