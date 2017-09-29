package sharknoon.dualide.ui.flowchart;

import javafx.scene.layout.AnchorPane;

/**
 *
 * @author Josua Frank
 */
public class WorkspaceMoving {

    private final AnchorPane workspace;
    private double startMouseX = 0;
    private double startMouseY = 0;
    private double startTranslationX = 0;
    private double startTranslationY = 0;

    public WorkspaceMoving(AnchorPane workspace) {
        this.workspace = workspace;
    }

    public void init() {

    }

    public void onMousePressed(double sceneX, double sceneY) {
        startMouseX = sceneX;
        startMouseY = sceneY;
        startTranslationX = workspace.getTranslateX();
        startTranslationY = workspace.getTranslateY();
    }

    public void onMouseDragged(double sceneX, double sceneY) {
        double deltaX = sceneX - startMouseX;
        double deltaY = sceneY - startMouseY;
        workspace.setTranslateX(startTranslationX + deltaX);
        workspace.setTranslateY(startTranslationY + deltaY);
    }

}
