package system;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class ColdBarrista implements Runnable {

	private ConnectionFactory fac;
	private Connection conn;
	private Channel channel;
	private Consumer consumer;

	@Override
	public void run() {
		fac = new ConnectionFactory();
		fac.setHost("localhost");
		try {
			conn = fac.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare("toColdBar", false, false, true, null);
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties props, byte[] body) {
					handleOrders(body);
				}
			};
			channel.basicConsume("toColdBar", true, consumer);
		} catch (IOException | TimeoutException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method used for producing the orders.
	 * 
	 * @param orders
	 *            An array containing the orders to produced, and the id of the
	 *            order.
	 */
	private void handleOrders(byte[] orders) {
		if (orders[1] > 0)
			for (int i = 0; i < orders[1]; i++)
				produceSmoothie(orders[1]);
		if (orders[2] > 0)
			for (int i = 0; i < orders[2]; i++)
				produceIC(orders[2]);
		returnToSender(orders);
	}

	/**
	 * Method used for sending answer back to the {@link Waiter}
	 * 
	 * @param orders
	 *            An array containing all the orders which came from the
	 *            {@link Waiter}
	 */
	private void returnToSender(byte[] orders) {
		try {
			channel.basicPublish("", Waiter.FROM_COLD_BARRISTA, null, orders);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void cleanMachine() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Method used for producing a smoothie
	 * 
	 * @return true if a smoothie is successfully produced, false if failed
	 */
	public boolean produceSmoothie(byte amount) {
		if(amount < 1)
			return false;
		try {
			Thread.sleep(4500*amount);
			return true;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	/**
	 * MEthod for producing an Ice Coffee
	 * 
	 * @return True if it's successfully brewed, false if not
	 */
	public boolean produceIC(byte amount) {
		if(amount < 1)
			return false;
		try {
			Thread.sleep(8000*amount);
			return true;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}
