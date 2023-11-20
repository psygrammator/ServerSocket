package top.jsoft;

import org.junit.Assert;
import org.junit.Test;
import top.jsoft.model.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by psygrammator
 * group jsoft.top
 */
public class SocketServerTest {
    @Test
    public void testReceiveClietMsg() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new StringReader("exit"));
        Client newClient = mock(Client.class);
        String newClientName = "Client-1";
        Socket client = mock(Socket.class);

        List<Client> clients = new CopyOnWriteArrayList<>();
        clients.add(newClient);

        SocketServer socketServer = new SocketServer();
        socketServer.receiveClientMsg(clients, bufferedReader, newClient, newClientName, client);

        Assert.assertEquals(0, clients.size());
    }

    @Test
    public void testReceiveFile() throws IOException {
        String msg = "file file.txt";
        String clientName = "Client-1";
        InputStream inputStream = new ByteArrayInputStream("Hello".getBytes());

        File file = new File(clientName + "_file.txt");
        file.deleteOnExit();

        SocketServer socketServer = new SocketServer();
        socketServer.receiveFile(msg, clientName, inputStream);

        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file)))
        {
            Assert.assertEquals("Hello", bufferedReader.readLine());
        }

        file.deleteOnExit();
    }

    @Test
    public void testNotifyAllClients()
    {
        List<Client> clients = new CopyOnWriteArrayList<>();
        Socket socket = mock(Socket.class);

        PrintWriter printWriter1 = mock(PrintWriter.class);
        PrintWriter printWriter2 = mock(PrintWriter.class);

        Client client1 = new Client(socket, "Client-1", LocalDateTime.now(), printWriter1);
        Client client2 = new Client(socket, "Client-2", LocalDateTime.now(), printWriter2);
        clients.add(client1);
        clients.add(client2);

        SocketServer serverSocket = new SocketServer();
        serverSocket.sendNotifyAllClients(clients, "Hello", client2.getName());

        verify(printWriter1).println("Hello");
        verify(printWriter2, never()).println("Hello");
    }
}
