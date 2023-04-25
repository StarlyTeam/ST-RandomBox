package net.starly.randombox.randombox.impl;

import lombok.Getter;
import lombok.Setter;
import net.starly.randombox.randombox.RandomBox;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RandomBoxImpl implements RandomBox {

    @Getter
    @Setter
    private String name;

    private List<ItemStack> items = new ArrayList<>();


    public RandomBoxImpl(String name) {
        this.name = name;
    }


    @Override
    public List<ItemStack> getItems() {
        return new ArrayList<>(items);
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public void setItems(List<ItemStack> items) {
        this.items = items;
    }

    @Override
    public void addItem(ItemStack... items) {
        this.items.addAll(Arrays.asList(items));
    }
}
