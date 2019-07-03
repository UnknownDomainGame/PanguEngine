package nullengine.item;

import nullengine.block.Block;
import nullengine.component.Component;
import nullengine.entity.Entity;
import nullengine.event.Event;
import nullengine.event.world.item.ItemBreakEvent;
import nullengine.event.world.item.ItemThrowEvent;
import nullengine.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ItemImpl implements Item,Runnable {
	private int maxStackAmount; //Max Amount can stack,you can set it
	private String name;
	private int hp;
	private int harm;
	private boolean canBeDestroy;

	private ItemImpl(String name,int hp,int harm,boolean canBeDestroy,int maxStackAmount){
		this.name=name;
		this.hp=hp;
		this.harm=harm;
		this.canBeDestroy=canBeDestroy;
		this.maxStackAmount=maxStackAmount;
	}

	public class ItemBuilder{
		public ItemBuilder setName(String setname){
			name=setname;
			return this;
		}
		public ItemBuilder setHp(int sethp){
			hp=sethp;
			return this;
		}
		public ItemBuilder setHarm(int setharm){
			harm=setharm;
			return this;
		}
		public ItemBuilder setNonDestroy(boolean nonDestroy){
			canBeDestroy=nonDestroy;
			return this;
		}
		public ItemBuilder setMaxStack(int maxStack){
			maxStackAmount=maxStack;
			return this;
		}
		public ItemImpl build(){
			return new ItemImpl(name,hp,harm,canBeDestroy,maxStackAmount);
		}
	}
	public void onItemUse(ItemImpl item, World world, Entity playerEntity,int tick){

	}

	public void onItemDamage(ItemImpl item,World world,Entity playerEntity){
		Event itemDamageEvent=new ItemBreakEvent(item,world,playerEntity);
	}

	public void onRightClick(ItemImpl item,World world, Entity entity, Block block){

	}

	public void onThrowAway(ItemImpl item,World world,Entity entity,Block block){
		Event itemThrowEvent=new ItemThrowEvent(item,world,entity,block);
	}

	@Override
	public Item registerName(String name) {
		return new ItemBuilder().setName(name).build();
	}

	@Override
	public String getRegisterName() {
		return name;
	}

	@Override
	public int getId() {
		return 0;
	}

	@Override
	public String getUniqueName() {
		return "";
	}


	@Override
	public void run() {

	}

	@Override
	public Class<Item> getEntryType() {
		return Item.class;
	}

	@Override
	public <T extends Component> Optional<T> getComponent(@Nonnull Class<T> type) {
		return Optional.empty();
	}

	@Override
	public <T extends Component> boolean hasComponent(@Nonnull Class<T> type) {
		return true;
	}

	@Nonnull
	@Override
	public Set<Class<?>> getComponents() {
		return new HashSet<>();
	}

	@Override
	public <T extends Component> void setComponent(@Nonnull Class<T> type, @Nullable T value) {

	}

	@Override
	public <T extends Component> void removeComponent(@Nonnull Class<T> type) {
		getComponents().remove(type);
	}
}
