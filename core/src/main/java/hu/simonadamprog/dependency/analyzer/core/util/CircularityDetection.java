package hu.simonadamprog.dependency.analyzer.core.util;

import java.util.Iterator;
import java.util.List;

public class CircularityDetection {

    // TODO: refactor this to smaller chunks.
    public static boolean isListCircularlyTheSame(List<String> existingList, List<String> marker) {
        Iterator<String> existingIterator = existingList.listIterator();
        Iterator<String> candidateIterator = marker.listIterator();
        String candidateCurrentValue;
        boolean foundFirstMatch = false;
        if (candidateIterator.hasNext()) {
            candidateCurrentValue = candidateIterator.next();
        }
        else {
            return false;
        }
        while (existingIterator.hasNext()) {
            if (candidateCurrentValue.equals(existingIterator.next())) {
                foundFirstMatch = true;
                break;
            }
        }
        if (!foundFirstMatch) {
            return false;
        }
        while (existingIterator.hasNext() && candidateIterator.hasNext()) {
            if (!candidateIterator.next().equals(existingIterator.next())) {
                return false;
            }
        }
        if (!existingIterator.hasNext()) {
            existingIterator = existingList.listIterator();
        }
        while (existingIterator.hasNext() && candidateIterator.hasNext()) {
            if (!candidateIterator.next().equals(existingIterator.next())) {
                return false;
            }
        }
        return !candidateIterator.hasNext();
    }
}
