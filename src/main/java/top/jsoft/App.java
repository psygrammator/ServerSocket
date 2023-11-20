package top.jsoft;

/**
 * Created by psygrammator
 * group jsoft.top
 */
public class App 
{
    public static void main( String[] args )
    {
        SocketServer socketServer = new SocketServer();
        socketServer.startServer();
    }
}
