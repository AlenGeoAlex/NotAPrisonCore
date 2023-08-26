package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.common.json.JsonWrapper;
import me.alenalex.notaprisoncore.api.database.SQLDatabase;
import me.alenalex.notaprisoncore.api.exceptions.database.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.store.IEntityStore;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public abstract class AbstractDataStore<E, I> implements IEntityStore<E, I> {
    private final SQLDatabase pluginDatabase;

    public AbstractDataStore(SQLDatabase pluginDatabase) {
        this.pluginDatabase = pluginDatabase;
    }

    protected abstract String tableName();
    protected abstract String insertQuery();
    protected abstract String updateQuery();
    protected SQLDatabase getPluginDatabase() {
        return pluginDatabase;
    }
    protected abstract Class<E> entityType();
    protected abstract Class<I> idType();
    protected abstract Optional<E> read(@NotNull ResultSet resultSet);
    protected abstract void write(@NotNull PreparedStatement preparedStatement, E entity ,boolean createStatement);

    @Override
    public CompletableFuture<Collection<E>> all() {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Collection<E>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<Collection<E>>() {
            @Override
            public Collection<E> get() {
                final Collection<E> result = new ArrayList<>();
                try (final Connection connection = getPluginDatabase().getConnection();
                     Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery("SELECT * FROM "+tableName()+";");
                ){
                    while (resultSet.next()){
                        Optional<E> optionalValue = read(resultSet);
                        if(!optionalValue.isPresent())
                            continue;

                        result.add(optionalValue.get());
                    }
                }catch (Exception e){
                    throw new RuntimeException(e);
                }
                return result;
            }
        });
    }

    @Override
    public CompletableFuture<E> id(I id) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<E> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> fetchById(id));
    }

    @Override
    public CompletableFuture<Boolean> deleteAsync(I entityId) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> deleteById(entityId));
    }

    @Override
    public CompletableFuture<I> createAsync(E entity) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<I> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> create(entity, idType()));
    }

    @Override
    public CompletableFuture<Boolean> updateAsync(E entity) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> update(entity));
    }

    @Override
    public boolean updateBatchSync(Collection<E> entities) {
        if (!getPluginDatabase().isConnected()) {
            throw new DatabaseNotAvailableException();
        }

        if(entities == null || entities.isEmpty())
            return true;

        try(final Connection connection = pluginDatabase.getConnection();
            final PreparedStatement preparedStatement = connection.prepareStatement(updateQuery())
        ) {
            connection.setAutoCommit(true);
            for (E entity : entities) {
                write(preparedStatement, entity, false);
                preparedStatement.addBatch();
            }

            preparedStatement.executeLargeBatch();
            return true;
        }catch (Exception e){
            throw new RuntimeException("Error updating data batch due to - "+e.toString(), e);
        }
    }

    private Boolean update(E entity){
        try (final Connection connection = pluginDatabase.getConnection();
             final PreparedStatement preparedStatement = prepareDmlStatement(connection, updateQuery(), entity, false);
        ) {
            if(preparedStatement.executeUpdate() == 0){
                throw new RuntimeException("Failed to update entity. Reason: No rows affected");
            }

            return true;
        }catch (Exception e){
            throw new RuntimeException("Error updating data of - "+e.toString(), e);
        }
    }

    private I create(E entity, Class<I> idType){
        try (final Connection connection = pluginDatabase.getConnection();
             final PreparedStatement preparedStatement = prepareDmlStatement(connection, insertQuery(), entity, true);
        ) {
            if(preparedStatement.executeUpdate() == 0){
                throw new RuntimeException("Failed to create entity. Reason: No rows affected");
            }

            try (ResultSet set = preparedStatement.getGeneratedKeys()) {
                if(!set.next()){
                    throw new RuntimeException("Failed to create entity. Reason: No ID obtained");
                }
                I generatedId = extractGeneratedId(set, idType);
                if (generatedId == null) {
                    throw new RuntimeException("Failed to create entity. Reason: Invalid ID type");
                }
                return generatedId;
            }catch (Exception e){
                throw new RuntimeException("Failed to create entity. Reason: Unknown error");
            }
        }catch (Exception e){
            throw new RuntimeException("Error create data of - "+e.toString(), e);
        }
    }

    private <T> T extractGeneratedId(ResultSet generatedKeys, Class<T> idType) throws SQLException {
        if (idType.equals(Integer.class)) {
            return idType.cast(generatedKeys.getInt(1));
        } else if (idType.equals(Long.class)) {
            return idType.cast(generatedKeys.getLong(1));
        } else if (idType.equals(UUID.class)) {
            return idType.cast(UUID.fromString(generatedKeys.getString(1)));
        }
        return null;
    }

    private boolean deleteById(I id){
        try (final Connection connection = getPluginDatabase().getConnection();
             final PreparedStatement preparedStatement = prepareStatementWithId(connection, "DELETE FROM " + tableName() + " WHERE `id` = ?", id);
             ) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error delete data by ID - "+id.toString(), e);
        }

        return true;
    }

    private E fetchById(I id) {
        try (final Connection connection = getPluginDatabase().getConnection();
             final PreparedStatement preparedStatement = prepareStatementWithId(connection, "SELECT * FROM " + tableName() + " WHERE `id` = ?", id);
             final ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return read(resultSet).orElse(null);
            }

            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching data by ID - "+id.toString(), e);
        }
    }

    private PreparedStatement prepareStatementWithId(Connection connection, String query , I id) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(query);

        if (id instanceof Integer) {
            preparedStatement.setInt(1, (Integer) id);
        } else if (id instanceof Long) {
            preparedStatement.setLong(1, (Long) id);
        } else {
            preparedStatement.setString(1, id.toString());
        }

        return preparedStatement;
    }

    private PreparedStatement prepareDmlStatement(Connection connection, String query, E entity, boolean isCreate) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(query);
        write(preparedStatement, entity, isCreate);
        return preparedStatement;
    }


    protected String stringify(Vector vector){
        return JsonWrapper.WRAPPER.get().toJson(vector);
    }

    protected Vector parseBukkitVector(String bukkitVectorJson){
        return JsonWrapper.WRAPPER.get().fromJson(bukkitVectorJson, Vector.class);
    }
    protected String stringify(Location location){
        return JsonWrapper.WRAPPER.get().toJson(location);
    }

    protected Location parseLocation(String locationJson){
        return JsonWrapper.WRAPPER.get().fromJson(locationJson, Location.class);
    }
}
