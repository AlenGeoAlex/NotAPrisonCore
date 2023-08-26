package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractFileStore;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.api.exceptions.store.world.WorldDataSaveException;
import me.alenalex.notaprisoncore.api.store.IWorldStore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.constants.StoreConstants;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class WorldStore extends AbstractFileStore implements IWorldStore {
    private final PrisonDataStore prisonDataStore;
    private AtomicInteger distance = new AtomicInteger(0);
    private Location defaultLocation;
    public WorldStore(PrisonDataStore prisonDataStore) {
        super("world-store", new File(prisonDataStore.getStoreParentDirectory(), "world-store.dat"));
        this.prisonDataStore = prisonDataStore;
    }

    public void load(){
        if(!this.getStoreDocument().contains(StoreConstants.WorldStoreConstants.DISTANCE_KEY)){
            set();
        }
        this.distance = new AtomicInteger(this.getStoreDocument().getInt(StoreConstants.WorldStoreConstants.DISTANCE_KEY));
        this.defaultLocation = this.getPrisonDataStore().getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().mineWorldConfiguration().getDefaultLocation().to().orElse(null);
    }


    public PrisonDataStore getPrisonDataStore() {
        return prisonDataStore;
    }

    private void set(boolean save){
        this.getStoreDocument().set(StoreConstants.WorldStoreConstants.DISTANCE_KEY, distance.get());
        if(save) {
            try {
                this.getStoreDocument().save(getStoreFile());
                this.prisonDataStore.getPluginInstance().getLogger().info("Saved World Data Store!");
            } catch (IOException e) {
                throw new WorldDataSaveException(e);
            }
        }
    }

    private void set(){
        this.set(true);
    }

    public int getDistance() {
        return distance.get();
    }

    @Override
    @NotNull
    public synchronized Location nextFreeLocation() {
        int atomicDistance = this.distance.incrementAndGet();
        set();
        return this.getXZForCurrent(atomicDistance);
    }

    private Location getXZForCurrent(int np) {
        if (np == 0) {
            return this.defaultLocation;
        }
        int dx = this.getPrisonDataStore().getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().mineWorldConfiguration().getMineDistance();
        int dy = 0;
        int segment_length = 1;
        int x = 0;
        int y = 0;
        int segment_passed = 0;
        for (int n = 0; n < np; ++n) {
            x += dx;
            y += dy;
            if (++segment_passed != segment_length) continue;
            segment_passed = 0;
            int buffer = dy;
            dy = -dx;
            dx = buffer;
            if (dx != 0) continue;
            ++segment_length;
        }
        return new Location(this.defaultLocation.getWorld(), x, this.defaultLocation.getBlockY(), y);
    }
}
