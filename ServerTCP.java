

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerTCP {
	public static void main(String[] args) throws IOException {
        System.out.println("Service started, waiting for connection");
        //Create ServerSocket object, bind port, start waiting for connection
        ServerSocket ss = new ServerSocket(8888);
        //accept method, return socket object
        Socket server = ss.accept();
        //Get input object, read file
        BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
        //Save to local
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("C:\\Java-Test-Example\\Server\\a.vob"));

        //Create byte array
        byte[] b = new byte[1024 * 8];
        //Read character array
        int len;
        while ((len = bis.read(b)) != -1) {
            bos.write(b, 0, len);
        }

        //close resource
        bos.close();
        bis.close();
        server.close();
        System.out.println("Upload succeeded");
    }
}
