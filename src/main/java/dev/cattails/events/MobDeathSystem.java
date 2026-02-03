package dev.cattails.events;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import dev.cattails.util.PlayerBuffStore;

import static dev.cattails.SurviveTheNight.INSTANCE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;


public class MobDeathSystem extends DeathSystems.OnDeathSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(NPCEntity.getComponentType());
    }

    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer){

        NPCEntity npc = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if(npc == null) return;

        Damage deathInfo = component.getDeathInfo();
        if (deathInfo == null) return;

        Damage.Source source = deathInfo.getSource();

        int rewardCurrency = ThreadLocalRandom.current().nextInt(5, 26);

        if(source instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> attackerRef = entitySource.getRef();
            Player player = store.getComponent(attackerRef, Player.getComponentType());
            PlayerRef playerRef = store.getComponent(attackerRef, PlayerRef.getComponentType());

            if(player == null || playerRef == null) return;

            World world = player.getWorld();

            world.execute(() -> {
                PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(player.getReference(), INSTANCE.getPlayerBuffsStoreComponent());

                LOGGER.atInfo().log("Player " + player.getDisplayName() + " Killed: " + npc.getNPCTypeId());

                playerBuffStore.addCurrency(rewardCurrency);

                NotificationUtil.sendNotification(
                        playerRef.getPacketHandler(),
                        Message.raw("+" + rewardCurrency),
                        Message.raw("For killing: " + npc.getNPCTypeId()),
                        new ItemStack("Furniture_Christmas_Chest_Small").toPacket()
                );

            });

        }

        if(source instanceof Damage.ProjectileSource projectileSource) {
            Ref<EntityStore> attackerRef = projectileSource.getRef();
            Player player = store.getComponent(attackerRef, Player.getComponentType());
            PlayerRef playerRef = store.getComponent(attackerRef, PlayerRef.getComponentType());

            if(player == null || playerRef == null) return;


            World world = player.getWorld();

            world.execute(() -> {
                PlayerBuffStore playerBuffStore = store.ensureAndGetComponent(player.getReference(), INSTANCE.getPlayerBuffsStoreComponent());

                LOGGER.atInfo().log("Player " + player.getDisplayName() + " Killed: " + npc.getNPCTypeId());

                playerBuffStore.addCurrency(rewardCurrency);

                NotificationUtil.sendNotification(
                        playerRef.getPacketHandler(),
                        Message.raw("+" + rewardCurrency),
                        Message.raw("For killing: " + npc.getNPCTypeId()),
                        new ItemStack("Furniture_Christmas_Chest_Small").toPacket()
                );

            });

        }



    }

}
