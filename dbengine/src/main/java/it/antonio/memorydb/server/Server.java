package it.antonio.memorydb.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

public class Server {
	private AsynchronousServerSocketChannel serverChannel;

	int times = 0;

	public Server(String url, int port) throws IOException {
		serverChannel = AsynchronousServerSocketChannel.open();
		InetSocketAddress hostAddress = new InetSocketAddress(url, port);
		serverChannel.bind(hostAddress);

	}

	public void start() {

		serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {

			@Override
			public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
				serverChannel.accept(null, this);

				read(socketChannel);

			}

			@Override
			public void failed(Throwable exc, Void attachment) {
				throw new RuntimeException(exc);
			}
		});

	}

	private void read(AsynchronousSocketChannel socketChannel) {
		final ByteBuffer buf = ByteBuffer.allocate(2048);

		socketChannel.read(buf, buf, new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer buff) {

				// System.out.println(times++);
				System.out.println("Read message:" + new String(buff.array()));

				// buffer.append(new String(buf.array()));

				read(socketChannel);

			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				throw new RuntimeException(exc);
			}
		});
	}

	public static void main(String[] args) {
		try {
			Server server = new Server("localhost", 4999);
			server.start();
			Thread.sleep(300);

			Client client = new Client("localhost", 4999);

			int i = 0;
			
			
			
			client.write(0, (v0) -> {
				client.write(1, (v1) -> {
					client.write(2, (v2) -> {
						client.write(3, (v3) -> {
							client.write(4, (v4) -> {
								client.write(5, (v5) -> {
									client.write(6, (v6) -> {
										client.write(7, (v7) -> {
											client.write(8, (v8) -> {
												client.write(9, (v9) -> {
													
												});
											});
										});
									});
								});
							});
						});
					});
				});
			});

			Thread.sleep(1000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static class Client {

		AsynchronousSocketChannel sockChannel;
		private Queue<String> queue = new ConcurrentLinkedQueue<>();

		public Client(String host, int port) throws IOException {
			sockChannel = AsynchronousSocketChannel.open();

			// try to connect to the server side
			sockChannel.connect(new InetSocketAddress(host, port), sockChannel,
					new CompletionHandler<Void, AsynchronousSocketChannel>() {
						@Override
						public void completed(Void result, AsynchronousSocketChannel channel) {
							System.out.println("CONNECTED");
						}

						@Override
						public void failed(Throwable exc, AsynchronousSocketChannel channel) {
							throw new RuntimeException(exc);
						}

					});

		}

		public void write(int i, Consumer<Void> c) {

			String s = "Antonio" + i;
			ByteBuffer buf = ByteBuffer.allocate(2048);
			buf.put(s.getBytes());
			buf.flip();

			//System.out.println("write " + s);

			sockChannel.write(buf, sockChannel, new CompletionHandler<Integer, AsynchronousSocketChannel>() {
				@Override
				public void completed(Integer result, AsynchronousSocketChannel channel) {
					c.accept(null);
				}

				@Override
				public void failed(Throwable exc, AsynchronousSocketChannel channel) {
					throw new RuntimeException(exc);
				}
			});
		}

	}

}
