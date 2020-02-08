package nullengine.enginemod;

import nullengine.Platform;
import nullengine.block.AirBlock;
import nullengine.block.Block;
import nullengine.block.component.ActivateBehavior;
import nullengine.block.component.ClickBehavior;
import nullengine.client.event.rendering.RegisterEntityRendererEvent;
import nullengine.client.game.GameClient;
import nullengine.client.gui.Scene;
import nullengine.client.input.controller.MotionType;
import nullengine.client.input.keybinding.Key;
import nullengine.client.input.keybinding.KeyBinding;
import nullengine.client.rendering.block.BlockDisplay;
import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.entity.EntityItemRenderer;
import nullengine.client.rendering.util.GLHelper;
import nullengine.enginemod.client.gui.game.GUIGameCreation;
import nullengine.enginemod.client.gui.game.GuiChat;
import nullengine.enginemod.client.gui.game.GuiIngameMenu;
import nullengine.enginemod.client.gui.game.GuiItemList;
import nullengine.entity.Entity;
import nullengine.entity.component.TwoHands;
import nullengine.entity.item.ItemEntity;
import nullengine.event.Listener;
import nullengine.event.block.BlockInteractEvent;
import nullengine.event.block.cause.BlockChangeCause;
import nullengine.event.block.cause.BlockInteractCause;
import nullengine.event.engine.EngineEvent;
import nullengine.event.entity.EntityInteractEvent;
import nullengine.event.entity.cause.EntityInteractCause;
import nullengine.event.item.ItemInteractEvent;
import nullengine.event.item.cause.ItemInteractCause;
import nullengine.event.mod.ModLifecycleEvent;
import nullengine.event.mod.ModRegistrationEvent;
import nullengine.item.ItemStack;
import nullengine.item.component.ActivateBlockBehavior;
import nullengine.item.component.ActivateEntityBehavior;
import nullengine.item.component.ClickBlockBehavior;
import nullengine.item.component.ClickEntityBehavior;
import nullengine.player.Player;
import nullengine.registry.Registries;
import nullengine.registry.impl.IdAutoIncreaseRegistry;
import nullengine.world.WorldProvider;
import nullengine.world.hit.BlockHitResult;
import nullengine.world.hit.EntityHitResult;
import nullengine.world.hit.HitResult;
import nullengine.world.provider.FlatWorldProvider;

public final class EngineModClientListeners {

    @Listener
    public static void onPreInit(ModLifecycleEvent.PreInitialization event) {
        Platform.getEngine().getEventBus().register(EngineModClientListeners.class);
    }

    @Listener
    public static void constructRegistry(ModRegistrationEvent.Construction e) {
        e.addRegistry(KeyBinding.class, () -> new IdAutoIncreaseRegistry<>(KeyBinding.class));
    }

    @Listener
    public static void registerWorldProvider(ModRegistrationEvent.Register<WorldProvider> event) {
        event.register(new FlatWorldProvider().name("flat"));
    }

    @Listener
    public static void registerBlocks(ModRegistrationEvent.Register<Block> event) {
        AirBlock.AIR.setComponent(BlockDisplay.class, new BlockDisplay().visible(false));
    }

