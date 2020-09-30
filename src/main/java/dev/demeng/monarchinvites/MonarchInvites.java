package dev.demeng.monarchinvites;

import dev.demeng.demlib.Registerer;
import dev.demeng.demlib.command.CommandMessages;
import dev.demeng.demlib.core.DemLib;
import dev.demeng.demlib.file.YamlFile;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.monarchinvites.commands.ReferCmd;
import dev.demeng.monarchinvites.data.InvitesDatabase;
import dev.demeng.monarchinvites.listener.RewardsQueueListener;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class MonarchInvites extends JavaPlugin {

  @Getter private YamlFile settingsFile;

  @Getter private InvitesDatabase database;

  @Override
  public void onEnable() {

    DemLib.setPlugin(this);
    DemLib.setPrefix("&7[&6Invites&7] &r");

    getLogger().info("Loading settings...");
    try {
      settingsFile = new YamlFile("settings.yml");

    } catch (Exception ex) {
      MessageUtils.error(ex, "Failed to load settings file.", true);
      return;
    }

    DemLib.setPrefix(getSettings().getString("prefix"));
    DemLib.setCommandMessages(new CommandMessages(getSettings()));

    getLogger().info("Connecting to database...");
    try {
      database = new InvitesDatabase(this);
    } catch (Throwable ex) {
      MessageUtils.error(ex, "Failed to connect to database.", true);
      return;
    }

    getLogger().info("Registering commands...");
    try {
      Registerer.registerCommand(new ReferCmd(this));
    } catch (NoSuchFieldException | IllegalAccessException ex) {
      MessageUtils.error(ex, "Failed to register commands.", true);
      return;
    }

    getLogger().info("Registering listeners...");
    Registerer.registerListener(new RewardsQueueListener(this));

    MessageUtils.console("&aMonarchInvites has been successfully enabled.");
  }

  @Override
  public void onDisable() {

    if (database != null) {
      try {
        database.close();
      } catch (SQLException ex) {
        MessageUtils.error(ex, "Failed to close database connection.", false);
        return;
      }
    }

    MessageUtils.console("&cMonarchInvites has been successfully disabled.");
  }

  public FileConfiguration getSettings() {
    return settingsFile.getConfig();
  }
}
