package me.alenalex.notaprisoncore.api.enums;

import me.alenalex.notaprisoncore.api.core.CoreApi;
import me.alenalex.notaprisoncore.api.core.ICoreApi;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.exceptions.api.IllegalApiAccessException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public enum MineAccess {

    OPEN,
    CLOSED,
    ONLY_FRIENDS;

    public MineAccessResponse getAccessPrivilegeOn(@NotNull IMine mine, @NotNull IPrisonUserProfile accessor){
        MineAccess access = mine.access();
        if(access == OPEN)
            return MineAccessResponse.APPROVED;
        UUID sourceId = accessor.getUserId();
        Player source = Bukkit.getPlayer(sourceId);
        if(source == null)
            return MineAccessResponse.DENIED;

        if(source.isOp() || source.hasPermission("npc.core.admin")){
            return MineAccessResponse.APPROVED;
        }
        UUID ownerId = mine.getOwnerId();


        //TODO: Check owner/co-owner or OP or bypass perms

        /*
        if(access == ONLY_FRIENDS){
            UUID userId = accessor.getUserId();
            if(mine.getMineSocials().isBlocked(userId)){
                return MineAccessResponse.BLOCKED;
            }

            if(mine.getMineSocials().isRequested(userId)){
                return MineAccessResponse.REQUEST_PENDING;
            }

            if(mine.getMineSocials().isFriend(userId)) {
                return MineAccessResponse.APPROVED;
            }
        }
           */
        return MineAccessResponse.DENIED;
    }

}
