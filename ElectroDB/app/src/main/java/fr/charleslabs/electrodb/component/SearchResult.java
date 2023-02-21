package fr.charleslabs.electrodb.component;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is designed to keep an component paired with a score
 * in order to be able to perform a search, and sort search results
 *
 */
class SearchResult implements Comparable<SearchResult> {
    private Component comp;
    private double similarity;

    @Override
    public int compareTo(@NonNull SearchResult entity) {
        return Double.compare(entity.similarity,similarity);
    }

    /**
     * Sets the SearchResult object values to the parameters.
     */
    void set(Component comp, double similarity){
        this.comp = comp;
        this.similarity = similarity;
    }

    /**
     * Cast a SearchResult array to a component list.
     * @param array The array we want to cast.
     * @return A component list, similar to the component
     * array that is part of the SearchResult array. May be
     * shorter if there are nulls.
     */
    static List<Component> toCompList(SearchResult[] array){
        List<Component> res = new ArrayList<>();

        //Copy non null values
        for (SearchResult anArray : array)
            if (anArray.comp != null)
                res.add(anArray.comp);
        return res;
    }

    /**
     * Sort the array in parameter in descending order of similarity score.
     * @param array The component array we want to sort.
     */
    static void sortResults(SearchResult[] array){
        Arrays.sort(array);
    }

    /**
     * @return the similarity
     */
    double getSimilarity() {
        return similarity;
    }
}