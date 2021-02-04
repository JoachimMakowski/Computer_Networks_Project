package client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerConnection implements Runnable{

    private String nickname;
    private String hostAddress;
    private int port;

    private Socket socket;
    private InputStream is;
    private OutputStream os;

    public ServerConnection(){
    }

    @Override
    public void run() {
        
    }
}
