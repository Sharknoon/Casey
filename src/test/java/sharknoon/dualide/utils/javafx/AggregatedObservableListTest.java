package sharknoon.dualide.utils.javafx;/*
/**
 * Testing the AggregatedObservableArrayList
 */

import javafx.beans.Observable;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class AggregatedObservableListTest {
    
    
    @Test
    public void testObservableValue() {
        final AggregatedObservableArrayList<IntegerProperty> aggregatedWrapper = new AggregatedObservableArrayList<>();
        final ObservableList<IntegerProperty> aggregatedList = aggregatedWrapper.getAggregatedList();
        aggregatedList.addListener((Observable observable) -> {
            System.out.println("observable = " + observable);
        });
        
        final ObservableList<IntegerProperty> list1 = FXCollections.observableArrayList();
        final ObservableList<IntegerProperty> list2 = FXCollections.observableArrayList();
        final ObservableList<IntegerProperty> list3 = FXCollections.observableArrayList();
        
        list1.addAll(new SimpleIntegerProperty(1), new SimpleIntegerProperty(2), new SimpleIntegerProperty(3), new SimpleIntegerProperty(4),
                new SimpleIntegerProperty(5));
        list2.addAll(new SimpleIntegerProperty(10), new SimpleIntegerProperty(11), new SimpleIntegerProperty(12), new SimpleIntegerProperty(13),
                new SimpleIntegerProperty(14), new SimpleIntegerProperty(15));
        list3.addAll(new SimpleIntegerProperty(100), new SimpleIntegerProperty(110), new SimpleIntegerProperty(120), new SimpleIntegerProperty(130),
                new SimpleIntegerProperty(140), new SimpleIntegerProperty(150));
        
        // adding list 1 to aggregate
        aggregatedWrapper.appendList(list1);
        assertEquals("wrong content", "[1,2,3,4,5]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // removing elems from list1
        list1.remove(2, 4);
        assertEquals("wrong content", "[1,2,5]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // adding second List
        aggregatedWrapper.appendList(list2);
        assertEquals("wrong content", "[1,2,5,10,11,12,13,14,15]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // removing elems from second List
        list2.remove(1, 3);
        assertEquals("wrong content", "[1,2,5,10,13,14,15]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // replacing element in first list
        list1.set(1, new SimpleIntegerProperty(3));
        assertEquals("wrong content", "[1,3,5,10,13,14,15]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // adding third List
        aggregatedWrapper.appendList(list3);
        assertEquals("wrong content", "[1,3,5,10,13,14,15,100,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // emptying second list
        list2.clear();
        assertEquals("wrong content", "[1,3,5,100,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // adding new elements to second list
        list2.addAll(new SimpleIntegerProperty(203), new SimpleIntegerProperty(202), new SimpleIntegerProperty(201));
        assertEquals("wrong content", "[1,3,5,203,202,201,100,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // sorting list2. this results in permutation
        list2.sort(Comparator.comparing(IntegerExpression::getValue));
        assertEquals("wrong content", "[1,3,5,201,202,203,100,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // removing list2 completely
        aggregatedWrapper.removeList(list2);
        assertEquals("wrong content", "[1,3,5,100,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // updating one integer value in list 3
        SimpleIntegerProperty integer = (SimpleIntegerProperty) list3.get(0);
        integer.set(1);
        assertEquals("wrong content", "[1,3,5,1,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        // prepending list 2 again
        aggregatedWrapper.prependList(list2);
        assertEquals("wrong content", "[201,202,203,1,3,5,1,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
        
        //swapping the first two lists
        aggregatedWrapper.swapLists(0, 1);
        assertEquals("wrong content", "[1,3,5,201,202,203,1,110,120,130,140,150]", aggregatedWrapper.dump(ObservableIntegerValue::get));
    }
}