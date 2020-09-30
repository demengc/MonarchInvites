package dev.demeng.monarchinvites.listener;

import dev.demeng.demlib.Common;
import dev.demeng.demlib.message.MessageUtils;
import dev.demeng.monarchinvites.MonarchInvites;
import dev.demeng.monarchinvites.commands.ReferCmd;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.UUID;

public class RewardsQueueListener implements Listener {

  private final MonarchInvites i;

  public RewardsQueueListener(MonarchInvites i) {
    this.i = i;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onPlayerJoin(PlayerJoinEvent e) {

    final UUID uuid = e.getPlayer().getUniqueId();

    Common.run(
        () -> {
          try {
            for (int n = 1; n <= i.getDatabase().getRewards(uuid); n++) {
              ReferCmd.sendRewards(Bukkit.getOfflinePlayer(uuid));
            }

            i.getDatabase().removeRewards(uuid);

          } catch (SQLException ex) {
            MessageUtils.error(ex, "Failed to send queued rewards to player.", false);
          }
        },
        true);
  }
}
