package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


class Cursor extends Node {
    private Rectangle cursor;

    Cursor(Node prev, Node next) {
        super(prev, next);
        cursor = new Rectangle();
    }

    /* inserts the cursor between Nodes prev and next */
    void insert(Node prev, Node next) {
        super.setPrev(prev);
        super.setNext(next);
        prev.setNext(this);
        next.setPrev(this);
    }

    /* removes cursor from text, MUST be called before insert to avoid bugs */
    void remove() {
        Node node = super.getPrev();
        node.setNext(super.getNext());
        super.getNext().setPrev(node);
    }

    /* adjusts cursor position to account for word wrap */
    void adjust() {
        TextNode text = (TextNode) getPrev();
        cursor.setX(text.getText().getX() + (int) text.getText().getLayoutBounds().getWidth());
        cursor.setY(text.getText().getY());
    }

    int getX() {
        return (int) cursor.getX();
    }

    int getY() {
        return (int) cursor.getY();
    }

    /* assigns cursor a (x, y) position and height */
    void put(int x, int y, int height) {
        cursor.setX(x);
        cursor.setY(y);
        cursor.setWidth(1);
        cursor.setHeight(height);
    }

    void display(Group root) {
        root.getChildren().remove(cursor);
        root.getChildren().add(cursor);
    }

    void setColor(Color color) {
        cursor.setFill(color);
    }

    /* starts blink animation */
    void startBlink() {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorEventHandler cursorChange = new CursorEventHandler(this);
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }
}
