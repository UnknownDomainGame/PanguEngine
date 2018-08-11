package unknowndomain.engine.item;

import unknowndomain.engine.Entity;
import unknowndomain.engine.Prototype;
import unknowndomain.engine.block.BlockPrototype;
import unknowndomain.engine.entity.Player;
import unknowndomain.engine.world.World;

public interface ItemPrototype extends Prototype<Item, Player> {
    UseBehavior DEFAULT_USE = (world, player, item) -> {
    };
    UseBlockBehavior DEFAULT_USE_BLOCK = (world, player, item, hit) -> {
    };
    HitBlockBehavior DEFAULT_HIT_BLOCK = (world, player, item, hit) -> {
    };

    interface UseBehavior {
        void onUseStart(World world, Player player, Item item);

        default boolean onUsing(World world, Player player, Item item, int tickElapsed) {
            return false;
        }

        default void onUseStop(World world, Player player, Item item, int tickElapsed) {
        }
    }

    interface UseBlockBehavior {
        void onUseBlockStart(World world, Player player, Item item, BlockPrototype.Hit hit);

        default boolean onUsingBlock(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed) {
            return false;
        }

        default void onUseBlockStop(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed) {
        }
    }

    interface HitBlockBehavior {
        void onHit(World world, Player player, Item item, BlockPrototype.Hit hit);

//        boolean onKeep(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed);

//        void onUseStop(Player player, Item item, BlockPrototype.Hit hit, int tickElapsed);
    }

    interface HitEntityBehavior {
        void onStart(Player player, Item item, Entity entity);

        boolean onKeep(Player player, Item item, Entity entity, int tickElapsed);

        void onStart(Player player, Item item, Entity entity, int tickElapsed);
    }
}
