package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IDataHolder;
import me.alenalex.notaprisoncore.api.data.IMineDataHolder;
import me.alenalex.notaprisoncore.api.data.IMineMetaDataHolder;
import me.alenalex.notaprisoncore.api.data.IProfileDataHolder;
import me.alenalex.notaprisoncore.api.provider.IUserSocialDataHolder;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;

public class DataHolder implements IDataHolder {

    private final NotAPrisonCore plugin;
    private final MineMetaDataHolder mineMetaDataHolder;
    private final UserProfileDataHolder userProfileDataHolder;
    private final MineDataHolder mineDataHolder;
    private final UserSocialDataHolder userSocialDataHolder;

    public DataHolder(NotAPrisonCore plugin) {
        this.plugin = plugin;
        this.mineMetaDataHolder = new MineMetaDataHolder(this);
        this.userProfileDataHolder = new UserProfileDataHolder(this);
        this.mineDataHolder = new MineDataHolder(this);
        this.userSocialDataHolder = new UserSocialDataHolder(this);
    }

    public void onEnable(){
        this.mineMetaDataHolder.onEnable();
        this.userProfileDataHolder.onEnable();
    }

    public void onDisable(){
        this.mineMetaDataHolder.onDisable();
    }

    @Override
    public IMineMetaDataHolder getMineMetaDataHolder() {
        return mineMetaDataHolder;
    }

    @Override
    public IProfileDataHolder getProfileDataHolder() {
        return this.userProfileDataHolder;
    }

    @Override
    public IMineDataHolder getMineDataHolder() {
        return this.mineDataHolder;
    }

    @Override
    public IUserSocialDataHolder getUserSocialDataHolder() {
        return this.userSocialDataHolder;
    }

    public NotAPrisonCore getPlugin() {
        return plugin;
    }
}
