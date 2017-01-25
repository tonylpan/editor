package editor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.paint.Color;

public class CursorEventHandler implements EventHandler<ActionEvent> {
    private Cursor cursor;
    private int currentColorIndex = 0;
    private Color[] colors = {Color.WHITE, Color.BLACK};

    CursorEventHandler(Cursor cursor) {
        this.cursor = cursor;
        blink();
    }

    private void blink() {
        cursor.setColor(colors[currentColorIndex]);
        currentColorIndex = (currentColorIndex + 1) % colors.length;
    }

    @Override
    public void handle(ActionEvent event) {
        blink();
    }
}
