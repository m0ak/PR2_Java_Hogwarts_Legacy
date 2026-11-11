/**
 *  A potion has a specific magic effect on its consumer
 */
public abstract class Potion extends MagicItem {
	/**
	 * @param name name
	 * @param usages number of usages still left
	 * @param price price
	 * @param weight weight
	 */
	public Potion(String name, int usages, int price, int weight) {
		super(name, usages, price, weight);
	}
	
	// Delegates to method call useOn(drinker); @param drinker the consumer of the potion
	public void drink(Wizard drinker) {
		if(drinker == null) {
			throw new IllegalArgumentException("Potion drink error");
		}
		this.useOn(drinker);
	}
	
	// returns "gulp" if usages is equal to 1, "gulps" otherwise
	@Override
	public String usageString() {
		if(getUsages() == 1) {
			return "gulp";
		} else {
			return "gulps";
		}
	}
}