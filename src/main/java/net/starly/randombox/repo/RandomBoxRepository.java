package net.starly.randombox.repo;

import net.starly.randombox.randombox.RandomBox;

import java.io.File;
import java.util.List;

public interface RandomBoxRepository {
    @Deprecated
    void initialize(File randomboxFolder);

    void setRandomBox(String name, RandomBox randomBox);

    List<RandomBox> getAllRandomBox();

    RandomBox getRandomBox(String name);

    void removeRandomBox(String name);

    void saveAll();
}
