package system;

/**
 * 
 * @author Jannik Davidsen
 *
 */
public class Order {

	/**
	 * A byte containing the id of the order. The id is represented as a
	 * number (Integer)
	 */
	private byte id;
	/**
	 * Byte containing the amount of cups of coffee. Amount is represented as a
	 * number (Integer)
	 */
	private byte coffee;
	/**
	 * Byte indicating the amount of cafe Latté that has been ordered. Amount
	 * is represented as a number (Integer)
	 */
	private byte cafeLatte;
	/**
	 * Byte indicating the amount of smoothies that has been ordered. Amount
	 * is represented as a number (Integer)
	 * 
	 */
	private byte smoothie;
	/**
	 * Byte indicating the amount of Ice coffee that has been ordered. Amount
	 * is represented as a number (Integer)
	 */
	private byte iceCoffee;
	/**
	 * Boolean showing if hot drinks has been served or not.
	 */
	private boolean hotServed;
	/**
	 * Boolean showing if cold drinks has been served or not.
	 */
	private boolean coldServed;
	
	/**
	 * Creates a new Order from a given byte array, and assigns the
	 * values. Expected input it
	 * [id, coffee, Cafe Latte, Smoothies, Ice Coffee]
	 * @param orders The byte array to use for creating the order.
	 */
	public Order(byte[] orders) {
		id = orders[0];
		coffee = orders[1];
		cafeLatte = orders[2];
		smoothie = orders[3];
		iceCoffee = orders[4];
	}

	public byte getId() {
		return id;
	}
	
	public boolean coldServed() {
		return coldServed;
	}

	public void serveCold(boolean coldServed) {
		this.coldServed = coldServed;
	}

	public void serveHot(boolean served) {
		hotServed = served;
	}
	
	public boolean hotServed() {
		return hotServed;
	}
	
	/**
	 * Method for converting the order into a byte array.
	 * @return a 5 columns byte array containing id, coffee, cafe latté,
	 * smooties, ice coffees.
	 */
	public byte[] toByteArray() {
		return new byte[] {id, coffee, cafeLatte, smoothie, iceCoffee};
	}
	
	/**
	 * Method for retrieving all the ordered cold drinks with id first.
	 * @return An array with numbers indicating the amount of smoothies and
	 * Ice coffee to make. Id of the order is first, smoothies next and last
	 * Ice coffee.
	 */
	public byte[] getColdDrinks() {
		return new byte[] {id, smoothie, iceCoffee};
	}
	
	/**
	 * Method for retrieving all the ordered hot drinks with id first.
	 * @return An array with numbers indicating the amount of coffee and
	 *  cafelatte to brew. Id of the order is first, coffee next and last
	 *  cafeLatte.
	 */
	public byte[] getHotDrinks() {
		return new byte[] {id, coffee, cafeLatte};
	}
}
