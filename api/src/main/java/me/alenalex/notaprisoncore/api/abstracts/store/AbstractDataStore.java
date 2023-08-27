package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.database.SQLDatabase;
import me.alenalex.notaprisoncore.api.exceptions.database.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.store.IEntityStore;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public abstract class AbstractDataStore<E, I> implements IEntityStore<E, I> {
    private final SQLDatabase pluginDatabase;
    public AbstractDataStore(SQLDatabase pluginDatabase) {
        this.pluginDatabase = pluginDatabase;
    }

    /**
     * Get the name of the table the store represents
     * @return String, the name of the table
     */
    @NotNull
    protected abstract String tableName();

    /**
     * Get the insert query which the store uses on {@link IEntityStore#createAsync(Object)}
     * method.
     * <br>
     * The store populates the value to the prepared statement with the help of {@link AbstractDataStore#write(PreparedStatement, Object, boolean)}
     * with the 3rd parameter (boolean) set to true
     * @return String, The DML Query for insertion
     */
    @NotNull
    protected abstract String insertQuery();

    /**
     * Get the update query which the store uses on {@link IEntityStore#updateAsync(Object)}, {@link IEntityStore#updateBatchSync(Collection)}
     * {@link IEntityStore#updateBatchSync(Collection)} methods.
     * <br>
     * The store populates the value to the prepared statement with the help of {@link AbstractDataStore#write(PreparedStatement, Object, boolean)}
     * with the 3rd parameter (boolean) set to false
     * @return String, The DML Query for Updation
     */
    @NotNull
    protected abstract String updateQuery();

    /**
     * Get the select all query which is used on {@link IEntityStore#all()} methods.
     * If you have custom requirements such as joins,
     * override the method and adjust the {@link AbstractDataStore#read(ResultSet)}
     * method accordingly.
     * <br>
     * <br>
     * <b>
     * The data is read using the abstract method {@link AbstractDataStore#read(ResultSet)}. This method is also
     * used on {@link IEntityStore#id(Object)} to read the data, So if the read requirement doesn't match. Please use
     * a custom method on the store interface.
     * </b>
     * @return String, The select query
     */
    @NotNull
    protected String selectQuery(){
        return "SELECT * FROM "+tableName()+";";
    }

    /**
     * Get the select all query which is used on {@link IEntityStore#id(Object)} methods.
     * If you have custom requirements such as joins,
     * override the method and adjust the {@link AbstractDataStore#read(ResultSet)}
     * method accordingly.
     * <br>
     * <br>
     * <b>
     * The data is read using the abstract method {@link AbstractDataStore#read(ResultSet)}. This method is also
     * used on {@link IEntityStore#all()} to read the data, So if the read requirement doesn't match. Please use
     * a custom method on the store interface.
     * </b>
     * @return String, The select query
     */
    @NotNull
    protected String selectByIdQuery(){
        return "SELECT * FROM " + tableName() + " WHERE `id` = ?";
    }

    /**
     * Get the delete query to delete the entity
     * @return String, The delete query
     */
    @NotNull
    protected String deleteQuery(){
        return "DELETE FROM " + tableName() + " WHERE `id` = ?";
    }

    /**
     * The Entity Type
     * @return Class of Entity Type
     */
    @NotNull
    protected abstract Class<E> entityType();

    /**
     * The type of unique id
     * @return Class of unique id of the entity
     */
    @NotNull
    protected abstract Class<I> idType();

    /**
     * Read the data of a result set with respect to the structure of the entity
     * If the read failed, Please provide an {@link Optional#empty()} instead of throwing an error
     * <br><br>
     * <b>NOTE: Please don't manipulate the cursor of the set inside the method, Its the responsibility of the caller methods</b>
     * @param resultSet The set which has the cursor pointed to the current read entity
     * @return An optional wrapped entity
     */
    @NotNull
    protected abstract Optional<E> read(@NotNull ResultSet resultSet);

    /**
     * Write to the prepared statement for update or insertion queries
     * <br><br>
     * <b>NOTE: Please don't call execute or other DbCommands with in the method, Its the responsibility of the caller method</b>
     * @param preparedStatement The prepared statement to write into
     * @param entity Entity which needs to be written
     * @param createStatement Whether the write is for insert or update
     */
    protected abstract void write(@NotNull PreparedStatement preparedStatement, @NotNull E entity ,boolean createStatement);
    @NotNull
    protected SQLDatabase getPluginDatabase() {
        return pluginDatabase;
    }
    @Override
    public CompletableFuture<Collection<E>> all() {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Collection<E>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(this::getAll);
    }

    @Override
    public CompletableFuture<Optional<E>> id(I id) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<E>> future = new CompletableFuture<>();
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
    public CompletableFuture<Optional<I>> createAsync(E entity) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<I>> future = new CompletableFuture<>();
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

    private Collection<E> getAll(){
        final Collection<E> result = new ArrayList<>();
        try (final Connection connection = getPluginDatabase().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(this.selectQuery());
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

    private Optional<I> create(E entity, Class<I> idType){
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
                return Optional.ofNullable(generatedId);
            }catch (Exception e){
                throw new RuntimeException("Failed to create entity. Reason: Unknown error", e);
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
             final PreparedStatement preparedStatement = prepareStatementWithId(connection, deleteQuery(), id);
             ) {
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Error delete data by ID - "+id.toString(), e);
        }
    }

    private Optional<E> fetchById(I id) {
        try (final Connection connection = getPluginDatabase().getConnection();
             final PreparedStatement preparedStatement = prepareStatementWithId(connection, selectByIdQuery(), id);
             final ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                return read(resultSet);
            }

            return Optional.empty();
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
        final PreparedStatement preparedStatement;
        if(isCreate){
            preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        }else{
            preparedStatement = connection.prepareStatement(query);
        }
        write(preparedStatement, entity, isCreate);
        return preparedStatement;
    }
}
