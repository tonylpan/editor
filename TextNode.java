package editor;

import javafx.scene.text.Text;

class TextNode extends Node{

    private Text text;
    private char chr;

    TextNode(Text text, Node prev, Node next) {
        super(prev, next);
        this.text = text;
        this.chr = text.getText().charAt(0);
    }

    /* instantiates everything to null, used as sentinel node */
    TextNode() {
        super(null, null);
        this.text = new Text("\0");
        this.chr = '\0';
    }

    Text getText() {
        return this.text;
    }

    /* returns String representation of Text obj */
    char getChar() {
        return this.chr;
    }
}
