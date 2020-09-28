package dev.demeng.monarchinvites.commands;

import dev.demeng.demlib.Common;
import dev.demeng.demlib.command.CustomCommand;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.monarchinvites.MonarchInvites;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class ReferCmd extends CustomCommand {

  private final MonarchInvites i;

  public ReferCmd(MonarchInvites i) {
    super("refer", true, "monarchinvites.invite", 1, "<player>");
    this.i = i;

    setDescription("Set a player as your referrer.");
    setAliases(Collections.singletonList("refferrer"));
  }

  @Override
  protected void run(CommandSender sender, String[] args) {

    final Player p = (Player) sender;

    if (p.getName().equalsIgnoreCase(args[0])) {
      returnTell(i.getSettings().getString("refer-self"));
    }

    @SuppressWarnings("deprecation")
    final OfflinePlayer referrer = Bukkit.getOfflinePlayer(args[0]);

    if (referrer.getName() == null || !referrer.hasPlayedBefore()) {
      returnTell(
          Objects.requireNonNull(i.getSettings().getString("invalid-referrer"))
              .replace("%player%", args[0]));
    }

    final UUID uuid = referrer.getUniqueId();

    Common.run(
        () -> {
          String ip = null;

          try {
            ip = Objects.requireNonNull(p.getAddress()).getAddress().getHostAddress();
          } catch (NullPointerException ex) {
            MessageUtils.tell(sender, i.getSettings().getString("internal-error"));
            return;
          }

          if (ip == null) {
            MessageUtils.tell(sender, i.getSettings().getString("internal-error"));
            return;
          }

          try {

            final UUID currentReferrerUuid = i.getDatabase().getReferrer(ip);

            if (currentReferrerUuid != null) {
              MessageUtils.tell(
                  sender,
                  Objects.requireNonNull(i.getSettings().getString("already-referred"))
                      .replace(
                          "%player%",
                          Objects.requireNonNull(
                              Bukkit.getOfflinePlayer(currentReferrerUuid).getName())));
              return;
            }

            i.getDatabase().addInvite(uuid, ip);

            MessageUtils.tell(
                sender,
                Objects.requireNonNull(i.getSettings().getString("referrer-set"))
                    .replace("%player%", args[0]));

          } catch (SQLException ex) {
            MessageUtils.error(ex, "Failed to check referral and add invite.", false);
            MessageUtils.tell(sender, i.getSettings().getString("internal-error"));
          }
        },
        true);
  }
}
