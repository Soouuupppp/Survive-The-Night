package dev.cattails.events;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerReadyEvent;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.item.PreventPickup;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.pages.OverlayHud;
import dev.cattails.util.PlayerStore;
import dev.cattails.pages.OverlayHud;

import java.util.concurrent.TimeUnit;

import static dev.cattails.SurviveTheNight.INSTANCE;

public class OnJoin {

    public OnJoin() {}

    public static void onPlayerReady(PlayerReadyEvent event) {

        Player player = (Player) event.getPlayer();
        PlayerRef playerRef = player.getPlayerRef();
        World world = player.getWorld();

        world.execute(() -> {

            Store<EntityStore> store = player.getReference().getStore();
            Ref<EntityStore> ref = player.getReference();

            PlayerStore playerStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerStoreComponent());

            if(playerStore.getGhostMode()){

                Player.setGameMode(ref, GameMode.Creative, store);
                // This doesn't seem to work
                store.putComponent(ref, PreventPickup.getComponentType(), PreventPickup.INSTANCE);

                player.sendMessage(Message.raw("You have died, spectator mode enabled.."));

            }

            if(player.isFirstSpawn()){

                ItemStack StoreItem = new ItemStack("StoreBench", 1);
                Inventory inventory = player.getInventory();
                ItemContainer hotBar = inventory.getHotbar();
                hotBar.addItemStackToSlot((short) 1, StoreItem);

            }

        });

        HytaleServer.SCHEDULED_EXECUTOR.scheduleAtFixedRate(() -> {
            world.execute(() -> {
                OverlayHud overlayHud = new OverlayHud(playerRef);
                player.getHudManager().setCustomHud(playerRef, overlayHud);
            });
        }, 1000, 333, TimeUnit.MILLISECONDS);

    }

}
