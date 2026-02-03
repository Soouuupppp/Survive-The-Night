package dev.cattails.pages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.protocol.packets.interface_.Page;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.cattails.util.PlayerStore;

import static dev.cattails.SurviveTheNight.INSTANCE;
import static dev.cattails.events.PlayerDeathSystem.DEAD_PLAYERS;

import javax.annotation.Nonnull;

public class onDeathPage extends InteractiveCustomUIPage<onDeathPage.onDeathPageData> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();


    public static class onDeathPageData {

        public String action;           // Button action identifier

        public static final BuilderCodec<onDeathPageData> CODEC =
                BuilderCodec.builder(onDeathPageData.class, onDeathPageData::new)
                        .append(new KeyedCodec<>
                                        ("Action", Codec.STRING),
                                (onDeathPageData o, String v) -> o.action = v,
                                (onDeathPageData o) -> o.action)
                        .add().build();
    }

    public onDeathPage(@Nonnull PlayerRef playerRef) {

        super(playerRef, CustomPageLifetime.CantClose, onDeathPageData.CODEC);

    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commandBuilder, @Nonnull UIEventBuilder eventBuilder, @Nonnull Store<EntityStore> store){

        commandBuilder.append("Pages/onDeath.ui");

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
                "#ButtonSpectate",
                new EventData().append("Action", "Spectate"));

        eventBuilder.addEventBinding(CustomUIEventBindingType.Activating,
                "#ButtonLeave",
                new  EventData().append("Action", "Leave"));

    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, onDeathPageData data) {

        Player player = store.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if(player == null || playerRef == null) return;
        World world = player.getWorld();

        PlayerStore playerStore = store.ensureAndGetComponent(ref, INSTANCE.getPlayerStoreComponent());

        if(data.action.equals("Spectate")){

            player.sendMessage(Message.raw("Clicked spectate!"));

            world.execute(() -> {

                Player.setGameMode(ref, GameMode.Creative, store);
                // This doesn't seem to work
//                store.putComponent(ref, PreventPickup.getComponentType(), PreventPickup.INSTANCE);

                Teleport teleport = Teleport.createForPlayer(world,
                        new Vector3d(playerStore.getDeathX(), playerStore.getDeathY(), playerStore.getDeathZ()), // Target position
                        new Vector3f(0, 0, 0)  // Target rotation (pitch, yaw, roll)
                );

                store.addComponent(ref, Teleport.getComponentType(), teleport);

            });


        }

        if(data.action.equals("Leave")){
            player.sendMessage(Message.raw("Clicked leave!"));
            DEAD_PLAYERS.remove(playerRef);
            playerRef.getPacketHandler().disconnect("You were defeated...");

        }

        player.getPageManager().setPage(ref, store, Page.None);

    }

}
