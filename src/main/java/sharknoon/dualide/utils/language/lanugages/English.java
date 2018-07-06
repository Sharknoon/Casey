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
package sharknoon.dualide.utils.language.lanugages;

import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

import java.util.Locale;

/**
 * @author frank
 */
public class English extends Language {
    
    {
        add(Word.EMTPY, "");
        add(Word.RUN, "Run!");
        //MenuBar
        add(Word.MENUBAR_OPTIONS_TEXT, "Options");
        add(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, "Lanuage");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, "Background");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, "Open backgroundfolder");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, "Background duration");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_SECONDS_TEXT, "seconds");
        add(Word.MENUBAR_OPTIONS_STYLE_TEXT, "Style");
        add(Word.MENUBAR_OPTIONS_STYLE_DARK_TEXT, "Dark");
        add(Word.MENUBAR_OPTIONS_STYLE_LIGHT_TEXT, "Bright");
        add(Word.MENUBAR_PROJECT_TEXT, "Project");
        add(Word.MENUBAR_PROJECT_CLOSE_TEXT, "Close");
        //ToolBar
        add(Word.TOOLBAR_BUTTON_SAVE_TEXT, "Save");
        add(Word.TOOLBAR_BUTTON_RUN_TEXT, "Run");
        //Welcomesite
        add(Word.WELCOME_SITE_TAB_TITLE, "Welcome");
        add(Word.WELCOMESITE_RECENT_PROJECTS, "Recent projects");
        add(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, "Create new project");
        add(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, "Load existing project");
        //Projectsite
        add(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, "Add package");
        add(Word.PROJECT_SITE_COMMENT_BUTTON_TEXT, "Comment project");
        add(Word.PROJECT_SITE_RENAME_BUTTON_TEXT, "Rename project");
        add(Word.PROJECT_SITE_DELETE_BUTTON_TEXT, "Delete project");
        add(Word.PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment package");
        add(Word.PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename package");
        add(Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete package");
        //Packagesite
        add(Word.PACKAGE_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment");
        add(Word.PACKAGE_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename");
        add(Word.PACKAGE_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete");
        add(Word.PACKAGE_SITE_ADD_PACKAGE_BUTTON_TEXT, "Add package");
        add(Word.PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT, "Add class");
        add(Word.PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT, "Add function");
        add(Word.PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT, "Add variable");
        add(Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT, "Comment package");
        add(Word.PACKAGE_SITE_RENAME_BUTTON_TEXT, "Rename package");
        add(Word.PACKAGE_SITE_DELETE_BUTTON_TEXT, "Delete package");
        //ClassSite
        add(Word.CLASS_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment");
        add(Word.CLASS_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename");
        add(Word.CLASS_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete");
        add(Word.CLASS_SITE_ADD_FUNCTION_BUTTON_TEXT, "Add function");
        add(Word.CLASS_SITE_ADD_VARIABLE_BUTTON_TEXT, "Add variable");
        add(Word.CLASS_SITE_COMMENT_BUTTON_TEXT, "Comment class");
        add(Word.CLASS_SITE_RENAME_BUTTON_TEXT, "Rename class");
        add(Word.CLASS_SITE_DELETE_BUTTON_TEXT, "Delete class");
        //VariableSite
        add(Word.VARIABLE_SITE_CLASS_LABEL_TEXT, "Variabletype");
        add(Word.VARIABLE_SITE_FINAL_COMBOBOX_TEXT, "Variable modifiable");
        add(Word.VARIABLE_SITE_COMMENT_BUTTON_TEXT, "Comment variable");
        add(Word.VARIABLE_SITE_RENAME_BUTTON_TEXT, "Rename variable");
        add(Word.VARIABLE_SITE_DELETE_BUTTON_TEXT, "Delete variable");
        //ParameterSite
        add(Word.PARAMETER_SITE_CLASS_LABEL_TEXT, "Parametertype");
        add(Word.PARAMETER_SITE_FINAL_COMBOBOX_TEXT, "Parameter modifiable");
        add(Word.PARAMETER_SITE_COMMENT_BUTTON_TEXT, "Comment parameter");
        add(Word.PARAMETER_SITE_RENAME_BUTTON_TEXT, "Rename parameter");
        add(Word.PARAMETER_SITE_DELETE_BUTTON_TEXT, "Delete parameter");
        //FunctionSite
        add(Word.FUNCTION_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment");
        add(Word.FUNCTION_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename");
        add(Word.FUNCTION_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete");
        add(Word.FUNCTION_SITE_ADD_VARIABLE_BUTTON_TEXT, "Add variable");
        add(Word.PARAMETER_SITE_ADD_PARAMETER_BUTTON_TEXT, "Add parameter");
        add(Word.FUNCTION_SITE_COMMENT_BUTTON_TEXT, "Comment function");
        add(Word.FUNCTION_SITE_RENAME_BUTTON_TEXT, "Rename function");
        add(Word.FUNCTION_SITE_DELETE_BUTTON_TEXT, "Delete function");
        add(Word.FUNCTION_SITE_ADD_NEW_END_BLOCK, "Add new end block");
        add(Word.FUNCTION_SITE_ADD_NEW_DECISION_BLOCK, "Add new decision block");
        add(Word.FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK, "Add new assignment block");
        add(Word.FUNCTION_SITE_ADD_NEW_CALL_BLOCK, "Add new call block");
        add(Word.FUNCTION_SITE_ADD_NEW_INPUT_BLOCK, "Add new input block");
        add(Word.FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK, "Add new output block");
        add(Word.FUNCTION_SITE_FUNCTION_LOGIC, "Logic of the function");
        add(Word.FUNCTION_SITE_FUNCTION_VARIABLES_AND_PARAMETER, "Variables and parameters of the function");
        add(Word.FUNCTION_SITE_FUNCTION_PARAMETERS, "Parameters of the function");
        add(Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, "Return of the function");
        //Dialogs
        ///New Project
        add(Word.NEW_PROJECT_DIALOG_TITLE, "Create new project");
        add(Word.NEW_PROJECT_DIALOG_HEADER_TEXT, "Enter projectname");
        add(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT, "Projectname");
        //Rename Project
        add(Word.RENAME_PROJECT_DIALOG_TITLE, "Rename project");
        add(Word.RENAME_PROJECT_DIALOG_HEADER_TEXT, "Enter new projectname");
        add(Word.RENAME_PROJECT_DIALOG_CONTENT_TEXT, "Projectname");
        ///Comment Project
        add(Word.COMMENT_PROJECT_DIALOG_TITLE, "Comment project");
        add(Word.COMMENT_PROJECT_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Project
        add(Word.DELETE_PROJECT_DIALOG_TITLE, "Delete project ${PROJECT}");
        add(Word.DELETE_PROJECT_DIALOG_HEADER_TEXT, "Really delete project ${PROJECT}?");
        add(Word.DELETE_PROJECT_DIALOG_CONTENT_TEXT, "All content of the project ${PROJECT} will be deleted permanently!");
        ///Save Project
        add(Word.SAVE_DIALOG_TITLE, "Save project");
        add(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT, "DualIDE project");
        ///Open Project
        add(Word.OPEN_DIALOG_TITLE, "Load project");
        ///New Package
        add(Word.NEW_PACKAGE_DIALOG_TITLE, "Create new package");
        add(Word.NEW_PACKAGE_DIALOG_HEADER_TEXT, "Enter packagename");
        add(Word.NEW_PACKAGE_DIALOG_CONTENT_TEXT, "Packagename");
        ///Rename Package
        add(Word.RENAME_PACKAGE_DIALOG_TITLE, "Rename package");
        add(Word.RENAME_PACKAGE_DIALOG_HEADER_TEXT, "Enter new packagename");
        add(Word.RENAME_PACKAGE_DIALOG_CONTENT_TEXT, "Packagename");
        ///Comment Package
        add(Word.COMMENT_PACKAGE_DIALOG_TITLE, "Comment package");
        add(Word.COMMENT_PACKAGE_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Package
        add(Word.DELETE_PACKAGE_DIALOG_TITLE, "Delete package ${PACKAGE}");
        add(Word.DELETE_PACKAGE_DIALOG_HEADER_TEXT, "Really delete package ${PACKAGE}?");
        add(Word.DELETE_PACKAGE_DIALOG_CONTENT_TEXT, "All content of the package ${PACKAGE} will be deleted permanently!");
        ///New Class
        add(Word.NEW_CLASS_DIALOG_TITLE, "Create new class");
        add(Word.NEW_CLASS_DIALOG_HEADER_TEXT, "Enter new classname");
        add(Word.NEW_CLASS_DIALOG_CONTENT_TEXT, "Classname");
        ///Rename Class
        add(Word.RENAME_CLASS_DIALOG_TITLE, "Rename class");
        add(Word.RENAME_CLASS_DIALOG_HEADER_TEXT, "Enter new classname");
        add(Word.RENAME_CLASS_DIALOG_CONTENT_TEXT, "Classname");
        ///Comment Class
        add(Word.COMMENT_CLASS_DIALOG_TITLE, "Comment class");
        add(Word.COMMENT_CLASS_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Class
        add(Word.DELETE_CLASS_DIALOG_TITLE, "Delete class ${CLASS}");
        add(Word.DELETE_CLASS_DIALOG_HEADER_TEXT, "Really delete class ${CLASS}?");
        add(Word.DELETE_CLASS_DIALOG_CONTENT_TEXT, "All content of the class ${CLASS} will be deleted permanently!");
        ///New Function
        add(Word.NEW_FUNCTION_DIALOG_TITLE, "Create new function");
        add(Word.NEW_FUNCTION_DIALOG_HEADER_TEXT, "Enter new functionname");
        add(Word.NEW_FUNCTION_DIALOG_CONTENT_TEXT, "Functionname");
        ///Rename Function
        add(Word.RENAME_FUNCTION_DIALOG_TITLE, "Rename function");
        add(Word.RENAME_FUNCTION_DIALOG_HEADER_TEXT, "Enter new functionname");
        add(Word.RENAME_FUNCTION_DIALOG_CONTENT_TEXT, "Functionname");
        ///Comment Function
        add(Word.COMMENT_FUNCTION_DIALOG_TITLE, "Comment function");
        add(Word.COMMENT_FUNCTION_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Function
        add(Word.DELETE_FUNCTION_DIALOG_TITLE, "Delete function ${FUNCTION}");
        add(Word.DELETE_FUNCTION_DIALOG_HEADER_TEXT, "Really delete function ${FUNCTION}?");
        add(Word.DELETE_FUNCTION_DIALOG_CONTENT_TEXT, "All content of this function ${FUNCTION} will be deleted permanently!");
        ///New Parameter
        add(Word.NEW_PARAMETER_VALUE_DIALOG_TITLE, "Create new parameter");
        add(Word.NEW_PARAMETER_VALUE_DIALOG_HEADER_TEXT, "Enter new parametername");
        add(Word.NEW_PARAMETER_VALUE_DIALOG_CONTENT_TEXT, "Parametername");
        ///Rename Parameter
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_TITLE, "Rename parameter");
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_HEADER_TEXT, "Enter new parametername");
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_CONTENT_TEXT, "Parametername");
        ///Comment Parameter
        add(Word.COMMENT_PARAMETER_DIALOG_TITLE, "Comment parameter");
        add(Word.COMMENT_PARAMETER_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Parameter
        add(Word.DELETE_PARAMETER_DIALOG_TITLE, "Delete parameter ${PARAMETER}");
        add(Word.DELETE_PARAMETER_DIALOG_HEADER_TEXT, "Really delete parameter ${PARAMETER}?");
        add(Word.DELETE_PARAMETER_DIALOG_CONTENT_TEXT, "The parameter ${PARAMETER} will be deleted permanently!");
        ///New Variable
        add(Word.NEW_VARIABLE_DIALOG_TITLE, "Create new Variable");
        add(Word.NEW_VARIABLE_DIALOG_HEADER_TEXT, "Enter new variablename");
        add(Word.NEW_VARIABLE_DIALOG_CONTENT_TEXT, "Variablename");
        ///Rename Variable
        add(Word.RENAME_VARIABLE_DIALOG_TITLE, "Rename variable");
        add(Word.RENAME_VARIABLE_DIALOG_HEADER_TEXT, "Enter new variablename");
        add(Word.RENAME_VARIABLE_DIALOG_CONTENT_TEXT, "Variablename");
        ///Comment Variable
        add(Word.COMMENT_VARIABLE_DIALOG_TITLE, "Comment variable");
        add(Word.COMMENT_VARIABLE_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Variable
        add(Word.DELETE_VARIABLE_DIALOG_TITLE, "Delete variable ${VARIABLE}");
        add(Word.DELETE_VARIABLE_DIALOG_HEADER_TEXT, "Really delete variable ${VARIABLE}?");
        add(Word.DELETE_VARIABLE_DIALOG_CONTENT_TEXT, "The variable ${VARIABLE} will be deleted permanently!");
        ///Corrupt project
        add(Word.PROJECT_CORRUPT_DIALOG_TITLE, "Corrupt project");
        add(Word.PROJECT_CORRUPT_DIALOG_HEADER_TEXT, "The project is corrupt");
        add(Word.PROJECT_CORRUPT_DIALOG_CONTENT_TEXT, "The porject couldn't be opened due to a fault in the projectfile. For details look at the stacktrace.");
        ///Class in Use
        add(Word.CLASS_IN_USE_DIALOG_TITLE, "Class in use");
        add(Word.CLASS_IN_USE_DIALOG_HEADER_TEXT, "The class is still in use");
        add(Word.TCLASS_IN_USE_DIALOG_CONTENT_TEXT, "The class could not be deleted due to the folowing dependencies:\n${LIST}");
        ///New Text value
        add(Word.NEW_TEXT_VALUE_DIALOG_TITLE, "New text");
        add(Word.NEW_TEXT_VALUE_DIALOG_HEADER_TEXT, "Please enter a text");
        add(Word.NEW_TEXT_VALUE_DIALOG_CONTENT_TEXT, "Text");
        ///New Number value
        add(Word.NEW_NUMBER_VALUE_DIALOG_TITLE, "New number");
        add(Word.NEW_NUMBER_VALUE_DIALOG_HEADER_TEXT, "Please enter a number");
        add(Word.NEW_NUMBER_VALUE_DIALOG_CONTENT_TEXT, "Number");
        ///New Boolean value
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_TITLE, "New Boolean");
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_HEADER_TEXT, "Please select a boolean");
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_CONTENT_TEXT, "Boolean");
        ///New Object value
        add(Word.NEW_OBJECT_VALUE_DIALOG_TITLE, "New Object");
        add(Word.NEW_OBJECT_VALUE_DIALOG_HEADER_TEXT, "Please select a type");
        add(Word.NEW_OBJECT_VALUE_DIALOG_CONTENT_TEXT, "Type");
        //ValueSelectionPopup
        add(Word.VALUE_SELECTION_POPUP_NEW_VALUES, "New values");
        add(Word.VALUE_SELECTION_POPUP_NEW_PRIMITIVE_VALUES, "New primitive values");
        add(Word.VALUE_SELECTION_POPUP_NEW_OBJECT_VALUES, "New object values");
        add(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION, "-values");
        add(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES, "Existing values");
        add(Word.VALUE_SELECTION_POPUP_STATIC_VALUES, "General values");
        add(Word.VALUE_SELECTION_POPUP_CLASS_VALUES, "Values of this class");
        add(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES, "Values of this function");
        add(Word.VALUE_SELECTION_POPUP_TITLE, "Valueselection");
        //TypeSelectionField
        add(Word.TYPE_SELECTION_FIELD_SELECT_TYPE, "Select type...");
        add(Word.TYPE_SELECTION_POPUP_NO_TYPES, "No types allowed");
        //TypeSelectionPopup
        add(Word.TYPE_SELECTION_POPUP_PRIMITIVE_TYPES, "Primitive types");
        add(Word.TYPE_SELECTION_POPUP_OBJECT_TYPES, "Objecttypes");
        add(Word.TYPE_SELECTION_POPUP_TITLE, "Typeselection");
        //VariableSelectionField
        add(Word.VARIABLE_SELECTION_FIELD_SELECT_VARIABLE, "Select variable...");
        add(Word.VARIABLE_SELECTION_POPUP_NO_TYPES, "No types allowed");
        //VariableSelectionPopup
        add(Word.VARIABLE_SELECTION_POPUP_TITLE, "Variable and parameterselection");
        //General
        add(Word.FUNCTION, "Function");
        add(Word.PARAMETER, "Parameter");
        add(Word.CLASS, "Class");
        add(Word.PROJECT, "Project");
        add(Word.VARIABLE, "Variable");
        add(Word.PACKAGE, "Package");
        add(Word.WELCOME, "Welcome");
        add(Word.NUMBER, "Number");
        add(Word.BOOLEAN, "Boolean");
        add(Word.TEXT, "Text");
        add(Word.OBJECT, "Object");
        add(Word.NUMBER_CREATION, "Create new Number");
        add(Word.BOOLEAN_CREATION, "Create new Boolean");
        add(Word.TEXT_CREATION, "Create new Text");
        add(Word.OBJECT_CREATION, "Create new Object");
        add(Word.ADD, "Addition");
        add(Word.SUBTRACT, "Subtraction");
        add(Word.MULTIPLY, "Multiplication");
        add(Word.DIVIDE, "Division");
        add(Word.MODULO, "Modulo");
        add(Word.EQUALS, "Equal");
        add(Word.NOT_EQUALS, "Not equal");
        add(Word.GREATER_THAN, "Greater than");
        add(Word.LESS_THAN, "Less than");
        add(Word.GREATER_OR_EQUAL_THAN, "Greater or equal than");
        add(Word.LESS_OR_EQUAL_THAN, "Less or equal than");
        add(Word.AND, "And");
        add(Word.OR, "Or");
        add(Word.NOT, "Not");
        add(Word.CONCAT, "Concat");
        add(Word.LENGTH, "Length");
        add(Word.DELETE, "Delete");
        add(Word.VOID, "Void");
    }
    
    public English() {
        super(Locale.ENGLISH);
    }
    
}
