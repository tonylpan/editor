package editor;

class Node {

    private Node prev;
    private Node next;

    Node(Node prev, Node next) {
        this.prev = prev;
        this.next = next;
    }

    void setPrev(Node node) {
        this.prev = node;
    }

    void setNext(Node node) {
        this.next = node;
    }

    Node getPrev() {
        return this.prev;
    }

    Node getNext() {
        return this.next;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
