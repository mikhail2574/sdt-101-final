package teamworks.algorithm;

import java.util.ArrayList;
import java.util.List;

public class ActionHistoryStack<T> {
    private Node<T> top;
    private int size;

    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

    public T pop() {
        if (top == null) {
            return null;
        }

        T value = top.value;
        top = top.previous;
        size--;
        return value;
    }

    public T peek() {
        return top == null ? null : top.value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public List<T> toList() {
        List<T> values = new ArrayList<>();
        Node<T> current = top;
        while (current != null) {
            values.add(current.value);
            current = current.previous;
        }
        return values;
    }

    private static final class Node<T> {
        private final T value;
        private final Node<T> previous;

        private Node(T value, Node<T> previous) {
            this.value = value;
            this.previous = previous;
        }
    }
}
