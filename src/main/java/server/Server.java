package server;

public class Server {
    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Usage: java Server <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
    }
}
