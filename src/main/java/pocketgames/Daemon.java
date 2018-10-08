package pocketgames;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.beust.jcommander.JCommander;

public class Daemon {

	public static void main(final String[] args) throws IOException, AWTException {
		Daemon main = new Daemon();
		JCommander.newBuilder().addObject(main).build().parse(args);
		main.run();
	}

	private void run() throws IOException, AWTException {
		DatagramSocket socket = new DatagramSocket(4445);

		boolean running = true;
		byte[] buf = new byte[256];
		Robot robot = new Robot();

		while (running) {
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);

			InetAddress address = packet.getAddress();
			int port = packet.getPort();
			packet = new DatagramPacket(buf, buf.length, address, port);
			String received = new String(packet.getData(), 0, packet.getLength());

			if (received.equals("end")) {
				running = false;
				continue;
			}

			System.out.println(received);

			int keycode = KeyEvent.VK_SPACE;
			robot.keyPress(keycode);
			robot.keyRelease(keycode);

			// echo
			socket.send(packet);
		}
		socket.close();
	}
}