    @Listener
    public static void registerKeyBindings(ModRegistrationEvent.Register<KeyBinding> event) {

        // TODO: When separating common and client, only register on client side
        // TODO: almost everything is hardcoded... Fix when GameContext and
        event.register(
                KeyBinding.builder()
                        .name("player.move.forward")
                        .key(Key.KEY_W)
                        .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.FORWARD, false))
                        .build());
        event.register(
                KeyBinding.builder()
                        .name("player.move.backward")
                        .key(Key.KEY_S)
                        .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, true))
                        .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.BACKWARD, false))
                        .build());
        event.register(KeyBinding.builder()
                .name("player.move.left")
                .key(Key.KEY_A)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.LEFT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.right")
                .key(Key.KEY_D)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.RIGHT, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.jump")
                .key(Key.KEY_SPACE)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.UP, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.move.sneak")
                .key(Key.KEY_LEFT_SHIFT)
                .startHandler(c -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, true))
                .endHandler((c, i) -> c.getCurrentGame().getClientPlayer().getEntityController().onInputMove(MotionType.DOWN, false))
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.left")
                .key(Key.MOUSE_BUTTON_LEFT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    HitResult hitResult = player.getWorld().raycast(camera.getPosition(), camera.getFront(), 10);
                    if (hitResult.isFailure()) {
                        var cause = new ItemInteractCause.PlayerCause(player);
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack -> {
                                    game.getEngine().getEventBus().post(new ItemInteractEvent.Click(itemStack, cause));
                                    itemStack.getItem().getComponent(nullengine.item.component.ClickBehavior.class).ifPresent(clickBehavior ->
                                            clickBehavior.onClicked(itemStack, cause));
                                }));
                        return;
                    }
                    if (hitResult instanceof BlockHitResult) {
                        var blockHitResult = (BlockHitResult) hitResult;
                        var cause = new BlockInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new BlockInteractEvent.Click(blockHitResult, cause));
                        blockHitResult.getBlock().getComponent(ClickBehavior.class).ifPresent(clickBehavior ->
                                clickBehavior.onClicked(blockHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ClickBlockBehavior.class).ifPresent(clickBlockBehavior ->
                                                clickBlockBehavior.onClicked(itemStack, blockHitResult, cause))));
                        // TODO: Remove it
                        player.getWorld().destroyBlock(blockHitResult.getPos(), new BlockChangeCause.PlayerCause(player));
                    } else if (hitResult instanceof EntityHitResult) {
                        var entityHitResult = (EntityHitResult) hitResult;
                        var cause = new EntityInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new EntityInteractEvent.Click(entityHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ClickEntityBehavior.class).ifPresent(clickBlockBehavior ->
                                                clickBlockBehavior.onClicked(itemStack, entityHitResult, cause))));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.right")
                .key(Key.MOUSE_BUTTON_RIGHT)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    HitResult hitResult = player.getWorld().raycast(camera.getPosition(), camera.getFront(), 10);
                    if (hitResult.isFailure()) {
                        var cause = new ItemInteractCause.PlayerCause(player);
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack -> {
                                    game.getEngine().getEventBus().post(new ItemInteractEvent.Activate(itemStack, cause));
                                    itemStack.getItem().getComponent(nullengine.item.component.ActivateBehavior.class).ifPresent(activateBehavior ->
                                            activateBehavior.onActivate(itemStack, cause));
                                }));
                        return;
                    }
                    if (hitResult instanceof BlockHitResult) {
                        var blockHitResult = (BlockHitResult) hitResult;
                        var cause = new BlockInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new BlockInteractEvent.Activate(blockHitResult, cause));
                        blockHitResult.getBlock().getComponent(ActivateBehavior.class).ifPresent(activateBehavior ->
                                activateBehavior.onActivated(blockHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ActivateBlockBehavior.class).ifPresent(activateBlockBehavior ->
                                                activateBlockBehavior.onActivate(itemStack, blockHitResult, cause))));
                    } else if (hitResult instanceof EntityHitResult) {
                        var entityHitResult = (EntityHitResult) hitResult;
                        var cause = new EntityInteractCause.PlayerCause(player);
                        game.getEngine().getEventBus().post(new EntityInteractEvent.Activate(entityHitResult, cause));
                        entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                twoHands.getMainHand().ifNonEmpty(itemStack ->
                                        itemStack.getItem().getComponent(ActivateEntityBehavior.class).ifPresent(activateEntityBehavior ->
                                                activateEntityBehavior.onActivate(itemStack, entityHitResult, cause))));
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("player.mouse.middle")
                .key(Key.MOUSE_BUTTON_3)
                .startHandler(c -> {
                    GameClient game = c.getCurrentGame();
                    Player player = game.getClientPlayer();
                    Camera camera = c.getRenderManager().getViewport().getCamera();
                    Entity entity = player.getControlledEntity();
                    player.getWorld().raycastBlock(camera.getPosition(), camera.getFront(), 10).ifSuccess(hit ->
                            entity.getComponent(TwoHands.class).ifPresent(twoHands ->
                                    Registries.getItemRegistry().getBlockItem(hit.getBlock()).ifPresent(item ->
                                            twoHands.setMainHand(new ItemStack(item)))));
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.chat")
                .key(Key.KEY_ENTER)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiChat(c.getCurrentGame()));
                    c.getRenderManager().getGUIManager().show(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.inventory")
                .key(Key.KEY_E)
                .startHandler(c -> {
                    Scene scene = new Scene(new GuiItemList(c.getRenderManager()));
                    c.getRenderManager().getGUIManager().show(scene);
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.menu")
                .key(Key.KEY_ESCAPE)
                .startHandler(c -> {
                    if (!c.getRenderManager().getGUIManager().isShowing()) {
                        c.getRenderManager().getGUIManager().show(new Scene(new GuiIngameMenu()));
                    } else {
                        c.getRenderManager().getGUIManager().close();
                    }
                })
                .build());
        event.register(KeyBinding.builder()
                .name("game.screenshot")
                .key(Key.KEY_F2)
                .startHandler(engineClient -> GLHelper.takeScreenshot(engineClient.getRunPath().resolve("screenshot")))
                .build());

        var renderContext = Platform.getEngineClient().getRenderManager();
        var hudManager = renderContext.getHUDManager();
//        var hudGameDebug = new HUDGameDebug();
//        renderContext.getScheduler().runTaskEveryFrame(() -> hudGameDebug.update(renderContext));
//        event.register(KeyBinding.builder()
//                .name("game.debug_display_switch")
//                .key(Key.KEY_F3)
//                .actionMode(ActionMode.SWITCH)
//                .startHandler(gameClient -> guiManager.showHud("debugGame", new Scene(hudGameDebug)))
//                .endHandler((gameClient, integer) -> guiManager.removeHud("debugGame"))
//                .build());
        event.register(KeyBinding.builder()
                .name("game.hud_display_switch")
                .key(Key.KEY_F1)
                .startHandler(gameClient -> hudManager.toggleVisible())
                .build());
    }

    @Listener
    public static void registerEntityRenderer(RegisterEntityRendererEvent event) {
        event.register(ItemEntity.class, new EntityItemRenderer());
    }

    @Listener
    public static void onEngineReady(EngineEvent.Ready event) {
        var renderContext = Platform.getEngineClient().getRenderManager();
        var guiManager = renderContext.getGUIManager();

        var scene = new Scene(new GUIGameCreation());
        guiManager.show(scene);
    }
}
