package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.provider.IUserSocialDataHolder;

public interface IDataHolder {
    IMineMetaDataHolder getMineMetaDataHolder();
    IProfileDataHolder getProfileDataHolder();
    IMineDataHolder getMineDataHolder();
    IUserSocialDataHolder getUserSocialDataHolder();

}
