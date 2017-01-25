package editor;

import javafx.scene.Group;
import javafx.scene.text.Text;

class TextLine {
    private int size;
    private TextNode sentinel;
    private Cursor cursor;

    TextLine() {
        size = 0;
        sentinel = new TextNode();
        cursor = new Cursor(this.sentinel, this.sentinel);
        sentinel.setPrev(cursor);
        sentinel.setNext(cursor);
    }

    /* inserts a Node in front of the cursor */
    void insert(Node node) {
        node.setPrev(cursor.getPrev());
        node.setNext(cursor);
        cursor.getPrev().setNext(node);
        cursor.setPrev(node);
        size++;
    }

    /* creates a TextNode, and inserts it in front of the cursor */
    TextNode insert(char letter) {
        TextNode node = new TextNode(new Text(String.valueOf(letter)), null, null);
        insert(node);
        //for undo/redo purposes
        return node;
    }

    /* deletes text to the left of the cursor, equivalent to backspace */
    TextNode deleteLeft() {
        if (size > 0 && cursor.getPrev() != sentinel) {
            TextNode deleted = (TextNode) cursor.getPrev();
            deleted.getPrev().setNext(cursor);
            cursor.setPrev(deleted.getPrev());
            size--;
            deleted.setNext(cursor.getNext());
            return deleted;
        }
        return null;
    }

    /* deletes text to the right of the cursor, equivalent to delete */
    TextNode deleteRight() {
        if (size > 0 && cursor.getNext() != sentinel) {
            TextNode deleted = (TextNode) cursor.getPrev();
            deleted.getNext().setPrev(cursor);
            cursor.setNext(deleted.getNext());
            size--;
            deleted.setPrev(cursor.getPrev());
            return deleted;
        }
        return null;
    }

    Cursor cursor() {
        return this.cursor;
    }

    /* removes cursor from text */
    void removeCursor() {
        cursor.remove();
    }

    void cursorLeft() {
        if (cursor.getPrev() != sentinel) {
            TextNode prev = (TextNode) cursor.getPrev();
            TextNode next = (TextNode) cursor.getNext();
            cursor.setPrev(prev.getPrev());
            cursor.setNext(prev);
            cursor.getPrev().setNext(cursor);
            cursor.getNext().setPrev(cursor);
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    void cursorRight() {
        if (cursor.getNext() != sentinel) {
            TextNode prev = (TextNode) cursor.getPrev();
            TextNode next = (TextNode) cursor.getNext();
            cursor.setPrev(next);
            cursor.setNext(next.getNext());
            cursor.getPrev().setNext(cursor);
            cursor.getNext().setPrev(cursor);
            prev.setNext(next);
            next.setPrev(prev);
        }
    }

    void insertCursor(Node prev, Node next) {
        cursor.insert(prev, next);
    }

    void putCursor(int x, int y, int height) {
        cursor.put(x, y, height);
    }

    void adjustCursor() {
        cursor.adjust();
    }

    void renderCursor(Group root) {
        cursor.display(root);
    }

    void cursorBlink() {
        cursor.startBlink();
    }

    int cursorX() {
        return cursor.getX();
    }

    int cursorY() {
        return cursor.getY();
    }

    TextNode sentinel() {
        return this.sentinel;
    }
}
