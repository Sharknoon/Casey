package sharknoon.dualide.ui.flowchart;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceContextMenu {

    private final Flowchart flowchart;
    private ContextMenu menu;
    private Point2D origin;

    public WorkspaceContextMenu(Flowchart flowchart) {
        this.flowchart = flowchart;
    }

    public void init() {

    }

    public void onMousePressed(Point2D screenOrigin) {
        origin = screenOrigin;
        if (menu != null) {
            menu.hide();
        }
    }

    public void onContextMenuRequested(Point2D workspaceOrigin, Point2D screenOrigin, Node originNode) {
        if (!screenOrigin.equals(origin)) {
            return;
        }
        double x = workspaceOrigin.getX();
        double y = workspaceOrigin.getY();
        if (x < 0 + Settings.paddingInsideWorkSpace) {
            return;
        } else if (x > Settings.maxWorkSpaceX - Settings.paddingInsideWorkSpace) {
            return;
        } else if (y < 0 + Settings.paddingInsideWorkSpace) {
            return;
        } else if (y > Settings.maxWorkSpaceY - Settings.paddingInsideWorkSpace) {
            return;
        }

        if (menu == null) {
            menu = new ContextMenu();
            menu.setAutoHide(true);
        } else {
            menu.hide();
        }
        if (!flowchart.isMouseOverShape()) {
            MenuItem addNewDecisionBlockItem = new MenuItem("Add Decision Block");
            MenuItem addNewProgressBlockItem = new MenuItem("Add Progress Block");
            MenuItem addNewEndBlockItem = new MenuItem("Add End Block");
            addNewDecisionBlockItem.setOnAction(e -> {
                flowchart.addDecisionBlock(workspaceOrigin);
            });
            addNewProgressBlockItem.setOnAction(e -> {
                flowchart.addProcessBlock(workspaceOrigin);
            });
            addNewEndBlockItem.setOnAction(e -> {
                flowchart.addEndBlock(workspaceOrigin);
            });
            menu.getItems().clear();
            menu.getItems().addAll(addNewDecisionBlockItem, addNewProgressBlockItem, addNewEndBlockItem);
            menu.show(originNode, screenOrigin.getX(), screenOrigin.getY());
        }
    }

}
