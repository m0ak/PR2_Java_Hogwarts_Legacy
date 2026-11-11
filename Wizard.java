import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Wizard class objects are the primary actors in the game. They can use and trade items, provide
 * magic energy, cast spells and also are affected be various magical effects.
 */
public class Wizard implements MagicSource, Trader, MagicEffectRealization {
	private String name; // Not null not empty
	private MagicLevel level; // Not null
	private int basicHP; // Not negative
	private int HP; // Not negative; defaults to basicHP
	private int basicMP; // Not less than the manapoints associated with the magic level
	private int MP; // Not negative; defaults to basicMP
	private int money; // Not negative
	private Set<Spell> knownSpells; // Not null, may be empty; use HashSet for instantiation
	private Set<AttackingSpell> protectedFrom; // Not null, may be empty; use HashSet for instantiation
	private int carryingCapacity; // Not negative
	private Set<Tradeable> inventory; // Not null, may be empty, use HashSet for instantiation, total weight of inventory may never exceed carryingCapacity

	/**
	 * @param name name
	 * @param level the magic level (proficiency needed to cast spells)
	 * @param basicHP base for percentage health calculations
	 * @param HP current health 
	 * @param basicMP base for percentage mana calculations
	 * @param MP current mana
	 * @param money current money
	 * @param knownSpells set of known spells
	 * @param protectedFrom set of spells the object is protected against
	 * @param carryingCapacity maximum carrying capacity
	 * @param inventory set of items the object is currently carrying
	 */
	public Wizard(String name, MagicLevel level, int basicHP, int HP, int basicMP, int MP, int money,
			Set<Spell> knownSpells, Set<AttackingSpell> protectedFrom, int carryingCapacity, Set<Tradeable> inventory) {
		if(name == null || name.isEmpty() || level == null || basicHP < 0 || HP < 0 || basicMP < level.toMana() || MP < 0 || money < 0 || knownSpells == null || protectedFrom == null || carryingCapacity < 0 || inventory == null) {
			throw new IllegalArgumentException("Wizard constructor error");
		}
		this.name = name;
		this.level = level;
		this.basicHP = basicHP;
		if(HP < 0) {
			this.HP = basicHP;
		} else {
			this.HP = HP;
		}
		this.basicMP = basicMP;
		if(MP < 0) {
			this.MP = basicMP;
		} else {
			this.MP = MP;
		}
		this.money = money;
		this.knownSpells = new HashSet<Spell>(knownSpells);
		this.protectedFrom = new HashSet<AttackingSpell>(protectedFrom);
		this.carryingCapacity = carryingCapacity;
		this.inventory = new HashSet<Tradeable>(inventory);
	}

	/**
	 * Return true, if HP is 0, false otherwise
	 * @return true, if HP is 0, false otherwise
	 */
	public boolean isDead() {
		if(this.HP == 0) {
			return true;
		}
		return false;
	}  
	  
	/**
	 * Calculates and returns the total weight of all the items in the inventory
	 * @return total weight of all items in inventory
	 */
	private int inventoryTotalWeight() {
		if(this.inventory.isEmpty()) {
			return 0;
		}
		int sumWeight = 0;
		for(Tradeable t : inventory) {
			sumWeight += t.getWeight();
		}
		return sumWeight;
	}
	  
	/**
	 * If spell is null, IllegalArgumentException has to be thrown;
	 * if wizard is dead (isDead) no action can be taken and false is returned;
	 * add spell to the set of knownSpells;
	 * returns true, if insertion was successful, false otherwise.
	 * @param s spell to be learned
	 * @return true, if insertion was successful, false otherwise.
	 */
	public boolean learn(Spell s) {
		if(s == null) {
			throw new IllegalArgumentException("Wizard learn error");
		}
		if(this.isDead()) {
			return false;
		}
		return this.knownSpells.add(s);
	}
	  
	/**
	 * If spell is null, IllegalArgumentException has to be thrown;
	 * if wizard is dead (isDead) no action can be taken and false is returned;
	 * remove spell from the set of knownSpells;
	 * returns true if removal was successful, false otherwise.
	 * @param s spell that the object is about to learn
	 * @return true, if removal was successful, false otherwise.
	 */
	public boolean forget(Spell s) {
		if(s == null) {
			throw new IllegalArgumentException("Wizard forget error");
		}
		if(this.isDead()) {
			return false;
		}
		return this.knownSpells.remove(s);
	}
	
