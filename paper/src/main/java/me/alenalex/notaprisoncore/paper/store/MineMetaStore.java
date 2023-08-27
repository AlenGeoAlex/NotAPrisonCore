package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.database.FailedDatabaseException;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.store.IMineMetaStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import me.alenalex.notaprisoncore.paper.entity.MineMeta;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MineMetaStore extends AbstractDataStore<IMineMeta, UUID> implements IMineMetaStore {

    private final PrisonDataStore store;
    public MineMetaStore(PrisonDataStore store) {
        super(store.getPluginInstance().getPrisonSqlDatabase());
        this.store = store;
    }

    @Override
    protected @NotNull String tableName() {
        return DbConstants.TableNames.MINE_META;
    }

    @Override
    protected @NotNull String insertQuery() {
        return DbConstants.MineMeta.INSERT_QUERY;
    }

    @Override
    protected @NotNull String updateQuery() {
        return DbConstants.MineMeta.UPDATE_QUERY;
    }

    @Override
    protected @NotNull Class<IMineMeta> entityType() {
        return IMineMeta.class;
    }

    @Override
    protected @NotNull Class<UUID> idType() {
        return UUID.class;
    }

    @Override
    protected @NotNull Optional<IMineMeta> read(@NotNull ResultSet resultSet) {
        IMineMeta meta = null;
        try {
            String spawnPointJson = resultSet.getString("spawn_point");
            String lowerMinePointJson = resultSet.getString("lower_mine_point");
            String upperMinePointJson = resultSet.getString("upper_mine_point");
            String lowerMineRegionJson = resultSet.getString("lower_mine_region");
            String upperMineRegionJson = resultSet.getString("upper_mine_region");
            String additionalPositionMapJson = resultSet.getString("additional_position_map");
            String idRaw = resultSet.getString("id");

            UUID id = UUID.fromString(idRaw);
            Location spawnPoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(spawnPointJson, Location.class);
            Location lowerMinePoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(lowerMinePointJson, Location.class);
            Location upperMinePoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(upperMinePointJson, Location.class);
            Vector lowerMineRegion =  IJsonWrapper.DEFAULT_INSTANCE.fromString(lowerMineRegionJson, Vector.class);
            Vector upperMineRegion =  IJsonWrapper.DEFAULT_INSTANCE.fromString(upperMineRegionJson, Vector.class);

            Type additionalMapType = new TypeToken<HashMap<String, Location>>() {
            }.getType();
            HashMap<String, Location> additionalPositionalMapJson = IJsonWrapper.DEFAULT_INSTANCE.fromString(additionalPositionMapJson, additionalMapType);

            meta = new MineMeta(id, new CuboidRegion(lowerMineRegion, upperMineRegion), lowerMinePoint, upperMinePoint, spawnPoint, additionalPositionalMapJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.ofNullable(meta);
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IMineMeta entity, boolean createStatement, @Nullable Object predefinedId) {
        try {
            preparedStatement.setString(1, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getSpawnPoint()));
            preparedStatement.setString(2, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getLowerMiningPoint()));
            preparedStatement.setString(3, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getUpperMiningPoint()));
            preparedStatement.setString(4, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getMineSchematicLowerPoint()));
            preparedStatement.setString(5, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getMineSchematicUpperPoint()));
            preparedStatement.setString(6, IJsonWrapper.DEFAULT_INSTANCE.stringify(((MineMeta) entity).getLocationIdentifier()));
            if(createStatement){
                if(predefinedId == null)
                    throw new RuntimeException("Please provide a valid ID for mine meta");

                preparedStatement.setString(7, predefinedId.toString());
            }else{
                preparedStatement.setString(7, entity.getMetaId().toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){

    }

    @Override
    public CompletableFuture<Collection<IMineMeta>> reserveMetas() {
        String serverName = this.store.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getServerName();
        int metaReservationCount = this.store.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getMetaReservationCount();
        return reserveMetas(serverName, metaReservationCount);
    }

    @Override
    public CompletableFuture<Collection<IMineMeta>> reserveMetas(@NotNull String serverName, @Range(from = 1, to = Integer.MAX_VALUE) int reservationCount) {
        if(serverName.isEmpty())
        {
            CompletableFuture<Collection<IMineMeta>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatastoreException("Invalid server name provided"));
            return future;
        }
        return CompletableFuture.supplyAsync(() -> reserveMetasInternal(serverName, reservationCount));
    }

    @Override
    public CompletableFuture<Boolean> releaseReservedMetas() {
        String serverName = this.store.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getServerName();
        return releaseReservedMetas(serverName);
    }

    @Override
    public CompletableFuture<Boolean> releaseReservedMetas(String serverName) {
        if(serverName.isEmpty()){
            CompletableFuture<Boolean> response = new CompletableFuture<>();
            response.completeExceptionally(new DatastoreException("Invalid server name provided for releasing reserved metas"));
            return response;
        }
        return CompletableFuture.supplyAsync(() -> releaseReservedMetasInternal(serverName));
    }

    private boolean releaseReservedMetasInternal(String serverName){
        String procedureQuery = "{CALL ReleaseReservedMeta(?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
             final CallableStatement callableStatement = prepareReleaseReservationMetaStatement(connection, procedureQuery ,serverName);
        ) {
            callableStatement.execute();
        }catch (Exception e){
            throw new FailedDatabaseException("Failed to reserve mine metas from SQL. Check stacktrace for more...",e);
        }
        return true;
    }

    private Collection<IMineMeta> reserveMetasInternal(String serverName, int reservationCount){
        Collection<IMineMeta> metaCollection = new ArrayList<>();
        String procedureQuery = "{CALL ReserveMineMeta(?, ?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
             final CallableStatement callableStatement = prepareReservationMetaStatement(connection, procedureQuery ,serverName, reservationCount);
             final ResultSet resultSet = callableStatement.executeQuery();
             ) {
            while (resultSet.next()){
                Optional<IMineMeta> metaOptional = read(resultSet);
                metaOptional.ifPresent(metaCollection::add);
            }
        }catch (Exception e){
            throw new FailedDatabaseException("Failed to reserve mine metas from SQL. Check stacktrace for more...",e);
        }
        return metaCollection;
    }

    private CallableStatement prepareReservationMetaStatement(Connection connection, String procedureQuery ,String serverName, int reservationCount) throws SQLException {
        CallableStatement statement = connection.prepareCall(procedureQuery);
        statement.setString(1, serverName);
        statement.setInt(2, reservationCount);
        return statement;
    }

    private CallableStatement prepareReleaseReservationMetaStatement(Connection connection, String procedureQuery, String serverName) throws SQLException {
        CallableStatement statement = connection.prepareCall(procedureQuery);
        statement.setString(1, serverName);
        return statement;
    }
}
