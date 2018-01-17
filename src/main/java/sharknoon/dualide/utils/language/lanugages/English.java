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
        add(Word.RUN, "Run!");
        add(Word.NEW_PROJECT_DIALOG_TITLE, "Create new project");
        add(Word.NEW_PROJECT_DIALOG_HEADER_TEXT, "Enter projectname");
        add(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT, "Projectname");
        add(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, "Create new project");
        add(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, "Load existing project");
        add(Word.WELCOME_SITE_TAB_TITLE, "Welcome");
        add(Word.MENUBAR_OPTIONS_TEXT, "Options");
        add(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, "Lanuage");
        add(Word.PROJECT_SITE_RENAME_PACKAGE_BUTTON_TEXT, "Rename package");
        add(Word.PROJECT_SITE_DELETE_PACKAGE_BUTTON_TEXT, "Delete package");
        add(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, "Add package");
        add(Word.NEW_PACKAGE_DIALOG_TITLE, "Create new package");
        add(Word.NEW_PACKAGE_DIALOG_HEADER_TEXT, "Enter packagename");
        add(Word.NEW_PACKAGE_DIALOG_CONTENT_TEXT, "Packagename");
        add(Word.RENAME_PACKAGE_DIALOG_TITLE, "Rename package");
        add(Word.RENAME_PACKAGE_DIALOG_HEADER_TEXT, "Enter new packagename");
        add(Word.RENAME_PACKAGE_DIALOG_CONTENT_TEXT, "Packagename");
        add(Word.DELETE_PACKAGE_DIALOG_TITLE, "Delete package #PACKAGE");
        add(Word.DELETE_PACKAGE_DIALOG_HEADER_TEXT, "Really delete package #PACKAGE?");
        add(Word.DELETE_PACKAGE_DIALOG_CONTENT_TEXT, "All content of the package #PACKAGE will be deleted permanently!");
        add(Word.PROJECT_SIDE_DELETE_PROJECT_BUTTON_TEXT, "Delete project");
        add(Word.DELETE_PROJECT_DIALOG_TITLE, "Delete project #PROJECT");
        add(Word.DELETE_PROJECT_DIALOG_HEADER_TEXT, "Really delete project #PROJECT?");
        add(Word.DELETE_PROJECT_DIALOG_CONTENT_TEXT, "All content of the project #PROJECT will be deleted permanently!");
        add(Word.TOOLBAR_BUTTON_SAVE_TEXT, "Save");
        add(Word.MENUBAR_PROJECT_TEXT, "Project");
        add(Word.MENUBAR_PROJECT_CLOSE_TEXT, "Close");
        add(Word.SAVE_DIALOG_TITLE, "Save project");
        add(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT, "DualIDE project");
        add(Word.OPEN_DIALOG_TITLE, "Load project");
        add(Word.WELCOMESITE_RECENT_PROJECTS, "Recent projects");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, "Background");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, "Open backgroundfolder");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, "Background duration");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_MINUTES_TEXT, "minutes");
        add(Word.PROJECT_SITE_COMMENT_PACKAGE_BUTTON_TEXT, "Comment package");
        add(Word.PROJECT_SIDE_COMMENT_PROJECT_BUTTON_TEXT, "Comment project");
    }

}
