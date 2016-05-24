package system;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Waiter implements Runnable {

	public static final String FROM_HOT_BARRISTA = "fromHotBar";
	public static final String FROM_COLD_BARRISTA = "fromColdBar";
	private LinkedList<Order> orders;
	private ConnectionFactory factory;
	private Channel channel;
	private Connection conn;
	private Consumer orderConsumer;
	private Consumer fromHotBar;
	private Consumer fromColdBar;
	private byte nextID = 1;

	@Override
	public void run() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		 orders = new LinkedList<Order>();
		try {
			conn = factory.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare("toWaiter", false, false, true, null);
			channel.queueDeclare(FROM_HOT_BARRISTA, false, false, true, null);
			channel.queueDeclare(FROM_COLD_BARRISTA, false, false, true, null);
			orderConsumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties props, byte[] body)
						throws IOException {
					byte[] tmp = new byte[5];
					tmp[0] = nextID;
					nextID++;
					channel.basicPublish("", "toClient", null, new byte[]{tmp[0]});
					for (int i = 1; i < tmp.length; i++)
						tmp[i] = body[i-1];
					orders.add(new Order(tmp));
					processOrders(tmp);
				}
			};
			channel.basicConsume("toWaiter", true, orderConsumer);
			fromHotBar = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties pros, byte[] body) throws IOException {
					assembleOrders(body, true);
				}
			};
			fromColdBar = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties pros, byte[] body) throws IOException {
					assembleOrders(body, false);
				}
			};
			channel.basicConsume(FROM_HOT_BARRISTA, true, fromHotBar);
			channel.basicConsume(FROM_COLD_BARRISTA,  true, fromColdBar);
		} catch (TimeoutException | IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void assembleOrders(byte[] incoming, boolean hot) {
		for(int i = 0; i < orders.size(); i++) {
			if(orders.get(i).getId() == incoming[0]) {
				if(hot) {
					if(incoming[1] == orders.get(i).getHotDrinks()[1] && incoming[2] == orders.get(i).getHotDrinks()[2]) {
						orders.get(i).serveHot(hot);
						if(orders.get(i).coldServed()) {
							sendToClient(orders.get(i).toByteArray());
							orders.remove(i);
						}
					}
				} else {
					if(incoming[1] == orders.get(i).getColdDrinks()[1] && incoming[2] == orders.get(i).getColdDrinks()[2]) {
						orders.get(i).serveCold(true);
						if(orders.get(i).hotServed()) {
							sendToClient(orders.get(i).toByteArray());
							orders.remove(i);
						}
					}
				}
			}
		}
	}
	
	private void sendToClient(byte[] order) {
		try {
			channel.basicPublish("", "toClient", null, order);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean processOrders(byte[] order) {
		boolean hot = false, cold = false;
		byte[] hotOrder = new byte[3];
		if(order[1] > 0 || order[2] > 0) {
			hotOrder[0] = order[0];
			hotOrder[1] = order[1];
			hotOrder[2] = order[2];
			hot = true;
		}
		byte[] coldOrder = new byte[3];
		if(order[3] > 0 || 0 < order[4]) {
			cold = true;
			coldOrder[0] = order[0];
			coldOrder[1] = order[3];
			coldOrder[2] = order[4];
		}
		try {
			if(hot)
				channel.basicPublish("", "toHotBar", null, hotOrder);
			else 
				assembleOrders(new byte[]{order[0],  0, 0}, true);
			if(cold)
				channel.basicPublish("", "toColdBar", null, coldOrder);
			else
				assembleOrders(new byte[]{order[0],  0, 0}, false);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}