package com.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Following is the TCP client program. It will accept the argument server IP,
 * PORT and filePath. It will upload the file on the given port.
 * 
 * @author Admin
 *
 */

public class TCPClient {
	
	

	public static void main(String[] args) {
		Process process = null;
		try {
			System.out.println("===========================");
			System.out.println("||Client File||");
			System.out.println("===========================");
			System.out.println();
			Scanner sc = new Scanner(System.in);
			System.out.print("Please enter Server IP ");
			String serverIP = sc.next();
			System.out.print("Please enter Port ");
			int serverPort = sc.nextInt();
			System.out.print("Please enter file path to trasfer ");
			String filePath = sc.next();
			System.out.println("You have entered IP: " + serverIP);
			System.out.println("You have entered Port: " + serverPort);
			System.out.println("You have entered FilePath: " + filePath);
			String command = "sudo tcpdump -s 64 port " + serverPort
					+ " -W 1500 -C 25 -w /home/java/temp/test.pcap";
			System.out.println("pcap file generation to start with command "
					+ command);
			process = Runtime.getRuntime().exec(command);
			// Create input stream
			BufferedInputStream bis = new BufferedInputStream(
					new FileInputStream(filePath));

			// Create Socket
			Socket client = new Socket(serverIP, serverPort);
			// client.setSendBufferSize(1);
			// Output stream
			BufferedOutputStream bos = new BufferedOutputStream(
					client.getOutputStream());

			// Write data
			byte[] b = new byte[1024 * 8];
			int len;
			while ((len = bis.read(b)) != -1) {
				bos.write(b, 0, len);
				bos.flush();
			}
			// close resource
			bos.close();
			client.close();
			bis.close();
			sc.close();
			System.out.println("Success!");
			Thread.sleep(10000);
			DataOutputStream ios = new DataOutputStream(
				process.getOutputStream());
			ios.writeBytes("pkill -SIGINT tcpdump");
			ios.flush();
			ios.close();
			System.out.println();
			//process.waitFor();
			long pid = process.pid();
			System.out.println("PID "+pid);
			Runtime.getRuntime().exec("kill -9" + pid);
		} catch (Exception e) {
			System.out.println("Failed!");
			long pid = process.pid();
			e.printStackTrace();
		}
	}
}
