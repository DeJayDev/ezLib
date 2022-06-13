package dev.dejay.ezlib.tools.items;

import dev.dejay.ezlib.MockedToasterTest;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Broken... somehow. Will revisit later.")
public class CraftingRecipesTest extends MockedToasterTest {

    @Test
    public void canGenerateRecipesShapes() {
        String[] shapes = CraftingRecipes.getRecipeShape("ABCDEFGHI");
        Assertions.assertArrayEquals(new String[]{"ABC", "DEF", "GHI"}, shapes);

        String[] shapesWithSpace = CraftingRecipes.getRecipeShape("ABCDEFG");
        Assertions.assertArrayEquals(new String[]{"ABC", "DEF", "G  "}, shapesWithSpace);
    }

    @Test
    public void canGenerateDummyShape() {
        // FIXME: this is a legacy material here, because of missing implementations in MockBukkit
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", result, Material.QUARTZ, Material.DIORITE);
        Assertions.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assertions.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assertions.assertEquals(recipe.getResult().getType(), result.getType());
        Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());

        recipe = CraftingRecipes.shaped("foo", result, new RecipeChoice.MaterialChoice(Material.QUARTZ));
        Assertions.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assertions.assertEquals(recipe.getResult().getType(), result.getType());
        Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
    }

    @Test
    public void canGenerateSimpleShape() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", result,
            "AB ", "   ", "  A",
            Material.QUARTZ, Material.DIORITE);
        Assertions.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assertions.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assertions.assertEquals(recipe.getResult().getType(), result.getType());
        Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assertions.assertArrayEquals(new String[]{"AB ", "   ", "  A"}, recipe.getShape());

        recipe = CraftingRecipes.shaped("foo", result,
            "AB ", "   ", "  A",
            new RecipeChoice.MaterialChoice(Material.QUARTZ));
        Assertions.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assertions.assertEquals(recipe.getResult().getType(), result.getType());
        Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assertions.assertArrayEquals(new String[]{"AB ", "   ", "  A"}, recipe.getShape());
    }

    @Test
    public void canGenerateRecipeMatchingOther() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        ShapedRecipe originalRecipe = CraftingRecipes.shaped("foo", result,
            "AB ", "   ", "  A",
            Material.QUARTZ, Material.DIORITE);

        ShapedRecipe recipe = CraftingRecipes.shaped("foo", originalRecipe, "AA ", "   ", "  B");
        Assertions.assertEquals(recipe.getKey().toString(), "ztoaster:foo");
        Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
        Assertions.assertEquals(Material.DIORITE, recipe.getIngredientMap().get('B').getType());
        Assertions.assertEquals(recipe.getResult().getType(), result.getType());
        Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        Assertions.assertArrayEquals(new String[]{"AA ", "   ", "  B"}, recipe.getShape());
    }

    @Test
    public void canGenerate2x2DiagonalRecipesWithOneMaterial() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
            Material.QUARTZ, result);

        Assertions.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"A  ", " A ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{" A ", "A  ", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{" A ", "  A", "   "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{"  A", " A ", "   "}, recipes.get(3).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "A  ", " A "}, recipes.get(4).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " A ", "A  "}, recipes.get(5).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " A ", "  A"}, recipes.get(6).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "  A", " A "}, recipes.get(7).getShape());

        recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
            new RecipeChoice.MaterialChoice(Material.QUARTZ), result);

        Assertions.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"A  ", " A ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{" A ", "A  ", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{" A ", "  A", "   "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{"  A", " A ", "   "}, recipes.get(3).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "A  ", " A "}, recipes.get(4).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " A ", "A  "}, recipes.get(5).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " A ", "  A"}, recipes.get(6).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "  A", " A "}, recipes.get(7).getShape());
    }

    @Test
    public void canGenerate2x2DiagonalRecipesWithTwoMaterials() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
            Material.QUARTZ, Material.DIORITE, result);

        Assertions.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"AB ", "BA ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{"BA ", "AB ", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{" AB", " BA", "   "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{" BA", " AB", "   "}, recipes.get(3).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "AB ", "BA "}, recipes.get(4).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "BA ", "AB "}, recipes.get(5).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " AB", " BA"}, recipes.get(6).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " BA", " AB"}, recipes.get(7).getShape());

        recipes = CraftingRecipes.get2x2DiagonalRecipes("foo",
            new RecipeChoice.MaterialChoice(Material.QUARTZ),
            new RecipeChoice.MaterialChoice(Material.DIORITE), result);

        Assertions.assertEquals(8, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"AB ", "BA ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{"BA ", "AB ", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{" AB", " BA", "   "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{" BA", " AB", "   "}, recipes.get(3).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "AB ", "BA "}, recipes.get(4).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "BA ", "AB "}, recipes.get(5).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " AB", " BA"}, recipes.get(6).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " BA", " AB"}, recipes.get(7).getShape());
    }

    @Test
    public void canGenerate2x2RecipesWithOneMaterial() {
        ItemStack result = new ItemStack(Material.LEGACY_QUARTZ);

        List<ShapedRecipe> recipes = CraftingRecipes.get2x2Recipes("foo", Material.QUARTZ, result);

        Assertions.assertEquals(4, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"AA ", "AA ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{" AA", " AA", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "AA ", "AA "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " AA", " AA"}, recipes.get(3).getShape());

        recipes = CraftingRecipes.get2x2Recipes("foo",
            new RecipeChoice.MaterialChoice(Material.QUARTZ), result);

        Assertions.assertEquals(4, recipes.size());
        for (int i = 0; i < recipes.size(); i++) {
            ShapedRecipe recipe = recipes.get(i);
            Assertions.assertEquals("ztoaster:foo" + (i + 1), recipe.getKey().toString());
            Assertions.assertEquals(Material.QUARTZ, recipe.getIngredientMap().get('A').getType());
            Assertions.assertEquals(recipe.getResult().getType(), result.getType());
            Assertions.assertEquals(recipe.getResult().getAmount(), result.getAmount());
        }

        Assertions.assertArrayEquals(new String[]{"AA ", "AA ", "   "}, recipes.get(0).getShape());
        Assertions.assertArrayEquals(new String[]{" AA", " AA", "   "}, recipes.get(1).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", "AA ", "AA "}, recipes.get(2).getShape());
        Assertions.assertArrayEquals(new String[]{"   ", " AA", " AA"}, recipes.get(3).getShape());
    }
}
