/**
 * @author shridhar
 * Name-Shridhar Kevati
 * Student Id- 999992831
 * Computer Network Assignment
 *
 */

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public final class WebServer {

	public static void main(String[] args) throws Exception {

		// Declare a TCP socket object and initialize to null
		// ServerSocket serverSocket = null;
		// Set the port number.
		int port = 3030;
		ServerSocket serverSocket = null;

		// Establish the listen socket
		try {

			serverSocket = new ServerSocket(port);
			System.out.println("TCP server created on port =" + port);


		// Process HTTP service requests in an infinite loop.
		while (true) {
			Socket clientSocket = null;

			clientSocket = serverSocket.accept();

			// Construct an object process the HTTP request message
			HttpRequest request = new HttpRequest(clientSocket);
			// create a new thread to process the request
			Thread thread = new Thread(request);
			// Start the thread
			thread.start();

		}
		}
		 catch (IOException ex) {
				System.out.println("Error : The server with port=" + port + "cannot be created");
			}
		// Closing the socket
		finally {
		    try {  
		    	serverSocket.close();
		    } catch(Exception e) {
		        // If you really want to know why you can't close the ServerSocket, like whether it's null or not
		    }
		}

	}
	
}
 final class HttpRequest implements Runnable {

	final static String CRLF = "\r\n";
	Socket socket;

	// Passing the socket object in HTTP request Constructor
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			processRequest();
            
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
//Process Client Request here and printing the connection information 
	private void processRequest() throws Exception {
		String server_url = "http://localhost:" + socket.getPort() + "/";
		System.out.println("server " + server_url);
		URL url = new URL(server_url);
		System.out.println("URL " + url);
		InputStream in = socket.getInputStream();
		DataOutputStream op = new DataOutputStream(socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String requestLine = br.readLine();
		System.out.println();
		// information from the connection objects,
		System.out.println("------------------------------------------");
		System.out.println("Information from the connection objects");
		System.out.println("------------------------------------------");
		System.out.println("RequestLine " + requestLine);
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());
		System.out.println("Port : " + socket.getPort());
		System.out.println("Protocol : " + url.getProtocol());
		System.out.println("TCP No Delay : " + socket.getTcpNoDelay());
		System.out.println("Timeout : " + socket.getSoTimeout());
		System.out.println("------------------------------------------");
		System.out.println();
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
        // Creating the StringTokenizer and passing requestline in constructor .
		//tokens object split the requestline and create the token 
		StringTokenizer tokens = new StringTokenizer(requestLine);

		tokens.nextToken(); // skip over the method, which should be “GET”
		String fileName = tokens.nextToken();
		// Prepend a “.” so that file request is within the current directory.
		fileName = "." + fileName;
		System.out.println("FileName GET" + fileName);
		// Open the requested file.
		FileInputStream fis = null;
		boolean fileExists = true;
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		// Construct the response message.
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		//Check file exist in directory or not
		if (fileExists) {
			statusLine = "200 OK ";
			contentTypeLine = "Content-Type:" + contentType(fileName) + CRLF;
		} else {
			statusLine = "HTTP/1.1 404 Not Found:";
			contentTypeLine = "Content-Type: text/html" + CRLF;
			entityBody = "<HTML>" + "<HEAD> <TITLE> Not Found</TITLE></HEAD>" + "<BODY> Not Found</BODY><HTML>";
		}
		// Send the status line.
		op.writeBytes(statusLine);
		op.writeBytes(CRLF);
		// Send the content type line.
		op.writeBytes(contentTypeLine);

		// Send a blank line to indicate the end of the header lines.
		op.writeBytes(CRLF);
		// Send the entity body.
		if (fileExists) {

			sendBytes(fis, op);

			fis.close();
		} else {
			op.writeBytes(entityBody);
		}
//close the open objects
		op.close();
		br.close();
		socket.close();

	}
//contentType Method which evalutes the file extension
	private static String contentType(String fileName) {
		if (fileName.endsWith(".txt") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		}
		if (fileName.endsWith(".gif")) {
			return "image/gif";
		}
		return "application/octet-stream";
	}

	private static void sendBytes(FileInputStream fis, OutputStream op) throws Exception {
		// Construct a 1K buffer to hold bytes on their way to the socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;

		// Copy requested file into the socket’s output stream.
		while ((bytes = fis.read(buffer)) != -1) {
			op.write(buffer, 0, bytes);
		}

	}

}