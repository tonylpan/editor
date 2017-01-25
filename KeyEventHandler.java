package editor;

import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class KeyEventHandler implements EventHandler<KeyEvent> {
    private TextBody text;
    private ScrollBar scrollBar;

    KeyEventHandler(TextBody text, ScrollBar scrollBar) {
        this.text = text;
        this.scrollBar = scrollBar;
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        KeyCode code = keyEvent.getCode();
        String charTyped = keyEvent.getCharacter();
        System.out.println((int) charTyped.charAt(0));
        if (keyEvent.isShortcutDown()) {
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                if (code == KeyCode.EQUALS) {
                    text.increaseFontSize();
                    keyEvent.consume();
                } else if (code == KeyCode.MINUS) {
                    text.decreaseFontSize();
                    keyEvent.consume();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 26) {
                    text.doUndo();
                } else if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 25) {
                    text.doRedo();
                } else if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 19) {
                    text.save(Editor.fileName);
                }
            }
        } else {
            if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                if (code == KeyCode.UP) {
                    text.moveCursor(1);
                } else if (code == KeyCode.DOWN) {
                    text.moveCursor(2);
                } else if (code == KeyCode.LEFT) {
                    text.moveCursor(3);
                } else if (code == KeyCode.RIGHT) {
                    text.moveCursor(4);
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_TYPED) {
                if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 8) {
                    text.doBackspace();
                } else if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 127) {
                    text.doDelete();
                } else if (charTyped.length() > 0 && (int) charTyped.charAt(0) == 13) {
                    text.insert('\n');
                } else {
                    text.insert(charTyped.charAt(0));
                }
            }
        }
        keyEvent.consume();
        text.render();
        scrollBar.setMax(text.getTextHeight());
        scrollBar.setValue(500 - text.applyOffset());
    }
}
