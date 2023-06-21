package com.sirdanieliii.teams.commands.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static com.sirdanieliii.teams.Teams.getThisPlugin;

public class ConfigYML implements Config {
    private File file;
    private FileConfiguration config;
    protected String path;
    protected String filename;
    protected String referencePath = null;
    protected String referenceFile = null;

    public ConfigYML(String path, String filename) {
        file = new File(getThisPlugin().getDataFolder() + path, filename + ".yml");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    updateVersion(1);
                } else {
                    getThisPlugin().getLogger().warning(String.format("Could not write file: %s", path + filename + ".yml"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        this.path = path;
        this.filename = filename;
    }

    public ConfigYML(String path, String filename, String referencePath, String referenceFile) {
        File destination = new File(getThisPlugin().getDataFolder() + "/" + path);
        file = new File(destination.getPath(), filename + ".yml");

        if (!file.exists()) {
            if (getThisPlugin().getResource(referencePath + referenceFile + ".yml") != null) {
                // Generate file
                getThisPlugin().saveResource(referencePath + referenceFile + ".yml", false);
                // Create directory if it doesn't exist so file.renameTo() works
                if (!path.equals("") && !destination.exists()) {
                    if (!destination.mkdirs()) getThisPlugin().getLogger().warning("Could not generate directory: " + destination.getPath());
                }
                // Rename & move file to specified location
                if (!new File(getThisPlugin().getDataFolder(), referenceFile + ".yml").renameTo(file)) {
                    getThisPlugin().getLogger().warning(String.format("Could not write file: %s", path + filename + ".yml"));
                }
            } else {
                try {
                    if (file.createNewFile()) {
                        updateVersion(1);
                    } else {
                        getThisPlugin().getLogger().warning("Could not generate file: " + file.getPath());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        this.path = path;
        this.filename = filename;
        this.referencePath = referencePath;
        this.referenceFile = referenceFile;
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("Could not save to data file");
        }
    }

    public void save(File f) {
        try {
            config.save(f);
        } catch (IOException e) {
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage("Could not save to data file");
        }
    }

    public void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reload(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public File getFile() {
        return file;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public int getVersion() {
        try {
            return Integer.parseInt(Objects.requireNonNull(config.getString("config-version")));
        } catch (NumberFormatException | NullPointerException ignored) {
        }
        return 0;
    }

    public void updateVersion(int version) {
        config.set("config-version", version);
        save();
    }

    public void deleteKey(String path) {
        config.set(path, null);
        save();
    }

    public boolean delete() {
        return file.delete();
    }

    public boolean update() {
        File oldFile = new File(getThisPlugin().getDataFolder() + path, filename + "-old.yml");
        if (oldFile.exists()) {
            if (oldFile.delete()) getThisPlugin().getLogger().info(String.format("Found %s before updating %s.yml! Deleting...", oldFile, filename));
        }
        if (!(this.getFile().renameTo(oldFile))) {
            getThisPlugin().getLogger().warning(String.format("Could not rename %s.yml to %s-old.yml while updating configs", filename, filename));
            return false;
        }
        this.reload(oldFile); // Update variables to reference the renamed file
        ConfigYML newFile = new ConfigYML(path, filename, referencePath, referenceFile);
        // From this point on, "this" refers to the "old file", and newFile is our new "updated file"

        if (this.getVersion() == newFile.getVersion()) {
            remapValuesToNew(this, newFile);
            if (this.delete()) { // Delete "old" file
                this.reload(newFile.getFile()); // Update variables to reference renamed file
                getThisPlugin().getLogger().info(String.format("Updated %s.yml", filename));
                return true;
            }
        }
        return false;
    }

    /**
     * Copies keys from one config file to the other
     *
     * @param oldF reference file
     * @param newF target file
     */
    public static void remapValuesToNew(ConfigYML oldF, ConfigYML newF) {
        oldF.getConfig().getValues(true).forEach((k, v) -> { // deep = true so all keys will contain any keys within child ConfigurationSections
            if (v != null) {
                newF.getConfig().set(k, v); // Don't map null values
            }
        });
        newF.save();
    }
}
