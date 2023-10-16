package me.alenalex.notaprisoncore.api.data;

public interface IDataHolder {
    IMineMetaDataHolder getMineMetaDataHolder();
    IProfileDataHolder getProfileDataHolder();
    IMineDataHolder getMineDataHolder();
    IUserSocialDataHolder getUserSocialDataHolder();

}
