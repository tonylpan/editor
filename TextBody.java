package editor;

import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

class TextBody {
    private int windowWidth;
    private int textHeight;
    private final String fontName = "Lato";
    private int fontSize = 16;
    private int MARGINS = 5;

    private int height;
    private double offset;

    private Group root;
    private TextLine text;
    private ArrayList<Node> newLines;
    private Stack<Action> undo = new Stack<>();
    private Stack<Action> redo = new Stack<>();

    TextBody(Group root, int windowWidth) {
        this.root = root;
        this.windowWidth = windowWidth;
        this.textHeight = 0;
        this.text = new TextLine();
        this.newLines = new ArrayList<>();
        undo.setSize(100);
        text.cursorBlink();
    }

    void updateWidth(int newWindowWidth) {
        windowWidth = newWindowWidth;
    }

    int getTextHeight() {
        return textHeight;
    }

    /* Inserts a character at the cursor's position */
    void insert(char letter) {
        redo.clear();
        TextNode inserted = text.insert(letter);
        undo.add(new Action(inserted, "add"));
    }

    /* Removes a character, equivalent to backspace on keyboard */
    void doBackspace() {
        redo.clear();
        TextNode deleted = text.deleteLeft();
        if (deleted != null) {
            undo.add(new Action(deleted, "del"));
        }
    }

    /* Removes a character, equivalent to delete on keyboard */
    void doDelete() {
        redo.clear();
        TextNode deleted = text.deleteRight();
        if (deleted != null) {
            undo.add(new Action(deleted, "del"));
        }
    }

    /* Moves the cursor up, down, left, right depending on direction input (1, 2, 3, 4) */
    void moveCursor(int direction) {
        switch (direction) {
            case 1:
                int newY = text.cursorY() - height + (int) offset;
                if (newY >= 0 || -offset > 0) {
                    this.placeCursor(text.cursorX(), newY);
                }
                break;
            case 2:
                newY = text.cursorY() + height + (int) offset;
                if (newY < height * newLines.size()) {
                    this.placeCursor(text.cursorX(), newY);
                }
                break;
            case 3:
                text.cursorLeft();
                break;
            case 4:
                text.cursorRight();
                break;
        }
    }

    /* Places the cursor on the screen given (x, y) coordinates */
    void placeCursor(int x, int y) {
        int line = (int) (y - offset) / Math.max(height, 1);
        int pos = MARGINS;
        TextNode currNode;
        text.removeCursor();
        if (line >= newLines.size()) {
            text.insertCursor(text.sentinel().getPrev(), text.sentinel());
        } else {
            currNode = (TextNode) newLines.get(line);
            if (currNode.getChar() == ' ' || currNode.getChar() == '\n') {
                currNode = (TextNode) currNode.getNext();
                if (currNode.getText().getY() != line * height) {
                    text.insertCursor(currNode.getPrev(), currNode);
                    return;
                }
            }
            while (true) {
                int currWidth = (int) Math.round(currNode.getText().getLayoutBounds().getWidth());
                if (currNode.getText().getY() != line * height || pos + currWidth / 2.0 > x) {
                    text.insertCursor(currNode.getPrev(), currNode);
                    return;
                } else if (pos + currWidth > x) {
                    text.insertCursor(currNode, currNode.getNext());
                    return;
                }
                pos += currWidth;
                currNode = (TextNode) currNode.getNext();
            }
        }
    }

