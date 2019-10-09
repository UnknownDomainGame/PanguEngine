package nullengine.client.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.util.Utils;

public class FlowPane extends Pane {

    public enum Direction{
        HORIZONTAL, VERTICAL
    }

    private final MutableValue<Pos> anchor = new SimpleMutableObjectValue<>(Pos.TOP_LEFT);
    private final MutableValue<Direction> direction = new SimpleMutableObjectValue<>(Direction.HORIZONTAL);
    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    private MutableFloatValue paneWidth = new SimpleMutableFloatValue(FOLLOW_PARENT);
    private MutableFloatValue paneHeight = new SimpleMutableFloatValue(FOLLOW_PARENT);

    public FlowPane(){
        anchor.addChangeListener((observable, oldValue, newValue) -> this.needsLayout());
        direction.addChangeListener((observable, oldValue, newValue) -> this.needsLayout());
    }

    public MutableValue<Pos> anchor() {
        return anchor;
    }

    public MutableValue<Direction> direction() {
        return direction;
    }

    public MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    public float prefWidth() {
        return paneWidth.get() == FOLLOW_PARENT ? (parent().isPresent() ? parent().getValue().width().get() : 0) : paneWidth.get();
    }
    @Override
    public float prefHeight() {
        return paneHeight.get() == FOLLOW_PARENT ? (parent().isPresent() ? parent().getValue().height().get() : 0) : paneHeight.get();
    }

    public MutableFloatValue paneWidth() {
        return paneWidth;
    }

    public MutableFloatValue paneHeight() {
        return paneHeight;
    }


    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float spacing = spacing().get();
        float x = anchor.getValue().getHpos() == Pos.HPos.RIGHT ? width().get() - padding.getRight() : padding.getLeft();
        float y = anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? height().get() - padding.getBottom() :padding.getTop();
        float lineW = 0;
        float lineH = 0;
        for (var child : getChildren()) {
            float pw = Utils.prefWidth(child);
            float ph = Utils.prefHeight(child);
            float x1 = anchor.getValue().getHpos() == Pos.HPos.RIGHT ? x - pw : x;
            float y1 = anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? y - ph : y;
            if(direction.getValue() == Direction.HORIZONTAL){
                lineH = Math.max(lineH, ph);
                if(x1 < 0 || x1 + pw > width().get() - padding.getRight()){
                    y += lineH + spacing;
                    x = anchor.getValue().getHpos() == Pos.HPos.RIGHT ? width().get() - padding.getRight() : padding.getLeft();
                    x1 = anchor.getValue().getHpos() == Pos.HPos.RIGHT ? x - pw : x;
                    y1 = anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? y - ph : y;
                    lineH = ph;
                }
            }else{
                lineW = Math.max(lineW, pw);
                if(y1 < 0 || y1 + ph > height().get() - padding.getBottom()){
                    x += lineW + spacing;
                    y = anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? height().get() - padding.getBottom() : padding.getTop();
                    x1 = anchor.getValue().getHpos() == Pos.HPos.RIGHT ? x - pw : x;
                    y1 = anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? y - ph : y;
                    lineW = pw;
                }
            }
            layoutInArea(child, x1, y1, pw, ph);
            if(direction.getValue() == Direction.HORIZONTAL) {
                x = x1 + (anchor.getValue().getHpos() == Pos.HPos.RIGHT ? -spacing : spacing + pw);
            }
            else{
                y = y1 + (anchor.getValue().getVpos() == Pos.VPos.BOTTOM ? -spacing : spacing + ph);
            }
        }
    }
}
