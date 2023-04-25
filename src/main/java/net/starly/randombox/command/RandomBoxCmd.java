package net.starly.randombox.command;

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil;
import net.starly.core.jb.version.nms.wrapper.ItemStackWrapper;
import net.starly.core.jb.version.nms.wrapper.NBTTagCompoundWrapper;
import net.starly.core.util.InventoryUtil;
import net.starly.randombox.RandomBoxMain;
import net.starly.randombox.data.holder.RandomBoxItemInventoryHolder;
import net.starly.randombox.message.MessageContext;
import net.starly.randombox.message.MessageType;
import net.starly.randombox.randombox.RandomBox;
import net.starly.randombox.randombox.impl.RandomBoxImpl;
import net.starly.randombox.repo.RandomBoxRepository;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomBoxCmd implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        MessageContext msgContext = MessageContext.getInstance();

        if (args.length == 0) {
            sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
            return true;
        }

        switch (args[0]) {
            case "생성": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "onlyPlayer"));
                    return true;
                } else if (!sender.hasPermission("starly.randombox.create")) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission"));
                    return true;
                }
                Player player = (Player) sender;
                
                if (args.length == 1) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noId"));
                    return true;
                } else if (args.length != 2) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
                    return true;
                }

                RandomBoxRepository randomBoxRepository = RandomBoxMain.getInstance().getRandomBoxRepository();
                String boxName = args[1];

                if (randomBoxRepository.getRandomBox(boxName) != null) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "idAlreadyTaken"));
                    return true;
                }

                randomBoxRepository.setRandomBox(boxName, new RandomBoxImpl(boxName));
                player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.NORMAL, "randomBoxCreated"));

                player.openInventory(RandomBoxMain.getInstance().getServer().createInventory(new RandomBoxItemInventoryHolder(boxName), 54, "아이템 설정 [" + boxName + "]"));
                return true;
            }

            case "삭제": {
                if (!sender.hasPermission("starly.randombox.delete")) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission"));
                    return true;
                }
                
                if (args.length == 1) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noId"));
                    return true;
                } else if (args.length != 2) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
                    return true;
                }

                RandomBoxRepository randomBoxRepository = RandomBoxMain.getInstance().getRandomBoxRepository();
                String boxName = args[1];

                if (randomBoxRepository.getRandomBox(boxName) == null) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "randomBoxNotExists"));
                    return true;
                }

                randomBoxRepository.removeRandomBox(boxName);
                sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.NORMAL, "randomBoxDeleted"));
                return true;
            }

            case "편집": {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "onlyPlayer"));
                    return true;
                } else if (!sender.hasPermission("starly.randombox.edit")) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission"));
                    return true;
                }
                Player player = (Player) sender;
                
                if (args.length == 1) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noId"));
                    return true;
                } else if (args.length != 2) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
                    return true;
                }

                String boxName = args[1];
                RandomBox randomBox = RandomBoxMain.getInstance().getRandomBoxRepository().getRandomBox(boxName);
                if (randomBox == null) {
                    player.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "randomBoxNotExists"));
                    return true;
                }

                Inventory inventory = RandomBoxMain.getInstance().getServer().createInventory(new RandomBoxItemInventoryHolder(boxName), 54, "아이템 설정 [" + boxName + "]");
                randomBox.getItems().forEach(inventory::addItem);

                player.openInventory(inventory);
                return true;
            }

            case "지급": {
                if (!sender.hasPermission("starly.randombox.give")) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noPermission"));
                    return true;
                }

                if (args.length == 1) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noId"));
                    return true;
                } else if (args.length == 2) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "noTarget"));
                    return true;
                } else if (args.length != 3) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
                    return true;
                }

                String boxName = args[1];
                RandomBox randomBox = RandomBoxMain.getInstance().getRandomBoxRepository().getRandomBox(boxName);
                if (randomBox == null) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "randomBoxNotExists"));
                    return true;
                }

                List<Player> targets;
                if (args[2].equals("@a")) targets = new ArrayList<>(RandomBoxMain.getInstance().getServer().getOnlinePlayers());
                else {
                    Player target = RandomBoxMain.getInstance().getServer().getPlayer(args[2]);
                    targets = target == null ? null : Collections.singletonList(target);
                }

                if (targets == null) {
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "playerNotFound"));
                    return true;
                }

                ItemStack itemStack = new ItemStack(Material.CHEST);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§6랜덤박스 [" + boxName + "]");
                itemMeta.setLore(Arrays.asList("§r§7[§e!§7] §f우클릭으로 랜덤박스를 열어보자!"));
                itemStack.setItemMeta(itemMeta);

                ItemStackWrapper nmsStack = NmsItemStackUtil.getInstance().asNMSCopy(itemStack);
                NBTTagCompoundWrapper tagCompound = nmsStack.getTag();
                if (tagCompound == null) tagCompound = NmsItemStackUtil.getInstance().getNbtCompoundUtil().newInstance();
                tagCompound.setString("RANDOMBOX_NAME", boxName);
                nmsStack.setTag(tagCompound);

                ItemStack finalItemStack = NmsItemStackUtil.getInstance().asBukkitCopy(nmsStack);

                targets.forEach(target -> {
                    Inventory inventory = target.getInventory();
                    if (InventoryUtil.getSpace(inventory) - 5 < 1) {
                        sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "inventoryIsFull").replace("{target}", target.getName()));
                        return;
                    }

                    inventory.addItem(finalItemStack);
                    sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.NORMAL, "gaveRandomBox").replace("{target}", target.getName()));
                });
                return true;
            }

            default: {
                sender.sendMessage(msgContext.getMessageAfterPrefix(MessageType.ERROR, "wrongCommand"));
                return true;
            }
        }
    }
}
