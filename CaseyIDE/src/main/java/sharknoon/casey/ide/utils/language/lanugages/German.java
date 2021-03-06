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
package sharknoon.casey.ide.utils.language.lanugages;

import sharknoon.casey.ide.utils.language.*;

import java.util.Locale;

/**
 * @author frank
 */
public class German extends Language {
    
    {
        add(Word.EMTPY, "");
        add(Word.RUN, "Los!");
        //MenuBar
        add(Word.MENUBAR_OPTIONS_TEXT, "Optionen");
        add(Word.MENUBAR_OPTIONS_LANGUAGE_TEXT, "Sprache");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_TEXT, "Hintergrund");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_OPEN_FOLDER_TEXT, "Öffne Hintergrundordner");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_RELOAD_IMAGES, "Hintergrundbilder aktualisieren");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_TEXT, "Hintergrunddauer");
        add(Word.MENUBAR_OPTIONS_BACKGROUND_SET_DURATION_SECONDS_TEXT, "Sekunden");
        add(Word.MENUBAR_OPTIONS_STYLE_TEXT, "Stil");
        add(Word.MENUBAR_OPTIONS_STYLE_DARK_TEXT, "Dunkel");
        add(Word.MENUBAR_OPTIONS_STYLE_LIGHT_TEXT, "Hell");
        add(Word.MENUBAR_OPTIONS_UPDATE_TEXT, "Auf Aktualisierungen überprüfen");
        add(Word.MENUBAR_OPTIONS_ABOUT_TEXT, "Über Casey");
        add(Word.MENUBAR_PROJECT_TEXT, "Projekt");
        add(Word.MENUBAR_PROJECT_OPEN_FOLDER_TEXT, "Projektordner öffnen");
        add(Word.MENUBAR_PROJECT_CLOSE_TEXT, "Schließen");
        //ToolBar
        add(Word.TOOLBAR_BUTTON_SAVE_TEXT, "Speichern");
        add(Word.TOOLBAR_BUTTON_RUN_TEXT, "Ausführen");
        add(Word.TOOLBAR_BUTTON_RUN_TOOLTIP, "Paketfunktionen ausgeführen");
        add(Word.TOOLBAR_BUTTON_CODEVIEWER_TEXT, "Quellcode anzeigen");
        //Welcomesite
        add(Word.WELCOME_SITE_TAB_TITLE, "Willkommen");
        add(Word.WELCOMESITE_RECENT_PROJECTS, "Zuletzt verwendet");
        add(Word.WELCOME_SITE_CREATE_NEW_PROJECT_BUTTON_TEXT, "Neues Projekt anlegen");
        add(Word.WELCOME_SITE_LOAD_PROJECT_BUTTON_TEXT, "Vorhandenes Projekt laden");
        //Projectsite
        add(Word.PROJECT_SITE_ADD_PACKAGE_BUTTON_TEXT, "Paket hinzufügen");
        add(Word.PROJECT_SITE_COMMENT_BUTTON_TEXT, "Projekt kommentieren");
        add(Word.PROJECT_SITE_RENAME_BUTTON_TEXT, "Projekt umbenennen");
        add(Word.PROJECT_SITE_DELETE_BUTTON_TEXT, "Projekt löschen");
        add(Word.PROJECT_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Kommentieren");
        add(Word.PROJECT_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Umbenennen");
        add(Word.PROJECT_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Löschen");
        //Packagesite
        add(Word.PACKAGE_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Kommentieren");
        add(Word.PACKAGE_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Umbenennen");
        add(Word.PACKAGE_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Löschen");
        add(Word.PACKAGE_SITE_ADD_PACKAGE_BUTTON_TEXT, "Paket hinzufügen");
        add(Word.PACKAGE_SITE_ADD_CLASS_BUTTON_TEXT, "Klasse hinzufügen");
        add(Word.PACKAGE_SITE_ADD_FUNCTION_BUTTON_TEXT, "Funktion hinzufügen");
        add(Word.PACKAGE_SITE_ADD_VARIABLE_BUTTON_TEXT, "Variable hinzufügen");
        add(Word.PACKAGE_SITE_COMMENT_BUTTON_TEXT, "Paket kommentieren");
        add(Word.PACKAGE_SITE_RENAME_BUTTON_TEXT, "Paket umbenennen");
        add(Word.PACKAGE_SITE_DELETE_BUTTON_TEXT, "Paket löschen");
        //ClassSite
        add(Word.CLASS_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Kommentieren");
        add(Word.CLASS_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Umbenennen");
        add(Word.CLASS_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Löschen");
        add(Word.CLASS_SITE_ADD_FUNCTION_BUTTON_TEXT, "Funktion hinzufügen");
        add(Word.CLASS_SITE_ADD_VARIABLE_BUTTON_TEXT, "Variable hinzufügen");
        add(Word.CLASS_SITE_COMMENT_BUTTON_TEXT, "Klasse kommentieren");
        add(Word.CLASS_SITE_RENAME_BUTTON_TEXT, "Klasse umbenennen");
        add(Word.CLASS_SITE_DELETE_BUTTON_TEXT, "Klasse löschen");
        //VariableSite
        add(Word.VARIABLE_SITE_CLASS_LABEL_TEXT, "Variablentyp");
        add(Word.VARIABLE_SITE_FINAL_COMBOBOX_TEXT, "Variable änderbar");
        add(Word.VARIABLE_SITE_COMMENT_BUTTON_TEXT, "Variable kommentieren");
        add(Word.VARIABLE_SITE_RENAME_BUTTON_TEXT, "Variable umbenennen");
        add(Word.VARIABLE_SITE_DELETE_BUTTON_TEXT, "Variable löschen");
        //ParameterSite
        add(Word.PARAMETER_SITE_CLASS_LABEL_TEXT, "Parametertyp");
        add(Word.PARAMETER_SITE_FINAL_COMBOBOX_TEXT, "Parameter änderbar");
        add(Word.PARAMETER_SITE_COMMENT_BUTTON_TEXT, "Parameter kommentieren");
        add(Word.PARAMETER_SITE_RENAME_BUTTON_TEXT, "Parameter umbenennen");
        add(Word.PARAMETER_SITE_DELETE_BUTTON_TEXT, "Parameter löschen");
        //FunctionSite
        add(Word.FUNCTION_SITE_COMMENT_CHILDREN_BUTTON_TEXT, "Kommentieren");
        add(Word.FUNCTION_SITE_RENAME_CHILDREN_BUTTON_TEXT, "Umbenennen");
        add(Word.FUNCTION_SITE_DELETE_CHILDREN_BUTTON_TEXT, "Löschen");
        add(Word.FUNCTION_SITE_ADD_VARIABLE_BUTTON_TEXT, "Variable hinzufügen");
        add(Word.PARAMETER_SITE_ADD_PARAMETER_BUTTON_TEXT, "Parameter hinzufügen");
        add(Word.FUNCTION_SITE_COMMENT_BUTTON_TEXT, "Funktion kommentieren");
        add(Word.FUNCTION_SITE_RENAME_BUTTON_TEXT, "Funktion umbenennen");
        add(Word.FUNCTION_SITE_DELETE_BUTTON_TEXT, "Funktion löschen");
        add(Word.FUNCTION_SITE_ADD_NEW_END_BLOCK, "Neuen Endblock erstellen");
        add(Word.FUNCTION_SITE_ADD_NEW_DECISION_BLOCK, "Neuen Entscheidungsblock erstellen");
        add(Word.FUNCTION_SITE_ADD_NEW_ASSIGNMENT_BLOCK, "Neuen Zuweisungsblock erstellen");
        add(Word.FUNCTION_SITE_ADD_NEW_CALL_BLOCK, "Neuen Funktionsblock erstellen");
        add(Word.FUNCTION_SITE_ADD_NEW_INPUT_BLOCK, "Neuen Eingabeblock erstellen");
        add(Word.FUNCTION_SITE_ADD_NEW_OUTPUT_BLOCK, "Neuen Ausgabeblock erstellen");
        add(Word.FUNCTION_SITE_FUNCTION_LOGIC, "Funktionslogik");
        add(Word.FUNCTION_SITE_FUNCTION_VARIABLES_AND_PARAMETER, "Funktionsvariablen und -parameter");
        add(Word.FUNCTION_SITE_FUNCTION_PARAMETERS, "Funktionsparameter");
        add(Word.FUNCTION_SITE_FUNCTION_RETURNTYPE, "Funktionsrückgabe");
        //Dialogs
        ///New Project
        add(Word.NEW_PROJECT_DIALOG_TITLE, "Neues Projekt anlegen");
        add(Word.NEW_PROJECT_DIALOG_HEADER_TEXT, "Projektname eingeben");
        add(Word.NEW_PROJECT_DIALOG_CONTENT_TEXT, "Projektname");
        //Rename Project
        add(Word.RENAME_PROJECT_DIALOG_TITLE, "Projekt umbenennen");
        add(Word.RENAME_PROJECT_DIALOG_HEADER_TEXT, "Neuen Projektnamen eingeben");
        add(Word.RENAME_PROJECT_DIALOG_CONTENT_TEXT, "Projektname");
        ///Comment Project
        add(Word.COMMENT_PROJECT_DIALOG_TITLE, "Projekt kommentieren");
        add(Word.COMMENT_PROJECT_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Project
        add(Word.DELETE_PROJECT_DIALOG_TITLE, "Projekt ${PROJECT} löschen");
        add(Word.DELETE_PROJECT_DIALOG_HEADER_TEXT, "Projekt ${PROJECT} wirklich löschen?");
        add(Word.DELETE_PROJECT_DIALOG_CONTENT_TEXT, "Alle Inhalte in dem Projekt ${PROJECT} werden unwiederruflich gelöscht!");
        ///Save Project
        add(Word.SAVE_DIALOG_TITLE, "Projekt speichern");
        add(Word.SAVE_DIALOG_EXTENSION_FILTER_CASEY_PROJECT, "Casey Projekt");
        ///Open Project
        add(Word.OPEN_DIALOG_TITLE, "Projekt laden");
        ///New Package
        add(Word.NEW_PACKAGE_DIALOG_TITLE, "Neues Paket anlegen");
        add(Word.NEW_PACKAGE_DIALOG_HEADER_TEXT, "Paketname eingeben");
        add(Word.NEW_PACKAGE_DIALOG_CONTENT_TEXT, "Paketname");
        ///Rename Package
        add(Word.RENAME_PACKAGE_DIALOG_TITLE, "Paket umbenennen");
        add(Word.RENAME_PACKAGE_DIALOG_HEADER_TEXT, "Neuen Paketnamen eingeben");
        add(Word.RENAME_PACKAGE_DIALOG_CONTENT_TEXT, "Paketname");
        ///Comment Package
        add(Word.COMMENT_PACKAGE_DIALOG_TITLE, "Paket kommentieren");
        add(Word.COMMENT_PACKAGE_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Package
        add(Word.DELETE_PACKAGE_DIALOG_TITLE, "Paket ${PACKAGE} löschen");
        add(Word.DELETE_PACKAGE_DIALOG_HEADER_TEXT, "Paket ${PACKAGE} wirklich löschen?");
        add(Word.DELETE_PACKAGE_DIALOG_CONTENT_TEXT, "Alle Inhalte in dem Paket ${PACKAGE} werden unwiederruflich gelöscht!");
        ///New Class
        add(Word.NEW_CLASS_DIALOG_TITLE, "Neue Klasse anlegen");
        add(Word.NEW_CLASS_DIALOG_HEADER_TEXT, "Klassenname eingeben");
        add(Word.NEW_CLASS_DIALOG_CONTENT_TEXT, "Klassenname");
        ///Rename Class
        add(Word.RENAME_CLASS_DIALOG_TITLE, "Klasse umbenennen");
        add(Word.RENAME_CLASS_DIALOG_HEADER_TEXT, "Neuen Klassennamen eingeben");
        add(Word.RENAME_CLASS_DIALOG_CONTENT_TEXT, "Klassenname");
        ///Comment Class
        add(Word.COMMENT_CLASS_DIALOG_TITLE, "Klasse kommentieren");
        add(Word.COMMENT_CLASS_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Class
        add(Word.DELETE_CLASS_DIALOG_TITLE, "Klasse ${CLASS} löschen");
        add(Word.DELETE_CLASS_DIALOG_HEADER_TEXT, "Klasse ${CLASS} wirklich löschen?");
        add(Word.DELETE_CLASS_DIALOG_CONTENT_TEXT, "Alle Inhalte der Klasse ${CLASS} werden unwiederruflich gelöscht!");
        ///New Function
        add(Word.NEW_FUNCTION_DIALOG_TITLE, "Neue Funktion anlegen");
        add(Word.NEW_FUNCTION_DIALOG_HEADER_TEXT, "Funktionsnamen eingeben");
        add(Word.NEW_FUNCTION_DIALOG_CONTENT_TEXT, "Funktionsname");
        ///Rename Function
        add(Word.RENAME_FUNCTION_DIALOG_TITLE, "Funktion umbenennen");
        add(Word.RENAME_FUNCTION_DIALOG_HEADER_TEXT, "Neuen Funktionsnamen eingeben");
        add(Word.RENAME_FUNCTION_DIALOG_CONTENT_TEXT, "Funktionsname");
        ///Comment Function
        add(Word.COMMENT_FUNCTION_DIALOG_TITLE, "Funktion kommentieren");
        add(Word.COMMENT_FUNCTION_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Function
        add(Word.DELETE_FUNCTION_DIALOG_TITLE, "Funktion ${FUNCTION} löschen");
        add(Word.DELETE_FUNCTION_DIALOG_HEADER_TEXT, "Funktion ${FUNCTION} wirklich löschen?");
        add(Word.DELETE_FUNCTION_DIALOG_CONTENT_TEXT, "Alle Inhalte der Funktion ${FUNCTION} werden unwiederruflich gelöscht!");
        ///New Parameter
        add(Word.NEW_PARAMETER_VALUE_DIALOG_TITLE, "Neuen Parameter anlegen");
        add(Word.NEW_PARAMETER_VALUE_DIALOG_HEADER_TEXT, "Parametername eingeben");
        add(Word.NEW_PARAMETER_VALUE_DIALOG_CONTENT_TEXT, "Parametername");
        ///Rename Parameter
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_TITLE, "Parameter umbenennen");
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_HEADER_TEXT, "Neuen Parameternamen eingeben");
        add(Word.RENAME_PARAMETER_VALUE_DIALOG_CONTENT_TEXT, "Parametername");
        ///Comment Parameter
        add(Word.COMMENT_PARAMETER_DIALOG_TITLE, "Parameter kommentieren");
        add(Word.COMMENT_PARAMETER_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Parameter
        add(Word.DELETE_PARAMETER_DIALOG_TITLE, "Parameter ${PARAMETER} löschen");
        add(Word.DELETE_PARAMETER_DIALOG_HEADER_TEXT, "Parameter ${PARAMETER} wirklich löschen?");
        add(Word.DELETE_PARAMETER_DIALOG_CONTENT_TEXT, "Der Parameter ${PARAMETER} wird unwiederruflich gelöscht!");
        ///New Variable
        add(Word.NEW_VARIABLE_DIALOG_TITLE, "Neue Variable anlegen");
        add(Word.NEW_VARIABLE_DIALOG_HEADER_TEXT, "Variablennamen eingeben");
        add(Word.NEW_VARIABLE_DIALOG_CONTENT_TEXT, "Variablenname");
        ///Rename Variable
        add(Word.RENAME_VARIABLE_DIALOG_TITLE, "Variable umbenennen");
        add(Word.RENAME_VARIABLE_DIALOG_HEADER_TEXT, "Neuen Variablennamen eingeben");
        add(Word.RENAME_VARIABLE_DIALOG_CONTENT_TEXT, "Variablenname");
        ///Comment Variable
        add(Word.COMMENT_VARIABLE_DIALOG_TITLE, "Variable kommentieren");
        add(Word.COMMENT_VARIABLE_DIALOG_HEADER_TEXT, "Kommentare eingeben");
        ///Delete Variable
        add(Word.DELETE_VARIABLE_DIALOG_TITLE, "Variable ${VARIABLE} löschen");
        add(Word.DELETE_VARIABLE_DIALOG_HEADER_TEXT, "Variable ${VARIABLE} wirklich löschen?");
        add(Word.DELETE_VARIABLE_DIALOG_CONTENT_TEXT, "Die Variable ${VARIABLE} wird unwiederruflich gelöscht!");
        ///Corrupt project
        add(Word.PROJECT_CORRUPT_DIALOG_TITLE, "Fehlerhaftes Projekt");
        add(Word.PROJECT_CORRUPT_DIALOG_HEADER_TEXT, "Projekt ist fehlerhaft");
        add(Word.PROJECT_CORRUPT_DIALOG_CONTENT_TEXT, "Das Projekt konnte aufgrund eines Fehlers in der Projektdatei nicht geöffnet werden. Siehe den Stacktrace für Details.");
        ///Class in Use
        add(Word.CLASS_IN_USE_DIALOG_TITLE, "Klasse noch in Benutzung");
        add(Word.CLASS_IN_USE_DIALOG_HEADER_TEXT, "Die Klasse ist noch in Benutzung");
        add(Word.CLASS_IN_USE_DIALOG_CONTENT_TEXT, "Die Klasse konnte aufgrund folgender Abhängigkeiten nicht gelöscht werden:\n${LIST}");
        ///New Text value
        add(Word.NEW_TEXT_VALUE_DIALOG_TITLE, "Neuer Text");
        add(Word.NEW_TEXT_VALUE_DIALOG_HEADER_TEXT, "Bitte Text eingeben");
        add(Word.NEW_TEXT_VALUE_DIALOG_CONTENT_TEXT, "Text");
        ///New Number value
        add(Word.NEW_NUMBER_VALUE_DIALOG_TITLE, "Neue Nummer");
        add(Word.NEW_NUMBER_VALUE_DIALOG_HEADER_TEXT, "Bitte Nummer eingeben");
        add(Word.NEW_NUMBER_VALUE_DIALOG_CONTENT_TEXT, "Nummer");
        ///New Boolean value
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_TITLE, "Neuer Boolean");
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_HEADER_TEXT, "Bitte Boolean auswählen");
        add(Word.NEW_BOOLEAN_VALUE_DIALOG_CONTENT_TEXT, "Boolean");
        ///New Object value
        add(Word.NEW_OBJECT_VALUE_DIALOG_TITLE, "Neues Objekt");
        add(Word.NEW_OBJECT_VALUE_DIALOG_HEADER_TEXT, "Bitte Typ auswählen");
        add(Word.NEW_OBJECT_VALUE_DIALOG_CONTENT_TEXT, "Typ");
        ///Update available
        add(Word.NEW_UPDATE_AVAILABLE_DIALOG_TITLE, "Update verfügbar");
        add(Word.NEW_UPDATE_AVAILABLE_DIALOG_HEADER_TEXT, "Es ist endlich ein neues Update verfügbar \uD83D\uDE03");
        add(Word.NEW_UPDATE_AVAILABLE_DIALOG_CONTENT_TEXT, "Bitte installieren Sie neue Updates, um Fehler zu beheben und neue Funktionen zu genießen.");
        ///No Update available
        add(Word.NO_NEW_UPDATE_AVAILABLE_DIALOG_TITLE, "Kein neues Update verfügbar");
        add(Word.NO_NEW_UPDATE_AVAILABLE_DIALOG_HEADER_TEXT, "Herzlichen Glückwunsch, Sie besitzen bereits die aktuellste Version von Casey \uD83D\uDE03");
        //ValueSelectionPopup
        add(Word.VALUE_SELECTION_POPUP_NEW_VALUES, "Neue Werte");
        add(Word.VALUE_SELECTION_POPUP_NEW_PRIMITIVE_VALUES, "Neue Primitivwerte");
        add(Word.VALUE_SELECTION_POPUP_NEW_OBJECT_VALUES, "Neue Objektwerte");
        add(Word.VALUE_SELECTION_POPUP_VALUES_EXTENSION, "-Werte");
        add(Word.VALUE_SELECTION_POPUP_EXISTING_VALUES, "Bestehende Werte");
        add(Word.VALUE_SELECTION_POPUP_STATIC_VALUES, "Allgemeine Werte");
        add(Word.VALUE_SELECTION_POPUP_CLASS_VALUES, "Werte dieser Klasse");
        add(Word.VALUE_SELECTION_POPUP_FUNCTION_VALUES, "Werte dieser Funktion");
        add(Word.VALUE_SELECTION_POPUP_TITLE, "Wertauswahl");
        //TypeSelectionField
        add(Word.TYPE_SELECTION_FIELD_SELECT_TYPE, "Typ auswählen...");
        add(Word.TYPE_SELECTION_POPUP_NO_TYPES, "Keine Typen erlaubt");
        //TypeSelectionPopup
        add(Word.TYPE_SELECTION_POPUP_PRIMITIVE_TYPES, "Primitive Typen");
        add(Word.TYPE_SELECTION_POPUP_OBJECT_TYPES, "Objekttypen");
        add(Word.TYPE_SELECTION_POPUP_TITLE, "Typauswahl");
        //VariableSelectionField
        add(Word.VARIABLE_SELECTION_FIELD_SELECT_VARIABLE, "Variable auswählen...");
        add(Word.VARIABLE_SELECTION_POPUP_NO_TYPES, "Keine Typen erlaubt");
        //VariableSelectionPopup
        add(Word.VARIABLE_SELECTION_POPUP_TITLE, "Variablen- und Parameterauswahl");
        //Jar-Error Window
        add(Word.EXECUTOR_ERROR_TITLE, "Java Ausführungsfehler");
        add(Word.EXECUTOR_ERROR_HEADER_TEXT, "Fehler während des Ausführens einer Java-Datei");
        add(Word.EXECUTOR_ERROR_CONTENT_TEXT, "Leider ist ein Fehler während des Ausführens einer Java-Datei aufgetreten");
        add(Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_TITLE, "Parameter");
        add(Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_HEADER_TEXT, "Bitte ergänzen Sie die fehlenden Parameterwerte");
        add(Word.COMPILER_ENTER_PARAMETER_VALUES_DIALOG_CONTENT_TEXT, "Parameterwerte");
        //Run Window
        add(Word.INPUT_OUTPUT_WINDOW_PROGRAM_ENDED, "Programm beendet. Zum Schließen eine beliebige Taste drücken...");
        //General
        add(Word.FUNCTION, "Funktion");
        add(Word.PARAMETER, "Parameter");
        add(Word.CLASS, "Klasse");
        add(Word.PROJECT, "Projekt");
        add(Word.VARIABLE, "Variable");
        add(Word.PACKAGE, "Paket");
        add(Word.WELCOME, "Willkommen");
        add(Word.NUMBER, "Nummer");
        add(Word.BOOLEAN, "Boolean");
        add(Word.TEXT, "Text");
        add(Word.OBJECT, "Objekt");
        add(Word.NUMBER_CREATION, "Nummer erzeugen");
        add(Word.BOOLEAN_CREATION, "Boolean erzeugen");
        add(Word.TEXT_CREATION, "Text erzeugen");
        add(Word.OBJECT_CREATION, "Objekt erzeugen");
        add(Word.ADD, "Addieren");
        add(Word.SUBTRACT, "Subtrahieren");
        add(Word.MULTIPLY, "Multiplizieren");
        add(Word.DIVIDE, "Teilen");
        add(Word.MODULO, "Modulo");
        add(Word.EQUALS, "Gleicht");
        add(Word.NOT_EQUALS, "Gleicht nicht");
        add(Word.GREATER_THAN, "Größer als");
        add(Word.LESS_THAN, "Kleiner als");
        add(Word.GREATER_OR_EQUAL_THAN, "Größer oder gleich als");
        add(Word.LESS_OR_EQUAL_THAN, "Kleiner oder gleich als");
        add(Word.AND, "Und");
        add(Word.OR, "Oder");
        add(Word.NOT, "Nicht");
        add(Word.CONCAT, "Zusammenfügen");
        add(Word.LENGTH, "Länge");
        add(Word.DELETE, "Löschen");
        add(Word.VOID, "Nichts");
        add(Word.HELP, "Hilfe");
    }
    
    public German() {
        super(Locale.GERMAN);
    }
}
