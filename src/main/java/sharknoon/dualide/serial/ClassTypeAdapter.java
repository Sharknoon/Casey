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
package sharknoon.dualide.serial;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import sharknoon.dualide.utils.settings.Logger;

/**
 *
 * @author Josua Frank
 */
public class ClassTypeAdapter extends TypeAdapter<Class> {
    
    public static final String CLASSPATH = "sharknoon.dualide.logic.";
    
    @Override
    public void write(JsonWriter out, Class value) throws IOException {
        out.value(value.getSimpleName().toLowerCase());
    }
    
    @Override
    public Class read(JsonReader in) throws IOException {
        Class clazz = null;
        try {
            if (in.hasNext()) {
                String className = in.nextString();
                className = Character.toUpperCase(className.charAt(0)) + className.substring(1);
                clazz = Class.forName(CLASSPATH+className);
            }
        } catch (ClassNotFoundException ex) {
            Logger.error("Item does not exist", ex);
        }
        return clazz;
    }
    
}
