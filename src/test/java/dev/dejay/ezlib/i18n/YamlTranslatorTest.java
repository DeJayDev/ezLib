/*
 * Copyright or © or Copr. QuartzLib contributors (2015 - 2020)
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */

package dev.dejay.ezlib.i18n;

import dev.dejay.ezlib.components.i18n.translators.Translator;
import dev.dejay.ezlib.components.i18n.translators.yaml.YAMLTranslator;
import dev.dejay.ezlib.tools.reflection.Reflection;
import dev.dejay.ezlib.TestsUtils;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class YamlTranslatorTest {

    private final Translator translator;

    public YamlTranslatorTest() throws IOException {
        translator = YAMLTranslator.getInstance(Locale.FRANCE, TestsUtils.tempResource("i18n/fr_FR.yml"));

        try {
            Reflection.call(translator, "load");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("Unable to load translations");
        }
    }

    @Test
    @BeforeEach
    public void testTranslatorTypeFromFileName() {
        Assertions.assertEquals(YAMLTranslator.class, translator.getClass(), "Translator instance badly loaded from file name");
    }

    @Test
    public void testAuthors() {
        Assertions.assertEquals("Amaury Carrade", translator.getLastTranslator(), "Last translator badly retrieved");
        Assertions.assertEquals("Amaury Carrade", translator.getTranslationTeam(), "Translation team badly retrieved");
        Assertions.assertEquals("AmauryCarrade", translator.getReportErrorsTo(), "ReportErrorsTo badly retrieved");
    }

    @Test
    public void testPluralIndex() {
        Assertions.assertEquals(0, ((int) translator.getPluralIndex(0)), "Bad index plural (count=0)");
        Assertions.assertEquals(0, ((int) translator.getPluralIndex(1)), "Bad index plural (count=1)");
        Assertions.assertEquals(0, ((int) translator.getPluralIndex(2)), "Bad index plural (count=2)");
        Assertions.assertEquals(0, ((int) translator.getPluralIndex(8)), "Bad index plural (count=8)");
    }

    @Test
    public void testLocale() {
        Assertions.assertEquals(Locale.FRANCE, translator.getLocale(), "Bad exposed locale");
    }

    @Test
    public void testBasicTranslations() {
        Assertions.assertEquals("Hi there", translator.translate(null, "greetings.hi", null, null), "Bad translation");
        Assertions.assertEquals("How are you?", translator.translate(null, "greetings.how", null, null), "Bad translation");
        Assertions.assertEquals("♨ Toaster! ♨", translator.translate(null, "toast", null, null), "Bad translation with UTF-8");
    }

    @Test
    public void testContexts() {
        Assertions.assertEquals("Hi!",
            translator.translate("other_context", "greetings.hi", null, null), "Bad translation with context");
        Assertions.assertEquals("♨ Toaster ♨",
            translator.translate("other_context", "toast", null, null), "Bad translation with context and UTF-8");
        Assertions.assertEquals("♨ Toaster! ♨",
            translator.translate(null, "toast", null, null), "Bad translation with same in another context and UTF-8");
    }

    @Test
    public void testPlurals() {
        Assertions.assertEquals("How are you?",
            translator.translate(null, "greetings.how", "greetings.hi", 1), "Bad translation with plural (singular)");
        Assertions.assertEquals("How are you?",
            translator.translate(null, "greetings.how", "greetings.hi", 2), "Bad translation with plural (plural, should be ignored)");
    }

    @Test
    public void testUnknownTranslations() {
        Assertions.assertNull(
            translator.translate(null, "unknown.translation", null, null), "Non-null translation from unknown messageId, without context");
        Assertions.assertNull(
            translator.translate("context", "unknown.translation", null, null), "Non-null translation from unknown messageId, with context");
        Assertions.assertNull(
            translator.translate("", "unknown.translation", null, null), "Non-null translation from unknown messageId, with empty context");
        Assertions.assertNull(
            translator.translate(null, "unknown.translation", "unknown.translations", 0),
            "Non-null translation from unknown messageId, without context, with plural (singular)");
        Assertions.assertNull(
            translator.translate(null, "unknown.translation", "unknown.translations", 2),
            "Non-null translation from unknown messageId, without context, with plural (plural, should be ignored)");
        Assertions.assertNull(
            translator.translate("context", "unknown.translation", "unknown.translations", 0),
            "Non-null translation from unknown messageId, with context, with plural (singular)");
        Assertions.assertNull(
            translator.translate("context", "unknown.translation", "unknown.translations", 2),
            "Non-null translation from unknown messageId, with context, with plural (plural, should be ignored)");
    }
}
