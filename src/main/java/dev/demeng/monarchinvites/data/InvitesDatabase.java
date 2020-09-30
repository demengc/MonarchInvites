package dev.demeng.monarchinvites.data;

import dev.demeng.demlib.connection.MySQL;
import dev.demeng.monarchinvites.MonarchInvites;
import dev.demeng.monarchinvites.serializer.ListSerializer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InvitesDatabase extends MySQL {

  public InvitesDatabase(MonarchInvites i) throws SQLException {
    super(
        false,
        i.getSettings().getString("storage.host"),
        i.getSettings().getInt("storage.port"),
        i.getSettings().getString("storage.database"),
        i.getSettings().getString("storage.username"),
        i.getSettings().getString("storage.password"),
        i.getSettings().getString("storage.additional-options"));

    executeUpdate(
        "CREATE TABLE IF NOT EXISTS monarchinvites_invites "
            + "(uuid VARCHAR(255), ips TEXT, PRIMARY KEY(uuid));");

    executeUpdate(
        "CREATE TABLE IF NOT EXISTS monarchinvites_queue "
            + "(uuid VARCHAR(255), rewards INTEGER, PRIMARY KEY(uuid));");
  }

  public UUID getReferrer(String referredIp) throws SQLException {

    final ResultSet rs =
        executeQuery(
            "SELECT uuid FROM monarchinvites_invites WHERE ips LIKE \"%" + referredIp + "%\";");

    if (rs.next()) {
      return UUID.fromString(rs.getString("uuid"));
    }

    return null;
  }

  public void addInvite(UUID referrer, String referredIp) throws SQLException {

    final ResultSet rs =
        executeQuery("SELECT ips FROM monarchinvites_invites WHERE uuid = ?;", referrer.toString());

    if (!rs.next()) {
      executeUpdate(
          "INSERT INTO monarchinvites_invites VALUES(?, ?);",
          referrer.toString(),
          "[" + referredIp + "]");

    } else {

      final List<String> ips = new ArrayList<>(ListSerializer.deserialize(rs.getString("ips")));
      ips.add(referredIp);

      executeUpdate(
          "UPDATE monarchinvites_invites SET ips = ? WHERE uuid = ?;",
          ListSerializer.serialize(ips),
          referrer.toString());
    }
  }

  public int getRewards(UUID uuid) throws SQLException {
    final ResultSet rs =
        executeQuery("SELECT rewards FROM monarchinvites_queue WHERE uuid = ?;", uuid.toString());

    if (!rs.next()) {
      return 0;
    }

    return rs.getInt(1);
  }

  public void addReward(UUID uuid) throws SQLException {
    executeUpdate(
        "INSERT INTO monarchinvites_queue VALUES(?, ?) ON DUPLICATE KEY UPDATE rewards = rewards + 1;",
        uuid.toString(),
        1);
  }

  public void removeRewards(UUID uuid) throws SQLException {
    executeUpdate("DELETE FROM monarchinvites_queue WHERE uuid = ?", uuid.toString());
  }

  public void close() throws SQLException {
    super.close();
  }
}
