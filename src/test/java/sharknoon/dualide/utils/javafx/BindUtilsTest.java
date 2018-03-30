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
package sharknoon.dualide.utils.javafx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josua Frank
 */
public class BindUtilsTest {

    public BindUtilsTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of map method, of class BindUtils.
     */
    @Test
    public void testMap_ObservableValue_Function() {
        StringProperty sp = new SimpleStringProperty("3");
        ObservableValue<Integer> i = BindUtils.map(sp, s -> Integer.valueOf(s));
        assertEquals((long) 3, (long) i.getValue());
        sp.set("42");
        assertEquals((long) 42, (long) i.getValue());
    }

    /**
     * Test of map method, of class BindUtils.
     */
    @Test
    public void testMap_ObservableList_Function() {
        ObservableList<Integer> l1 = FXCollections.observableArrayList(1, 2, 3);
        ObservableList<Integer> result = BindUtils.map(l1, i -> i * 2);
        assertEquals("[2, 4, 6]", result.toString());
        l1.set(1, 22);
        assertEquals("[2, 44, 6]", result.toString());
    }

    /**
     * Test of concat method, of class BindUtils.
     */
    @Test
    public void testConcat() {
        ObservableList<Integer> l1 = FXCollections.observableArrayList(1, 2, 3);
        ObservableList<Integer> l2 = FXCollections.observableArrayList(5, 6, 7);
        ObservableList<Integer> l3 = FXCollections.observableArrayList(9, 10, 11);

        ObservableList<Integer> lc = BindUtils.concat(l1, l2, l3);

        l1.add(4);
        l3.add(0, 8);

        assertEquals("[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]", lc.toString());

        l1.remove(1);
        l1.add(1, 22);

        assertEquals("[1, 22, 3, 4, 5, 6, 7, 8, 9, 10, 11]", lc.toString());
    }

}