	/**
	 * If s or target is null, IllegalArgumentException has to be thrown;
	 * if wizard is dead (isDead) no action can be taken and false is returned;
	 * if wizard does not know the spell, false is returned;
	 * call cast on s with this as source and parameter target as target
	 * return true, if cast was called;
	 * @param s spell to be cast
	 * @param target target of the spell to cast
	 * @return true, if cast was called, false otherwise;
	 */
	public boolean castSpell(Spell s, MagicEffectRealization target) {
		if(s == null || target == null) {
			throw new IllegalArgumentException("Wizard castSpell error");
		}
		if(this.isDead()) {
			return false;
		}
		if(!this.knownSpells.contains(s)) {
			return false;
		}
		s.cast(this, target);
		return true;
	}
	  
	/**
	 * If this object's knownSpells is empty, return false
	 * otherwise choose a random spell from knownSpells and delegate to
	 * castSpell(Spell, MagicEffectRealization)
	 * @param target target of the spell to cast
	 * @return false, if the object does not know a spell, otherwise the
	 * result of the delegation to castSpell
	 */
	public boolean castRandomSpell(MagicEffectRealization target) {
		if(this.knownSpells.isEmpty()) {
			return false;
		}
		int ksSize = this.knownSpells.size();
		int randomNum = new Random().nextInt(ksSize);
		int i = 0;
		for(Spell s : this.knownSpells) {
			if(i == randomNum) {
				return this.castSpell(s, target);
			}
			++i;
		}
		return false;
	}
	  
	/**
	 * If item or target is null, IllegalArgumentException has to be thrown;
	 * if wizard is dead (isDead) no action can be taken and false is returned;
	 * if wizard does not possess the item, false is returned;
	 * call useOn on the item with parameter target as target;
	 * return true, if useOn was called.
	 * @param item item to be used
	 * @param target target on which item is to be used on
	 * @return true, if useOn was called, false otherwise
	 */
	public boolean useItem(Tradeable item, MagicEffectRealization target) {
		if(item == null || target == null) {
			throw new IllegalArgumentException("Wizard useItem error");
		}
		if(this.isDead()) {
			return false;
		}
		if(!this.inventory.contains(item)) {
			return false;
		}
		item.useOn(target);
		return true;
	}

	/**
	 * If this object's inventory is empty, return false;
	 * otherwise choose a random item from inventory and delegate to
	 * useItem(Tradeable, MagicEffectRealization).
	 * @param target target on which item is to be used on
	 * @return false, if the object does not possess any item, otherwise the
	 * result of the delegation to useItem
	 */
	public boolean useRandomItem(MagicEffectRealization target) {
		if(this.inventory.isEmpty()) {
			return false;
		}
		int invSize = this.inventory.size();
		int randomNum = new Random().nextInt(invSize);
		int i = 0;
		for(Tradeable t : this.inventory) {
			if(i == randomNum) {
				return this.useItem(t, target);
			}
			++i;
		}
		return false;
	}
	  
	/**
	 * If item or target is null, IllegalArgumentException has to be thrown;
	 * if wizard is dead (isDead), no action can be taken and false is returned;
	 * call purchase on the item with this as seller and target as buyer;
	 * return true, if purchase was called successfully (returned true), false
	 * otherwise.
	 * @param item item to be sold
	 * @param target object the item is sold to (buyer)
	 * @return true, if purchase was called successfully (returned true), false
	 * otherwise.
	 */ 
	public boolean sellItem(Tradeable item, Trader target) {
		if(item == null || target == null) {
			throw new IllegalArgumentException("Wizard sellItem error");
		}
		if(this.isDead()) {
			return false;
		}
		return item.purchase(this, target);
	}

	/**
	 * If this object's inventory is empty, return false,
	 * otherwise choose a random item from inventory and delegate to
	 * sellItem(Tradeable, MagicEffectRealization).
	 * @param target object the item is sold to (buyer)
	 * @return false, if the object does not possess any item, otherwise the
	 * result of the delegation to sellItem
	 */
	public boolean sellRandomItem(Trader target) {
		if(this.inventory.isEmpty()) {
			return false;
		}
		int invSize = this.inventory.size();
		int randomNum = new Random().nextInt(invSize);
		int i = 0;
		for(Tradeable t : this.inventory) {
			if(i == randomNum) {
				return this.sellItem(t, target);
			}
			++i;
		}
		return false;
	}

