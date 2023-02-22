package fr.charleslabs.electrodb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.xdrop.diffutils.DiffUtils;
import me.xdrop.diffutils.structs.MatchingBlock;

//Thanks to acdcjunior on https://stackoverflow.com/questions/955110/similarity-string-comparison-in-java
public class StringSearch {
    /**
     * Calculates the similarity (a number within 0 and 100) between two strings.
     * From: https://github.com/xdrop/fuzzywuzzy
     */
    public static int partialRatio(String s1, String s2) {
        String shorter;
        String longer;

        if (s1.length() <= s2.length()) {
            shorter = s1;
            longer = s2;
        } else {
            shorter = s2;
            longer = s1;
        }

        MatchingBlock[] matchingBlocks = DiffUtils.getMatchingBlocks(shorter, longer);

        List<Double> scores = new ArrayList<>();

        for (MatchingBlock mb : matchingBlocks) {
            int dist = mb.dpos - mb.spos;

            int long_start = Math.max(dist, 0);
            int long_end = long_start + shorter.length();

            if (long_end > longer.length()) long_end = longer.length();

            String long_substr = longer.substring(long_start, long_end);

            double ratio = DiffUtils.getRatio(shorter, long_substr);

            if (ratio > .995) {
                return 100;
            } else {
                scores.add(ratio);
            }
        }

        return (int) Math.round(100 * Collections.max(scores));
    }
}