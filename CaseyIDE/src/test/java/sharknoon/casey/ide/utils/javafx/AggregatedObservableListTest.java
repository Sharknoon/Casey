package sharknoon.casey.ide.utils.javafx;/*
/**
 * Testing the AggregatedObservableList
 */

import javafx.beans.Observable;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.Test;
import sharknoon.casey.ide.utils.javafx.bindings.AggregatedObservableList;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AggregatedObservableListTest {
    
    
    @Test
    public void testObservableValue() {
        final AggregatedObservableList<IntegerProperty> aggregatedList = new AggregatedObservableList<>();
        aggregatedList.addListener((Observable observable) -> {
            System.out.println("observable = " + observable);
        });
        
        final ObservableList<IntegerProperty> list1 = FXCollections.observableArrayList();
        final ObservableList<IntegerProperty> list2 = FXCollections.observableArrayList();
        final ObservableList<IntegerProperty> list3 = FXCollections.observableArrayList();
        final ObservableList<IntegerProperty> list4 = FXCollections.observableArrayList();
        
        list1.addAll(new SimpleIntegerProperty(1), new SimpleIntegerProperty(2), new SimpleIntegerProperty(3), new SimpleIntegerProperty(4),
                new SimpleIntegerProperty(5));
        list2.addAll(new SimpleIntegerProperty(10), new SimpleIntegerProperty(11), new SimpleIntegerProperty(12), new SimpleIntegerProperty(13),
                new SimpleIntegerProperty(14), new SimpleIntegerProperty(15));
        list3.addAll(new SimpleIntegerProperty(100), new SimpleIntegerProperty(110), new SimpleIntegerProperty(120), new SimpleIntegerProperty(130),
                new SimpleIntegerProperty(140), new SimpleIntegerProperty(150));
        list4.addAll(new SimpleIntegerProperty(200), new SimpleIntegerProperty(210), new SimpleIntegerProperty(220), new SimpleIntegerProperty(230),
                new SimpleIntegerProperty(240), new SimpleIntegerProperty(250));
        
        // adding list 1 to aggregate
        aggregatedList.appendList(list1);
        assertEquals("[1,2,3,4,5]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // removing elems from list1
        list1.remove(2, 4);
        assertEquals("[1,2,5]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // adding second List
        aggregatedList.appendList(list2);
        assertEquals("[1,2,5,10,11,12,13,14,15]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // removing elems from second List
        list2.remove(1, 3);
        assertEquals("[1,2,5,10,13,14,15]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // replacing element in first list
        list1.set(1, new SimpleIntegerProperty(3));
        assertEquals("[1,3,5,10,13,14,15]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // adding third List
        aggregatedList.appendList(list3);
        assertEquals("[1,3,5,10,13,14,15,100,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // emptying second list
        list2.clear();
        assertEquals("[1,3,5,100,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // adding new elements to second list
        list2.addAll(new SimpleIntegerProperty(203), new SimpleIntegerProperty(202), new SimpleIntegerProperty(201));
        assertEquals("[1,3,5,203,202,201,100,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // sorting list2. this results in permutation
        list2.sort(Comparator.comparing(IntegerExpression::getValue));
        assertEquals("[1,3,5,201,202,203,100,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // removing list2 completely
        aggregatedList.removeList(list2);
        assertEquals("[1,3,5,100,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // updating one integer value in list 3
        SimpleIntegerProperty integer = (SimpleIntegerProperty) list3.get(0);
        integer.set(1);
        assertEquals("[1,3,5,1,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        // prepending list 2 again
        aggregatedList.prependList(list2);
        assertEquals("[201,202,203,1,3,5,1,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
        
        //swapping the first two lists
        aggregatedList.swapLists(0, 1);
        assertEquals("[1,3,5,201,202,203,1,110,120,130,140,150]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
    
        assertThrows(UnsupportedOperationException.class, () -> aggregatedList.add(new SimpleIntegerProperty()), "Modifying should not be allowed!");
    
        AggregatedObservableList<IntegerProperty> anotherAggregatedList = new AggregatedObservableList<>();
        anotherAggregatedList.appendList(list4);
        aggregatedList.mergeWith(anotherAggregatedList);
        assertEquals("[1,3,5,201,202,203,1,110,120,130,140,150,200,210,220,230,240,250]", aggregatedList.dump(ObservableIntegerValue::get), "wrong content");
    }
}