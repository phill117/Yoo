import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
 
 
/**
 * Simple Web Server for learning purposes. Handles one client connection 
 * at a time and sends back a static HTML page as response.
 */
public class WebServer {

    public static void main(String[] args) {

        final int LISTENING_PORT = 8080;

        WebServer webServer = new WebServer();
        try {
            webServer.runServer(LISTENING_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

	ServerSocket s;
 
	 /**
	  * Creates and returns server socket.
	  * @param port Server port.
	  * @return created server socket
	  * @throws Exception Exception thrown, if socket cannot be created.
	  */
    protected ServerSocket getServerSocket(int port) throws Exception {
        return new ServerSocket(port);
    }
 
    /**
     * Starts web server and handles web browser requests.
     * @param port Server port(ex. 80, 8080)
     * @throws Exception Exception thrown, if server fails to start.
     */
    public void runServer(int port) throws Exception {
        s = getServerSocket(port);
 
        while (true) {
            try {
                Socket serverSocket = s.accept();
                handleYo(serverSocket);
            } catch(IOException e) {
            	System.out.println("Failed to start server: " + e.getMessage());
                System.exit(0);
                return;
            }
        }
    }

    String webServerAddress;

    public void handleYo(Socket s){
        BufferedReader is;     // inputStream from web browser
        String request;        // Request from web browser
        try {
            webServerAddress = s.getInetAddress().toString(  );
            System.out.println("Accepted connection from " + webServerAddress);
            System.out.println("ACCEPTING YO");
            is = new BufferedReader(new InputStreamReader(s.getInputStream()));
 
            request = is.readLine();
            if(request == null) return;
            System.out.println("Server recieved request from client: " + request);

            if(!request.contains("username=")){
                printHTTP(s,"HTTP...excuse me sir but this is a Yo service. I believe you are doing it wrong.");
                s.close();
                return;
            }

            StringBuilder strBuilder = new StringBuilder();
            int i = request.indexOf("username=") + "username=".length();
            char[] reqChars = request.toCharArray();

            while(reqChars[i] != ' ' && reqChars[i] != '&'){
                strBuilder.append(reqChars[i]);
                i++;
                if(i == request.length()) break;
            }

            String username = strBuilder.toString();

            System.out.println("Yo'd from "+username);

            printHTTP(s, username);
            
            s.close();
        } catch (IOException e) {
            System.out.println("Failed to send response to client: " + e.getMessage());
        } finally {
            if(s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return;
    }


    public void printHTTP(Socket s, String name){
        try{
            PrintWriter os;     // outputStream to web browser
            os = new PrintWriter(s.getOutputStream(), true);
            os.println("HTTP/1.0 200");
            os.println("Content-type: text/html");
            os.println("Server-name: myserver");
            String response = "<html><head>" +
                "<title>Simpl Web Page</title></head>\n" +
                "<h1>Congratulations!!!</h1>\n" +
                "<h3>This page was returned by " + webServerAddress + "</h3>\n" +
                "<p>You got from from ... " + name + 
                "</html>\n";
            os.println("Content-length: " + response.length(  ));
            os.println("");
            os.println(response);
            os.flush();
            os.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}