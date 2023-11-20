package top.jsoft.model;

import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * Created by psygrammator
 * group jsoft.top
 */
public class Client {
    private final Socket socket;
    private final String name;
    private final LocalDateTime dateTime;
    private final PrintWriter printWriter;

    public Client(Socket socket, String name, LocalDateTime dateTime, PrintWriter printWriter) {
        this.socket = socket;
        this.name = name;
        this.dateTime = dateTime;
        this.printWriter = printWriter;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }
}
