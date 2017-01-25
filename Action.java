package editor;

public class Action {

    private TextNode node;
    private String type;

    Action(TextNode node, String type) {
        this.node = node;
        this.type = type;
    }

    TextNode getAction() {
        return node;
    }

    String getType() {
        return type;
    }
}
