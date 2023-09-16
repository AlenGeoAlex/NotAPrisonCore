package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.database.sql.ISQLDatabase;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.store.IUserProfileStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;

public class UserProfileStore extends AbstractDataStore<IPrisonUserProfile, UUID> implements IUserProfileStore {
    private final PrisonDataStore prisonDataStore;
    private final File userDataDirectory;
    public UserProfileStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getSqlDatabase());
        this.prisonDataStore = prisonDataStore;
        this.userDataDirectory = new File(prisonDataStore.getStoreParentDirectory(), "meta"+File.separator+"user");
        if(!this.userDataDirectory.exists())
            this.userDataDirectory.mkdirs();
    }

    @Override
    protected @NotNull String tableName() {
        return null;
    }

    @Override
    protected @Nullable String insertQuery() {
        return null;
    }

    @Override
    protected @Nullable String updateQuery() {
        return null;
    }

    @Override
    protected @NotNull Class<IPrisonUserProfile> entityType() {
        return IPrisonUserProfile.class;
    }

    @Override
    protected @NotNull Class<UUID> idType() {
        return UUID.class;
    }

    @Override
    protected @NotNull Optional<IPrisonUserProfile> read(@NotNull ResultSet resultSet) {
        return Optional.empty();
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IPrisonUserProfile entity, boolean createStatement, @Nullable Object predefinedId) {

    }
}
