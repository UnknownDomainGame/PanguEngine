package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.Region;
import nullengine.client.gui.input.MouseActionEvent;
import nullengine.client.gui.input.MouseEvent;
import nullengine.client.gui.shape.Rect;
import nullengine.util.Color;
import org.joml.Vector2f;


public class HSlider extends Region {
    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue(150);
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue(15);
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);

    private boolean select = false;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        min.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue > max.get()) {
                min.set(oldValue);
            } else {
                rebuild();
            }
        });
        max.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue < min.get()) {
                max.set(oldValue);
            } else {
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue == 0) {
                step.set(oldValue);
            } else {
                resizeSlider(sliderLength.get() * (float) (step.get() / (max.get() - min.get())), sliderThickness.get());
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> rebuild());
        sliderThickness.addChangeListener((observable, oldValue, newValue) -> rebuild());
        this.getChildren().addAll(back, slider);
        backBg().setValue(Color.BLUE);
        sliderBg().setValue(Color.WHITE);

        addEventHandler(MouseActionEvent.MOUSE_PRESSED, this::onMousePressed);
        addEventHandler(MouseActionEvent.MOUSE_RELEASED, this::onMouseReleased);
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMove);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public MutableDoubleValue max() {
        return max;
    }

    public MutableDoubleValue min() {
        return min;
    }

    public MutableFloatValue sliderThickness() {
        return sliderThickness;
    }

    public MutableFloatValue sliderLength() {
        return sliderLength;
    }

    public MutableFloatValue step() {
        return step;
    }

    public void rebuild() {
        if (value.get() > max.get()) {
            value.set(max.get());
        } else if (value.get() < min.get()) {
            value.set(min.get());
        }
        resizeBack(sliderLength.get(), sliderThickness.get());
        resizeSlider(sliderLength.get() * (float) (step.get() / (max.get() - min.get())), sliderThickness.get());
        slider.x().set((float) ((back.rectSize().get().x() - slider.rectSize().get().x()) * ((value.get() - min.get()) / (max.get() - min.get()))));
        slider.y().set(back.y().get());
    }

    private void onMousePressed(MouseActionEvent e) {
        if (e.getX() > slider.x().get() + slider.width().get()) {
            value.set(value.getValue() + step.get());
        } else if (e.getX() < slider.x().get()) {
            value.set(value.getValue() - step.get());
        }
        if (slider.contains(e.getX(), e.getY()))
            select = true;
    }

    private void onMouseMove(MouseEvent event) {
        if (!select) return;
        if ((event.getX() - slider.x().get() - slider.rectSize().get().x()) / width().get() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.getValue() + step.get());
        } else if ((slider.x().get() - event.getX()) / width().get() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.getValue() - step.get());
        }
    }

    private void onMouseReleased(MouseActionEvent event) {
        select = false;
    }

    private void resizeBack(float width, float height) {
        back.rectSize().setValue(new Vector2f(width, height));
    }

    private void resizeSlider(float width, float height) {
        slider.rectSize().setValue(new Vector2f(width, height));
    }

    public MutableValue<Color> backBg() {
        return back.fillColor();
    }

    public MutableValue<Color> sliderBg() {
        return slider.fillColor();
    }

}
