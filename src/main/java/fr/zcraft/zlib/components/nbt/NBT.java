/*
 * Copyright or © or Copr. ZLib contributors (2015 - 2016)
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

package fr.zcraft.zlib.components.nbt;

import fr.zcraft.zlib.tools.items.ItemUtils;
import fr.zcraft.zlib.tools.reflection.NMSException;
import fr.zcraft.zlib.tools.reflection.Reflection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * This class provides various utilities to manipulate NBT data.
 */
public abstract class NBT 
{
    private NBT() {}


    /**
     * Returns the NBT JSON representation of the given object.
     * This method returns a non-strict JSON representation of the object, 
     * because minecraft (both client and server) can't deal with strict JSON
     * for some item nbt tags.
     *
     * @param value The value to JSONify.
     * @return the NBT JSON representation of the given object. 
     */
    static public String toNBTJSONString(Object value)
    {
        StringBuilder sb = new StringBuilder();
        toNBTJSONString(sb, value);
        return sb.toString();
    }


    /* ========== Item utilities ========== */
    
    /**
     * Returns the NBT tag for the specified item.
     * The tag is read-write, and any modification applied to it will also be
     * applied to the item's NBT tag.
     *
     * @param item The item to get the tag from.
     * @return the NBT tag for the specified item.
     * @throws NMSException 
     */
    static public NBTCompound fromItemStack(ItemStack item) throws NMSException
    {
        init();
        try
        {
            return new NBTCompound(getMcNBTCompound(item));
        }
        catch(Exception ex)
        {
            throw new NMSException("Unable to retrieve NBT data", ex);
        }
    }
    
    /**
     * Returns an NBT-like representation of the specified item meta.
     * It is useful as a fallback if you need item data as an NBT format, but
     * the actual NBT couldn't be retrieved for some reason.
     *
     * @param meta The item meta to get the data from.
     * @return an NBT-like representation of the specified item meta.
     */
    static public Map<String, Object> fromItemMeta(ItemMeta meta)
    {
        Map<String, Object> itemData = new HashMap<>();

        if (meta.hasDisplayName())
            itemData.put("Name", meta.getDisplayName());

        if (meta.hasLore())
            itemData.put("Lore", meta.getLore());
        
        return itemData;
    }
    
    /**
     * Returns an NBT-like representation of the specified enchantments.
     * It is useful as a fallback if you need item data as an NBT format, but
     * the actual NBT couldn't be retrieved for some reason.
     *
     * @param enchants the enchantment list to get the data from.
     * @return an NBT-like representation of the specified enchantments.
     */
    static public List<Map<String, Object>> fromEnchantments(Map<Enchantment, Integer> enchants)
    {
        List<Map<String, Object>> enchantList = new ArrayList<>();
        
        for (Map.Entry<Enchantment, Integer> enchantment : enchants.entrySet())
        {
            Map<String, Object> enchantmentData = new HashMap<>();
            enchantmentData.put("id", enchantment.getKey().getId());
            enchantmentData.put("lvl", enchantment.getValue());
            enchantList.add(enchantmentData);
        }
        
        return enchantList;
    }
    
    /**
     * Returns an NBT-like representation of the item flags (HideFlags).
     * It is useful as a fallback if you need item data as an NBT format, but
     * the actual NBT couldn't be retrieved for some reason.
     *
     * @param itemFlags item flag set to get the data from.
     * @return an NBT-like representation of the item flags (HideFlags).
     */
    static public byte fromItemFlags(Set<ItemFlag> itemFlags)
    {
        byte flags = 0;

        for (ItemFlag flag : itemFlags)
        {
            switch (flag)
            {
                case HIDE_ENCHANTS:
                    flags += 1; break;
                case HIDE_ATTRIBUTES: 
                    flags += 1 << 1; break;
                case HIDE_UNBREAKABLE:
                    flags += 1 << 2; break;
                case HIDE_DESTROYS:
                    flags += 1 << 3; break;
                case HIDE_PLACED_ON:
                    flags += 1 << 4; break;
                case HIDE_POTION_EFFECTS:
                    flags += 1 << 5; break;
            }
        }
        
        return flags;
    }

