package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface IMineDataHolder extends ICommonDataHolder{

    @Nullable
    IMine get(UUID uuid);

    @Nullable
    default IMine get(IPrisonUserProfile profile){
        if(!profile.hasMine())
            return null;

        return get(profile.getMineId());
    }

}
