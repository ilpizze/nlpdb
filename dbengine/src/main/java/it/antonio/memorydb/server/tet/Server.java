package it.antonio.memorydb.server.tet;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.AsynchronousChannelGroup;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

    public static void main (String [] args)
            throws IOException {
	
        new Server().go();
    }

    private Thread currentThread;

    private void go()
            throws IOException {
			
        final AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(5, Executors.defaultThreadFactory());

        final AsynchronousServerSocketChannel listener = AsynchronousServerSocketChannel.open(group);
			
        InetSocketAddress hostAddress = new InetSocketAddress("localhost", 3883);
        listener.bind(hostAddress);
		
        System.out.println("Server channel bound to port: " + hostAddress.getPort());
        System.out.println("Waiting for client to connect... ");
		
        currentThread = Thread.currentThread();
		
        final String att1 = "First connection";

        listener.accept(att1, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel ch, Object att) {
            	CompletionHandler<AsynchronousSocketChannel, Object> c = this; 
            	
            	
            	System.out.println("Completed: " + att);
                ByteBuffer buffer = ByteBuffer.allocate(32);
                ch.read(buffer, null, new CompletionHandler<Integer, Void>() {

        			@Override
        			public void completed(Integer result, Void attachment) {
        				buffer.flip();
        		        String msg = new String(buffer.array()).trim();
        		        System.out.println("Message from client: " + msg);
        		        buffer.clear();
        				
        		        
        		        if (msg.equals("Bye")) {

                            if (! group.isTerminated()) {

                                System.out.println("Terminating the group...");

                                try{
                                    group.shutdownNow();
                                    group.awaitTermination(10, TimeUnit.SECONDS);
                                }
                                catch (IOException | InterruptedException e) {
        						
                                    System.out.println("Exception during group termination");
                                    e.printStackTrace();
                                }
        						
                                currentThread.interrupt();
                            }
                        }
        		      
        		        listener.accept(att, c);
        		       
        			}

        			@Override
        			public void failed(Throwable exc, Void attachment) {
        				exc.printStackTrace();
        			}
        		});
					
               
                
            }
			
            @Override
            public void failed(Throwable e, Object att) {
			
                System.out.println(att + " - handler failed");
                e.printStackTrace();
                currentThread.interrupt();
            }
        });
			
        try {
            currentThread.join();
        }
        catch (InterruptedException e) {
        }
		
        System.out.println ("Exiting the server");	
    } // go()
			
    
}