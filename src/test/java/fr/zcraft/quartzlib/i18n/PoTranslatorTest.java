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
import fr.zcraft.quartzlib.components.i18n.translators.gettext.GettextPOTranslator;
import fr.zcraft.quartzlib.tools.reflection.Reflection;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PoTranslatorTest {

    private final Translator translator;

    public PoTranslatorTest() throws IOException {
        translator = Translator.getInstance(Locale.FRANCE, TestsUtils.tempResource("i18n/fr_FR.po"));

        try {
            Reflection.call(translator, "load");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Assertions.fail("Unable to load translations");
        }
    }

    @Test
    @BeforeEach
    public void testTranslatorTypeFromFileName() {
        Assertions.assertEquals(GettextPOTranslator.class, translator.getClass(), "Translator instance badly loaded from file name");
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
        Assertions.assertEquals(1, ((int) translator.getPluralIndex(2)), "Bad index plural (count=2)");
        Assertions.assertEquals(1, ((int) translator.getPluralIndex(8)), "Bad index plural (count=8)");
    }

    @Test
    public void testLocale() {
        Assertions.assertEquals(Locale.FRANCE, translator.getLocale(), "Bad exposed locale");
    }

    @Test
    public void testBasicTranslations() {
        Assertions.assertEquals("Il n'y a pas de pain grillé ici...",
            translator.translate(null, "There are no toasts here ...", null, null), "Bad translation");
    }

    @Test
    public void testQuoteEscapement() {
        Assertions.assertEquals("Ce n'est qu'un \"toaster\"",
            translator.translate(null, "It's just a \"toaster\"", null, null), "Quotes badly escaped");
        Assertions.assertNull(translator.translate(null, "It's just a \\\"toaster\\\"", null, null), "Raw escaped quotes retrieved");
    }

    @Test
    public void testContexts() {
        Assertions.assertEquals("{gold}{bold}Cuit",
            translator.translate("sidebar", "{gold}{bold}Cooked", null, null), "Bad translation with context");
        Assertions.assertEquals("{red}{bold}♨ Grille-pain ♨",
            translator.translate("sidebar", "{red}{bold}♨ Toaster ♨", null, null), "Bad translation with context and UTF-8");
        Assertions.assertNull(translator.translate("", "  Toast #{0}", null, null), "Bad translation with empty context");
    }

    @Test
    public void testPlurals() {
        Assertions.assertEquals("Un pain ajouté.",
            translator.translate(null, "One toast added.", "{0} toasts added.", 1), "Bad translation with plural (singular)");
        Assertions.assertEquals("{0} pains ajoutés.",
            translator.translate(null, "One toast added.", "{0} toasts added.", 2), "Bad translation with plural (plural)");
    }

    @Test
    public void testUnknownTranslations() {
        Assertions.assertNull(
            translator.translate(null, "Unknown translation", null, null), "Non-null translation from unknown messageId, without context");
        Assertions.assertNull(
            translator.translate("context", "Unknown translation", null, null), "Non-null translation from unknown messageId, with context");
        Assertions.assertNull(
            translator.translate("", "Unknown translation", null, null), "Non-null translation from unknown messageId, with empty context");
        Assertions.assertNull(
            translator.translate(null, "Unknown translation", "Unknown translations", 0), "Non-null translation from unknown messageId, without context, with plural (singular)");
        Assertions.assertNull(
            translator.translate(null, "Unknown translation", "Unknown translations", 2), "Non-null translation from unknown messageId, without context, with plural (plural)");
        Assertions.assertNull(
            translator.translate("context", "Unknown translation", "Unknown translations", 0), "Non-null translation from unknown messageId, with context, with plural (singular)");
        Assertions.assertNull(
            translator.translate("context", "Unknown translation", "Unknown translations", 2), "Non-null translation from unknown messageId, with context, with plural (plural)");

        Assertions.assertNull(
            translator.translate(null, "{darkgreen}{bold}Cook", null, null), "Translation retrieved from bad context (null)");
        Assertions.assertNull(
            translator.translate("sidebar", "There are no toasts here ...", null, null), "Translation retrieved from bad context (non-null)");
        Assertions.assertNull(
            translator.translate("", "{blue}Toaster", null, null), "Translation retrieved from bad context (empty)");
        Assertions.assertNull(translator.translate("unknown-context", "{blue}Toaster", null, null), "Translation retrieved from bad context (unknown)");
    }
}
