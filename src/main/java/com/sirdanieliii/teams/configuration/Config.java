package com.sirdanieliii.teams.commands.configuration;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

public interface Config {
    /**
     * Saves the YAML file
     */
    void save();

    /**
     * Reloads the file
     */
    void reload();

    /**
     * Returns the File
     *
     * @return the class' File object
     */
    File getFile();

    /**
     * Returns the FileConfiguration
     *
     * @return the class' FileConfiguration object
     */
    FileConfiguration getConfig();

    /**
     * Returns the config file's version number
     *
     * @return integer
     */
    int getVersion();

    /**
     * Updates the version number in the file
     *
     * @param version integer
     */
    void updateVersion(int version);

    /**
     * Deletes a key in the YAML file
     *
     * @param path location of the key to delete
     */
    void deleteKey(String path);

    /**
     * Delete the file
     *
     * @return true if file was successfully deleted, false otherwise
     */
    boolean delete();

    /**
     * Override method to handle updating data
     */
    boolean update();
}