    /* Renders all text to the screen, and performs word wrap */
    void render() {
        int xPos = 0;
        int yPos = 0;
        int lineWidth = windowWidth - 2 * MARGINS;
        Node currNode = text.sentinel().getNext();
        Text template = new Text("\0");
        template.setFont(Font.font(fontName, fontSize));
        height = (int) Math.round(template.getLayoutBounds().getHeight());
        ArrayList<Text> buffer = new ArrayList<>();
        Node bufferStart = null;
        int bufferLength = 0;
        root.getChildren().clear();
        newLines.clear();

        if (currNode.equals(text.sentinel())) {
            text.putCursor(MARGINS, 0, height);
            text.renderCursor(root);
        }

        while (!(currNode.equals(text.sentinel()))) {
            if (currNode.equals(text.cursor())) {
                /* Places the cursor in the top right for an empty text body */
                text.putCursor(xPos + MARGINS, yPos, height);
                text.renderCursor(root);
            } else {
                TextNode textNode = (TextNode) currNode;
                Text str = textNode.getText();
                str.setTextOrigin(VPos.TOP);
                str.setFont(Font.font(fontName, fontSize));
                double width = str.getLayoutBounds().getWidth();
                if (textNode.getChar() == '\n') {
                    xPos = 0;
                    yPos += height;
                    str.setX(xPos + MARGINS);
                    str.setY(yPos);
                    newLines.add(textNode);
                } else if (currNode.equals(text.cursor())) {
                    text.putCursor(xPos + MARGINS, yPos, (int) str.getLayoutBounds().getHeight());
                    text.renderCursor(root);
                } else if (xPos + width < lineWidth) {
                    if (str.getText().equals(" ")) {
                        /* Clears the buffer on spaces, indicating the current
                        word has ended and a new one is beginning */
                        buffer.clear();
                        buffer.add(str);
                        if (currNode.getNext() != text.sentinel()) {
                            bufferStart = textNode.getNext();
                        }
                        bufferLength = 0;
                    } else {
                        buffer.add(str);
                        bufferLength += Math.round(width);
                    }
                    str.setX(xPos + MARGINS);
                    str.setY(yPos);
                    xPos += Math.round(width);
                    root.getChildren().add(str);
                    if (currNode == text.sentinel().getNext()) {
                        newLines.add(textNode);
                    }
                } else if (xPos + width > lineWidth && str.getText().equals(" ")) {
                    /* Clears a buffer, and tracks a new word for word wrap */
                    str.setX(xPos + MARGINS);
                    str.setY(yPos);
                    xPos += Math.round(width);
                    buffer.add(str);
                    bufferLength += Math.round(width);
                } else if (xPos + width > lineWidth) {
                    buffer.add(str);
                    bufferLength += Math.round(width);
                    if (bufferLength < lineWidth) {
                        /* Word wrap, in the event a word can fit on a new line */
                        xPos = 0;
                        yPos += Math.round(height);
                        Text start = buffer.remove(0);
                        if (start.getText().equals(" ")) {
                            start.setX(xPos + MARGINS - (int) start.getLayoutBounds().getWidth());
                        } else {
                            start.setX(xPos + MARGINS);
                            xPos += Math.round(start.getLayoutBounds().getWidth());
                        }
                        start.setY(yPos);
                        for (Text t : buffer) {
                            System.out.print(t.getText());
                            t.setX(xPos + MARGINS);
                            t.setY(yPos);
                            xPos += Math.round(t.getLayoutBounds().getWidth());
                        }
                        System.out.println();
                        root.getChildren().add(str);
                        newLines.add(bufferStart);
                    } else {
                        /* If the word is too long to be wrapped, continue on next line */
                        buffer.add(str);
                        bufferLength += Math.round(width);
                        xPos = 0;
                        yPos += Math.round(height);
                        str.setX(xPos + MARGINS);
                        str.setY(yPos);
                        root.getChildren().add(str);
                        xPos += Math.round(width);
                        newLines.add(textNode);
                    }
                }
            }
            currNode = currNode.getNext();
        }
        textHeight = height * newLines.size();
        if (text.cursor().getPrev() != text.sentinel()) {
            text.adjustCursor();
        }
    }

    /* Offsets cursor to account for shifts during word wrap */
    double applyOffset() {
        offset = root.getLayoutY();
        int cursorPos = text.cursorY() + height;
        if (-root.getLayoutY() + 500 < cursorPos) {
            offset = 500 - cursorPos;
            root.setLayoutY(offset);
        } else if (cursorPos - height < -root.getLayoutY()) {
            offset = - (cursorPos - height);
            root.setLayoutY(offset);
        }
        return offset;
    }

    /* Removes all actions from the undo stack */
    void clearUndo() {
        undo.clear();
    }

    /* Performs an undo operation, and moves the action to the redo stack */
    void doUndo() {
        if (undo.size() > 0) {
            Action action = undo.pop();
            if (action != null) {
                TextNode node = action.getAction();
                String actionType = action.getType();
                if (actionType.equals("del")) {
                    text.removeCursor();
                    text.insertCursor(node.getPrev(), node.getNext());
                    text.insert(node);
                } else if (actionType.equals("add")) {
                    text.removeCursor();
                    text.insertCursor(node, node.getNext());
                    text.deleteLeft();
                }
                redo.add(action);
            }
        }
    }

    /* Performs a redo operation, and moves the action to the undo stack */
    void doRedo() {
        if (redo.size() > 0) {
            Action action = redo.pop();
            if (action != null) {
                TextNode node = action.getAction();
                String actionType = action.getType();
                if (actionType.equals("del")) {
                    text.removeCursor();
                    text.insertCursor(node, node.getNext());
                    text.deleteLeft();
                } else if (actionType.equals("add")) {
                    text.removeCursor();
                    text.insertCursor(node.getPrev(), node.getNext());
                    text.insert(node);
                }
                undo.add(action);
            }
        }
    }

    void increaseFontSize() {
        fontSize += 4;
    }

    void decreaseFontSize() {
        if (fontSize > 4) {
            fontSize -= 4;
        }
    }

    /* Saves the text to a file fileName */
    void save(String fileName) {
        Node node = text.sentinel().getNext();
        if (fileName == null) {
            return;
        }

        try {
            FileWriter writer = new FileWriter(fileName);
            System.out.println("attempting save");
            while (!node.equals(text.sentinel())) {
                if (!node.equals(text.cursor())) {
                    writer.write(((TextNode) node).getChar());
                    System.out.println(((TextNode) node).getChar());
                }
                node = node.getNext();
            }
            writer.close();
        } catch (IOException ioException) {
            System.out.println("Error while saving. Exception: " + ioException);
        }
    }

}
