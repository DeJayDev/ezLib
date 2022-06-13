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

package fr.zcraft.quartzlib.i18n;

import fr.zcraft.quartzlib.TestsUtils;
import fr.zcraft.quartzlib.components.i18n.translators.Translator;
import fr.zcraft.quartzlib.components.i18n.translators.properties.PropertiesTranslator;
import fr.zcraft.quartzlib.tools.reflection.Reflection;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PropertiesTranslatorTest {

    private final Translator translator;

    public PropertiesTranslatorTest() throws IOException {
        translator = Translator.getInstance(Locale.FRANCE, TestsUtils.tempResource("i18n/fr_FR.properties"));

        try {
            Reflection.call(translator, "load");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("Unable to load translations");
        }
    }

    @Test
    @BeforeEach
    public void testTranslatorTypeFromFileName() {
        Assertions.assertEquals(PropertiesTranslator.class, translator.getClass(), "Translator instance badly loaded from file name");
    }

    @Test
    public void testAuthors() {
        Assertions.assertEquals("Amaury Carrade", translator.getLastTranslator(), "Last translator badly retrieved");
        Assertions.assertEquals("Amaury Carrade", translator.getTranslationTeam(), "Translation team badly retrieved");
        Assertions.assertEquals("AmauryCarrade", translator.getReportErrorsTo(), "ReportErrorsTo badly retrieved");
    }

    @Test
    public void testAuthorsWithoutMeta() throws IOException {
        PropertiesTranslator noMetaTranslator =
            new PropertiesTranslator(Locale.UK, TestsUtils.tempResource("i18n/en_GB.no-meta.properties"));

        try {
            Reflection.call(noMetaTranslator, "load");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("Unable to load translations");
        }

        Assertions.assertNull(noMetaTranslator.getLastTranslator(), "Last translator retrieved in no-meta mode");
        Assertions.assertNull(noMetaTranslator.getTranslationTeam(), "Translation team retrieved in no-meta mode");
        Assertions.assertNull(noMetaTranslator.getReportErrorsTo(), "ReportErrorsTo retrieved in no-meta mode");
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
        Assertions.assertEquals("{darkgreen}{bold}Cuisinier",
            translator.translate(null, "sidebar.cook", null, null), "Bad translation");
        Assertions.assertEquals("{yellow}{bold}Dans le toaster",
            translator.translate(null, "sidebar.inside-the-toaster", null, null), "Bad translation with spaces");
        Assertions.assertEquals("{red}{bold}♨ Toaster ♨",
            translator.translate(null, "sidebar.toaster-cooking", null, null), "Bad translation with UTF-8");
    }

    @Test
    public void testContexts() {
        Assertions.assertEquals("{gold}{bold}Cuit",
            translator.translate("context", "sidebar.cooked", null, null), "Bad translation: context not ignored");
        Assertions.assertEquals("{yellow}{bold}Dans le toaster",
            translator.translate("context", "sidebar.inside-the-toaster", null, null), "Bad translation with spaces: context not ignored");
        Assertions.assertEquals("{red}{bold}♨ Toaster ♨",
            translator.translate("context", "sidebar.toaster-cooking", null, null), "Bad translation with UTF-8: context not ignored");
    }

    @Test
    public void testPlurals() {
        Assertions.assertEquals("Un toast ajouté.",
            translator.translate(null, "one-toast-added", "toasts-added", 1), "Bad translation with plural (singular)");
        Assertions.assertEquals("Un toast ajouté.",
            translator.translate(null, "one-toast-added", "toasts-added", 2), "Bad translation with plural (plural, should be ignored)");
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
