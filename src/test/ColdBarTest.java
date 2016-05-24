package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import system.ColdBarrista;

@RunWith(Parameterized.class)
public class ColdBarTest {

	private ColdBarrista coldBar;
	private byte smoothie;
	private byte iceCoffee;
	
	@Before
	public void setup() {
		coldBar = new ColdBarrista();
	}
	
	public ColdBarTest(Byte id, Byte smoothie, Byte iceCoffee) {
		this.smoothie = smoothie;
		this.iceCoffee = iceCoffee;
	}
	
	@Parameters
	public static Collection massiveOrders () {
		return Arrays.asList(new Byte[][]{
			{1,1,0},
			{2,0,1},
			{3,1,1},
			{4,-1,0},
			{127,127,127}
		});
	}
	
	@Test
	public void testSmoothie() {
		if(smoothie > 0)
			assertTrue(coldBar.produceSmoothie(smoothie));
		else 
			assertFalse(coldBar.produceSmoothie(smoothie));
	}
	
	@Test
	public void testIC() {
		if(iceCoffee > 0)
			assertTrue(coldBar.produceIC(iceCoffee));
		else
			assertFalse(coldBar.produceIC(iceCoffee));
	}
}
