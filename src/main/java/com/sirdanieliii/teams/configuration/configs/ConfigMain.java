package com.sirdanieliii.teams.commands.configuration.configs;


import com.sirdanieliii.teams.commands.configuration.ConfigYML;

import java.io.File;

import static com.sirdanieliii.teams.Teams.getThisPlugin;

public class ConfigMain extends ConfigYML {
    public ConfigMain(String path, String filename, String referencePath, String referenceFile) {
        super(path, filename, referencePath, referenceFile);
    }

    @Override
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
                getThisPlugin().getLogger().info(String.format(String.format("Updated %s.yml", filename)));
                return true;
            }
            return false;
        }
        return false;
    }
}
