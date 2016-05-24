package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import system.ColdBarrista;
import system.HotBarrista;
import system.Waiter;

@RunWith(Parameterized.class)
public class RouterTest {
	
	private Waiter waiter;
	private Channel channel;
	private Connection conn;
	private Consumer consumer;
	private byte[] order;
	private String reply;
	private ColdBarrista coldBar;
	private HotBarrista hotBar;
	
	@Before
	public void setUp() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
//		reply = "";
		try {
			conn = factory.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare(Waiter.FROM_HOT_BARRISTA, false, false, true, null);
			channel.queueDeclare(Waiter.FROM_COLD_BARRISTA, false, false, true, null);
			channel.queueDeclare("toClient", false, false, true, null);
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties pros, byte[] body) throws UnsupportedEncodingException {
					for(int i = 0; i < body.length; i++) {
						if(body.length>1)
							reply += body[i];
					}
					System.out.println("reply = " + reply);
				}
			};
			channel.basicConsume("toClient", true, consumer);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
		waiter = new Waiter();
		hotBar = new HotBarrista();
		coldBar = new ColdBarrista();
		hotBar.run();
		coldBar.run();
		waiter.run();
	}
	
	public RouterTest(Byte coffee, Byte latte, Byte smoothie, Byte ic) {
		order = new byte[] {coffee, latte, smoothie, ic};
		reply = "";
	}
	
	@Parameters
	public static Collection produceOrders() {
		return Arrays.asList(new Byte[][] {
			{1,0,0,0},
			{0,0,1,0},
			{1,0,1,0},
			{1,1,1,1},
			{3,0,1,0},
			{10,0,10,0}
		});
	}
	
	@Test
	public void testOrderProcessing() throws IOException {
		channel.basicPublish("", "toWaiter", null, order);
		int millis = (order[0]*3000 + order[1]*5000 + order[2]*4500 + order[3]*8000);
		try {
			Thread.sleep(millis+100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail();
		}
		if(reply.length() > 0)
			fail();
		reply = "";
	}
}
