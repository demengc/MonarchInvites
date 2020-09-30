package dev.demeng.monarchinvites.commands;

import dev.demeng.demlib.command.CustomCommand;
import dev.demeng.demlib.core.DemLib;
import dev.demeng.monarchinvites.MonarchInvites;
import org.bukkit.command.CommandSender;

public class MonarchInvitesCmd extends CustomCommand {

  private final MonarchInvites i;

  public MonarchInvitesCmd(MonarchInvites i) {
    super("monarchinvites", false, "monarchinvites.reload", 0, "[reload]");
    this.i = i;

    setDescription("Main command for MonarchInvites.");
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    if (args.length != 1 || !args[0].equalsIgnoreCase("reload")) {
      return;
    }

    i.getSettingsFile().reloadConfig();
    DemLib.setPrefix(i.getSettings().getString("prefix"));
    returnTell(i.getSettings().getString("reloaded"));
  }
}