    /**
     * Adds or replaces the tags in the given ItemStack by the given tags.
     *
     * This operation is only possible on a CraftItemStack. As a consequence,
     * this method <strong>returns</strong> an {@link ItemStack}. If the given
     * ItemStack was a CraftItemStack, the same instance will be returned, but
     * in the other cases, it will be a new one (a copy).
     *
     * @param item    The ItemStack to change.
     * @param tags    The tags to place inside the stack.
     * @param replace {@code true} to replace the whole set of tags. If {@code
     *                false}, tags will be added.
     *
     * @return An item stack with the modification applied. It may (if you given
     * a CraftItemStack) or may not (else) be the same instance as the given
     * one.
     * @throws NMSException if the operation cannot be executed.
     */
    static public ItemStack addToItemStack(final ItemStack item, final Map<String, Object> tags, final boolean replace) throws NMSException
    {
        init();

        try
        {
            final ItemStack craftItemStack = (ItemStack) ItemUtils.getCraftItemStack(item);
            final Object mcItemStack = ItemUtils.getNMSItemStack(item);
            final NBTCompound compound = fromItemStack(craftItemStack);

            if (replace) compound.clear();
            compound.putAll(tags);
            Object tag = compound.getNBTTagCompound();

            if (tag != null)
            {
                final ItemMeta craftItemMeta = (ItemMeta) Reflection.call(craftItemStack.getClass(), null, "getItemMeta", new Object[] {mcItemStack});
                Reflection.call(CB_CRAFT_ITEM_META, craftItemMeta, "applyToItem", tag);

                craftItemStack.setItemMeta(craftItemMeta);
            }

            return craftItemStack;
        }
        catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | NMSException e)
        {
            throw new NMSException("Cannot set item stack tags", e);
        }
    }

    /**
     * Replaces the tags in the given ItemStack by the given tags.
     *
     * This operation is only possible on a CraftItemStack. As a consequence,
     * this method <strong>returns</strong> an {@link ItemStack}. If the given
     * ItemStack was a CraftItemStack, the same instance will be returned, but
     * in the other cases, it will be a new one (a copy).
     *
     * @param item The ItemStack to change.
     * @param tags The tags to place inside the stack.
     *
     * @return An item stack with the modification applied. It may (if you given
     * a CraftItemStack) or may not (else) be the same instance as the given
     * one.
     * @throws NMSException if the operation cannot be executed.
     * @see #addToItemStack(ItemStack, Map, boolean) This method is equivalent
     * to this one with replace = true.
     */
    static public ItemStack addToItemStack(ItemStack item, Map<String, Object> tags) throws NMSException
    {
        return addToItemStack(item, tags, true);
    }


    /* ========== Internal utilities ========== */
    
    static Class<?> MC_ITEM_STACK = null;
    static Class<?> MC_NBT_TAG_COMPOUND = null;
    static Class<?> CB_CRAFT_ITEM_META = null;


    static Class getMinecraftClass(String className) throws NMSException
    {
        try
        {
            return Reflection.getMinecraftClassByName(className);
        }
        catch (ClassNotFoundException ex)
        {
            throw new NMSException("Unable to find class: " + className, ex);
        }
    }

    static Class getCraftBukkitClass(String className) throws NMSException
    {
        try
        {
            return Reflection.getBukkitClassByName(className);
        }
        catch (ClassNotFoundException ex)
        {
            throw new NMSException("Unable to find class: " + className, ex);
        }
    }

    static private void init() throws NMSException
    {
        if (MC_ITEM_STACK != null) return; // Already initialized

        MC_ITEM_STACK = getMinecraftClass("ItemStack");
        MC_NBT_TAG_COMPOUND = getMinecraftClass("NBTTagCompound");
        CB_CRAFT_ITEM_META = getCraftBukkitClass("inventory.CraftMetaItem");
    }
    
    static private Object getMcNBTCompound(ItemStack item) throws NMSException
    {
        Object mcItemStack = ItemUtils.getNMSItemStack(item);
        if (mcItemStack == null) return null;

        try
        {
            Object tag = Reflection.getFieldValue(MC_ITEM_STACK, mcItemStack, "tag");

            if (tag == null)
            {
                tag = Reflection.instantiate(MC_NBT_TAG_COMPOUND);

                try
                {
                    Reflection.call(MC_ITEM_STACK, mcItemStack, "setTag", tag);
                }
                catch (NoSuchMethodException e) // If the set method change—more resilient, as the setTag will only update the field without any kind of callback.
                {
                    Reflection.setFieldValue(MC_ITEM_STACK, mcItemStack, "tag", tag);
                }
            }

            return tag;
        }
        catch (Exception ex)
        {
            throw new NMSException("Unable to retrieve NBT tag from item", ex);
        }
    }

    static Object fromNativeValue(Object value)
    {
        if (value == null) return null;

        NBTType type = NBTType.fromClass(value.getClass());
        return type.newTag(value);
    }

    static Object toNativeValue(Object nbtTag)
    {
        if (nbtTag == null) return null;
        NBTType type = NBTType.fromNmsNbtTag(nbtTag);
        
        switch(type)
        {
            case TAG_COMPOUND:
                return new NBTCompound(nbtTag);
            case TAG_LIST:
                return new NBTList(nbtTag);
            default:
                return type.getData(nbtTag);
        }
    }


    /* ========== NBT String Utilities ========== */
    
    static private void toNBTJSONString(StringBuilder builder, Object value)
    {
        if (value == null) return;
        
        if (value instanceof List)
            toNBTJSONString(builder, (List) value);
        else if (value instanceof Map)
            toNBTJSONString(builder, (Map) value);
        else if (value instanceof String)
            toNBTJSONString(builder, (String) value);
        else
            builder.append(value.toString());
    }
    
    static private void toNBTJSONString(StringBuilder builder, List list)
    {
        builder.append("[");
        
        boolean isFirst = true;
        for(Object value : list)
        {
            if(!isFirst)
                builder.append(",");
            toNBTJSONString(builder, value);
            isFirst = false;
        }
        
        builder.append("]");
    }
    
    static private void toNBTJSONString(StringBuilder builder, Map<Object, Object> map)
    {
        builder.append("{");

        boolean isFirst = true;
        for (Entry<Object, Object> entry : map.entrySet())
        {
            if (!isFirst)
                builder.append(",");
            builder.append(entry.getKey().toString());
            builder.append(':');
            toNBTJSONString(builder, entry.getValue());
            isFirst = false;
        }
        
        builder.append("}");
    }
    
    static private void toNBTJSONString(StringBuilder builder, String value)
    {
        builder.append('"');
        builder.append(value.replace("\"", "\\\""));
        builder.append('"');
    }
}
