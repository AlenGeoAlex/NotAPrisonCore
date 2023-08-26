package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.common.json.JsonWrapper;
import me.alenalex.notaprisoncore.api.database.SQLDatabase;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.store.IMineMetaStore;
import me.alenalex.notaprisoncore.paper.constants.DbConstants;
import me.alenalex.notaprisoncore.paper.entity.MineMeta;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


public class MineMetaStore extends AbstractDataStore<IMineMeta, UUID> implements IMineMetaStore {

    public MineMetaStore(SQLDatabase pluginDatabase) {
        super(pluginDatabase);
    }

    @Override
    protected String tableName() {
        return DbConstants.TableNames.MINE_META;
    }

    @Override
    protected String insertQuery() {
        return DbConstants.MineMeta.INSERT_QUERY;
    }

    @Override
    protected String updateQuery() {
        return DbConstants.MineMeta.UPDATE_QUERY;
    }

    @Override
    protected Optional<IMineMeta> read(@NotNull ResultSet resultSet) {
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
            Location spawnPoint = parseLocation(spawnPointJson);
            Location lowerMinePoint = parseLocation(lowerMinePointJson);
            Location upperMinePoint = parseLocation(upperMinePointJson);
            Vector lowerMineRegion = JsonWrapper.WRAPPER.get().fromJson(lowerMineRegionJson, Vector.class);
            Vector upperMineRegion = JsonWrapper.WRAPPER.get().fromJson(upperMineRegionJson, Vector.class);

            Type additionalMapType = new TypeToken<HashMap<String, Location>>() {
            }.getType();
            HashMap<String, Location> additionalPositionalMapJson = JsonWrapper.WRAPPER.get().fromJson(additionalPositionMapJson, additionalMapType);

            meta = new MineMeta(id, new CuboidRegion(lowerMineRegion, upperMineRegion), lowerMinePoint, upperMinePoint, spawnPoint, additionalPositionalMapJson);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.ofNullable(meta);
    }

    @Override
    protected void write(@NotNull PreparedStatement preparedStatement,IMineMeta entity, boolean createStatement) {
        try {
            preparedStatement.setString(1, stringify(entity.getSpawnPoint()));
            preparedStatement.setString(2, stringify(entity.getLowerMiningPoint()));
            preparedStatement.setString(3, stringify(entity.getUpperMiningPoint()));
            preparedStatement.setString(4, stringify(entity.getMineSchematicLowerPoint()));
            preparedStatement.setString(5, stringify(entity.getMineSchematicUpperPoint()));
            preparedStatement.setString(6, JsonWrapper.WRAPPER.get().toJson(((MineMeta) entity).getLocationIdentifier()));
            if(!createStatement){
                preparedStatement.setString(7, entity.getMetaId().toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<Boolean> updateAsync(IMineMeta entity) {
        if(!getPluginDatabase().isConnected())
            return CompletableFuture.completedFuture(false);

        return null;
    }

    @Override
    public boolean updateBatchSync(Collection<IMineMeta> entities) {
        if(!getPluginDatabase().isConnected())
            return false;

        return false;
    }

}
