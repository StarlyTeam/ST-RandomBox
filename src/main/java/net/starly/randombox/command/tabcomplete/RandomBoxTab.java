package net.starly.randombox.command.tabcomplete;

import net.starly.randombox.RandomBoxMain;
import net.starly.randombox.randombox.RandomBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RandomBoxTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            if (sender.hasPermission("starly.randombox.delete")) completions.add("삭제");
            if (sender.hasPermission("starly.randombox.give")) completions.add("지급");

            if (sender instanceof Player) {
                if (sender.hasPermission("starly.randombox.create")) completions.add("생성");
                if (sender.hasPermission("starly.randombox.edit")) completions.add("편집");
            }
        } else if (args.length == 2) {
            if (args[0].equals("생성")) {
                completions.add("<랜덤박스ID>");
            } else if (Arrays.asList("삭제", "편집", "지급").contains(args[0])) {
                completions.add("<랜덤박스ID>");
                completions.addAll(RandomBoxMain.getInstance().getRandomBoxRepository().getAllRandomBox().stream().map(RandomBox::getName).collect(Collectors.toList()));
            }
        } else if (args.length == 3) {
            if (args[0].equals("지급")) {
                completions.add("<플레이어>");
                completions.add("@a");
                completions.addAll(RandomBoxMain.getInstance().getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            }
        }

        return completions;
    }
}
