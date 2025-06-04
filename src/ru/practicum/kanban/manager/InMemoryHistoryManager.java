package ru.practicum.kanban.manager;

import ru.practicum.kanban.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class InMemoryHistoryManager<T extends Task> implements HistoryManager {

    private Map <Integer, Node> historyMap = new HistoryHashMap<>();

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        HistoryHashMap history = (HistoryHashMap) historyMap;
        history.linkLast(task);
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

        HistoryHashMap history = (HistoryHashMap) historyMap;
        Node lastNode = history.getTail();

        while (lastNode != null) {

            tasksList.add(lastNode.data);
            lastNode = lastNode.previous;

        }

        return tasksList;
    }

    public class Node {

        public Node next = null;
        public Node previous = null;
        public Task data;

        public Node(Task data) {
            this.data = data;
        }


    }

    public class HistoryHashMap<K, V extends Node> extends HashMap<K, V> {

        private Node head;
        private Node tail;

        public void linkLast(Task task) {
            if (tail != null && tail.data == task) {
                return;
            }

            Integer id = task.getId();
            if (this.containsKey(id)) {
                this.remove(id);
            }

            Node newNode = new Node(task);

            if (tail != null) {
                tail.next = newNode;
            }

            newNode.previous = tail;
            put((K) id, (V) newNode);
        }

        @Override
        public V put(K key, V value) {

            if (tail == value) {
                return value;
            }

            if (head == null) {
                head = value;
                tail = head;
            } else {
                tail = value;
            }
            return super.put(key, value);
        }

        @Override
        public V remove(Object key) {
            if (this.containsKey(key)) {
                Node nodeForRemove = (Node) this.get(key);
                Node previous = nodeForRemove.previous;
                Node next = nodeForRemove.next;

                if (previous != null) {
                    previous.next = next;
                } else {
                    head = next;
                }

                if (next != null) {
                    next.previous = previous;
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


