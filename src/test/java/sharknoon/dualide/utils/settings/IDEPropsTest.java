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
package sharknoon.dualide.utils.settings;

import java.util.Optional;
import java.util.UUID;
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
public class IDEPropsTest {

    public IDEPropsTest() {
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
     * Test of set method, of class IDEProps.
     */
    @Test
    public void test() {
        String uniqueKeyTest = UUID.randomUUID().toString();
        String valueTest = "Blaaaa";
        Props.set(uniqueKeyTest, valueTest);
        assertEquals(valueTest, Props.get(uniqueKeyTest).join().get());
        String remove = Props.remove(uniqueKeyTest).join();
        assertEquals(valueTest, remove);
        assertFalse(Props.get(uniqueKeyTest).join().isPresent());
    }

}
