package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import system.HotBarrista;

public class HotBarTest {

	private HotBarrista hotbar;
	
	@Before
	public void setUp() throws Exception {
		hotbar = new HotBarrista();
	}

	@Test(timeout=3010)
	public void testCoffeeProduction() {
		assertTrue(hotbar.makeCoffee());
	}
	
	@Test(timeout=5010)
	public void testLattéProduction() {
		assertTrue(hotbar.makeLatté());
	}

}
