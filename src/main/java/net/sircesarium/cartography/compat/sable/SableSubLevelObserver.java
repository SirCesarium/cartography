package net.sircesarium.cartography.compat.sable;

import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import net.minecraft.server.level.ServerLevel;
import net.sircesarium.cartography.Cartography;
import net.sircesarium.cartography.data.storage.SubLevelData;
import net.sircesarium.cartography.helpers.FileIOHelper;
import net.sircesarium.cartography.manager.SubLevelManager;
import net.sircesarium.cartography.util.SubLevelScanner;

import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;

public class SableSubLevelObserver implements SubLevelObserver {

    private static final int SCAN_INTERVAL = 20;

    private final ServerLevel level;
    private int tickCounter;
    private volatile boolean closed;

    public SableSubLevelObserver(ServerLevel level) {
        this.level = level;
        this.tickCounter = 0;
        this.closed = false;
    }

    public void close() {
        Cartography.LOGGER.info("[Cartography] Observer close() called for level {}", level.dimension().location());

        this.closed = true;
    }

    @Override
    public void onSubLevelAdded(SubLevel subLevel) {
        UUID uuid = subLevel.getUniqueId();

        Cartography.LOGGER.info("[Cartography] onSubLevelAdded: {} closed={}", uuid, closed);

        if (closed) return;

        SubLevelData data = SubLevelManager.getOrCreate(level, uuid);

        data.loadPose();
        data.savePose();
    }

    @Override
    public void onSubLevelRemoved(SubLevel subLevel, SubLevelRemovalReason reason) {
        UUID uuid = subLevel.getUniqueId();
        Cartography.LOGGER.info("[Cartography] onSubLevelRemoved: {} reason={} closed={}", uuid, reason, closed);

        SubLevelData data = SubLevelManager.remove(uuid);

        if (data != null) {
            Cartography.LOGGER.info("[Cartography] Saving chunks before removal...");

            data.saveAllChunks();

            if (closed) return;

            Cartography.LOGGER.info("[Cartography] Chunks saved. Deleting folder async...");

            Path basePath = data.getBasePath();

            CompletableFuture.runAsync(() -> {
                Cartography.LOGGER.info("[Cartography] Deleting {}...", basePath);

                FileIOHelper.deleteRecursive(basePath);

                Cartography.LOGGER.info("[Cartography] Delete done.");
            });
        }
    }

    @Override
    public void tick(SubLevelContainer container) {
        if (closed) return;

        tickCounter++;

        if (tickCounter % SCAN_INTERVAL != 0) return;

        try {
            for (SubLevel subLevel : container.getAllSubLevels()) {
                UUID uuid = subLevel.getUniqueId();

                SubLevelData data = SubLevelManager.get(uuid);

                if (data == null) {
                    data = SubLevelManager.getOrCreate(level, uuid);
                }

                SubLevelScanner.scanSubLevel(subLevel, data);

                data.updatePose(
                        subLevel.logicalPose().position(),
                        subLevel.logicalPose().scale()
                );

                data.saveAllChunks();

                if (data.isPoseDirty()) {
                    data.savePose();
                }
            }
        } catch (Exception e) {
            Cartography.LOGGER.error("[Cartography] Error in sublevel tick", e);
        }
    }

}
