package client;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Client {

	private byte[] orders = new byte[4];
	private JFrame frame;
	private ConnectionFactory fac;
	private Connection conn;
	private Channel channel;
	private Consumer consumer;
	private int id;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client window = new Client();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Client() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		fac = new ConnectionFactory();
		fac.setHost("localhost");
		try {
			conn = fac.newConnection();
			channel = conn.createChannel();
			channel.queueDeclare("toClient", false, false, true, null);
			consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String tag, Envelope env, AMQP.BasicProperties pro, byte[] body) throws IOException {
					if(body.length==1) {
						id = body[0];
						System.out.println("id set to " + id);
					} else {
						if(body[0] == id) {
							for(int i = 1; i < body.length; i++) {
								if(body[i] != orders[i-1])
									System.err.println("error in drinks");
							}
							System.out.println("done checking order");
							frame.setVisible(false);
							frame.dispose();
						} else {
							channel.basicPublish("", "toClient", null, body);
						}
					}
				}
			};
			channel.basicConsume("toClient", true, consumer);
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		} 
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		textArea = new JTextArea("Nuværende ordre: ", 40, 6);
		textArea.setEditable(false);
		JLabel label_4 = new JLabel("Kaffe");
		
		textField = new JTextField();
		textField.setColumns(10);
		JLabel label_2 = new JLabel("Kaffelatt\u00E9");
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		JLabel label = new JLabel("Smoothie");
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		
		JLabel lblIceCoffee = new JLabel("Ice coffee");
		
		textField_3 = new JTextField();
		textField_3.setColumns(10);
		
		JButton button_2 = new JButton("Tilf\u00F8j");
		
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateOrder();
			}
		});
		
		JButton btnSendOrdre = new JButton("Send ordre");
		btnSendOrdre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bestil();
			}
		});
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(2)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(lblIceCoffee, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGap(96))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(label, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
							.addGap(6)
							.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(84)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 167, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnSendOrdre, GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(button_2, GroupLayout.PREFERRED_SIZE, 86, GroupLayout.PREFERRED_SIZE)
					.addGap(137))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(1)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(label_4, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(label_2, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(label, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE))
								.addGroup(groupLayout.createSequentialGroup()
									.addGap(9)
									.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
							.addGap(11)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblIceCoffee, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(textArea, GroupLayout.DEFAULT_SIZE, 94, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnSendOrdre))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(47)
							.addComponent(button_2)))
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	private void updateOrder() {
		if(!textField.getText().isEmpty())
			orders[0] = Byte.parseByte(textField.getText());
		if(!textField_1.getText().isEmpty())
			orders[1] = Byte.parseByte(textField_1.getText());
		if(!textField_2.getText().isEmpty())
			orders[2] = Byte.parseByte(textField_2.getText());
		if(!textField_3.getText().isEmpty())
			orders[3] = Byte.parseByte(textField_3.getText());
		textArea.setText("Nuværende ordre: \nKaffe: " + orders[0] + "\nKaffelatté: " + orders[1] + "\nSmoothies: " + orders[2] + "\nIce coffee: " + orders[3]);
	}
	
	public void setOrder(byte[] orders) {
		this.orders = orders;
	}
	
	public byte[] getOrders() {
		return orders;
	}
	
	public boolean bestil() {
		try {
			channel.basicPublish("", "toWaiter", null, orders);
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * @deprecated Use {@link Client#setOrder(byte[])} and then {@link Client#bestil()} instead.
	 * @param s A String containing the order
	 * @return True if successfully ordered, false if not.
	 */
	public boolean bestil(String s) {
		try {
			channel.basicPublish("", "toWaiter", null, s.getBytes());
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
