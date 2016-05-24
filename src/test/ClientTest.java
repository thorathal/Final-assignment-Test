package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.Test;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import client.Client;

public class ClientTest {

	private Client client;
	private Channel fromClient;
	private Connection conn;
	
	@Before
	public void setup() {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {
			conn = factory.newConnection();
			fromClient = conn.createChannel();
//			fromClient.queueDeclare("toRouter", false, false, true, null);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
		client = new Client();
	}
	
	@Test
	public void bestillingstest() {
		client.setOrder(new byte[] {-2,2,3,5});
		byte[] tmp = client.getOrders();
		System.out.println("orders = " + tmp[0] + ";" + tmp[1] + ";" + tmp[2] + ";" + tmp[3]);
		assertTrue(client.bestil());
	}
}
