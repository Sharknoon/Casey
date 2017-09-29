package sharknoon.dualide.ui.flowchart.blocks;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 *
 * @author Josua Frank
 */
public interface Moveable {

    public double getMinX();

    public double getMinY();

    public default double getWidth() {
        return getMaxX() - getMinX();
    }

    public default double getHeight() {
        return getMaxY() - getMinY();
    }

    public double getMaxX();

    public double getMaxY();

    public void setMinX(double x);

    public void setMinY(double y);

    public boolean canMoveTo(double x, double y);

    public default Bounds getBounds() {
        return new BoundingBox(getMinX(), getMinY(), getWidth(), getHeight());
    }

}
