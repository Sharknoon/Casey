package sharknoon.dualide.utils.javafx;

/*
 * Copyright 2016 Andreas Fester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.scene.control.Slider;

/**
 * Sub class of Slider which only returns values once they are snapped to
 * corresponding ticks.
 */
public class SnapSlider extends Slider {
    
    /**
     * The finalValue property - this property is modified once the snapping
     * slider has reached a final value.
     */
    private final ReadOnlyDoubleWrapper finalValue = new ReadOnlyDoubleWrapper();
    /**
     * Flag to indicate whether we (potentially) observed the final value.
     * Initial assumption: no dragging - clicked value is the final one.
     * Flag changes to "false" once dragging starts.
     */
    private boolean isFinal = true;
    /**
     * The stored value which might potentially be delivered as the final value
     * at a later time.
     */
    private Double storedValue = null;
    
    /**
     * Creates a new SnapSlider.
     */
    public SnapSlider(double min, double max, double value) {
        super(min, max, value);
        setSnapToTicks(true);
        
        final double minCompare = min + Math.ulp(min);
        final double maxCompare = max - Math.ulp(max);
        
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (isFinal) {  /* either dragging of knob has stopped or
             * no dragging was done at all (direct click or
             * keyboard navigation)
             */
                finalValue.set((Double) newValue);
                storedValue = null;
            } else {        // dragging in progress
                
                double val = (double) newValue;
                if (val > maxCompare || val < minCompare) {
                    isFinal = true; /* this value will be treated as final value
                     * once the valueChangingProperty goes to false
                     */
                    storedValue = (Double) newValue; // remember current value
                } else {
                    isFinal = false;    // no final value anymore - slider
                    
                    storedValue = null; /* has been dragged to a position within
                     * minimum and maximum
                     */
                }
                
            }
        });
        
        valueChangingProperty().addListener((observable, oldValue, newValue) -> {
            // dragging of knob stopped AND captured value is already the final one since it is either the minimum or the maximum value
            if (newValue == false && isFinal == true) {
                //Can be null in the case in which the super of this method causes this property to change, but the finalValue isnt instanciated
                if (finalValue != null) {
                    finalValue.set(storedValue);
                }
                storedValue = null;
            }
            // adjust the isFinal flag according to the new property value
            isFinal = !newValue;
        });
    }
    
    public Double getFinalValue() {
        return finalValue.get();
    }
    
    public final ReadOnlyDoubleProperty finalValueProperty() {
        return finalValue.getReadOnlyProperty();
    }
}
