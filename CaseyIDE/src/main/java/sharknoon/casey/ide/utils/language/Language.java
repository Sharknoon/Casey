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
package sharknoon.casey.ide.utils.language;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Labeled;
import sharknoon.casey.ide.utils.collection.Collections;
import sharknoon.casey.ide.utils.language.lanugages.English;
import sharknoon.casey.ide.utils.language.lanugages.German;
import sharknoon.casey.ide.utils.settings.Props;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author frank
 */

/**
 * This class is used to manage the Language dependent parts of the programm.
 * This class is also the main point for storing the references to the
 * languages.
 *
 * @author frank
 */
public abstract class Language {
    
    //The database key for storing the current language
    private static final String LANGUAGE_PROPERTY_KEY = "language";
    //A list of all languages
    private static final Map<Locale, Language> LANGUAGES = new HashMap<>();
    //A list of all controls
    private static final Map<Labeled, Word> CONTROLS = new HashMap<>();
    //A list of all customs
    private static final Set<Custom> CUSTOMS = new HashSet<>();
    private static final Language ENGLISH = new English();
    private static final Language GERMAN = new German();
    //User-specific Part--------------------------------------------------------
    //The current selected language
    private static ReadOnlyObjectWrapper<Language> currentLanguage = new ReadOnlyObjectWrapper<>() {
        @Override
        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
            if (get() == null) {
                set(ENGLISH);
            } else {
                Props.set(LANGUAGE_PROPERTY_KEY, get().getLanguageTag());
                Locale.setDefault(get().getLocale());
                refreshAllControls();
                refreshAllCustoms();
            }
        }
    };
    //ADD NEW LANGUAGES HERE
    
    //Static Part---------------------------------------------------------------
    static {
        Optional<String> languageTagFromDB = Props.get(LANGUAGE_PROPERTY_KEY).join();
        if (!languageTagFromDB.isPresent()) {//If no language has been set
            String languageTagFromSystem = System.getProperty("user.language");
            Locale localeFromSystem = Locale.forLanguageTag(languageTagFromSystem);
            currentLanguage.set(LANGUAGES.get(localeFromSystem));
            
        } else {//If a language has already been set, either manually or through a previous run
            Locale localeFromDB = Locale.forLanguageTag(languageTagFromDB.get());
            currentLanguage.set(LANGUAGES.get(localeFromDB));
        }
    }
    
    /**
     * Returns all Languages in a Map ordered by the Java Locale
     *
     * @return Returns all languages in a Map
     */
    public static Map<Locale, Language> getAllLanguages() {
        return Collections.silentUnmodifiableMap(LANGUAGES);
    }
    
    /**
     * Returns the Language for the Locale or English if this language isnt
     * available
     *
     * @param locale
     * @return
     */
    public static Language forLocale(Locale locale) {
        return LANGUAGES.getOrDefault(locale, ENGLISH);
    }
    
    /**
     * Returns the requested Word in the Requested Language
     *
     * @param word     The word to be returned
     * @param language Language
     * @return
     */
    public static final String get(Word word, Language language) {
        return language.words.get(word);
    }
    
    /**
     * Returns a word in the language og the user User
     * {@link #set(Word, Labeled)}
     * to set a specific Components Text<br>
     * It is deprecated because changing the Language doesnt automatically
     * change
     *
     * @param word the word to be returned
     * @return The requested word in the user language
     */
    public static final String get(Word word) {
        return currentLanguage.get().words.getOrDefault(word, word.name());
    }
    
    /**
     * Automatically sets the value of the labeled Control, this is useful, if
     * the user changes the language, all components are changed
     *
     * @param word           the word to be returned
     * @param labeledControl
     */
    public static final void set(Word word, Labeled labeledControl) {
        CONTROLS.put(labeledControl, word);
        refreshControl(labeledControl, word);
    }
    
    /**
     * Removes the automatic chanceing of the labeled Control, this is useful,
     * if you want to bind another value to the labeled Control
     *
     * @param labeledControl
     */
    public static final void unset(Labeled labeledControl) {
        CONTROLS.remove(labeledControl);
    }
    
    /**
     * Sets a custom text to a custom object<br><br>
     * <p>
     * If the User changes the Language:<br><br>
     * 1. The word is being translated<br>
     * 2. The translated word enters the ValueSetter which sets the
     * value<br><br>
     * <p>
     * Example:<br>
     * <pre>
     * {@code
     * Textfield field = ... ;
     * Language.setCustom(Word.HELLO, v -> field.setPlaceholder(v));
     * }
     * </pre> In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you just want to use
     * the Standard field.setVariable(...) you can use the
     * {@link #set(Word, Labeled)} for that).<br>
     * You set the translated string as placeholder for your textfield as
     * example.
     *
     * @param word   the word to be returned
     * @param setter
     */
    public static void setCustom(Word word, ValueSetter setter) {
        setCustom(word, null, setter);
    }
    
    /**
     * Sets a custom text to a custom object<br><br>
     * <p>
     * If the User changes the Language:<br><br>
     * 1. A word is being supplied<br>
     * 2. The supplied word enters the ValueSetter which sets the value<br><br>
     * <p>
     * In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you just want to use
     * the Standard field.setVariable(...) you can use the
     * {@link #set(Word, Labeled)} for that).<br>
     * You set the modified string as placeholder for your textfield as example.
     *
     * @param supplier
     * @param setter
     */
    public static void setCustom(Supplier<String> supplier, ValueSetter setter) {
        setCustom(null, v -> supplier.get(), setter);
    }
    
    /**
     * Sets a custom text to a custom object<br><br>
     * <p>
     * If the User changes the Language:<br><br>
     * 1. The word is being translated<br>
     * 2. The translated word enters the Stringmodifier<br>
     * 3. The potentially modified word enters the ValueSetter which sets the
     * value<br><br>
     * <p>
     * The modifier can be null if you dont want to change the word.<br><br>
     * <p>
     * Example:<br>
     * <pre>
     * {@code
     * Textfield field = ... ;
     * Language.setCustom(Word.TODAY_IS, s -> s + LocalDate.now().toString(), v -> field.setPlaceholder(v));
     * }
     * </pre> In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you dont want to
     * modify the word AND just want to use the Standard field.setVariable(...) you
     * can use the {@link #set(Word, Labeled)} for that).<br>
     * You do this by modifying the string s by appending the local date as
     * example.<br>
     * In the end you set the modified string as placeholder for your textfield
     * as example.
     *
     * @param word     the word to be returned
     * @param modifier
     * @param setter
     */
    public static void setCustom(Word word, StringModifier modifier, ValueSetter setter) {
        Custom custom = new Custom(word, modifier, setter);
        CUSTOMS.add(custom);
        refreshCustom(custom);
    }
    
    public static void changeLanguage(Language language) {
        currentLanguage.set(language == null ? currentLanguage.get() : language);
    }
    
    public static void addLanguageChangeListener(Consumer<Language> languageListener) {
        currentLanguage.addListener((observable, oldValue, newValue) -> languageListener.accept(newValue));
    }
    
    public static Language getLanguage() {
        return currentLanguage.get();
    }
    
    public static ReadOnlyObjectProperty<Language> languageProperty() {
        return currentLanguage.getReadOnlyProperty();
    }
    
    private static void refreshAllCustoms() {
        CUSTOMS.forEach(Language::refreshCustom);
    }
    
    private static void refreshAllControls() {
        CONTROLS.forEach(Language::refreshControl);
    }
    
    private static void refreshCustom(Custom custom) {
        Word word = custom.word;
        StringModifier modifier = custom.modifier;
        ValueSetter setter = custom.setter;
        String translated = word == null ? "" : currentLanguage.get().words.getOrDefault(word, word.name());
        translated = modifier != null ? modifier.modifyString(translated) : translated;
        if (setter != null) {
            setter.setValue(translated);
        }
    }
    
    private static void refreshControl(Labeled labeledControl, Word word) {
        String value = word == null ? "" : currentLanguage.get().words.getOrDefault(word, word.name());
        if (labeledControl != null) {
            labeledControl.setText(value);
        }
    }
    
    //Part to be inherited by Language classes----------------------------------
    private final Map<Word, String> words = new HashMap<>();
    private final Locale locale;
    
    protected Language(Locale locale) {
        this.locale = locale;
        LANGUAGES.put(locale, this);
    }
    
    protected final void add(Word word, String translation) {
        words.put(word, translation);
    }
    
    private String getLanguageTag() {
        return locale.toLanguageTag();
    }
    
    public Locale getLocale() {
        return locale;
    }
    
    @FunctionalInterface
    public interface StringModifier {
        
        String modifyString(String translatedWord);
    }
    
    @FunctionalInterface
    public interface ValueSetter {
        
        void setValue(String value);
    }
    
    private static class Custom {
        
        Word word;
        StringModifier modifier;
        ValueSetter setter;
        
        public Custom(Word word, StringModifier modifier, ValueSetter setter) {
            this.word = word;
            this.modifier = modifier;
            this.setter = setter;
        }
        
        @Override
        public int hashCode() {
            int hash = 3;
            hash = 71 * hash + Objects.hashCode(this.word);
            hash = 71 * hash + Objects.hashCode(this.modifier);
            hash = 71 * hash + Objects.hashCode(this.setter);
            return hash;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Custom other = (Custom) obj;
            if (this.word != other.word) {
                return false;
            }
            if (!Objects.equals(this.modifier, other.modifier)) {
                return false;
            }
            return Objects.equals(this.setter, other.setter);
        }
        
    }
    
}
