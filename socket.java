public class socket implements Runnable {

    private String host;
    private int port;
    

    public socket(String host, int port) {
        this.host = host;
        this.port = port;
    }

    
    public void run() {
        System.out.println("Hello from socket");
    }
    
}
