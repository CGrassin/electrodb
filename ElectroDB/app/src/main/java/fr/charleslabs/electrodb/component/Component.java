package fr.charleslabs.electrodb.component;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import fr.charleslabs.electrodb.utils.JSONutils;

public class Component implements Serializable{
    public static final String packageValve = "VALVE";
    public static final String packageArduino = "Arduino";
    public static final String packageNucleo  = "Nucleo";
    public static final String packageRaspPi  = "Raspberry";
    public static final String packageDIP  = "DIP";
    public static final String packageTO92 = "TO-92" ;
    public static final String packageTO220= "TO-220" ;
    public static final String packageTO   = "TO" ;
    public static final String packageSOT  = "SOT";
    public static final String packageSO   = "SO" ;
    public static final String packageQFN  = "QFN";
    public static final String packageQFP  = "QFP";
    public static final String packageLGA  = "LGA";
    public static final String packageBGA  = "BGA";
    public static final String packagePGA  = "PGA";
    public static final String packagePLCC = "PLCC";
    private static final String[] packageTypes = {packageValve,packageArduino,packageNucleo,packageRaspPi,packagePLCC
                                                    ,packageDIP,packageSOT,packageQFN,packageQFP,
                                                    packageBGA,packageLGA,packageTO92,packageTO220,
                                                    packageTO,packageSO,
                                                    packagePGA};

    private String name;
    private String description;
    private String datasheet;
    private String category;
    private String housing;
    private TreeMap<String,String> pins;

    /**
     * Constructor of a component object from a JSONObject.
     * @param jsonObject An objet that *MUST* be a component from a
     * 					 DB JSON file.
     * @param index The index of the conponent, because one pinout can
     * 				be several objects. It is the rank of the desired
     * 				component in the "names" array of the JSON.
     */
    public Component(JSONObject jsonObject, int index) throws JSONException {
            this.name = jsonObject.getJSONArray("names").getString(index);
            this.description = jsonObject.getJSONArray("descriptions").getString(index);
            this.datasheet = jsonObject.getJSONArray("datasheets").getString(index);
            this.category = jsonObject.getString("category");
            this.housing = jsonObject.getString("package");
            this.pins = JSONutils.toTreeMap(jsonObject.getJSONArray("pins"));
    }

    /**
     * Extract a package type from the component housing
     * String. If several package types can be found in
     * the housing attribute of the class, it will return
     * one based on the priority defined in the private
     * static packageTypes array.
     *
     * @return A String that give the name of the package, or
     * null if nothing matches. Use the static String attributes
     * of this class that begins with "package" to compare.
     */
    public String getHousingType(){
        if (!this.hasHousing()) return "";
        for(String type:Component.packageTypes)
            if(this.getHousing().contains(type))
                return type;
        return "";
    }

    public boolean isPinoutNumeric() {
        for (Map.Entry<String, String> entry : this.getPins().entrySet())
            if (!entry.getKey().matches("[0-9]+"))
                return false;
        return true;
    }

    //---------VERIFICATIONS----------
    public boolean hasDatasheet(){
        return (!datasheet.isEmpty());
    }
    public boolean hasDescription(){
        return (!description.isEmpty());
    }
    public boolean hasHousing(){ return (!housing.isEmpty());}
    public boolean hasCategory(){return (!category.isEmpty());}
    public boolean hasPinout(){
        return (this.pins.size()>0);
    }

    //----------- GETTERS-------------
    public String getName() {return name;}
    public String getDescription() {
        return description;
    }
    public String getDatasheet() {
        return datasheet;
    }
    public String getCategory() {
        return category;
    }
    public String getHousing() {
        return housing;
    }
    public TreeMap<String,String> getPins() {return pins;}
    public TreeMap<Integer,String> getSortedPins(){
        if (!isPinoutNumeric()) return null;
        TreeMap<Integer,String> pins = new TreeMap<>();
        for(Map.Entry<String,String> entry: getPins().entrySet())
            pins.put(Integer.parseInt(entry.getKey()),entry.getValue());
        return pins;
    }
    public int getPinCount(){
        if (isPinoutNumeric()){
            int nbPins=0;
            for(Map.Entry<String,String> entry: getPins().entrySet())
                nbPins = (Integer.parseInt(entry.getKey())>nbPins)? Integer.parseInt(entry.getKey()) : nbPins;
            return nbPins;
        }else
            return getPins().size();
    }
}