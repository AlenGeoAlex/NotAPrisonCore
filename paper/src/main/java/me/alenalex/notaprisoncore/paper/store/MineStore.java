package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStoreWithFile;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.store.IMineStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import me.alenalex.notaprisoncore.paper.entity.dataholder.LocalEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.mine.BlockChoices;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.apache.commons.lang3.NotImplementedException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MineStore extends AbstractDataStoreWithFile<IMine, UUID> implements IMineStore {
    private final PrisonDataStore prisonDataStore;
    public MineStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getSqlDatabase(),
                new File(prisonDataStore.getStoreParentDirectory(), "meta"+File.separator+"mine"),
                prisonDataStore.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getCompressionConfiguration().isCompressMineLocalData()
        );
        this.prisonDataStore = prisonDataStore;
    }

    @Override
    protected @NotNull String tableName() {
        return DbConstants.TableNames.MINES;
    }

    @Override
    protected @Nullable String insertQuery() {
        throw new NotImplementedException("Insert Query on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String updateQuery() {
        throw new NotImplementedException("Update Query on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String selectQuery() {
        throw new NotImplementedException("Select Query on Mine-Store is not implemented, It doesn't have a select all implementation");
    }

    @Override
    protected @Nullable String selectByIdQuery() {
        throw new NotImplementedException("Select By Id Query on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
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
        throw new NotImplementedException("Read on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IMine entity, boolean createStatement, @Nullable Object predefinedId) {
        throw new NotImplementedException("Write on Mine-Store is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    public CompletableFuture<Optional<IMine>> id(@NotNull UUID id) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<IMine>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> idInternal(id));
    }

    @Override
    public CompletableFuture<Boolean> updateAsync(IMine entity) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> updateInternal((Mine) entity));
    }

    @Override
    public boolean updateBatchSync(Collection<IMine> entities) {
        if (!getPluginDatabase().isConnected()) {
            throw new DatabaseNotAvailableException();
        }
        String query = "{CALL UpdateMine(?, ?, ?, ?, ?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement statement = connection.prepareCall(query);
        ) {
            for (IMine entity : entities) {
                prepareUpdateMineCallable(statement, (Mine) entity);
                statement.addBatch();
            }
            statement.executeUpdate();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean updateInternal(Mine mine){
        String query = "{CALL UpdateMine(?, ?, ?, ?, ?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
             final CallableStatement callableStatement = prepareUpdateMineCallable(connection, query, mine)
        ) {
            callableStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private Optional<IMine> idInternal(UUID id){
        String query = "{CALL FetchMineById(?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
             final CallableStatement callableStatement = prepareGetMineByIdCallable(connection, query, id);
        ) {
            // Execute the stored procedure
            boolean hasRows = callableStatement.execute();

            // If no rows are returned, return an empty Optional
            if (!hasRows)
                return Optional.empty();

            IMine mine = extractMineFromResultSet(callableStatement, id);
            if(mine == null)
                return Optional.empty();

            // Fetch and set block choices
            fetchAndSetBlockChoices(callableStatement, mine);

            return Optional.of(mine);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatastoreException("Failed to load the mine with " + id.toString(), e);
        }
    }

    private MineMeta extractMetaFromResultSet(ResultSet resultSet) throws SQLException{
        String spawnPointJson = resultSet.getString("spawn_point");
        String lowerMinePointJson = resultSet.getString("lower_mine_point");
        String upperMinePointJson = resultSet.getString("upper_mine_point");
        String lowerMineRegionJson = resultSet.getString("lower_mine_region");
        String upperMineRegionJson = resultSet.getString("upper_mine_region");
        String additionalPositionMapJson = resultSet.getString("additional_position_map");
        String mineMetaIdRaw = resultSet.getString("mine_meta_id");

        UUID metaId = UUID.fromString(mineMetaIdRaw);
        Location spawnPoint =  GsonWrapper.singleton().fromString(spawnPointJson, Location.class);
        Location lowerMinePoint =  GsonWrapper.singleton().fromString(lowerMinePointJson, Location.class);
        Location upperMinePoint =  GsonWrapper.singleton().fromString(upperMinePointJson, Location.class);
        Vector lowerMineRegion =  GsonWrapper.singleton().fromString(lowerMineRegionJson, Vector.class);
        Vector upperMineRegion =  GsonWrapper.singleton().fromString(upperMineRegionJson, Vector.class);

        Type additionalMapType = new TypeToken<HashMap<String, Location>>() {
        }.getType();
        HashMap<String, Location> additionalPositionalMapJson = GsonWrapper.singleton().fromString(additionalPositionMapJson, additionalMapType);

        return new MineMeta(metaId, new CuboidRegion(BukkitUtil.toVector(lowerMineRegion), BukkitUtil.toVector(upperMineRegion)), lowerMinePoint, upperMinePoint, spawnPoint, additionalPositionalMapJson);
    }

    // Extracts IMine from the ResultSet
    private IMine extractMineFromResultSet(CallableStatement callableStatement, UUID id) throws SQLException {
        try (ResultSet resultSet = callableStatement.getResultSet()) {
            if (resultSet.last()) {
                MineMeta meta = extractMetaFromResultSet(resultSet);
                String rawId = resultSet.getString("mine_id");
                String ownerIdRaw = resultSet.getString("owner_id");
                int mineAccess = resultSet.getInt("mine_access");
                String binarySharedData = resultSet.getString("shared_data");
                BigDecimal vaultBalance = resultSet.getBigDecimal("vault_balance");

                UUID mineId = UUID.fromString(rawId);
                UUID ownerId = UUID.fromString(ownerIdRaw);
                MineAccess access = MineAccess.values()[mineAccess - 1];
                SharedEntityMetaDataHolder sharedDataHolder = null;
                if(binarySharedData == null){
                    sharedDataHolder = new SharedEntityMetaDataHolder();
                }else{
                    sharedDataHolder = SharedEntityMetaDataHolder.decode(binarySharedData, SharedEntityMetaDataHolder.class);
                }
                LocalEntityMetaDataHolder localDataHolder = null;
                try {
                    localDataHolder = LocalEntityMetaDataHolder.decode(readLocal(mineId.toString()), LocalEntityMetaDataHolder.class);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(localDataHolder == null)
                        localDataHolder = new LocalEntityMetaDataHolder();
                }

                Mine mine = new Mine(ownerId, mineId, meta, vaultBalance, localDataHolder, sharedDataHolder);
                mine.access(access);
                return mine;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DatastoreException("Failed to fetch the meta of id " + id.toString());
        }
        return null;
    }

    // Fetches and sets block choices for the given mine
    private void fetchAndSetBlockChoices(CallableStatement callableStatement, IMine mine) throws SQLException {
        boolean hasBlockChoices = callableStatement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
        if (!hasBlockChoices) {
            mine.getBlockChoices().clearAndSetDefault();
            return;
        }

        try (ResultSet blockChoiceSet = callableStatement.getResultSet()) {
            blockChoiceSet.beforeFirst();
            List<BlockEntry> blockEntryList = new ArrayList<>();

            while (blockChoiceSet.next()) {
                byte data = (byte) blockChoiceSet.getInt("data");
                String materialName = blockChoiceSet.getString("material_type");
                Material material = Material.getMaterial(materialName);

                BlockEntry entry = new BlockEntry(material, data);
                blockEntryList.add(entry);
            }

            if (blockEntryList.isEmpty()) {
                mine.getBlockChoices().clearAndSetDefault();
            } else {
                mine.getBlockChoices().setChoices(blockEntryList);
            }
        } catch (Exception e) {
            mine.sendPluginNotification(ChatColor.RED + "Failed to load your mine data. Please login again or if the issue still persists, open up a support ticket at discord");
            e.printStackTrace();
            throw new DatastoreException("Failed to load block choices of the player " + mine.getOwnerId() + ". The player has been notified");
        }
    }

    @Override
    public CompletableFuture<Optional<UUID>> createAsync(IMine entity) {
        return claimMine(entity);
    }

    @Override
    public CompletableFuture<Optional<UUID>> claimMine(IMine mine) {
        if(mine == null){
            CompletableFuture<Optional<UUID>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatastoreException("Failed to claim a mine when the provided mine is null"));
            return future;
        }

        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<UUID>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> claimMineInternal((Mine) mine));
    }

    private Optional<UUID> claimMineInternal(Mine mine){
        String statementQuery = "{CALL ClaimMine(?, ?, ?, ?, ?, ?, ?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement callableStatement = prepareClaimMineCallable(connection, statementQuery, mine);
        ){
            callableStatement.execute();
            String rawMineId = callableStatement.getString("MineId");
            if(rawMineId == null || rawMineId.isEmpty())
                return Optional.empty();
            UUID mineId = UUID.fromString(rawMineId);
            try{
                writeLocal(mineId.toString(), ((LocalEntityMetaDataHolder) mine.getLocalMetaDataHolder()).encode());
            }catch (Exception e){
                e.printStackTrace();
                mine.sendPluginNotification("Failed to save the local meta-data to the store. Please notify the admins");
            }
            return Optional.of(mineId);
        }catch (Exception e){
            e.printStackTrace();
            throw new DatastoreException(e);
        }
    }

    private CallableStatement prepareClaimMineCallable(Connection connection, String query, Mine mine) throws SQLException {
        CallableStatement statement = connection.prepareCall(query);
        statement.setString(1, mine.getMetaId().toString());
        statement.setString(2, mine.getOwnerId().toString());
        statement.setInt(3, mine.access().ordinal());
        statement.setString(4, ((SharedEntityMetaDataHolder) mine.getSharedMetaDataHolder()).encode());
        statement.setString(5, ((BlockChoices) mine.getBlockChoices()).toJson());
        statement.setBigDecimal(6, mine.getVault().getBalance());
        statement.registerOutParameter("MineId", Types.VARCHAR);
        return statement;
    }

    private CallableStatement prepareGetMineByIdCallable(Connection connection, String query, UUID id) throws SQLException {
        CallableStatement statement = connection.prepareCall(query);
        statement.setString(1, id.toString());
        return statement;
    }

    private CallableStatement prepareUpdateMineCallable(Connection connection, String query, Mine mine) throws SQLException{
        CallableStatement statement = connection.prepareCall(query);
        statement.setInt(1, mine.access().ordinal());
        statement.setBigDecimal(2, mine.getVault().getBalance());
        statement.setString(3, ((SharedEntityMetaDataHolder) mine.getSharedMetaDataHolder()).encode());
        statement.setString(4, ((BlockChoices) mine.getBlockChoices()).toJson());
        statement.setString(5, mine.getId().toString());
        return statement;
    }

    private void prepareUpdateMineCallable(CallableStatement statement, Mine mine) throws SQLException {
        statement.setInt(1, mine.access().ordinal());
        statement.setBigDecimal(2, mine.getVault().getBalance());
        statement.setString(3, ((SharedEntityMetaDataHolder) mine.getSharedMetaDataHolder()).encode());
        statement.setString(4, ((BlockChoices) mine.getBlockChoices()).toJson());
        statement.setString(5, mine.getId().toString());
    }


}
