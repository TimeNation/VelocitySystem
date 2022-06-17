package net.timenation.velocitysystem.manager;

import com.velocitypowered.api.proxy.Player;
import net.timenation.timevelocityapi.TimeVelocityAPI;
import net.timenation.timevelocityapi.manager.language.I18n;
import net.timenation.timevelocityapi.mysql.MySQL;
import net.timenation.timevelocityapi.utils.Components;
import net.timenation.velocitysystem.VelocitySystem;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PunishManager {

    public PunishManager(MySQL mySQL) {
        mySQL.updateDatabase("CREATE TABLE IF NOT EXISTS BannedPlayers (Spielername VARCHAR(100), UUID VARCHAR(100), Ende LONG, Grund VARCHAR(100), Banner VARCHAR(100))");
        mySQL.updateDatabase("CREATE TABLE IF NOT EXISTS MutedPlayers (Spielername VARCHAR(100), UUID VARCHAR(100), Ende VARCHAR(100), Grund VARCHAR(100), MUTER VARCHAR(100))");
    }

    public void banPlayer(UUID uuid, String playername, String reason, long seconds, Player banner) {
        long end = 0;
        if (seconds == -1) {
            end = -1;
        } else {
            long current = System.currentTimeMillis();
            long millis = seconds * 1000;
            end = current + millis;
        }

        long finalEnd = end;
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
            if(player.hasPermission("timenation.punish.see")) {
                if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(player.getUniqueId())) {
                    player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.messages.punish.banplayer", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), reason, finalEnd, TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(banner.getUniqueId()).getPlayersNameWithRankColor(banner.getUniqueId()))));
                }
            }
        });
        VelocitySystem.getInstance().getMySQL().updateDatabase("INSERT INTO BannedPlayers (Spielername, UUID, Ende, Grund, Banner) VALUES ('" + playername + "','" + uuid + "','" + end + "','" + reason + "','" + banner.getUsername() + "')");
    }

    public void unbanPlayer(UUID uuid, Player player) {
        VelocitySystem.getInstance().getMySQL().updateDatabase("DELETE FROM BannedPlayers WHERE UUID='" + uuid + "'");

        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
            if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(players.getUniqueId())) {
                if(players.hasPermission("timenation.punish.see")) {
                    players.sendMessage(Components.parse(I18n.format(players, I18n.format(players, "velocity.prefix.punish"), "velocity.messages.punish.unban", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(player.getUniqueId()).getPlayersNameWithRankColor(player.getUniqueId()))));
                }
            }
        });
    }

    public void unbanPlayer(UUID uuid) {
        VelocitySystem.getInstance().getMySQL().updateDatabase("DELETE FROM BannedPlayers WHERE UUID='" + uuid + "'");

        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
            if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(players.getUniqueId())) {
                if(players.hasPermission("timenation.punish.see")) {
                    players.sendMessage(Components.parse(I18n.format(players, I18n.format(players, "velocity.prefix.punish"), "velocity.messages.punish.unban", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), "<dark_red>SYSTEM")));
                }
            }
        });
    }

    public boolean isBanned(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT Ende FROM BannedPlayers WHERE UUID='" + uuid + "'");
        try {
            return rs.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getBanReason(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM BannedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getString("Grund");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getBanner(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM BannedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getString("Banner");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public Long getBanEnd(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM BannedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getLong("Ende");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getBannedPlayersFromPlayer(String name) {
        ArrayList<String> list = new ArrayList<>();
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM BannedPlayers WHERE Banner='" + name + "'");
        try {
            while (rs.next()) {
                list.add(rs.getString("Banner"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public List<String> getBannedPlayers() {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM BannedPlayers");
        try {
            while (rs.next()) {
                list.add(rs.getString("Spielername"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String getBanReamainingTime(Player player, UUID uuid) {
        long current = System.currentTimeMillis();
        long end = getBanEnd(uuid);
        if (end == -1) {
            return "§4§lPERMANENT";
        }
        long millis = end - current;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long weeks = 0;
        while (millis > 1000) {
            millis -= 1000;
            ++seconds;
        }
        while (seconds > 60) {
            seconds -= 60;
            ++minutes;
        }
        while (minutes > 60) {
            minutes -= 60;
            ++hours;
        }
        while (hours > 24) {
            hours -= 24;
            ++days;
        }
        while (days > 7) {
            days -= 7;
            ++weeks;
        }
        return I18n.format(player, "velocity.punish.reamainingtime", weeks, days, hours, minutes, seconds);
    }

    public String getBanReamainingTime(Player player, long time) {
        long current = System.currentTimeMillis();
        long end = time;
        if (end == -1) {
            return "§4§lPERMANENT";
        }
        long millis = end - current;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long weeks = 0;
        while (millis > 1000) {
            millis -= 1000;
            ++seconds;
        }
        while (seconds > 60) {
            seconds -= 60;
            ++minutes;
        }
        while (minutes > 60) {
            minutes -= 60;
            ++hours;
        }
        while (hours > 24) {
            hours -= 24;
            ++days;
        }
        while (days > 7) {
            days -= 7;
            ++weeks;
        }
        return I18n.format(player, "velocity.punish.reamainingtime", weeks, days, hours, minutes, seconds);
    }

    @SuppressWarnings("deprecation")
    public void mutePlayer(UUID uuid, String playername, String reason, long seconds, Player muter) {
        long end = 0;
        if (seconds == -1) {
            end = -1;
        } else {
            long current = System.currentTimeMillis();
            long millis = seconds * 1000;
            end = current + millis;
        }

        final long finalEnd = end;
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(player -> {
            if(player.hasPermission("timenation.punish.see")) {
                if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(player.getUniqueId())) {
                    player.sendMessage(Components.parse(I18n.format(player, I18n.format(player, "velocity.prefix.punish"), "velocity.messages.punish.muteplayer", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), reason, finalEnd, TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(player.getUniqueId()).getPlayersNameWithRankColor(player.getUniqueId()))));
                }
            }
        });

        VelocitySystem.getInstance().getMySQL().updateDatabase("INSERT INTO MutedPlayers (Spielername, UUID, Ende, Grund, MUTER) VALUES ('" + playername + "','" + uuid + "','" + end + "','" + reason + "','" + muter.getUsername() + "')");
    }

    public void unmutePlayer(UUID uuid, Player player) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
            if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(players.getUniqueId())) {
                if(players.hasPermission("timenation.punish.see")) {
                    players.sendMessage(Components.parse(I18n.format(players, I18n.format(players, "velocity.prefix.punish"), "velocity.messages.punish.unmute", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(player.getUniqueId()).getPlayersNameWithRankColor(player.getUniqueId()))));
                }
            }
        });

        VelocitySystem.getInstance().getMySQL().updateDatabase("DELETE FROM MutedPlayers WHERE UUID='" + uuid + "'");
    }

    public void unmutePlayer(UUID uuid) {
        VelocitySystem.getInstance().getProxyServer().getAllPlayers().forEach(players -> {
            if(TimeVelocityAPI.getInstance().getNotificationManager().hasNotificationEnabled(players.getUniqueId())) {
                if(players.hasPermission("timenation.punish.see")) {
                    players.sendMessage(Components.parse(I18n.format(players, I18n.format(players, "velocity.prefix.punish"), "velocity.messages.punish.unmute", TimeVelocityAPI.getInstance().getRankManager().getPlayersRank(uuid).getPlayersNameWithRankColor(uuid), "<dark_red>SYSTEM")));
                }
            }
        });

        VelocitySystem.getInstance().getMySQL().updateDatabase("DELETE FROM MutedPlayers WHERE UUID='" + uuid + "'");
    }

    public boolean ismuted(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT Ende FROM MutedPlayers WHERE UUID='" + uuid + "'");
        try {
            return rs.next();
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
    }

    public String getMuteReason(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM MutedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getString("Grund");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public String getMuter(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM MutedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getString("Muter");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public Long getMuteEnd(UUID uuid) {
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM MutedPlayers WHERE UUID='" + uuid + "'");
        try {
            if (rs.next()) {
                return rs.getLong("Ende");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<String> getMutedPlayersFromPlayer(String name) {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM MutedPlayers WHERE MUTER='" + name + "'");
        try {
            while (rs.next()) {
                list.add(rs.getString("MUTER"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public List<String> getMutedPlayers() {
        ArrayList<String> list = new ArrayList<String>();
        ResultSet rs = VelocitySystem.getInstance().getMySQL().getDatabaseResult("SELECT * FROM MutedPlayers");
        try {
            while (rs.next()) {
                list.add(rs.getString("Spielername"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                rs.close();
            } catch ( SQLException e ) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String getMuteReamainingTime(Player player, UUID uuid) {
        long current = System.currentTimeMillis();
        long end = getMuteEnd(uuid);
        if (end == -1) {
            return "§c§lPERMANENT";
        }
        long millis = end - current;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long weeks = 0;
        while (millis > 1000) {
            millis -= 1000;
            ++seconds;
        }
        while (seconds > 60) {
            seconds -= 60;
            ++minutes;
        }
        while (minutes > 60) {
            minutes -= 60;
            ++hours;
        }
        while (hours > 24) {
            hours -= 24;
            ++days;
        }
        while (days > 7) {
            days -= 7;
            ++weeks;
        }
        return I18n.format(player, "velocity.punish.reamainingtime", weeks, days, hours, minutes, seconds);
    }

    public String getMuteReamainingTime(Player player, long time) {
        long current = System.currentTimeMillis();
        long end = time;
        if (end == -1) {
            return "§c§lPERMANENT";
        }
        long millis = end - current;
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;
        long weeks = 0;
        while (millis > 1000) {
            millis -= 1000;
            ++seconds;
        }
        while (seconds > 60) {
            seconds -= 60;
            ++minutes;
        }
        while (minutes > 60) {
            minutes -= 60;
            ++hours;
        }
        while (hours > 24) {
            hours -= 24;
            ++days;
        }
        while (days > 7) {
            days -= 7;
            ++weeks;
        }
        return I18n.format(player, "velocity.punish.reamainingtime", weeks, days, hours, minutes, seconds);
    }
}
