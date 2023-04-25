package net.starly.randombox.data.holder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@AllArgsConstructor
public class RandomBoxItemInventoryHolder implements InventoryHolder {

    @Getter
    private final String boxName;

    @Override
    public Inventory getInventory() {
        return null;
    }
}
