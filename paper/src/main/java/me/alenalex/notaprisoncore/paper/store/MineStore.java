package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.api.exceptions.database.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.store.IMineStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import me.alenalex.notaprisoncore.paper.constants.LocaleConstants;
import me.alenalex.notaprisoncore.paper.entity.mine.BlockChoices;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class MineStore extends AbstractDataStore<IMine, UUID> implements IMineStore {
    private final PrisonDataStore prisonDataStore;
    public MineStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getPrisonSqlDatabase());
        this.prisonDataStore = prisonDataStore;
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
    protected @Nullable String selectQuery() {
        return null;
    }

    @Override
    protected @Nullable String selectByIdQuery() {
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
    public CompletableFuture<Optional<IMine>> id(@NotNull UUID id) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<IMine>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> idInternal(id));
    }

    private Optional<IMine> idInternal(UUID id){
        String query = "{CALL FetchMineById(?)}";
        try (final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement callableStatement = prepareGetMineByIdCallable(connection, query, id);
        ){

            boolean hasRows = callableStatement.execute();
            if(!hasRows)
                return Optional.empty();

            IMine mine = null;

            try (ResultSet resultSet = callableStatement.getResultSet()){
                MineMeta meta = null;
                if(resultSet.next()){
                    String spawnPointJson = resultSet.getString("spawn_point");
                    String lowerMinePointJson = resultSet.getString("lower_mine_point");
                    String upperMinePointJson = resultSet.getString("upper_mine_point");
                    String lowerMineRegionJson = resultSet.getString("lower_mine_region");
                    String upperMineRegionJson = resultSet.getString("upper_mine_region");
                    String additionalPositionMapJson = resultSet.getString("additional_position_map");
                    String mineMetaIdRaw = resultSet.getString("mine_meta_id");

                    UUID metaId = UUID.fromString(mineMetaIdRaw);
                    Location spawnPoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(spawnPointJson, Location.class);
                    Location lowerMinePoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(lowerMinePointJson, Location.class);
                    Location upperMinePoint =  IJsonWrapper.DEFAULT_INSTANCE.fromString(upperMinePointJson, Location.class);
                    Vector lowerMineRegion =  IJsonWrapper.DEFAULT_INSTANCE.fromString(lowerMineRegionJson, Vector.class);
                    Vector upperMineRegion =  IJsonWrapper.DEFAULT_INSTANCE.fromString(upperMineRegionJson, Vector.class);

                    Type additionalMapType = new TypeToken<HashMap<String, Location>>() {
                    }.getType();
                    HashMap<String, Location> additionalPositionalMapJson = IJsonWrapper.DEFAULT_INSTANCE.fromString(additionalPositionMapJson, additionalMapType);

                    meta = new MineMeta(metaId, new CuboidRegion(lowerMineRegion, upperMineRegion), lowerMinePoint, upperMinePoint, spawnPoint, additionalPositionalMapJson);

                    String rawId = resultSet.getString("mine_id");
                    String ownerIdRaw = resultSet.getString("owner_id");
                    int mineAccess = resultSet.getInt("mine_access");
                    String binarySharedData = resultSet.getString("shared_data");
                    BigDecimal vaultBalance = resultSet.getBigDecimal("vault_balance");

                    UUID mineId = UUID.fromString(rawId);
                    UUID ownerId = UUID.fromString(ownerIdRaw);
                    MineAccess access = MineAccess.values()[mineAccess - 1];
                    mine = new Mine(ownerId, mineId, meta, vaultBalance);
                    mine.access(access);
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new DatastoreException("Failed to fetch the meta of id "+id.toString());
            }
            if(mine == null){
                return Optional.empty();
            }

            boolean hasBlockChoices = callableStatement.getMoreResults();

            if(!hasBlockChoices){
                mine.getBlockChoices().clearAndSetDefault();
                return Optional.of(mine);
            }

            try (ResultSet resultSet = callableStatement.getResultSet()) {
                List<BlockEntry> blockEntryList = new ArrayList<>();
                while (resultSet.next()){
                    byte data = (byte) resultSet.getInt("data");
                    String materialName = resultSet.getString("material_type");
                    Material material = Material.getMaterial(materialName);

                    BlockEntry entry = new BlockEntry(material, data);
                    blockEntryList.add(entry);
                }

                if(blockEntryList.isEmpty()){
                    mine.getBlockChoices().clearAndSetDefault();
                }

                mine.getBlockChoices().setChoices(blockEntryList);
            }catch (Exception e){
                mine.sendPluginNotification(ChatColor.RED+"Failed to load your mine data. Please login again or if the issue still persist, Open up a support ticket at discord");
                e.printStackTrace();
                throw new DatastoreException("Failed to load block choices of the player "+mine.getOwnerId()+". The player has been notified");
            }

            return Optional.of(mine);
        }catch (Exception e){
            e.printStackTrace();
            throw new DatastoreException("Failed to load the mine with "+id.toString()+".", e);
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
            String mineId = callableStatement.getString("MineId");
            if(mineId == null || mineId.isEmpty())
                return Optional.empty();

            return Optional.of(UUID.fromString(mineId));
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
        statement.setString(4, null);
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
}
