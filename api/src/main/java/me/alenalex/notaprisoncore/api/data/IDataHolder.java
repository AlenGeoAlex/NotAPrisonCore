package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.provider.IUserSocialDataHolder;

public interface IDataHolder {
    IMineMetaDataHolder mineMetaDataHolder();
    IProfileDataHolder profileDataHolder();
    IMineDataHolder mineDataHolder();
    IUserSocialDataHolder userSocialDataHolder();

}
