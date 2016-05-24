package system;

import client.Client;

public class Main {

	public static void main(String[] args) {
		HotBarrista hotBar = new HotBarrista();
		hotBar.run();
		ColdBarrista coldBar = new ColdBarrista();
		coldBar.run();
		Waiter waiter = new Waiter();
		waiter.run();
		Client.main(args);
	}

}
