package ru.yandex_practicum;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {

    private HistoryHashMap<Integer, Node> historyMap = new HistoryHashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        historyMap.linkLast(task);
    }

    @Override
    public void remove(int id) {
        historyMap.remove(id);
    }

    @Override
    public List<Task> getHistory() {

        int historySize = historyMap.size();

        ArrayList<Task> tasksList = new ArrayList<>();
        if (historySize == 0) {
            return tasksList;
        }

        Node lastNode = historyMap.getTail();

        while (lastNode != null) {

            tasksList.add(lastNode.getData());
            lastNode = lastNode.getPrevious();

        }

        return tasksList;
    }

    public class HistoryHashMap<K, V> extends HashMap<K, V> {

        private Node head;
        private Node tail;

        public void linkLast(Task task) {
            if (tail != null && tail.getData() == task) {
                return;
            }

            Node newNode = new Node(task);

            if (tail != null) {
                tail.setNext(newNode);
            }

            newNode.setPrevious(tail);
            Integer id = task.getId();
            put((K) id, (V) newNode);
        }

        @Override
        public V put(K key, V value) {
            Node valueNode = (Node) value;

            if (tail == valueNode) {
                return value;
            }

            if (head == null) {
                head = valueNode;
                tail = head;
            } else {
                tail = valueNode;
            }
            return super.put(key, value);
        }

        @Override
        public V remove(Object key) {
            if (this.containsKey(key)) {
                Node nodeForRemove = (Node) this.get(key);
                Node previous = nodeForRemove.getPrevious();
                Node next = nodeForRemove.getNext();

                if (previous != null) {
                    previous.setNext(next);
                } else {
                    head = next;
                }

                if (next != null) {
                    next.setPrevious(previous);
                } else {
                    tail = previous;
                }

                return super.remove(key);
            }

            return null;
        }

        public Node getHead() {
            return head;
        }

        public Node getTail() {
            return tail;
        }
    }
}
