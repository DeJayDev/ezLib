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

package dev.dejay.ezlib.components.rawtext;

import dev.dejay.ezlib.tools.text.ChatColorParser;
import dev.dejay.ezlib.tools.text.ChatColoredString;
import java.util.EnumSet;
import org.bukkit.ChatColor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChatColorParserTest {

    @Test
    public void helloWorldTest() {
        String chatColoredText = ChatColor.RED + "Hello" + ChatColor.GREEN + " world !";
        ChatColorParser parser = new ChatColorParser(chatColoredText);

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("Hello", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));

        Assertions.assertTrue(parser.hasNext());
        str = parser.next();
        Assertions.assertEquals(" world !", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.GREEN));

        Assertions.assertFalse(parser.hasNext());
    }

    @Test
    public void emptyTest() {
        ChatColorParser parser = new ChatColorParser("");

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.noneOf(ChatColor.class));

        Assertions.assertFalse(parser.hasNext());
    }

    @Test
    public void delimiterAtTheEndTest() {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED.toString());

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));

        Assertions.assertFalse(parser.hasNext());
    }

    @Test
    public void resetTest() {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED + "Hello" + ChatColor.RESET + " world !");

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("Hello", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED));

        Assertions.assertTrue(parser.hasNext());
        str = parser.next();
        Assertions.assertEquals(" world !", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.noneOf(ChatColor.class));

        Assertions.assertFalse(parser.hasNext());
    }

    @Test
    public void doubleCodeTest() {
        ChatColorParser parser = new ChatColorParser(ChatColor.RED + "" + ChatColor.UNDERLINE + "Hello");

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("Hello", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED, ChatColor.UNDERLINE));

        Assertions.assertFalse(parser.hasNext());
    }

    @Test
    public void colorTest() {
        String chatColoredText = ChatColor.RED + "" + ChatColor.UNDERLINE + "Hello" + ChatColor.GREEN + " world !";
        ChatColorParser parser = new ChatColorParser(chatColoredText);

        Assertions.assertTrue(parser.hasNext());
        ChatColoredString str = parser.next();
        Assertions.assertEquals("Hello", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.RED, ChatColor.UNDERLINE));

        Assertions.assertTrue(parser.hasNext());
        str = parser.next();
        Assertions.assertEquals(" world !", str.getString());
        Assertions.assertEquals(str.getModifiers(), EnumSet.of(ChatColor.GREEN, ChatColor.UNDERLINE));

        Assertions.assertFalse(parser.hasNext());

    }
}
