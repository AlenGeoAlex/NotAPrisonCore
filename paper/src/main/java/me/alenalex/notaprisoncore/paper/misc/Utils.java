package me.alenalex.notaprisoncore.paper.misc;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Utils {

    public static void onJoin(Player player){

    }

    public static void onLeave(Player player){
        Freeze.onLeave(player);
    }


    public static final class Freeze {
        private static final Set<UUID> FREEZE_PLAYER = ConcurrentHashMap.newKeySet();

        private static void onJoin(Player player){

        }

        private static void onLeave(Player player){
            FREEZE_PLAYER.remove(player.getUniqueId());
        }

        public static void freeze(@NotNull  Player player){
            if(player == null)
                return;

            freeze(player.getUniqueId());
        }

        public static void freeze(UUID uuid){
            FREEZE_PLAYER.add(uuid);
        }

        public static void removeFreeze(@NotNull  Player player){
            if(player == null)
                return;

            removeFreeze(player.getUniqueId());
        }

        public static void removeFreeze(UUID uuid){
            FREEZE_PLAYER.add(uuid);
        }

        public static boolean isFrozen(UUID uuid){
            return FREEZE_PLAYER.contains(uuid);
        }

        public static boolean isFrozen(Player player){
            if(player == null)
                return false;

            return isFrozen(player.getUniqueId());
        }
    }

}
