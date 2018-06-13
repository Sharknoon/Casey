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
package sharknoon.dualide.utils.language;

/**
 * This enum stores all the word that can be translated.
 *
 * @author frank
 */
public enum Word {
    EMTPY,
    RUN,
    //MenuBar
    MENUBAR_OPTIONS_TEXT,
    MENUBAR_OPTIONS_LANGUAGE_TEXT,
    MENUBAR_OPTIONS_BACKGROUND_TEXT,
    MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT,
    MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT,
    MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_MINUTES_TEXT,
    MENUBAR_PROJECT_TEXT,
    MENUBAR_PROJECT_CLOSE_TEXT,
    //ToolBar
    TOOLBAR_BUTTON_SAVE_TEXT,
    //Welcomesite
    WELCOME_SITE_TAB_TITLE,
    WELCOMESITE_RECENT_PROJECTS,
    WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT,
    WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT,
    //Projectite
    PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT,
    PROJECT_SITE_COMMENT_BUTTON_TEXT,
    PROJECT_SITE_RENAME_BUTTON_TEXT,
    PROJECT_SITE_DELETE_BUTTON_TEXT,
    PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT,
    PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT,
    PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT,
    //Packagesite
    PACKAGE_SITE_COMMENT_CHILDREN_BUTTON_TEXT,
    PACKAGE_SITE_RENAME_CHILDREN_BUTTON_TEXT,
    PACKAGE_SITE_DELETE_CHILDREN_BUTTON_TEXT,
    PACKAGE_SITE_ADD_PACKAGE_BUTTON_TEXT,
    PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT,
    PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT,
    PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT,
    PACKAGE_SITE_COMMENT_BUTTON_TEXT,
    PACKAGE_SITE_RENAME_BUTTON_TEXT,
    PACKAGE_SITE_DELETE_BUTTON_TEXT,
    //ClassSite
    CLASS_SITE_COMMENT_CHILDREN_BUTTON_TEXT,
    CLASS_SITE_RENAME_CHILDREN_BUTTON_TEXT,
    CLASS_SITE_DELETE_CHILDREN_BUTTON_TEXT,
    CLASS_SITE_ADD_FUNCTION_BUTTON_TEXT,
    CLASS_SITE_ADD_VARIABLE_BUTTON_TEXT,
    CLASS_SITE_COMMENT_BUTTON_TEXT,
    CLASS_SITE_RENAME_BUTTON_TEXT,
    CLASS_SITE_DELETE_BUTTON_TEXT,
    //VariableSite
    VARIABLE_SITE_CLASS_LABEL_TEXT,
    VARIABLE_SITE_FINAL_COMBOBOX_TEXT,
    VARIABLE_SITE_COMMENT_BUTTON_TEXT,
    VARIABLE_SITE_RENAME_BUTTON_TEXT,
    VARIABLE_SITE_DELETE_BUTTON_TEXT,
    //FunctionSite
    FUNCTION_SITE_ADD_NEW_END_BLOCK,
    FUNCTION_SITE_ADD_NEW_DECISION_BLOCK,
    FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK,
    FUNCTION_SITE_ADD_NEW_CALL_BLOCK,
    FUNCTION_SITE_ADD_NEW_INPUT_BLOCK,
    FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK,
    //Dialogs
    ///New Project
    NEW_PROJECT_DIALOG_TITLE,
    NEW_PROJECT_DIALOG_HEADER_TEXT,
    NEW_PROJECT_DIALOG_CONTENT_TEXT,
    ///Rename Project
    RENAME_PROJECT_DIALOG_TITLE,
    RENAME_PROJECT_DIALOG_HEADER_TEXT,
    RENAME_PROJECT_DIALOG_CONTENT_TEXT,
    ///Comment Project
    COMMENT_PROJECT_DIALOG_TITLE,
    COMMENT_PROJECT_DIALOG_HEADER_TEXT,
    ///Delete Project
    DELETE_PROJECT_DIALOG_TITLE,
    DELETE_PROJECT_DIALOG_HEADER_TEXT,
    DELETE_PROJECT_DIALOG_CONTENT_TEXT,
    ///Save Project
    SAVE_DIALOG_TITLE,
    SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT,
    ///Open Project
    OPEN_DIALOG_TITLE,
    ///New Package
    NEW_PACKAGE_DIALOG_TITLE,
    NEW_PACKAGE_DIALOG_HEADER_TEXT,
    NEW_PACKAGE_DIALOG_CONTENT_TEXT,
    ///Rename Package
    RENAME_PACKAGE_DIALOG_TITLE,
    RENAME_PACKAGE_DIALOG_HEADER_TEXT,
    RENAME_PACKAGE_DIALOG_CONTENT_TEXT,
    ///Comment Package
    COMMENT_PACKAGE_DIALOG_TITLE,
    COMMENT_PACKAGE_DIALOG_HEADER_TEXT,
    ///Delete Package
    DELETE_PACKAGE_DIALOG_TITLE,
    DELETE_PACKAGE_DIALOG_HEADER_TEXT,
    DELETE_PACKAGE_DIALOG_CONTENT_TEXT,
    ///New Class
    NEW_CLASS_DIALOG_TITLE,
    NEW_CLASS_DIALOG_HEADER_TEXT,
    NEW_CLASS_DIALOG_CONTENT_TEXT,
    ///Rename Class
    RENAME_CLASS_DIALOG_TITLE,
    RENAME_CLASS_DIALOG_HEADER_TEXT,
    RENAME_CLASS_DIALOG_CONTENT_TEXT,
    ///Comment Class
    COMMENT_CLASS_DIALOG_TITLE,
    COMMENT_CLASS_DIALOG_HEADER_TEXT,
    //Delete Class
    DELETE_CLASS_DIALOG_TITLE,
    DELETE_CLASS_DIALOG_HEADER_TEXT,
    DELETE_CLASS_DIALOG_CONTENT_TEXT,
    ///New Function
    NEW_FUNCTION_DIALOG_TITLE,
    NEW_FUNCTION_DIALOG_HEADER_TEXT,
    NEW_FUNCTION_DIALOG_CONTENT_TEXT,
    ///New Variable
    NEW_VARIABLE_DIALOG_TITLE,
    NEW_VARIABLE_DIALOG_HEADER_TEXT,
    NEW_VARIABLE_DIALOG_CONTENT_TEXT,
    ///Rename Variable
    RENAME_VARIABLE_DIALOG_TITLE,
    RENAME_VARIABLE_DIALOG_HEADER_TEXT,
    RENAME_VARIABLE_DIALOG_CONTENT_TEXT,
    ///Comment Variable
    COMMENT_VARIABLE_DIALOG_TITLE,
    COMMENT_VARIABLE_DIALOG_HEADER_TEXT,
    ///Delete Variable
    DELETE_VARIABLE_DIALOG_TITLE,
    DELETE_VARIABLE_DIALOG_HEADER_TEXT,
    DELETE_VARIABLE_DIALOG_CONTENT_TEXT,
    ///Corrupt project
    PROJECT_CORRUPT_DIALOG_TITLE,
    PROJECT_CORRUPT_DIALOG_HEADER_TEXT,
    PROJECT_CORRUPT_DIALOG_CONTENT_TEXT,
    ///Class in Use
    CLASS_IN_USE_DIALOG_TITLE,
    CLASS_IN_USE_DIALOG_HEADER_TEXT,
    TCLASS_IN_USE_DIALOG_CONTENT_TEXT,
    ///New Text value
    NEW_TEXT_VALUE_DIALOG_TITLE,
    NEW_TEXT_VALUE_DIALOG_HEADER_TEXT,
    NEW_TEXT_VALUE_DIALOG_CONTENT_TEXT,
    ///New Number value
    NEW_NUMBER_VALUE_DIALOG_TITLE,
    NEW_NUMBER_VALUE_DIALOG_HEADER_TEXT,
    NEW_NUMBER_VALUE_DIALOG_CONTENT_TEXT,
    ///New Boolean value
    NEW_BOOLEAN_VALUE_DIALOG_TITLE,
    NEW_BOOLEAN_VALUE_DIALOG_HEADER_TEXT,
    NEW_BOOLEAN_VALUE_DIALOG_CONTENT_TEXT,
    ///New Object value
    NEW_OBJECT_VALUE_DIALOG_TITLE,
    NEW_OBJECT_VALUE_DIALOG_HEADER_TEXT,
    NEW_OBJECT_VALUE_DIALOG_CONTENT_TEXT,
    //ValueSelectionPopup
    VALUE_SELECTION_POPUP_NEW_VALUES,
    VALUE_SELECTION_POPUP_NEW_PRIMITIVE_VALUES,
    VALUE_SELECTION_POPUP_NEW_OBJECT_VALUES,
    VALUE_SELECTION_POPUP_VALUES_EXTENSION,
    VALUE_SELECTION_POPUP_EXISTING_VALUES,
    VALUE_SELECTION_POPUP_STATIC_VALUES,
    VALUE_SELECTION_POPUP_CLASS_VALUES,
    VALUE_SELECTION_POPUP_FUNCTION_VALUES,
    VALUE_SELECTION_POPUP_TITLE,
    //TypeSelectionField
    TYPE_SELECTION_FIELD_SELECT_TYPE,
    //TypeSelectionPopup
    TYPE_SELECTION_POPUP_PRIMITIVE_TYPES,
    TYPE_SELECTION_POPUP_OBJECT_TYPES,
    TYPE_SELECTION_POPUP_TITLE,
    //General
    FUNCTION,
    CLASS,
    PROJECT,
    VARIABLE,
    PACKAGE,
    WELCOME,
    NUMBER,
    BOOLEAN,
    TEXT,
    OBJECT,
    NUMBER_CREATION,
    BOOLEAN_CREATION,
    TEXT_CREATION,
    OBJECT_CREATION,
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MODULO,
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_OR_EQUAL_THAN,
    LESS_OR_EQUAL_THAN,
    AND,
    OR,
    NOT,
    CONCAT,
    LENGTH,
    DELETE, 

}
