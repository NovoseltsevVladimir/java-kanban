package ru.yandex_practicum;

public class Node {

    private Node next = null;
    private Node previous = null;
    private Task data;

    public Node(Task data) {
        this.data = data;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Task getData() {
        return data;
    }
}
