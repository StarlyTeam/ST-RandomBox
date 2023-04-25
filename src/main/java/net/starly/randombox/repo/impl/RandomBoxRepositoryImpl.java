package net.starly.randombox.repo.impl;

import net.starly.randombox.randombox.RandomBox;
import net.starly.randombox.randombox.impl.RandomBoxImpl;
import net.starly.randombox.repo.RandomBoxRepository;
import net.starly.randombox.util.EncodeUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class RandomBoxRepositoryImpl implements RandomBoxRepository {

    private File randomboxFolder;
    private final Map<String, RandomBox> randomBoxMap = new HashMap<>();


    @Override
    @Deprecated
    public void initialize(File randomboxFolder) {
        this.randomBoxMap.clear();
        this.randomboxFolder = randomboxFolder;

        if (!randomboxFolder.exists()) randomboxFolder.mkdirs();
        else {
            for (File randomboxFile : randomboxFolder.listFiles()) {
                FileConfiguration randomBoxConfig = YamlConfiguration.loadConfiguration(randomboxFile);
                String boxName = randomboxFile.getName().replace(".yml", "");

                RandomBox randomBox = new RandomBoxImpl(boxName);
                randomBox.setItems(randomBoxConfig.getList("items").stream().map(byte[].class::cast).map(EncodeUtil::decode).map(ItemStack.class::cast).collect(Collectors.toList()));
                randomBoxMap.put(boxName, randomBox);
            }
        }
    }

    @Override
    public void setRandomBox(String name, RandomBox randomBox) {
        randomBoxMap.put(name, randomBox);
    }

    @Override
    public List<RandomBox> getAllRandomBox() {
        return new ArrayList<>(randomBoxMap.values());
    }

    @Override
    public RandomBox getRandomBox(String name) {
        return randomBoxMap.get(name);
    }

    @Override
    public void removeRandomBox(String name) {
        randomBoxMap.remove(name);
    }

    @Override
    public void saveAll() {
        for (Map.Entry<String, RandomBox> entry : randomBoxMap.entrySet()) {
            String boxName = entry.getKey();
            RandomBox randomBox = entry.getValue();

            File randomboxFolder = new File(this.randomboxFolder, boxName + ".yml");
            FileConfiguration randomboxConfig = YamlConfiguration.loadConfiguration(randomboxFolder);

            randomboxConfig.set("items", randomBox.getItems().stream().map(EncodeUtil::encode).collect(Collectors.toList()));
            randomboxConfig.set("name", boxName);

            try {
                randomboxConfig.save(randomboxFolder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
