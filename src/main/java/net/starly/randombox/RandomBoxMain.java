package net.starly.randombox;

import lombok.Getter;
import net.starly.core.bstats.Metrics;
import net.starly.randombox.command.RandomBoxCmd;
import net.starly.randombox.command.tabcomplete.RandomBoxTab;
import net.starly.randombox.listener.RandomBoxItemGUIListener;
import net.starly.randombox.listener.RandomBoxRightClickListener;
import net.starly.randombox.message.MessageContext;
import net.starly.randombox.repo.RandomBoxRepository;
import net.starly.randombox.repo.impl.RandomBoxRepositoryImpl;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class RandomBoxMain extends JavaPlugin {

    private static RandomBoxMain instance;
    public static RandomBoxMain getInstance() {
        return instance;
    }

    @Getter
    private RandomBoxRepository randomBoxRepository;


    @Override
    public void onEnable() {
        /* DEPENDENCY
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        if (!isPluginEnabled("ST-Core")) {
            getServer().getLogger().warning("[" + getName() + "] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.");
            getServer().getLogger().warning("[" + getName() + "] 다운로드 링크 : §fhttp://starly.kr/");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        /* SETUP
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        instance = this;
        randomBoxRepository = new RandomBoxRepositoryImpl();
        new Metrics(this, 18296);

        /* CONFIG
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        File randomBoxFolder = new File(getDataFolder(), "randombox/");
        if (!randomBoxFolder.exists()) randomBoxFolder.mkdirs();
        else randomBoxRepository.initialize(randomBoxFolder);

        File messageFile = new File(getDataFolder(), "message.yml");
        if (!messageFile.exists()) saveResource("message.yml", true);
        MessageContext.getInstance().initialize(YamlConfiguration.loadConfiguration(messageFile));

        /* COMMAND
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getServer().getPluginCommand("random-box").setExecutor(new RandomBoxCmd());
        getServer().getPluginCommand("random-box").setTabCompleter(new RandomBoxTab());

        /* LISTENER
         ──────────────────────────────────────────────────────────────────────────────────────────────────────────────── */
        getServer().getPluginManager().registerEvents(new RandomBoxItemGUIListener(), instance);
        getServer().getPluginManager().registerEvents(new RandomBoxRightClickListener(), instance);
    }

    @Override
    public void onDisable() {
        randomBoxRepository.saveAll();
    }

    private boolean isPluginEnabled(String name) {
        Plugin plugin = getServer().getPluginManager().getPlugin(name);
        return plugin != null && plugin.isEnabled();
    }
}
