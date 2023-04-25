package net.starly.randombox.listener;

import net.starly.randombox.RandomBoxMain;
import net.starly.randombox.data.holder.RandomBoxItemInventoryHolder;
import net.starly.randombox.randombox.RandomBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

public class RandomBoxItemGUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null) return;
        if (inventory.getHolder() instanceof RandomBoxItemInventoryHolder) {
            RandomBoxItemInventoryHolder holder = (RandomBoxItemInventoryHolder) inventory.getHolder();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof RandomBoxItemInventoryHolder) {
            RandomBoxItemInventoryHolder holder = (RandomBoxItemInventoryHolder) inventory.getHolder();
            String boxName = holder.getBoxName();
            RandomBox randomBox = RandomBoxMain.getInstance().getRandomBoxRepository().getRandomBox(boxName);
            randomBox.setItems(Arrays.stream(inventory.getContents()).filter(Objects::nonNull).collect(Collectors.toList()));
        }
    }
}