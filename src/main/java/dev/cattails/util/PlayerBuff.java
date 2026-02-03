package dev.cattails.util;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

import static dev.cattails.SurviveTheNight.INSTANCE;

public final class PlayerBuff extends DelayedEntitySystem<EntityStore> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public PlayerBuff() {
        super(5.0f);
    }

    @Nonnull
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    @Override
    public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
        Player player = store.getComponent(ref, Player.getComponentType());

        if(player == null) {

            LOGGER.atInfo().log("Player not found at index: " + index);
            return;
        }

        World world = player.getWorld();
        if(world == null) {

            LOGGER.atInfo().log("World not found for player: " + player.getDisplayName());
            return;
        }

        world.execute(() -> {

            PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerBuffsStoreComponent());

            EntityStatMap statMap = store.getComponent(ref, EntityStatMap.getComponentType());
            if (statMap == null) return;

            statMap.addStatValue(DefaultEntityStatTypes.getHealth(), playerBuffStore.getHealthBoost());
            statMap.addStatValue(DefaultEntityStatTypes.getStamina(), playerBuffStore.getStaminaBoost());
            statMap.addStatValue(DefaultEntityStatTypes.getSignatureEnergy(), playerBuffStore.getUltimateBoost());

        });



    }

}
