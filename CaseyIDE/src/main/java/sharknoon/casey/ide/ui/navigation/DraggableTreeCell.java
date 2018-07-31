package sharknoon.casey.ide.ui.navigation;


import javafx.geometry.Point2D;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import sharknoon.casey.ide.logic.items.Item;


public class DraggableTreeCell extends TreeCell<Item> {
    
    /**
     * Using a static here, it's just too convenient.
     */
    private static TreeItem<Item> draggedTreeItem;
    private static WorkDropType workDropType;
    private Object controller;
    
    public DraggableTreeCell() {
        
        getStyleClass().add("tree-cell");
        
        setOnDragOver(event -> {
            if (isDraggableToParent() && isNotAlreadyChildOfTarget(DraggableTreeCell.this.getTreeItem()) && draggedTreeItem.getParent() != getTreeItem()) {
                Point2D sceneCoordinates = DraggableTreeCell.this.localToScene(0d, 0d);
                
                double height = DraggableTreeCell.this.getHeight();
                
                // get the y coordinate within the control
                double y = event.getSceneY() - (sceneCoordinates.getY());
                
                // if the drop is three quarters of the way down the control
                // then the drop will be a sibling and not into the tree item
                
                // set the dnd effect for the required action
                if (y > (height * .75d)) {
                    setEffect(null);
                    
                    getStyleClass().add("dnd-below");
                    
                    workDropType = WorkDropType.REORDER;
                } else {
                    getStyleClass().remove("dnd-below");
                    
                    InnerShadow shadow;
                    
                    shadow = new InnerShadow();
                    shadow.setOffsetX(1.0);
                    shadow.setColor(Color.web("#666666"));
                    shadow.setOffsetY(1.0);
                    setEffect(shadow);
                    
                    workDropType = WorkDropType.DROP_INTO;
                }
                
                event.acceptTransferModes(TransferMode.MOVE);
            }
        });
        setOnDragDetected(event -> {
            ClipboardContent content;
            
            content = new ClipboardContent();
            content.putString("TROLOLOL");
            
            Dragboard dragboard;
            
            dragboard = getTreeView().startDragAndDrop(TransferMode.MOVE);
            dragboard.setContent(content);
            
            draggedTreeItem = getTreeItem();
            
            event.consume();
        });
        setOnDragDropped(event -> {
            boolean dropOK = false;
            
            if (draggedTreeItem != null) {
                
                TreeItem<?> draggedItemParent = draggedTreeItem.getParent();
                
                var draggedWork = draggedTreeItem.getValue();
                
                if (workDropType == WorkDropType.DROP_INTO) {
                    
                    if (isDraggableToParent() && isNotAlreadyChildOfTarget(DraggableTreeCell.this.getTreeItem()) && draggedTreeItem.getParent() != getTreeItem()) {
                        draggedWork.move(getTreeItem().getValue());
                        //draggedWork.removeLinkFrom(draggedItemParent.getValue());
                        //.addLinkTo(getTreeItem().getValue());
                        
                        //draggedItemParent.getValue().getChildren().remove(draggedWork);
                        
                        //getTreeItem().getValue().getChildren().add(draggedWork);
                        
                        getTreeItem().setExpanded(true);
                        
                        //clickListeners.leftClickListener.get().itemSelected(draggedWork);
                    }
                } else if (workDropType == WorkDropType.REORDER) {
                
                }
                
                dropOK = true;
                
                draggedTreeItem = null;
            }
            
            event.setDropCompleted(dropOK);
            event.consume();
        });
        setOnDragExited(event -> {
            // remove all dnd effects
            setEffect(null);
            getStyleClass().remove("dnd-below");
        });
    }
    
    protected boolean isDraggableToParent() {
        return draggedTreeItem.getValue().canMoveTo(getTreeItem().getValue());
    }
    
    protected boolean isNotAlreadyChildOfTarget(TreeItem<Item> treeItemParent) {
        if (draggedTreeItem == treeItemParent)
            return false;
        
        if (treeItemParent.getParent() != null)
            return isNotAlreadyChildOfTarget(treeItemParent.getParent());
        else
            return true;
    }
    
    protected void updateItem(Item item, boolean empty) {
        
        // if a tree cell is showing the text of another value that was
        // selected, it may be that the properties are still bound to a form control
        if (getItem() != null) {
            //getItem().unbind(this);
        }
        
        super.updateItem(item, empty);
        if (!empty && item != null) {
            setText(item.toString());
            setGraphic(getTreeItem().getGraphic());
        } else {
            setText(null);
            setGraphic(null);
        }
        if (item != null) {
            //item.updateTreeCell(this, controlLoader);
        }
    }
    
    public Object getController() {
        return controller;
    }
    
    public void setController(Object controller) {
        this.controller = controller;
    }
    
    private enum WorkDropType {DROP_INTO, REORDER}
}