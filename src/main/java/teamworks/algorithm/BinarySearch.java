package teamworks.algorithm;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

public final class BinarySearch {
    private BinarySearch() {
    }

    public static <T, K> int findIndex(
            List<T> items,
            Function<T, K> keyExtractor,
            K target,
            Comparator<? super K> comparator
    ) {
        int left = 0;
        int right = items.size() - 1;

        while (left <= right) {
            int middle = left + (right - left) / 2;
            T item = items.get(middle);
            K currentKey = keyExtractor.apply(item);
            int comparison = comparator.compare(currentKey, target);

            if (comparison == 0) {
                return middle;
            }
            if (comparison < 0) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return -1;
    }
}
