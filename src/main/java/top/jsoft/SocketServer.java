package top.jsoft;

import top.jsoft.model.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by psygrammator
 * group jsoft.top
 */
public class SocketServer {
    private static final int PORT = 7777;
    private final List<Client> clients = new CopyOnWriteArrayList<>();
    private static int indexNewClient = 1;

    public void startServer()
    {
        try(ServerSocket socketServer = new ServerSocket(PORT))
        {
            System.out.println("Server is run! Address: " + socketServer.getInetAddress().getHostAddress() + " Port: " + PORT);

            while (true) {
                Socket client = socketServer.accept();
                String newClientName = "Client-" + indexNewClient;

                new Thread(() -> {
                    try (BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(client.getInputStream())));
                         PrintWriter outWriter = new PrintWriter(client.getOutputStream(), true))
                    {
                        Client newClient = new Client(client, newClientName, LocalDateTime.now(), outWriter);
                        clients.add(newClient);
                        System.out.println("Client connected: " + newClientName);
                        indexNewClient++;

                        sendNotifyAllClients(clients, newClientName + " connected to server.", newClientName);

                        receiveClientMsg(clients, bufferedReader, newClient, newClientName, client);
                    }
                    catch (SocketException e)
                    {
                        if(clients.removeIf(c -> newClientName.equals(c.getName())))
                            System.out.println(newClientName + " disconnected.");
                    }
                    catch (IOException e) {
                        System.out.println("IOException: " + e);
                    }
                }).start();
            }
        }
        catch (IOException e) {
            System.out.println("" + e);
        }
    }

    public void receiveClientMsg(List<Client> clients, BufferedReader bufferedReader, Client newClient, String newClientName, Socket client) throws IOException {
        String msg;
        while ((msg = bufferedReader.readLine()) != null) {
            if ("exit".equals(msg)) {
                clients.remove(newClient);
                sendNotifyAllClients(clients, newClientName + ": disconnected.", newClientName);
                break;
            } else if (msg.startsWith("file ")) {
                receiveFile(msg, newClient.getName(), client.getInputStream());
            }
            else {
                System.out.println(newClientName + ": " + msg);
                sendNotifyAllClients(clients, newClientName + ": " + msg, newClientName);
            }
        }
    }
    public void receiveFile(String msg, String clientName, InputStream inputStream) throws IOException {
        String fileName = clientName + "_" + (msg.substring(msg.lastIndexOf(' ') + 1)).trim();
        File file = new File(fileName);
        int length;
        byte[] bufferBytes = new byte[8192];
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            while ((length = inputStream.read(bufferBytes)) >= 0) {
                bufferedOutputStream.write(bufferBytes, 0, length);
            }
        }
    }

    public void sendNotifyAllClients(List<Client> clients, String message, String fromClient) {
        clients.stream().filter(c -> !fromClient.equals(c.getName())).forEach(c -> c.getPrintWriter().println(message));
    }
}
