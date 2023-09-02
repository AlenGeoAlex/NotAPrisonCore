package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.database.SQLDatabase;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.store.IMineStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MineStore extends AbstractDataStore<IMine, UUID> implements IMineStore {
    public MineStore(SQLDatabase pluginDatabase) {
        super(pluginDatabase);
    }

    @Override
    protected @NotNull String tableName() {
        return DbConstants.TableNames.MINES;
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
    protected @NotNull Class<IMine> entityType() {
        return IMine.class;
    }

    @Override
    protected @NotNull Class<UUID> idType() {
        return UUID.class;
    }

    @Override
    protected @NotNull Optional<IMine> read(@NotNull ResultSet resultSet) {
        return Optional.empty();
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IMine entity, boolean createStatement, @Nullable Object predefinedId) {

    }

    @Override
    public CompletableFuture<UUID> claimMine(IMine mine) {
        if(mine == null){
            CompletableFuture<UUID> future = new CompletableFuture<>();
            future.completeExceptionally(new DatastoreException("Failed to claim a mine when the provided mine is null"));
            return future;
        }

        return null;
    }
}
