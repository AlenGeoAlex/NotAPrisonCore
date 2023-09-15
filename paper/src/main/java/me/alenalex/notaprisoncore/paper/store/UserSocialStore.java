package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.store.IUserSocialStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserSocialStore extends AbstractDataStore<IUserSocial, UUID> implements IUserSocialStore {
    private final PrisonDataStore prisonDataStore;

    public UserSocialStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getSqlDatabase());
        this.prisonDataStore = prisonDataStore;
    }

    @Override
    protected @NotNull String tableName() {
        return DbConstants.TableNames.USER_SOCIALS;
    }

    @Override
    protected @Nullable String insertQuery() {
        throw new NotImplementedException("Insert Query on UserSocials is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String updateQuery() {
        throw new NotImplementedException("Update Query on UserSocials is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String selectQuery() {
        throw new NotImplementedException("Select Query onUserSocials is not implemented, It doesn't have a select all implementation");
    }

    @Override
    protected @Nullable String selectByIdQuery() {
        throw new NotImplementedException("Select By Id Query on UserSocials is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @NotNull Class<IUserSocial> entityType() {
        return IUserSocial.class;
    }

    @Override
    protected @NotNull Class<UUID> idType() {
        return UUID.class;
    }

    @Override
    protected @NotNull Optional<IUserSocial> read(@NotNull ResultSet resultSet) {
        throw new NotImplementedException("Read on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IUserSocial entity, boolean createStatement, @Nullable Object predefinedId) {
        throw new NotImplementedException("Write on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    public CompletableFuture<Optional<UUID>> createAsync(IUserSocial entity) {
        return super.createAsync(entity);
    }
}
