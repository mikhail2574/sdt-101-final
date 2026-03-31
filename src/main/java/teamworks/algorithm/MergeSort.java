package teamworks.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class MergeSort {
    private MergeSort() {
    }

    public static <T> List<T> sort(List<T> items, Comparator<? super T> comparator) {
        if (items == null) {
            return new ArrayList<>();
        }
        if (items.size() <= 1) {
            return new ArrayList<>(items);
        }

        int middle = items.size() / 2;
        List<T> left = sort(new ArrayList<>(items.subList(0, middle)), comparator);
        List<T> right = sort(new ArrayList<>(items.subList(middle, items.size())), comparator);

        return merge(left, right, comparator);
    }

    private static <T> List<T> merge(List<T> left, List<T> right, Comparator<? super T> comparator) {
        List<T> merged = new ArrayList<>(left.size() + right.size());
        int leftIndex = 0;
        int rightIndex = 0;

        while (leftIndex < left.size() && rightIndex < right.size()) {
            T leftValue = left.get(leftIndex);
            T rightValue = right.get(rightIndex);

            if (comparator.compare(leftValue, rightValue) <= 0) {
                merged.add(leftValue);
                leftIndex++;
            } else {
                merged.add(rightValue);
                rightIndex++;
            }
        }

        while (leftIndex < left.size()) {
            merged.add(left.get(leftIndex));
            leftIndex++;
        }

        while (rightIndex < right.size()) {
            merged.add(right.get(rightIndex));
            rightIndex++;
        }

        return merged;
    }
}
