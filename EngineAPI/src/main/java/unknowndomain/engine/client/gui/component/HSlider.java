package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.Region;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.event.Event;


public class HSlider extends Region {
    private Image slider = new Image();

    private ImageButton back = new ImageButton();

    private MutableDoubleValue value = new SimpleMutableDoubleValue(0.5);

    private double preMove = 0;

    private boolean select = false;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        backBg().addChangeListener((ob, o, n) -> reSize());
        sliderBg().addChangeListener((ob, o, n) -> reSize());
        this.getChildren().addAll(back, slider);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public void setPreMove(float preMove) {
        this.preMove = preMove;
    }

    public void rebuild() {
        if (value.get() > 1) {
            value.set(1);
        } else if (value.get() < 0) {
            value.set(0);
        }
        slider.x().set((float) ((back.prefWidth()) * value.get() - slider.prefWidth() / 2));
        slider.y().set(back.y().get() + back.prefHeight() / 2 - slider.prefHeight() / 2);
    }

    public void reSize() {
        slider.x().set(back.x().get() + back.prefWidth() / 2 - slider.prefWidth() / 2);
        slider.y().set(back.y().get() + back.prefHeight() / 2 - slider.prefHeight() / 2);
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        if (e.getPosX() > slider.x().get() + slider.prefWidth()) {
            value.set(value.getValue() + preMove);
        } else if (e.getPosX() < slider.x().get()) {
            value.set(value.getValue() - preMove);
        }
        if (slider.contains(e.getPosX(), e.getPosY()))
            select = true;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof MouseEvent.MouseMoveEvent && select) {
            value.set((((MouseEvent.MouseMoveEvent) event).getNewPosX() - x().get())/prefWidth());
        } else if (event instanceof MouseEvent.MouseReleasedEvent) {
            select = false;
        } else if (event instanceof MouseEvent.MouseLeaveEvent) {
            select = false;
        }
    }

    public MutableValue<AssetPath> backBg() {
        return back.buttonBackground();
    }

    public SimpleMutableObjectValue<AssetPath> sliderBg(){
        return slider.path();
    }
}