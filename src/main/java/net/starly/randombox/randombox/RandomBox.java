package net.starly.randombox.randombox;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RandomBox {
    List<ItemStack> getItems();

    ItemStack getItem(int index);

    void setItems(List<ItemStack> items);

    void addItem(ItemStack... items);

    String getName();

    void setName(String name);
}
