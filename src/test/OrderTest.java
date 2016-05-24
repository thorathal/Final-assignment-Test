package test;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import system.Order;

@RunWith(Parameterized.class)
public class OrderTest {

	private Order order;
	
	@Before
	public void setup() {
		order = new Order(new byte[]{1,1,1,1,1});
	}
	
	/**
	 * This test case is created because we don't want our messaging to wait
	 * for long before an {@link Order} has been converted to a byte array.
	 */
	@Test(timeout=2)
	public void convertTest() {
		order.toByteArray();
	}
	
	public OrderTest(Byte id, Byte coffee, Byte cafelatte, Byte smoothie, Byte iceCoffee) {
		order = new Order(new byte[] {id, coffee, cafelatte, smoothie, iceCoffee});
	}
	
	@Parameters
	public static Collection produceorders() {
		return Arrays.asList(new Byte[][] {
			{1, 1, 0, 1, 0},
			{2, 1, 1, 0, 0},
			{3, 1, 1, 1, 1},
			{4, 0, 0, 1, 1},
			{127, 127, 127, 127, 127}, //testing converting time for maximum byte values
			{-128, -128, -128, -128, -128}
		});
	}
	
	@Test
	public void serveOrder() {
		order.serveHot(true);
		assertTrue(order.hotServed());
		order.serveCold(true);
		assertTrue(order.coldServed());
	}
}
