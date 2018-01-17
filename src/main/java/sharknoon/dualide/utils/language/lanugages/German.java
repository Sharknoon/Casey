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
public class German extends Language {

    public German() {
        super(Locale.GERMAN);
    }

    {
        add(Word.RUN, "Los!");
        add(Word.NEW_PROJECT_DIALOG_TITLE, "Neues Projekt anlegen");
        add(Word.NEW_PROJECT_DIALOG_HEADER_TEXT, "Projektname eingeben");
        add(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT, "Projektname");
        add(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, "Neues Projekt anlegen");
        add(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, "Vorhandenes Projekt laden");
        add(Word.WELCOME_SITE_TAB_TITLE, "Willkommen");
        add(Word.MENUBAR_OPTIONS_TEXT, "Optionen");
        add(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, "Sprache");
        add(Word.PROJECT_SITE_RENAME_PACKAGE_BUTTON_TEXT, "Paket umbenennen");
        add(Word.PROJECT_SITE_DELETE_PACKAGE_BUTTON_TEXT, "Paket löschen");
        add(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, "Paket hinzufügen");
        add(Word.NEW_PACKAGE_DIALOG_TITLE, "Neues Paket anlegen");
        add(Word.NEW_PACKAGE_DIALOG_HEADER_TEXT, "Paketname eingeben");
        add(Word.NEW_PACKAGE_DIALOG_CONTENT_TEXT, "Paketname");
        add(Word.RENAME_PACKAGE_DIALOG_TITLE, "Paket umbenennen");
        add(Word.RENAME_PACKAGE_DIALOG_HEADER_TEXT, "Neuen Paketnamen eingeben");
        add(Word.RENAME_PACKAGE_DIALOG_CONTENT_TEXT, "Paketname");
        add(Word.DELETE_PACKAGE_DIALOG_TITLE, "Paket #PACKAGE löschen");
        add(Word.DELETE_PACKAGE_DIALOG_HEADER_TEXT, "Paket #PACKAGE wirklich löschen?");
        add(Word.DELETE_PACKAGE_DIALOG_CONTENT_TEXT, "Alle Inhalte in dem Paket #PACKAGE werden unwiederruflich gelöscht!");
        add(Word.PROJECT_SIDE_DELETE_PROJECT_BUTTON_TEXT, "Projekt löschen");
        add(Word.DELETE_PROJECT_DIALOG_TITLE, "Projekt #PROJECT löschen");
        add(Word.DELETE_PROJECT_DIALOG_HEADER_TEXT, "Projekt #PROJECT wirklich löschen?");
        add(Word.DELETE_PROJECT_DIALOG_CONTENT_TEXT, "Alle Inhalte in dem Projekt #PROJECT werden unwiederruflich gelöscht!");
        add(Word.TOOLBAR_BUTTON_SAVE_TEXT, "Speichern");
        add(Word.MENUBAR_PROJECT_TEXT, "Projekt");
        add(Word.MENUBAR_PROJECT_CLOSE_TEXT, "Schließen");
        add(Word.SAVE_DIALOG_TITLE, "Projekt speichern");
        add(Word.SAVE_DIALOG_EXTENSION_FILTER_DUALIDE_PROJECT, "DualIDE Projekt");
        add(Word.OPEN_DIALOG_TITLE, "Projekt laden");
        add(Word.WELCOMESITE_RECENT_PROJECTS, "Zuletzt verwendet");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, "Hintergrund");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, "Öffne Hintergrundordner");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, "Hintergrunddauer");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_MINUTES_TEXT, "Minuten");
        add(Word.PROJECT_SITE_COMMENT_PACKAGE_BUTTON_TEXT, "Paket kommentieren");
        add(Word.PROJECT_SIDE_COMMENT_PROJECT_BUTTON_TEXT, "Projekt kommentieren");
    }
}
