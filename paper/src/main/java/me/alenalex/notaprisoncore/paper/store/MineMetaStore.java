package me.alenalex.notaprisoncore.paper.store;

import com.google.common.reflect.TypeToken;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractDataStore;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


public class MineMetaStore extends AbstractDataStore<IMineMeta, UUID> implements IMineMetaStore {

    public MineMetaStore(PrisonDataStore store) {
        super(store.getPluginInstance().getPrisonSqlDatabase());
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
    protected void write(@NotNull PreparedStatement preparedStatement, @NotNull IMineMeta entity, boolean createStatement) {
        try {
            preparedStatement.setString(1, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getSpawnPoint()));
            preparedStatement.setString(2, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getLowerMiningPoint()));
            preparedStatement.setString(3, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getUpperMiningPoint()));
            preparedStatement.setString(4, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getMineSchematicLowerPoint()));
            preparedStatement.setString(5, IJsonWrapper.DEFAULT_INSTANCE.stringify(entity.getMineSchematicUpperPoint()));
            preparedStatement.setString(6, IJsonWrapper.DEFAULT_INSTANCE.stringify(((MineMeta) entity).getLocationIdentifier()));
            if(!createStatement){
                preparedStatement.setString(7, entity.getMetaId().toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(){

    }
}
