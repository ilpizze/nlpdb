package it.antonio.memorydb.server.tet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

import it.antonio.memorydb.Results;
import it.antonio.memorydb.query.Eq;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetSocketAddress;

public class Client {

    public static void main (String [] args)
            throws Exception {
	
    	ExecutorService service = Executors.newFixedThreadPool(10);
		
		for(int i = 0; i < 1; i++) {
			
				service.execute(() -> {
					try {
						new Client().go();
					} catch (Exception e) {
						e.printStackTrace();
					}
						
				});
				
			
			
		}
		service.shutdown();
		while(!service.isTerminated()) {}
        
    }

    private void go()
            throws IOException, InterruptedException, ExecutionException {
	
        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3883);
        Future future = client.connect(hostAddress);
        future.get(); // returns null

        System.out.println("Client is started");
        System.out.println("Sending message to server: ");
		
        byte [] bytes = new String("Bye1").getBytes();
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        Future result = client.write(buffer);
		
        while (! result.isDone()) {
            System.out.println("... ");
        }
        System.out.println(new String(buffer.array()).trim());
        buffer.clear();	
        client.close();
        
        
        bytes = new String("Bye2").getBytes();
        buffer = ByteBuffer.wrap(bytes);
        result = client.write(buffer);
        while (! result.isDone()) {
            System.out.println("... ");
        }
		
        
        System.out.println(new String(buffer.array()).trim());
        buffer.clear();	
        client.close();
    }
}