	/**
	 * Returns a string in the format
	 * "['name'('level'): 'HP'/'basicHP' 'MP'/'basicMP'; 'money' 'KnutOrKnuts'; knows 'knownSpells'; carries 'inventory']";
	 * where 'level' is the asterisks representation of the level
	 * (see MagicLevel.toString) and 'knownSpells' and 'inventory' use
	 * the default toString method of Java Set; 'KnutOrKnuts' is Knut
	 * if 'money' is 1, Knuts otherwise.
	 * E.g. [Ignatius(**): 70/100 100/150; 72 Knuts; knows [[Episkey(*): 5 mana; +20 HP], [Confringo(*): 10 mana; -20 HP]]; carries []]
	 * @return "['name'('level'): 'HP'/'basicHP' 'MP'/'basicMP'; 'money' 'KnutOrKnuts'; knows 'knownSpells'; carries 'inventory']"
	 */ 
	@Override
	public String toString() {
		String output = "[" + this.name + "(" + this.level.toString() + "): " + String.valueOf(this.HP)+ "/" + String.valueOf(this.basicHP) + " " + String.valueOf(this.MP)+ "/" + String.valueOf(this.basicMP) + "; " + String.valueOf(this.money);
		if(money == 1) {
			output += " Knut; ";
		} else {
			output += " Knuts; ";
		}
		output += "knows " + this.knownSpells.toString() + "; carries " + this.inventory.toString() + "]";
		return output;
	}
	
	//MagicSource Interface
	
	/**
	 * If wizard is dead (isDead) no action can be taken and false is returned:
	 * check if level is at least levelNeeded, return false otherwise;
	 * if MP is less than manaAmount return false;
	 * subtract manaAmount from MP and return true.
	 * @param levelNeeded minimum magic level needed for the action 
	 * @param manaAmount amount of mana needed for the action 
	 * @return true, if mana can be successfully provided, false otherwise
	 */
	@Override
	public boolean provideMana(MagicLevel levelNeeded, int manaAmount) {
		if(levelNeeded == null || manaAmount < 0) {
			throw new IllegalArgumentException("Wizard provideMana error");
		}
		if(this.isDead()) {
			return false;
		}
		if(this.level.compareTo(levelNeeded) < 0) {
			return false;
		}
		if(this.MP < manaAmount) {
			return false;
		}
		this.MP -= manaAmount;
		return true;
	}

	//Trader Interface

	/**
	 * Return true, if the item is in the inventory, false otherwise
	 * @param item object is tested, if it possesses this item
	 * @return true, if the item is in the inventory, false otherwise
	 */
	@Override
	public boolean possesses(Tradeable item) {
		if(item == null) {
			throw new IllegalArgumentException("Wizard possesses error");
		}
		return this.inventory.contains(item);
	}

