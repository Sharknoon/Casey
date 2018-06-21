/*
 * Copyright 2018 Shark Industries.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sharknoon.dualide.ui.dialogs;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Josua Frank
 */
public class ExceptionDialog extends Alert {
    
    public static void show(String message, Throwable e) {
        if (Platform.isFxApplicationThread()) {
            new ExceptionDialog(message, e).showAndWait();
        } else {
            showNotOnJavaFX(message, e);
        }
    }
    
    private static void showNotOnJavaFX(String message, Throwable error) {
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        var exceptionText = sw.toString();
        
        JOptionPane.showMessageDialog(null, message + "\n" + exceptionText, "Exception Dialog", JOptionPane.ERROR_MESSAGE);
    }
    
    private ExceptionDialog(String message, Throwable ex) {
        super(AlertType.ERROR);
        setTitle("Exception Dialog");
        setHeaderText("A fatal error occured!");
        setContentText(message);
    
        // Create expandable Exception.
        var sw = new StringWriter();
        var pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        var exceptionText = sw.toString();
    
        var label = new Label("The exception stacktrace was:");
    
        var textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
    
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
    
        var expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
    
        // Set expandable Exception into the dialog pane.
        getDialogPane().setExpandableContent(expContent);
        getDialogPane().setExpanded(true);
        getDialogPane().setPrefWidth(700);
    }
    
}
