package com.latency.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientTCP {
	public static void main(String[] args) throws IOException {
        //Create input stream
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream("C:\\Java-Test-Example\\Client\\VTS_01_1.vob"));
        //Create Socket
        Socket client = new Socket("172.20.10.14", 8888);
        //Output stream
        BufferedOutputStream bos = new BufferedOutputStream(client.getOutputStream());
        //Write data
        byte[] b = new byte[1024 * 8];
        int len;
        while ((len = bis.read(b)) != -1) {
            bos.write(b, 0, len);
            bos.flush();
        }
        System.out.println("File uploaded");

        //close resource
        bos.close();
        client.close();
        bis.close();
        System.out.println("File upload completed");
    }	
}
