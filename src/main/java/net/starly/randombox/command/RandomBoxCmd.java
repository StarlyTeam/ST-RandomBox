package net.starly.randombox.command;

import net.starly.core.jb.version.nms.tank.NmsItemStackUtil;
import net.starly.core.jb.version.nms.wrapper.ItemStackWrapper;
import net.starly.core.jb.version.nms.wrapper.NBTTagCompoundWrapper;
import net.starly.core.util.InventoryUtil;
import net.starly.randombox.RandomBoxMain;
import net.starly.randombox.data.holder.RandomBoxItemInventoryHolder;
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
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("잘못된 명령어입니다.");
            return true;
        }

        switch (args[0]) {
            case "생성": {
                if (args.length == 1) {
                    player.sendMessage("뽑기상자의 ID를 입력해주세요.");
                    return true;
                } else if (args.length != 2) {
                    player.sendMessage("잘못된 명령어입니다.");
                    return true;
                }

                RandomBoxRepository randomBoxRepository = RandomBoxMain.getInstance().getRandomBoxRepository();
                String boxName = args[1];

                if (randomBoxRepository.getRandomBox(boxName) != null) {
                    player.sendMessage("해당 ID의 뽑기상자는 이미 존재합니다.");
                    return true;
                }

                randomBoxRepository.setRandomBox(boxName, new RandomBoxImpl(boxName));
                player.sendMessage("뽑기상자를 생성했습니다.");

                player.openInventory(RandomBoxMain.getInstance().getServer().createInventory(new RandomBoxItemInventoryHolder(boxName), 54, "아이템 설정 [" + boxName + "]"));
                player.sendMessage("뽑기상자의 아이템을 설정해주세요.");
                return true;
            }

            case "편집": {
                if (args.length == 1) {
                    player.sendMessage("뽑기상자의 ID를 입력해주세요.");
                    return true;
                } else if (args.length != 2) {
                    player.sendMessage("잘못된 명령어입니다.");
                    return true;
                }

                String boxName = args[1];
                RandomBox randomBox = RandomBoxMain.getInstance().getRandomBoxRepository().getRandomBox(boxName);
                if (randomBox == null) {
                    player.sendMessage("해당 ID의 뽑기상자가 존재하지 않습니다.");
                    return true;
                }

                Inventory inventory = RandomBoxMain.getInstance().getServer().createInventory(new RandomBoxItemInventoryHolder(boxName), 54, "아이템 설정 [" + boxName + "]");
                randomBox.getItems().forEach(inventory::addItem);

                player.openInventory(inventory);
                return true;
            }

            case "지급": {
                if (args.length == 1) {
                    player.sendMessage("뽑기상자의 ID를 입력해주세요.");
                    return true;
                } else if (args.length == 2) {
                    player.sendMessage("지급 대상을 입력해주세요.");
                    return true;
                } else if (args.length != 3) {
                    player.sendMessage("잘못된 명령어입니다.");
                    return true;
                }

                String boxName = args[1];
                RandomBox randomBox = RandomBoxMain.getInstance().getRandomBoxRepository().getRandomBox(boxName);
                if (randomBox == null) {
                    player.sendMessage("해당 ID의 뽑기상자가 존재하지 않습니다.");
                    return true;
                }

                List<Player> targets;
                if (args[2].equals("@a")) targets = new ArrayList<>(RandomBoxMain.getInstance().getServer().getOnlinePlayers());
                else {
                    Player target = RandomBoxMain.getInstance().getServer().getPlayer(args[2]);
                    targets = target == null ? null : Collections.singletonList(target);
                }

                if (targets == null) {
                    player.sendMessage("플레이어가 존재하지 않거나, 온라인이 아닙니다.");
                    return true;
                }

                ItemStack itemStack = new ItemStack(Material.CHEST);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("§6뽑기상자 [" + boxName + "]");
                itemMeta.setLore(Arrays.asList("§r§7[§e!§7] §f우클릭으로 뽑기상자를 열어보자!"));
                itemStack.setItemMeta(itemMeta);

                ItemStackWrapper nmsStack = NmsItemStackUtil.getInstance().asNMSCopy(itemStack);
                NBTTagCompoundWrapper tagCompound = nmsStack.getTag();
                if (tagCompound == null) tagCompound = NmsItemStackUtil.getInstance().getNbtCompoundUtil().newInstance();
                tagCompound.setString("RANDOMBOX_NAME", boxName);
                nmsStack.setTag(tagCompound);

                ItemStack finalItemStack = NmsItemStackUtil.getInstance().asBukkitCopy(nmsStack);

                targets.stream().map(Player::getInventory).forEach(inv -> {
                    if (InventoryUtil.getSpace(inv) - 5 < 1) {
                        player.sendMessage(inv.getViewers().get(0).getName() + "님의 인벤토리에 빈 공간이 없어 지급되지 않았습니다.");
                        return;
                    }

                    inv.addItem(finalItemStack);
                    player.sendMessage(inv.getViewers().get(0).getName() + "님에게 뽑기상자를 지급했습니다.");
                });
                return true;
            }

            default: {
                player.sendMessage("잘못된 명령어입니다.");
                return true;
            }
        }
    }
}
