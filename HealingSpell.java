/**
 * Healing spells increase the targets health or mana
 */
public class HealingSpell extends Spell {
	private boolean type; // Defines, if HP or MP is affected; type == true: affects HP; type == false: affects MP
	private boolean percentage; // Defines, if amount is interpreted as an absolute value or as a percentage. percentage == true: deduct value to subtract as 'amount' percentage of basic value; percentage == false: subtract amount directly
	private int amount; // Has to be non negative; if percentage==true, amount must be in the interval [0,100]

	/**
	 * @param name name
	 * @param manaCost manaCost
	 * @param levelNeeded levelNeeded
	 * @param type defines if health or mana is affected
	 * @param percentage defines if amount is an absolute or a percentage value
	 * @param amount amount
	 */
	public HealingSpell(String name, int manaCost, MagicLevel levelNeeded, boolean type, boolean percentage, int amount) {
		super(name, manaCost, levelNeeded);
		this.type = type;
		this.percentage = percentage;
		if(amount < 0) {
			throw new IllegalArgumentException("HealingSpell constructor amount < 0");
		}
		if(percentage) {
			if(amount < 0 || amount > 100) {
				throw new IllegalArgumentException("HealingSpell constructor amount not in interval");
			}
		}
		this.amount = amount;
	}
	
	/**
	 * Use one of the functions heal, healPercent, enforceMagic or enforceMagicPercent according
	 * to the flags type and percentage
	 * @param target target that receives healing
	 */
	@Override	
	public void doEffect(MagicEffectRealization target) {
		if(target == null) {
			throw new IllegalArgumentException("HealingSpell doEffekt error");
		}
		if(this.percentage) {
			if(type) {
				target.healPercent(amount);
			} else {
				target.enforceMagicPercent(amount);
			}
		} else {
			if(type) {
				target.heal(amount);
			} else {
				target.enforceMagic(amount);
			}
		}
	}

	/**
	 * Returns "; +'amount' 'percentage' 'HPorMP'", where 'percentage' is a '%'-sign if percentage
	 * is true, empty otherwise and HPorMP is HP if type is true, MP otherwise
	 * e. g. "; +10 HP" or "; +50 % MP"
	 * @return "; +'amount' 'percentage' 'HPorMP'"
	 */
	@Override
	public String additionalOutputString() {
		String output = "; +" + String.valueOf(this.amount);
		if(this.percentage) {
			output += " %";
		}
		if(this.type) {
			output += " HP";
		} else {
			output += " MP";
		}
		return output;
	}
}