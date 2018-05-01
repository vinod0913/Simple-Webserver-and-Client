
/**
 * @author shridhar
 * Name-Shridhar Kevati
 * Student Id- 999992831
 * Computer Network Assignment
 *
 */
//Import Required Packages
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

public class Client {
	// Initialize instance Variables
	private static Socket socket = null;
	final static String CRLF = "\r\n";
	private static String fileName;
	// private static String serverHostName = "localhost";
	private static int port = 3030;

	// Main Method
	public static void main(String args[]) throws Exception {
		long timeStart, timeExit, timeRun;
		timeStart = System.nanoTime();
		// Setting connection with the server
		String server_url = "http://" + "localhots" + ":" + port + "/";
		URL url = new URL(server_url);
		;
		socket = new Socket("127.0.0.1", port);
		System.out.println("connection established with the server");

		// requesting file from the server
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter the file you want to search");
		fileName = scan.next();
		PrintStream printstream = new PrintStream(socket.getOutputStream());
		printstream.println("GET" + " /" + fileName + " " + "HTTP/1.1" + CRLF);
		System.out.println("Connection received from : " + socket.getInetAddress().getHostName());
		System.out.println("Port : " + socket.getPort());
		System.out.println("Protocol :" + url.getProtocol());
		System.out.println("TCP No Delay : " + socket.getTcpNoDelay());
		System.out.println("TCP No Delay : " + socket);

		System.out.println("Timeout : " + socket.getSoTimeout());
		System.out.println("response from server");
		// printing server response
		try {
			BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			StringBuilder stringbuilder = new StringBuilder();
			String get_String;
			while ((get_String = bufferedreader.readLine()) != null) {
				stringbuilder.append(get_String + "\n");
			}
			bufferedreader.close();
			System.out.println(stringbuilder.toString());
			socket.close();
			timeExit = System.nanoTime();
			timeRun = (timeExit - timeStart) / 1000000;
			// Calulation of RTT
			System.out.println("Calculation of RTT=" + (double) Math.round(timeRun * 100) / 100 + " ms");

		} catch (IOException ex) {
			System.out.println(ex.getMessage());
			scan.close();
		}
	}
}