	/**
	 * Return true, if money is greater than or equal to amount, false otherwise
	 * @param amount object is tested, if it owns enough money to pay this amount
	 * @return true, if money is greater than or equal to amount, false otherwise
	 */
	@Override
	public boolean canAfford(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard canAffourd error");
		}
		if(this.money >= amount) {
			return true;
		}
		return false;
	}

	/**
	 * Return true, if inventoryTotalWeight+weight is less than or equal to carryingCapacity, false otherwise
	 * @param weight test, if this weight can be added to object's inventory, without exceeding the
	 * carryingCapacity
	 * @return true, if inventoryTotalWeight+weight is less than or equal to carryingCapacity, false otherwise
	 */
	@Override
	public boolean hasCapacity(int weight) {
		if(weight < 0) {
			throw new IllegalArgumentException("Wizard hasCapacity error");
		}
		if((this.inventoryTotalWeight() + weight) <= this.carryingCapacity) {
			return true;
		}
		return false;
	}

	/**
	 * If wizard is dead (isDead) no action can be taken and false is returned;
	 * if this owns enough money deduct amount from money and return true,
	 * return false otherwise
	 * @param amount to be payed
	 * @return true, if payment succeeds, false otherwise
	 */
	@Override
	public boolean pay(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard pay error");
		}
		if(this.isDead()) {
			return false;
		}
		if(this.canAfford(amount)) {
			this.money -= amount;
			return true;
		}
		return false;
	}
	    
	/**
	 * If wizard is dead (isDead), no action can be taken and false is returned;
	 * add amount to this object's money and return true.
	 * @param amount amount to be received
	 * @return true, if reception succeeds, false otherwise 
	 */
	@Override
	public boolean earn(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard earn error");
		}
		if(this.isDead()) {
			return false;
		}
		this.money += amount;
		return true;
	}
	    
	/**
	 * Add item to inventory if carryingCapacity is sufficient.
	 * returns true, if item is successfully added, false otherwise
	 * (carrying capacity exceeded or item is already in the inventory)
	 * @param item item to be added to object's inventory
	 * @return true. if item is successfully added, false otherwise
	 */
	@Override
	public boolean addToInventory(Tradeable item) {
		if(item == null) {
			throw new IllegalArgumentException("Wizard addToInventory error");
		}
		if(this.hasCapacity(item.getWeight())) {
			return this.inventory.add(item);
		}
		return false;
	}

	/**
	 * Remove item from inventory.
	 * returns true, if item is successfully removed, false otherwise
	 * (item not in the inventory).
	 * @param item item to be removed from object's inventory
	 * @return true, if item is successfully removed, false otherwise
	 */
	@Override
	public boolean removeFromInventory(Tradeable item) {
		if(item == null) {
			throw new IllegalArgumentException("Wizard removeFromInventory error");
		}
		return this.inventory.remove(item);
	}
	
	/**
	 * Returns true, if this object's HP are not 0 (alive wizard).
	 * @return true, if the object is alive
	 */
	@Override
	public boolean canSteal() {
		if(this.isDead()) {
			return false;
		}
		return true;
	}
	
	/**
	 * If thief is null, IllegalArgumentException has to be thrown;
	 * if thief cannot steal (canSteal returns false) no action can be taken
	 * and false is returned;
	 * returns false if, the object's inventory is empty;
	 * otherwise transfers a random item from the this object's inventory into
	 * the thief's inventory;
	 * if the thief's inventory has not enough capacity, the object just vanishes
	 * and false is returned;
	 * returns true, if theft was successful.
	 * @param thief object that is stealing the item from the this-object.
	 * @return true, if theft was successful
	 */
	@Override
	public boolean steal(Trader thief) {
		if(thief == null) {
			throw new IllegalArgumentException("Wizard steal error");
		}
		if(!thief.canSteal()) {
			return false;
		}
		if(this.inventory.isEmpty()) {
			return false;
		}
		Tradeable stolen = null;
		int invSize = this.inventory.size();
		int randomNum = new Random().nextInt(invSize);
		int i = 0;
		for(Tradeable t : this.inventory) {
			if(i == randomNum) {
				this.removeFromInventory(t);
				stolen = t;
				break;
			}
			++i;
		}
		return thief.addToInventory(stolen);
	}

	/**
	 * Returns true if, this object's HP are 0 (dead wizard)
	 * @return true if the object is dead
	 */
	@Override
	public boolean isLootable() {
		return this.isDead();
	}

	/**
	 * Returns true if this object's HP are not 0 (alive wizard)
	 * @return true, if the object is alive
	 */
	@Override
	public boolean canLoot() {
		if(this.isDead()) {
			return false;
		}
		return true;
	}
	
	/**
	 * If looter is null, IllegalArgumentException has to be thrown;
	 * if looter cannot loot (canLoot returns false), no action can be taken
	 * and false is returned;
	 * if the this object can be looted (isLootable), transfer all the items
	 * in the object's inventory into the looter's inventory;
	 * items that don't fit in the looter's inventory because auf the weight
	 * limitation just vanish.
	 * returns true, if at least one item was successfully transferred, false
	 * otherwise.
	 * @param looter object that is looting this-object.
	 * @return true, if looting was successful, false otherwise
	 */
	@Override
	public boolean loot(Trader looter) {
		if(looter == null) {
			throw new IllegalArgumentException("Wizard loot error");
		}
		if(!looter.canLoot()) {
			return false;
		}
		boolean itemTransferred = false;
		if(this.isLootable()) {
			for(Tradeable t : this.inventory) {
				if(looter.addToInventory(t)) {
					itemTransferred = true;
				}
			}
			this.inventory.clear();
		}
		return itemTransferred;
	}
	  
	//MagicEffectRealization Interface
	
	/**
	 * Reduce the object's HP by amount ensuring however that HP does not
	 * become negative.
	 * @param amount amount to be deducted from health 
	 */
	@Override
	public void takeDamage(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard takeDamage error");
		}
		if(amount >= this.HP) {
			this.HP = 0;
		} else {
			this.HP -= amount;
		}
	}
	    
	/**
	 * Reduce the object's HP by the percentage given of the object's basic
	 * HP value ensuring however, that HP does not become negative.
	 * Do calculations in double truncating to int only for the assignment
	 * @param percentage percentage of damage done
	 */
	@Override
	public void takeDamagePercent(int percentage) {
		if(percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Wizard takeDamagePercent error");
		}
		double damage = ((double) this.basicHP * percentage) / 100.0;
		this.takeDamage((int) damage);
	}
	    
	/**
	 * Reduce the object's MP by amount ensuring however that MP does not
	 * become negative.
	 * @param amount amount to be deducted from mana 
	 */
	@Override
	public void weakenMagic(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard weakenMagic error");
		}
		if(amount >= this.MP) {
			this.MP = 0;
		} else {
			this.MP -= amount;
		}
	}
	  
	/**
	 * Reduce the object's MP by the percentage given of the object's basic
	 * MP value ensuring however, that MP does not become negative.
	 * Do calculations in double truncating to int only for the assignment
	 * @param percentage percentage of damage done
	 */
	@Override
	public void weakenMagicPercent(int percentage) {
		if(percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Wizard weakenMagicPercent error");
		}
		double damage = ((double) this.basicMP * percentage) / 100.0;
		this.weakenMagic((int) damage);
	}
	  
	/**
	 * Increase the object's HP by the amount given.
	 * @param amount amount to increase health
	 */
	@Override
	public void heal(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard heal error");
		}
		this.HP += amount;
	}
	    
	/**
	 * Increase the object's HP by the percentage given of the object's
	 * basic HP. Do calculations in double truncating to int only for
	 * the assignment
	 * @param percentage percentage of healing done
	 */
	@Override
	public void healPercent(int percentage) {
		if(percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Wizard healPercent error");
		}
		double healAmount = ((double) this.basicHP * percentage) / 100.0;
		this.heal((int) healAmount);
	}

	/**
	 * Increase the object's MP by the amount given.
	 * @param amount amount to increase mana
	 */
	@Override
	public void enforceMagic(int amount) {
		if(amount < 0) {
			throw new IllegalArgumentException("Wizard enforceMagic error");
		}
		this.MP += amount;
	}
	  
	/**
	 * Increase the object's MP by the percentage given of the object's
	 * basic MP. Do calculations in double truncating to int only for
	 * the assignment
	 * @param percentage percentage of mana increase
	 */
	@Override
	public void enforceMagicPercent(int percentage) {
		if(percentage < 0 || percentage > 100) {
			throw new IllegalArgumentException("Wizard enforceMagicPercent error");
		}
		double manaAmount = ((double) this.basicMP * percentage) / 100.0;
		this.enforceMagic((int) manaAmount);
	}
	    
	/**
	 * Return true, if s is contained in instance variable protectedFrom
	 * @param s spell that is tested for
	 * @return true, if object is protected against spell s, false otherwise
	 */ 
	@Override
	public boolean isProtected(Spell s) {
		if(s == null) {
			throw new IllegalArgumentException("Wizard isProtected error");
		}
		return this.protectedFrom.contains(s);
	}
	    
	/**
	 * Add all spells from attacks to instance variable protectedFrom
	 * @param attacks spells against which protection is provided
	 */
	@Override
	public void setProtection(Set<AttackingSpell> attacks) {
		if(attacks == null) {
			throw new IllegalArgumentException("Wizard setProtection error");
		}
		this.protectedFrom.addAll(attacks);
	}

	/**
	 * Remove all spells from attacks from instance variable protectedFrom
	 * @param attacks spells against which protection is removed 
	 */
	@Override
	public void removeProtection(Set<AttackingSpell> attacks) {
		if(attacks == null) {
			throw new IllegalArgumentException("Wizard removeProtection error");
		}
		this.protectedFrom.removeAll(attacks);
	}
}