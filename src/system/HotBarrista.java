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

public class HotBarrista implements Runnable {

	private ConnectionFactory factory;
	private Channel channel;
	private Connection conn;
	private Consumer consumer;

	@Override
	public void run() {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			conn = factory.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare("toHotBar", false, false, true, null);
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties props, byte[] body)
						throws IOException {
					try {
						handleInts(body);
					} catch (Exception exx) {
						exx.printStackTrace();
					}
				}
			};
			channel.basicConsume("toHotBar", true, consumer);
		} catch (TimeoutException | IOException ex) {
			ex.printStackTrace();
		}
	}

	private void handleInts(byte[] orders) throws Exception {
		if (orders[1] > 0)
			for (int i = 0; i < orders[1]; i++)
				makeCoffee();
		if (orders[2] > 0)
			for (int i = 0; i < orders[2]; i++)
				makeLatté();
		returnToSender(orders);
	}

	private void returnToSender(byte[] msg) {
		try {
			channel.basicPublish("", Waiter.FROM_HOT_BARRISTA, null, msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method used for producing a cup of coffee
	 * 
	 * @return {@link Boolean#TRUE} if a cup is successfully brewed, False
	 *         otherwise.
	 */
	public boolean makeCoffee() {
		try {
			Thread.sleep(3000);
			return true;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean makeLatté() {
		try {
			Thread.sleep(5000);
			return true;
		} catch (InterruptedException ex) {
			ex.printStackTrace();
			return false;
		}
	}
}