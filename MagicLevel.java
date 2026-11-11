/**
 * Defines the various magic levels.
 * wizards have a magic level.
 * in order to be able to use specific spells a minimum magic level is necessary.
 * Note: the compiler generated default constructor may not be sufficient for your implementation
 */
public enum MagicLevel {
	NOOB(50),
	ADEPT(100),
	STUDENT(200),
	EXPERT(500),
	MASTER(1000);

	private final int mp; // final ist wie const in c++

	MagicLevel(int mp) {
		this.mp = mp;
	}
	
	public int toMana() {
		return this.mp;
	}
	
	@Override
	public String toString() {
		return "*".repeat(this.ordinal() + 1); // ordinal() ist die Position der Konstanten (NOOB = 0, ADEPT = 1, ...)
	}
}