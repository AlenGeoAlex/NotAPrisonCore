package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.FailedDatabaseException;
import me.alenalex.notaprisoncore.api.store.IUserSocialStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
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
        if(!getPluginDatabase().isConnected()){
            CompletableFuture<Optional<UUID>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> createAsyncInternal(entity));
    }

    private Optional<UUID> createAsyncInternal(IUserSocial entity) {
        String query = "{CALL CreateSocial(?, ?, ?, ?)}";
        UUID genId = UUID.randomUUID();
        try(final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement callableStatement = prepareCallableStatement(connection, query, entity, genId);
            ){
            callableStatement.execute();
            boolean success = callableStatement.getBoolean("Success");

            if(!success)
                return Optional.empty();

        }catch (Exception e){
            e.printStackTrace();
            throw new FailedDatabaseException("Failed to create a social connection for "+entity.getSourceId().toString()+" - "+entity.getTargetId().toString(), e);
        }

        return Optional.of(genId);
    }

    @Override
    public CompletableFuture<Collection<IUserSocial>> getSocialOf(UUID uuid) {
        return null;
    }

    private CallableStatement prepareCallableStatement(Connection connection, String query, IUserSocial social, UUID id) throws SQLException {
        CallableStatement statement = connection.prepareCall(query);
        statement.setString(1, id.toString());
        statement.setString(2, social.getSourceId().toString());
        statement.setString(3, social.getTargetId().toString());
        statement.setInt(4, social.getStatus().ordinal());
        statement.registerOutParameter("Success", Types.INTEGER);
        return statement;
    }
}
