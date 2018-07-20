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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class Item {
    
    
    //The name of the item (required)
    public String name = "";
    
    //Comments of this item (required)
    public String comments = "";
    
    //The type of this item (required)
    public ItemType item;
    
    //The id of this item (required)
    public UUID id;
    
    //The list of children of this item (required)
    public List<Item> children;
    
    //The type of this variable or parameter (only for variables and parameters)
    public String type;
    
    //The returntype of this function (only for functions)
    public String returntype;
    
    //The list of blocks of this function (only for functions)
    public List<Block> blocks;
    
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("comments")
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    @JsonProperty("item")
    public void setItem(String item) {
        try {
            this.item = ItemType.valueOf(item.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            //TODO ERROR
        }
    }
    
    @JsonProperty("id")
    public void setId(String id) {
        try {
            this.id = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            //TODO Error
        }
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
