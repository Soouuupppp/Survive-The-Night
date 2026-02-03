package dev.cattails.events;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.gameplay.DeathConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.pages.onDeathPage;
import dev.cattails.util.PlayerStore;

import static dev.cattails.SurviveTheNight.INSTANCE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;


public class PlayerDeathSystem extends DeathSystems.OnDeathSystem {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static final Set<PlayerRef> DEAD_PLAYERS = new HashSet<>();
    public static int TOTAL_DEADS = 0;

    @Nullable
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }

    public void onComponentAdded(@Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer){

        Player player = store.getComponent(ref, Player.getComponentType());

        if(player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());

        assert playerRef != null;
        LOGGER.atInfo().log("Player " + playerRef.getUsername() + " has died");

        World world =  player.getWorld();
        if(world == null) return;

        world.execute(() -> {

            DEAD_PLAYERS.add(playerRef);
            TOTAL_DEADS++;

            PlayerStore playerStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerStoreComponent());

            component.setItemsLossMode(DeathConfig.ItemsLossMode.ALL);
            component.setItemsAmountLossPercentage(100.0);
            component.setItemsDurabilityLossPercentage(0.0);

            playerStore.setGhostMode(true);

            TransformComponent playerLoc = store.getComponent(ref, TransformComponent.getComponentType());
            if(playerLoc != null){

                playerStore.setDeathX(playerLoc.getPosition().x);
                playerStore.setDeathY(playerLoc.getPosition().y);
                playerStore.setDeathZ(playerLoc.getPosition().z);
                LOGGER.atInfo().log("Player Loc: " + playerStore.getDeathX() + " " + playerStore.getDeathY() + " " + playerStore.getDeathZ());
            } else {
                LOGGER.atWarning().log("Player Loc is null!");
            }

            store.putComponent(ref, INSTANCE.getPlayerStoreComponent(), playerStore);

            onDeathPage page = new onDeathPage(playerRef);
            player.getPageManager().openCustomPage(ref, store, page);

        });

    }

}
