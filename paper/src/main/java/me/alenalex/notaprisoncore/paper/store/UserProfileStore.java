package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStoreWithDirectory;
import me.alenalex.notaprisoncore.api.common.Octet;
import me.alenalex.notaprisoncore.api.common.Triplet;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.DatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.FailedDatabaseException;
import me.alenalex.notaprisoncore.api.exceptions.meta.FailedMetaDataInitialization;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.store.IUserProfileStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import me.alenalex.notaprisoncore.paper.entity.dataholder.LocalEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.entity.profile.PrisonUserProfile;
import me.alenalex.notaprisoncore.paper.entity.profile.UserSocial;
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
import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class UserProfileStore extends AbstractDataStoreWithDirectory<IPrisonUserProfile, UUID> implements IUserProfileStore {
    private final PrisonDataStore prisonDataStore;
    private final File userDataDirectory;
    public UserProfileStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getSqlDatabase(),
                new File(prisonDataStore.getStoreParentDirectory(), "meta"+File.separator+"profile"),
                prisonDataStore.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getCompressionConfiguration().isCompressMineLocalData()
        );
        this.prisonDataStore = prisonDataStore;
        this.userDataDirectory = new File(prisonDataStore.getStoreParentDirectory(), "meta"+File.separator+"user");
        if(!this.userDataDirectory.exists())
            this.userDataDirectory.mkdirs();
    }

    @Override
    protected @NotNull String tableName() {
        return DbConstants.TableNames.USER_PROFILE;
    }

    @Override
    protected @Nullable String insertQuery() {
        throw new NotImplementedException("Insert Query on user-profile is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String updateQuery() {
        return DbConstants.UserProfile.UPDATE_QUERY;
    }

    @Override
    protected @Nullable String selectQuery() {
        throw new NotImplementedException("Select Query on user-profile is not implemented, It is handled internally by its own corresponding methods!");
    }

    @Override
    protected @Nullable String selectByIdQuery() {
        return DbConstants.UserProfile.GET_OR_CREATE_PROCEDURE;
    }

    @Override
    protected @Nullable String deleteQuery() {
        throw new NotImplementedException("Delete Query on user-profile is not implemented, It is handled internally by its own corresponding methods!");
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
        throw new NotImplementedException("Read on user-profile is not implemented, It is handled internally by its own corresponding methods!");
    }



    @Override
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IPrisonUserProfile entity, boolean createStatement, @Nullable Object predefinedId) {
        //No need to check createStatement or not, only update is possible in this
        try {
            preparedStatement.setString(1, ((SharedEntityMetaDataHolder) entity.getSharedDataHolder()).encode());
            preparedStatement.setLong(2, entity.getPlayerLevel());
            preparedStatement.setString(3, entity.getPoints().toString());
            preparedStatement.setString(4, entity.getLocaleType());
            preparedStatement.setString(5, entity.getUserId().toString());
        } catch (SQLException e) {
            throw new DatastoreException(e);
        }
    }

    @Override
    public CompletableFuture<Optional<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>>> getOrCreateUserProfile(UUID id){
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> getOrCreateInternal(id));
    }

    private Optional<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>> getOrCreateInternal(UUID userId){
        try(final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement statement = prepareCallableStatementForId(connection, selectByIdQuery(), userId.toString())
        ) {
            statement.execute();
            boolean newProfile = statement.getBoolean("NewProfile");
            ResultSet resultSet = statement.getResultSet();
            if(!resultSet.next()){
                throw new FailedDatabaseException("Failed to create or get user "+userId.toString()+" from sql database.");
            }

            Optional<IPrisonUserProfile> userProfileOptional = readUserData(resultSet, newProfile);
            if(!userProfileOptional.isPresent()){
                throw new FailedDatabaseException("Failed to read the user data of "+userId.toString()+" from sql database.");
            }

            IPrisonUserProfile userProfile = userProfileOptional.get();
            List<IUserSocial> userSocials = Collections.emptyList();
            statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
            try(final ResultSet set = statement.getResultSet()){
                userSocials = getUserSocials(set);
            }

            IMine mine = null;
            boolean hasMineData = statement.getMoreResults(Statement.CLOSE_CURRENT_RESULT);
            if(hasMineData && userProfile.hasMine()){
                try (ResultSet set = statement.getResultSet()){
                    mine = extractMine(set);
                }

                if(mine != null){
                    fetchAndSetBlockChoices(statement, mine);
                }
            }

            return Optional.of(new Octet<>(userProfile, userSocials, mine, newProfile));
        }catch (Exception e){
            e.printStackTrace();
            throw new FailedDatabaseException("Failed to get the user from database. ",e);
        }
    }


    private IMine extractMine(ResultSet set) throws SQLException, FailedMetaDataInitialization {
        if(set.next()){
            MineMeta meta = extractMetaFromResultSet(set);
            String rawId = set.getString("mine_id");
            String ownerIdRaw = set.getString("owner_id");
            int mineAccess = set.getInt("mine_access");
            String binarySharedData = set.getString("shared_data");
            BigDecimal vaultBalance = set.getBigDecimal("vault_balance");



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

        return null;
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
        org.bukkit.util.Vector lowerMineRegion =  GsonWrapper.singleton().fromString(lowerMineRegionJson, org.bukkit.util.Vector.class);
        org.bukkit.util.Vector upperMineRegion =  GsonWrapper.singleton().fromString(upperMineRegionJson, Vector.class);

        Type additionalMapType = new TypeToken<HashMap<String, Location>>() {
        }.getType();
        HashMap<String, Location> additionalPositionalMapJson = GsonWrapper.singleton().fromString(additionalPositionMapJson, additionalMapType);

        return new MineMeta(metaId, new CuboidRegion(BukkitUtil.toVector(lowerMineRegion), BukkitUtil.toVector(upperMineRegion)), lowerMinePoint, upperMinePoint, spawnPoint, additionalPositionalMapJson);
    }

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

    private List<IUserSocial> getUserSocials(ResultSet set) throws SQLException {
        List<IUserSocial> returnList = new ArrayList<>();
        while (set.next()){
            String socialIdRaw = set.getString("id");
            String sourceIdRaw = set.getString("source_id");
            String targetIdRaw = set.getString("target_id");
            int statusOrdinal = set.getInt("status");
            Timestamp requested = set.getTimestamp("requested");
            Timestamp actedOn = set.getTimestamp("acted");

            UUID socialId = UUID.fromString(socialIdRaw);
            UUID sourceId = UUID.fromString(sourceIdRaw);
            UUID targetId = UUID.fromString(targetIdRaw);
            SocialStatus status = SocialStatus.values()[statusOrdinal - 1];

            UserSocial social = new UserSocial(socialId, sourceId, targetId, status, requested, actedOn);
            returnList.add(social);
        }
        return returnList;
    }

    /**
     * Get the user profile. This will create a new profile if the existing profile doesn't exists
     * @param id of the entity
     * @return
     */
    @Override
    @Deprecated
    public CompletableFuture<Optional<IPrisonUserProfile>> id(UUID id) {
        if (!getPluginDatabase().isConnected()) {
            CompletableFuture<Optional<IPrisonUserProfile>> future = new CompletableFuture<>();
            future.completeExceptionally(new DatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(() -> idInternal(id));
    }

    private Optional<IPrisonUserProfile> idInternal(UUID uuid){
        try(final Connection connection = getPluginDatabase().getConnection();
            final CallableStatement statement = prepareCallableStatementForId(connection, selectByIdQuery(), uuid.toString())
        ) {
            statement.execute();
            boolean newProfile = statement.getBoolean("NewProfile");
            ResultSet resultSet = statement.getResultSet();
            if(!resultSet.next()){
                throw new FailedDatabaseException("Failed to create or get user "+uuid.toString()+" from sql database.");
            }

            Optional<IPrisonUserProfile> userProfileOptional = readUserData(resultSet, newProfile);
            if(!userProfileOptional.isPresent()){
                throw new FailedDatabaseException("Failed to read the user data of "+uuid.toString()+" from sql database.");
            }

            IPrisonUserProfile userProfile = userProfileOptional.get();
            return userProfileOptional;
        }catch (Exception e){
            e.printStackTrace();
            throw new FailedDatabaseException("Failed to get the user from database. ",e);
        }
    }

    private CallableStatement prepareCallableStatementForId(Connection connection, String query, String id) throws SQLException {
        CallableStatement callableStatement = connection.prepareCall(query);
        callableStatement.registerOutParameter("NewProfile", Types.BIT);
        callableStatement.setString(1, id);
        return callableStatement;
    }



    private Optional<IPrisonUserProfile> readUserData(ResultSet resultSet, boolean newProfile){
        IPrisonUserProfile userProfile = null;
        try {
            String userIdRaw = resultSet.getString("id");
            String sharedBinaryData = resultSet.getString("shared_data");
            Timestamp createdAt = resultSet.getTimestamp("created_at");
            Timestamp lastLoggedIn = resultSet.getTimestamp("last_logged_in");
            long level = resultSet.getLong("level");
            String pointRaw = resultSet.getString("points");
            BigInteger points = null;
            if(pointRaw == null || pointRaw.isEmpty()){
                points = new BigInteger(String.valueOf(level * 100));
            }else{
                points = new BigInteger(pointRaw);
            }
            String mineIdRaw = resultSet.getString("mine_id");
            String localeKey = resultSet.getString("locale_key");
            LocalEntityMetaDataHolder localDataHolder = null;
            if(newProfile){
                localDataHolder = new LocalEntityMetaDataHolder();
                writeLocal(userIdRaw, localDataHolder.encode());
            }else{
                try {
                    localDataHolder = LocalEntityMetaDataHolder.decode(readLocal(userIdRaw), LocalEntityMetaDataHolder.class);
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    if(localDataHolder == null)
                        localDataHolder = new LocalEntityMetaDataHolder();
                }
            }
            SharedEntityMetaDataHolder sharedEntityMetaDataHolder = null;
            if(sharedBinaryData == null) {
                sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();
            }else{
                sharedEntityMetaDataHolder = SharedEntityMetaDataHolder.decode(sharedBinaryData, SharedEntityMetaDataHolder.class);
            }

            UUID userId = UUID.fromString(userIdRaw);
            UUID mineId = (mineIdRaw == null || mineIdRaw.isEmpty()) ? null : UUID.fromString(mineIdRaw);
            userProfile = new PrisonUserProfile(userId, localeKey, localDataHolder, sharedEntityMetaDataHolder, createdAt, lastLoggedIn, level, points, mineId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return Optional.ofNullable(userProfile);
    }
}
