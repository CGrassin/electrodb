package fr.charleslabs.electrodb.component;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import fr.charleslabs.electrodb.utils.StringSearch;

import static fr.charleslabs.electrodb.utils.JSONutils.fileToString;

/**
 * This class is a component database that loads and interact with
 * a JSON file.
 * Warning : this class is a singleton!
 * @author Charles Grassin
 *
 */
public class ComponentsDB {
    //Class variables:
    private JSONArray jsonComponents;

    //Singleton
    private static ComponentsDB instance;
    public static ComponentsDB getInstance(Context c){
        if(instance == null){
            instance = new ComponentsDB(c.getApplicationContext());
        }
        return instance;
    }
    private ComponentsDB(Context c){
        //String fileContent = c.getResources().openRawResource(android.R.raw.db);
        try {
            String fileContent =  fileToString(c.getResources().openRawResource(
                    c.getResources().getIdentifier("db","raw",c.getPackageName())));
            jsonComponents = (new JSONObject(fileContent)).getJSONArray("components");
        } catch (IOException | JSONException e) {
            // @TODO : REACT TO THIS
            e.printStackTrace();
        }
    }

    /**
     * Perform a search by name in the DB.
     * @param searchName The name to search in the DB.
     * @param nbResults The number of expected results. The higher,
     * 					the more time the search may take.
     * @return A ComponentActivity array, sorted by relevance (using a word
     * 			similarity based algorithm). If there aren't enough
     * 			matching results, the array may be shorter than
     * 			expected.
     */
    public List<Component> searchComponentByName(String searchName, final int nbResults) throws JSONException {
        //Search algorithm variables:
        SearchResult[] results = new SearchResult[nbResults];
        for(int i=0;i<results.length;i++)
            results[i] = new SearchResult();
        double worstScore = Double.MIN_VALUE; double currentScore; int worstScoreIndex = 0;
        JSONArray jsonNames;

        //Loop for each component of the JSON DB
        for(int i=0;i<jsonComponents.length();i++){
            jsonNames = jsonComponents.getJSONObject(i).getJSONArray("names");

            //Loop for each name (i.e. each actual component)
            for(int j=0;j<jsonNames.length();j++) {
                currentScore = StringSearch.partialRatio(searchName,jsonNames.getString(j).toUpperCase());

                //Check if similarity is worse than the worst yet found
                //if it is, add it instead of the worst.
                if (currentScore > worstScore) {
                    worstScore=Double.MAX_VALUE;
                    results[worstScoreIndex].set(new Component(jsonComponents.getJSONObject(i),j), currentScore);

                    //Loop through the results to find the worst one (next to be replaced)
                    for(int k=0;k<nbResults;k++){
                        if(results[k].getSimilarity() < worstScore){
                            worstScore = results[k].getSimilarity();
                            worstScoreIndex = k;
                        }
                    }
                }
            }
        }
        // At the end, sort by similarity
        SearchResult.sortResults(results);

        return SearchResult.toCompList(results);
    }

    /**
     * Counts the components in the DB.
     * @return the number of distinct component in the database.
     */
    public int getComponentCount() throws JSONException {
        int count = 0;
        for(int i=0;i<jsonComponents.length();i++){
            count += jsonComponents.getJSONObject(i).getJSONArray("names").length();
        }
        return count;
    }
    // @ TODO Search by cat (single implementation of search with several arg combinations)?
    // @ TODO Search in description?
}
