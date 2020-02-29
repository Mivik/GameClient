package com.mivik.gameclient;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class SocketManager implements Closeable {
	private static final Logger L = Logger.getLogger(SocketManager.class);

	public static final Charset DEFAULT_CHARSET = Charset.forName("utf-8");

	private InetAddress addr;
	private String host;
	private int port;
	private Socket socket;
	private InputStream in;
	private OutputStream out;
	private Charset charset = DEFAULT_CHARSET;
	private Receiver receiver;

	public SocketManager(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public SocketManager(InetAddress addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	public void setAddress(String host, int port) {
		this.addr = null;
		this.host = host;
		this.port = port;
	}

	public void setAddress(InetAddress addr, int port) {
		this.addr = addr;
		this.port = port;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public Charset getCharset() {
		return charset;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public InputStream getInputStream() {
		return in;
	}

	public OutputStream getOutputStream() {
		return out;
	}

	public void connect() throws IOException {
		close();
		L.info(host + ":" + port);
		if (addr == null) addr = InetAddress.getByName(host);
		socket = new Socket(addr, port);
		in = socket.getInputStream();
		out = socket.getOutputStream();
	}

	public void startReceiving() {
		new Thread() {
			@Override
			public void run() {
				try {
					byte[] buf = new byte[1025];
					while (receiver.shouldReceive()) {
						int read = in.read(buf);
						if (socket == null) break;
						if (!receiver.shouldReceive()) break;
						receiver.onReceive(new String(buf, 0, read, charset));
					}
				} catch (IOException e) {
					L.error("Failed to receive message", e);
					receiver.onReceive("Exit|" + e.getMessage());
				}
			}
		}.start();
	}

	public void send(final String msg, final boolean showErr) {
		new Thread() {
			@Override
			public void run() {
				try {
					out.write(msg.getBytes(charset));
				} catch (IOException e) {
					L.error("Failed to send message", e);
					if (showErr) {
						e.printStackTrace();
						System.exit(1);
					}
				}
			}
		}.start();
	}

	@Override
	public void close() throws IOException {
		if (socket != null) {
			socket.close();
			socket = null;
		}
		in = null;
		out = null;
	}

	public interface Receiver {
		boolean shouldReceive();

		void onReceive(String msg);
	}
}