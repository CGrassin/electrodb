package fr.charleslabs.electrodb.component;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import fr.charleslabs.electrodb.utils.JSONutils;

public class Component implements Serializable{
    // General
    public static final String packageValve = "Valve";
    public static final String packageDisplay = "Display";
    public static final String packageOscillator = "Oscillator";

    // Modules
    public static final String packageArduino = "Arduino";
    public static final String packageNucleo  = "Nucleo";
    public static final String packageRaspPi  = "Raspberry";
    public static final String packageESP  = "ESP8266";

    // DIP
    public static final String packageDIP ="DIP";
    public static final String packageSIP ="SIP";

    // QFN/QFP
    public static final String packageCSP  = "CSP";
    public static final String packagePLCC  = "PLCC";
    public static final String packageMLPQ ="MLPQ";
    public static final String packageLLP ="LLP";
    public static final String packageLLC ="LLC";
    public static final String packageSON ="SON";
    public static final String packageQFN  = "QFN";
    public static final String packageDFN  = "DFN";
    public static final String packageQFP  = "QFP";
    public static final String packageMLF  = "MLF";

    // Diodes
    public static final String packageMELF  = "MELF" ;
    public static final String packageSMA  = "SMA" ;
    public static final String packageSMB  = "SMB" ;
    public static final String packageSMC  = "SMC" ;
    public static final String packageSOD  = "SOD" ;

    // SOT
    public static final String packageSOT323 = "SOT-323";
    public static final String packageSOT416 = "SOT-416";
    public static final String packageSOT353 = "SOT-353";
    public static final String packageSOT23 = "SOT-23";
    public static final String packageSOT223 = "SOT-223";
    public static final String packageSOT89 = "SOT-89";
    public static final String packageSOT = "SOT";

    // TO
    public static final String packageTO92  = "TO-92" ;
    public static final String packageTO220 = "TO-220" ;
    public static final String packageTO252 = "TO-252" ;
    public static final String packageTO263 = "TO-263" ;
    public static final String packageTO    = "TO" ;

    // BGA/LGA
    public static final String packageLGA  = "LGA";
    public static final String packageBGA  = "BGA";
    public static final String packagePGA  = "PGA";

    // SO (SOIC/SSOP/...)
    public static final String packageSO   = "SO" ;

    private static final String[] packageTypes = {packageValve,
            packageDisplay,
            packageOscillator,
            packageArduino,
            packageNucleo,
            packageRaspPi,
            packageESP,
            packageDIP,
            packageSIP,
            packageCSP,
            packagePLCC,
            packageMLPQ,
            packageLLP,
            packageLLC,
            packageSON,
            packageQFN,
            packageDFN,
            packageQFP,
            packageMLF,
            packageMELF,
            packageSMA,
            packageSMB,
            packageSMC,
            packageSOD,
            packageSOT23,
            packageSOT353,
            packageSOT416,
            packageSOT323,
            packageSOT223,
            packageSOT89,
            packageSOT,
            packageTO92,
            packageTO220,
            packageTO252,
            packageTO263,
            packageTO,
            packageLGA,
            packageBGA,
            packagePGA,
            packageSO};

    private final String name;
    private final String description;
    private final String datasheet;
    private final String category;
    private final String housing;
    private final TreeMap<String,String> pins;

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