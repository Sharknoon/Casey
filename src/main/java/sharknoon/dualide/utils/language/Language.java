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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javafx.scene.control.Labeled;
import sharknoon.dualide.utils.collection.Collections;
import sharknoon.dualide.utils.language.lanugages.English;
import sharknoon.dualide.utils.language.lanugages.German;
import sharknoon.dualide.utils.settings.IDEProps;

/**
 *
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

    //Static Part---------------------------------------------------------------
    private static final String LANGUAGE_PROPERTY_KEY = "language";
    private static final HashMap<Locale, Language> LANGUAGES = new HashMap<>();
    public static final Language ENGLISH = new English();
    public static final Language GERMAN = new German();
    //ADD NEW LANGUAGES HERE

    /**
     * Returns the requested Word in the Requested Language
     *
     * @param word The word to be returned
     * @param language Language
     * @return
     */
    public static final String get(Word word, Language language) {
        return language.words.get(word);
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

    protected String getLanguageTag() {
        return locale.toLanguageTag();
    }

    public Locale getLocale() {
        return locale;
    }

    //User-specific Part--------------------------------------------------------
    private static Language currentLanguage;

    static {
        Optional<String> languageFromPropertiesFile = IDEProps.get(LANGUAGE_PROPERTY_KEY);
        String languageTagFromSystem = System.getProperty("user.language");
        if (!languageFromPropertiesFile.isPresent()) {//If no language has been set
            currentLanguage = LANGUAGES.getOrDefault(Locale.forLanguageTag(languageTagFromSystem), ENGLISH);
            IDEProps.set(LANGUAGE_PROPERTY_KEY, currentLanguage.getLanguageTag());
        } else {//If a language has already been set, either manually or through a previous run
            currentLanguage = LANGUAGES.getOrDefault(Locale.forLanguageTag(languageFromPropertiesFile.get()), ENGLISH);
        }
    }

    /**
     * Returns a word in the language og the user User
     * {@link #set(dhbw.clippinggorilla.languages.Word, com.vaadin.ui.Component)}
     * to set a specific Components Text<br>
     * It is deprecated because changing the Language doesnt automatically
     * change
     *
     * @param word the word to be returned
     * @return The requested word in the user language
     */
    public static final String get(Word word) {
        return currentLanguage.words.getOrDefault(word, word.name());
    }

    private static final Map<Labeled, Word> CONTROLS = new HashMap<>();
    private static final Set<Custom> CUSTOMS = new HashSet<>();

    /**
     * Automatically sets the value of the Vaadin Component, this is useful, if
     * the user changes the language, all components are changed
     *
     * @param word the word to be returned
     * @param labeledControl
     */
    public static final void set(Word word, Labeled labeledControl) {
        CONTROLS.put(labeledControl, word);
        refreshControl(labeledControl, word);
    }

    /**
     * Sets a custom text to a custom object<br><br>
     *
     * If the User changes the Language:<br><br>
     * 1. The word is being translated<br>
     * 2. The translated word enters the ValueSetter which sets the
     * value<br><br>
     *
     * Example:<br>
     * <pre>
     * {@code
     * Textfield field = ... ;
     * Language.setCustom(Word.HELLO, v -> field.setPlaceholder(v));
     * }
     * </pre> In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you just want to use
     * the Standard field.setValue(...) you can use the
     * {@link #set(Word, Component)} for that).<br>
     * You set the translated string as placeholder for your textfield as
     * example.
     *
     * @param word the word to be returned
     * @param setter
     */
    public static void setCustom(Word word, ValueSetter setter) {
        setCustom(word, null, setter);
    }

    /**
     * Sets a custom text to a custom object<br><br>
     *
     * If the User changes the Language:<br><br>
     * 1. A empty Word enteres the Stringmodifier<br>
     * 2. The potentially modified word enters the ValueSetter which sets the
     * value<br><br>
     *
     * Example:<br>
     * <pre>
     * {@code
     * Textfield field = ... ;
     * Language.setCustom(s -> Language.getLanguage().toString(), v -> field.setPlaceholder(v));
     * }
     * </pre> In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you just want to use
     * the Standard field.setValue(...) you can use the
     * {@link #set(Word, Component)} for that).<br>
     * You set the modified string as placeholder for your textfield as example.
     *
     * @param modifier
     * @param setter
     */
    public static void setCustom(StringModifier modifier, ValueSetter setter) {
        setCustom(null, modifier, setter);
    }

    /**
     * Sets a custom text to a custom object<br><br>
     *
     * If the User changes the Language:<br><br>
     * 1. The word is being translated<br>
     * 2. The translated word enters the Stringmodifier<br>
     * 3. The potentially modified word enters the ValueSetter which sets the
     * value<br><br>
     *
     * The modifier can be null if you dont want to change the word.<br><br>
     *
     * Example:<br>
     * <pre>
     * {@code
     * Textfield field = ... ;
     * Language.setCustom(Word.TODAY_IS, s -> s + LocalDate.now().toString(), v -> field.setPlaceholder(v));
     * }
     * </pre> In this Example you have a Textfield.<br>
     * You want to set a specific Text as Placeholder (If you dont want to
     * modify the word AND just want to use the Standard field.setValue(...) you
     * can use the {@link #set(Word, Component)} for that).<br>
     * You do this by modifying the string s by appending the local date as
     * example.<br>
     * In the end you set the modified string as placeholder for your textfield
     * as example.
     *
     * @param word the word to be returned
     * @param modifier
     * @param setter
     */
    public static void setCustom(Word word, StringModifier modifier, ValueSetter setter) {
        Custom custom = new Custom(word, modifier, setter);
        CUSTOMS.add(custom);
        refreshCustom(custom);
    }

    public static void changeLanguage(Language language) {
        currentLanguage = language == null ? currentLanguage : language;
        IDEProps.set(LANGUAGE_PROPERTY_KEY, currentLanguage.getLanguageTag());
        refreshAllControls();
        refreshAllCustoms();
    }

    public static Language getLanguage() {
        return currentLanguage;
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
        String translated = word == null ? "" : currentLanguage.words.getOrDefault(word, word.name());
        translated = modifier != null ? modifier.modifyString(translated) : translated;
        if (setter != null) {
            setter.setValue(translated);
        }
    }

    private static void refreshControl(Labeled labeledControl, Word word) {
        String value = word == null ? "" : currentLanguage.words.getOrDefault(word, word.name());
        if (labeledControl != null) {
            labeledControl.setText(value);//Evtl Platform.runLater(...)
        }
    }

    @FunctionalInterface
    public interface StringModifier {

        public String modifyString(String translatedWord);
    }

    @FunctionalInterface
    public interface ValueSetter {

        public void setValue(String value);
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
