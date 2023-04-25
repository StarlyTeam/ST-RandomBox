package net.starly.randombox.message;

import net.starly.core.util.collection.STList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MessageContext {

    private static MessageContext instance;

    public static MessageContext getInstance() {
        if (instance == null) instance = new MessageContext();
        return instance;
    }


    private final Map<MessageType, Map<String, Object>> map = new HashMap<>();
    private String prefix;

    @Deprecated
    public void $loadConfig(FileConfiguration config) {
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefix"));
        Arrays.asList(MessageType.values()).forEach(type -> initializeMessages(type, config.getConfigurationSection(type.getPath())));
    }

    private void initializeMessages(MessageType type, ConfigurationSection section) {
        Map<String, Object> messages = new HashMap<>();
        section.getKeys(true).forEach(key -> {
            if (section.isList(key)) {
                messages.put(key, new STList<>(section.getStringList(key)).map(line -> ChatColor.translateAlternateColorCodes('&', line)));
            } else if (section.isString(key)) {
                messages.put(key, ChatColor.translateAlternateColorCodes('&', section.getString(key)));
            } else throw new IllegalArgumentException("'" + type.getPath() + "." + key + "' 는 지원되지 않는 형식의 값입니다.");
        });

        map.remove(type);
        map.put(type, messages);
    }

    public String getPrefix() {
        return prefix;
    }

    public String getMessage(MessageType type, String path) {
        return (String) map.get(type).get(path);
    }

    public String getMessageAfterPrefix(MessageType type, String path) {
        return getPrefix() + getMessage(type, path);
    }

    public STList<String> getMessages(MessageType type, String path) {
        return (STList<String>) map.get(type).get(path);
    }

    public STList<String> getMessagesAfterPrefix(MessageType type, String path) {
        return getMessages(type, path).map(line -> getPrefix() + line);
    }
}
