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

import dev.dejay.ezlib.TestsUtils;
import dev.dejay.ezlib.components.i18n.translators.Translation;
import dev.dejay.ezlib.components.i18n.translators.gettext.POFile;
import java.io.InputStreamReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PoParserTest {
    private final POFile po = new POFile(new InputStreamReader(TestsUtils.getResource("i18n/fr_FR.po")));

    @Test
    @BeforeEach
    public void testPoParsing() {
        try {
            po.parse();
        } catch (POFile.CannotParsePOException e) {
            Assertions.fail("PO file parsing throws exception for a valid PO file");
        }
    }

    @Test
    public void testAuthors() {
        Assertions.assertEquals("Amaury Carrade", po.getLastTranslator(), "Last translator badly retrieved");
        Assertions.assertEquals("Amaury Carrade", po.getTranslationTeam(), "Translation team badly retrieved");
        Assertions.assertEquals("AmauryCarrade", po.getReportErrorsTo(), "ReportErrorsTo badly retrieved");
    }

    @Test
    public void testPlurals() {
        Assertions.assertEquals(2, (int) po.getPluralCount(), "Bad plural count");
        Assertions.assertEquals("n>1", po.getPluralFormScript(), "Plural script badly retrieved");
    }

    @Test
    public void testTranslationsCount() {
        Assertions.assertEquals(po.getTranslations().size(), 20, "Translations from PO file missing or are too many");
    }

    @Test
    public void testTranslations() {
        for (Translation translation : po.getTranslations()) {
            switch (translation.getOriginal()) {
                case "{darkgreen}{bold}Cook" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for single translation");
                    Assertions.assertEquals("{darkgreen}{bold}Cuistot", translation.getTranslations().get(0), "Bad translation");
                    Assertions.assertEquals("sidebar", translation.getContext(), "Bad context");
                }
                case "{red}{bold}♨ Toaster ♨" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for single translation with UTF-8");
                    Assertions.assertEquals("{red}{bold}♨ Grille-pain ♨", translation.getTranslations().get(0), "Bad translation with UTF-8");
                    Assertions.assertEquals("sidebar", translation.getContext(), "Bad context with UTF-8 messageId");
                }
                case "One toast added." -> {
                    Assertions.assertEquals(2, translation.getTranslations().size(), "Bad translations count for translation with plural");
                    Assertions.assertEquals("{0} toasts added.", translation.getOriginalPlural(), "Bad extracted plural messageId");
                    Assertions.assertEquals("Un pain ajouté.", translation.getTranslations().get(0), "Bad singular translation");
                    Assertions.assertEquals("{0} pains ajoutés.", translation.getTranslations().get(1), "Bad plural translation");
                    Assertions.assertNull(translation.getContext(), "Bad null context with plurals");
                }
                case "There are no toasts here ..." -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for single translation without context");
                    Assertions.assertEquals("Il n'y a pas de pain grillé ici...", translation.getTranslations().get(0), "Bad translation without context");
                    Assertions.assertNull(translation.getContext(), "Bad null context");
                }
                case "  Toast #{0}" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for single translation with empty context");
                    Assertions.assertEquals("  Pain grillé no. {0}", translation.getTranslations().get(0), "Bad translation with empty context");
                    Assertions.assertNull(translation.getContext(), "Bad empty context");
                }
                case "It's just a \"toaster\"" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count with escaped quotes");
                    Assertions.assertEquals("Ce n'est qu'un \"toaster\"", translation.getTranslations().get(0), "Bad translation with escaped quotes");
                    Assertions.assertNotEquals("Ce n'est qu'un \\\"toaster\\\"", translation.getTranslations().get(0), "Translation retrieved with raw escaped quotes");
                }
                case "It's just a \\\"toaster\\\"" ->
                    Assertions.fail("Translation retrieved from raw escaped quotes");
                case "Multi-lines message" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for multi-lines messages");
                    Assertions.assertEquals("Message multi-ligne", translation.getTranslations().get(0), "Bad translations for multi-lines messages");
                }
                case "Multi-lines message with trailing space" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(), "Bad translations count for multi-lines messages");
                    Assertions.assertEquals("Message multi-ligne avec une espace séparatrice explicite",
                        translation.getTranslations().get(0), "Bad translations for multi-lines messages");
                }
                case "Multi-lines message  with multiple trailing spaces" -> {
                    Assertions.assertEquals(1, translation.getTranslations().size(),
                        "Bad translations count for multi-lines messages with multiple trailing spaces");
                    Assertions.assertEquals("Bad translations for multi-lines messages with multiple trailing spaces",
                        "Message  multi-ligne avec plusieurs espaces séparatrices explicites",
                        translation.getTranslations().get(0));
                    Assertions.assertNotEquals("Message multi-ligne avec plusieurs espaces séparatrices explicites", translation.getTranslations().get(0), "Bad translations for multi-lines messages with multiple trailing spaces: trailing spaces skipped");
                }
                case "Multi-lines message with multiple trailing spaces" ->
                    Assertions.fail("Bad translations for multi-lines messages with multiple trailing spaces: "
                        + "trailing spaces skipped in messageId");
                case "Multi-lines message  with trailing space" -> Assertions.fail(
                    "Bad translations for multi-lines messages with one trailing spaces: "
                        + "trailing spaces duplicated in messageId");
                case " {gray}It will shrink by one block every {0} second(s) until {1} blocks in diameter." ->
                    Assertions.fail(
                        "Bad translation for multi-line messages with empty lines: "
                            + "a trailing space was incorrectly added");
                case "{gray}When clicked, a sign will open; write the name of the team inside." ->
                    Assertions.fail("Translation with commented out msgid and msgstr retrieved");
                default -> {
                }
            }
        }
    }
}
