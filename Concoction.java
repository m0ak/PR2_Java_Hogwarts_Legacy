import java.util.ArrayList;
import java.util.List;

/**
 * Concoctions can affect health and mana of the target at the same time and additionally
 * cast any number of spells
 * <p>
 * It is not allowed for health and mana to be both 0 and spells to be empty.
 * The concoction must at least have one effect
 */
public class Concoction extends Potion {
	private int health; // change of health on target; may be any int value
	private int mana; // change of mana on target; may be any int value
	private List<Spell> spells; // spells cast when concoction is consumed; must not be null but may be empty; Use ArrayList as concrete type

	/**
	 * @param name name
	 * @param usages number of usages still left
	 * @param price price
	 * @param weight weight
	 * @param health change of health on target
	 * @param mana change of mana on target
	 * @param spells list of spells that are cast when consuming the concoction 
	 */
	public Concoction(String name,	int usages, int price, int weight, int health, int mana, List<Spell> spells) {
		super(name, usages, price, weight);
		this.health = health;
		this.mana = mana;
		if(spells == null) {
			throw new IllegalArgumentException("Concoction constructor List == null");
		}
		if(this.health == 0 && this.mana == 0 && spells.isEmpty()) {
			throw new IllegalArgumentException("Concoction constructor health & mana == 0 && spells.isEmpty");
		}
		this.spells = new ArrayList<Spell>(spells);
	}
	
	/**
	 * Returns "; '+/-''health' HP; '+/-''mana' MP; cast 'spells' ";
	 * here '+/-' denotes the appropriate sign, spells will be a bracketed list
	 * of spells (Java default toString method for lists)
	 * e.g. (total result of toString) "[My Brew; 2 g; 2 Knuts; 4 gulps; -5 HP; +10 MP; cast [[Confringo(*) -20 HP], [Diffindo(*) -15 HP]]]"
	 * If health or mana is 0 or spells is empty, then the respective part(s) are suppressed
	 * e. g. "[Your Brew; 2 g; 1 Knut; 1 gulp; +5 MP]
	 * @return "; '+/-''health' HP; '+/-''mana' MP; cast 'spells' "
	 */
	@Override
	public String additionalOutputString() {
		String output = "";
		if(this.health > 0) {
			output += "; +" + String.valueOf(this.health) + " HP";
		} else if (this.health < 0) {
			output += "; " + String.valueOf(this.health) + " HP";
		}
		
		if(this.mana > 0) {
			output += "; +" + String.valueOf(this.mana) + " MP";
		} else if (this.mana < 0) {
			output += "; " + String.valueOf(this.mana) + " MP";
		}
		if(!spells.isEmpty()) {
			output += "; cast " + spells.toString();
		}
		return output;
	}

	/**
	 * If usages is greater than 0 reduce usages by 1 (tryUsage method) and
	 * change HP of target by health (call method heal(health) or takeDamage(health)
	 * depending on sign of health) and
	 * change MP of target by mana (call method enforceMagic(mana) or weakenMagic(mana)
	 * depending on sign of mana) and
	 * call cast Method for every spell in spells.
	 * @param target target that takes the magic effects
	 */
	@Override  
	public void useOn(MagicEffectRealization target) {
		if(target == null) {
			throw new IllegalArgumentException("Concoction useOn target == null");
		}
		if(this.tryUsage()) {
			if(this.health < 0) {
				target.takeDamage(-this.health);
			}
			if(this.health > 0) {
				target.heal(this.health);
			}
			if(this.mana < 0) {
				target.weakenMagic(-this.mana);
			}
			if(this.mana > 0) {
				target.enforceMagic(this.mana);
			}
			for(Spell s : this.spells) {
				s.cast(this, target);
			}
		}
	}
}