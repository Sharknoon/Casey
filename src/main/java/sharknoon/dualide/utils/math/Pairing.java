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
package sharknoon.dualide.utils.math;

/**
 *
 * @author Josua Frank
 */
public class Pairing {
    
    /**
     * Creates a unique number from two numbers
     * @param d1
     * @param d2
     * @return 
     */
    public static final double pair(double d1, double d2){
        return 0.5 * (d1 + d2) * (d1 + d2 + 1) + d2;
    }
 
    public static final double pair(double d1, double d2, double... d){
        var dr = pair(d1, d2);
        for (double dx : d) {
            dr = pair(dr, dx);
        }
        return dr;
    }
    
}
