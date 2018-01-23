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

import java.util.Locale;
import sharknoon.dualide.utils.language.Language;
import sharknoon.dualide.utils.language.Word;

/**
 *
 * @author frank
 */
public class English extends Language {

    public English() {
        super(Locale.ENGLISH);
    }

    {
        add(Word.EMTPY, "");
        add(Word.RUN, "Run!");
        //MenuBar
        add(Word.MENUBAR_OPTIONS_TEXT, "Options");
        add(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, "Lanuage");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, "Background");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, "Open backgroundfolder");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, "Background duration");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_MINUTES_TEXT, "minutes");
        add(Word.MENUBAR_PROJECT_TEXT, "Project");
        add(Word.MENUBAR_PROJECT_CLOSE_TEXT, "Close");
        //ToolBar
        add(Word.TOOLBAR_BUTTON_SAVE_TEXT, "Save");
        //Welcomesite
        add(Word.WELCOME_SITE_TAB_TITLE, "Welcome");
        add(Word.WELCOMESITE_RECENT_PROJECTS, "Recent projects");
        add(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, "Create new project");
        add(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, "Load existing project");
        //Projectsite
        add(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, "Add package");
        add(Word.PROJECT_SITE_COMMENT_BUTTON_TEXT, "Comment project");
        add(Word.PROJECT_SITE_DELETE_BUTTON_TEXT, "Delete project");
        add(Word.PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment package");
        add(Word.PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename package");
        add(Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete package");
        //Packagesite
        add(Word.PACKAGE_SITE_ADD_PACKAGE_BUTTON_TEXT, "Add package");
        add(Word.PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT, "Add class");
        add(Word.PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT, "Add function");
        add(Word.PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT, "Add variable");
        add(Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT, "Comment package");
        add(Word.PACKAGE_SITE_DELETE_BUTTON_TEXT, "Delete package");
        add(Word.PACKAGE_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment");
        add(Word.PACKAGE_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename");
        add(Word.PACKAGE_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete");
        //ClassSite
        add(Word.CLASS_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Comment");
        add(Word.CLASS_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Rename");
        add(Word.CLASS_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Delete");
        add(Word.CLASS_SITE_ADD_FUNCTION_BUTTON_TEXT, "Add function");
        add(Word.CLASS_SITE_ADD_VARIABLE_BUTTON_TEXT, "Add variable");
        add(Word.CLASS_SITE_COMMENT_BUTTON_TEXT, "Comment class");
        add(Word.CLASS_SITE_DELETE_BUTTON_TEXT, "Delete class");
        //Dialogs
        ///New Project
        add(Word.NEW_PROJECT_DIALOG_TITLE, "Create new project");
        add(Word.NEW_PROJECT_DIALOG_HEADER_TEXT, "Enter projectname");
        add(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT, "Projectname");
        ///Comment Project
        add(Word.COMMENT_PROJECT_DIALOG_TITLE, "Comment project");
        add(Word.COMMENT_PROJECT_DIALOG_HEADER_TEXT, "Enter comments");
        ///Delete Project
        add(Word.DELETE_PROJECT_DIALOG_TITLE, "Delete project #PROJECT");
        add(Word.DELETE_PROJECT_DIALOG_HEADER_TEXT, "Really delete project #PROJECT?");
        add(Word.DELETE_PROJECT_DIALOG_CONTENT_TEXT, "All content of the project #PROJECT will be deleted permanently!");
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
        add(Word.DELETE_PACKAGE_DIALOG_TITLE, "Delete package #PACKAGE");
        add(Word.DELETE_PACKAGE_DIALOG_HEADER_TEXT, "Really delete package #PACKAGE?");
        add(Word.DELETE_PACKAGE_DIALOG_CONTENT_TEXT, "All content of the package #PACKAGE will be deleted permanently!");
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
        //Delete Class
        add(Word.DELETE_CLASS_DIALOG_TITLE, "Delete class #CLASS");
        add(Word.DELETE_CLASS_DIALOG_HEADER_TEXT, "Really delete class #CLASS?");
        add(Word.DELETE_CLASS_DIALOG_CONTENT_TEXT, "All content of the class #CLASS will be deleted permanently!");
        ///New Function
        add(Word.NEW_FUNCTION_DIALOG_TITLE, "Create new function");
        add(Word.NEW_FUNCTION_DIALOG_HEADER_TEXT, "Enter new functionname");
        add(Word.NEW_FUNCTION_DIALOG_CONTENT_TEXT, "Functionname");
        ///New Variable
        add(Word.NEW_VARIABLE_DIALOG_TITLE, "Create new Variable");
        add(Word.NEW_VARIABLE_DIALOG_HEADER_TEXT, "Enter new variablename");
        add(Word.NEW_VARIABLE_DIALOG_CONTENT_TEXT, "Variablename");

    }

}
