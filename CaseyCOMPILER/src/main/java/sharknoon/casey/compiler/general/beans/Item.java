package sharknoon.casey.compiler.general.beans;/*
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

import java.util.List;
import java.util.UUID;

public class Item {
    
    
    //The name of the item (required)
    public String name = "";
    
    //Comments of this item (required)
    public String comments = "";
    
    //The type of this item (required)
    public ItemType item;
    
    //The list of children of this item (required)
    public List<Item> children = List.of();
    
    //The id of this project (only for projects)
    public UUID id;
    
    //The type of this variable or parameter (only for variables and parameters)
    public String type;
    
    //The returntype of this function (only for functions)
    public String returntype;
    
    //The list of blocks of this function (only for functions)
    public List<Block> blocks = List.of();
    
    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", comments='" + comments + '\'' +
                ", item=" + item +
                ", children= size=" + (children != null ? children.size() : 0) +
                ", id=" + id +
                ", type='" + type + '\'' +
                ", returntype='" + returntype + '\'' +
                ", blocks= size=" + (blocks != null ? blocks.size() : 0) +
                '}';
    }
    
    public enum ItemType {
        FUNCTION,
        PROJECT,
        PACKAGE,
        VARIABLE,
        PARAMETER,
        CLASS
    }
}
