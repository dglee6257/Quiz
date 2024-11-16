import java.io.*;
import java.net.*;

/**
 * QuizClient connects to the QuizServer, receives questions, sends answers,
 * and displays feedback and the final score.
 */
public class QuizClient {
    private static String serverIP; 
    private static int serverPort;
    public static void main(String[] args) {
        BufferedReader in = null;
        BufferedReader stin = null;
        PrintWriter out = null;

        Socket socket = null;
        
        try {
            try {
                BufferedReader br = new BufferedReader(new FileReader("server_info.dat"));
                serverIP = br.readLine();
                String portString = br.readLine();
                if (portString != null) {
                    serverPort = Integer.parseInt(portString.trim());
                } else {
                    throw new NumberFormatException("Port number is missing in file");
                }
                socket = new Socket(serverIP, serverPort);
            } catch (IOException | NumberFormatException e) {
                System.out.println("File doesn't exist or invalid port. Connect to default IP and port number");
                socket = new Socket("localhost", 8888);
            }
            
            
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stin = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintWriter(socket.getOutputStream(), true); 

            String inputMessage;
            String outputMessage;

            System.out.println("Let's start the quiz. (Total score is 50)");

            while ((inputMessage = in.readLine()) != null) {
                if (inputMessage.equalsIgnoreCase("end")) {
                    break;
                }

                // Check if the message is the final score
                if (inputMessage.startsWith("Your final score is:")) {
                    System.out.println(inputMessage);
                    // After receiving the final score, the server will send "end"
                    continue;
                }

                // Assume the message is a question
                System.out.println("Question: " + inputMessage);
                System.out.print("Your answer: ");

                // Read user's answer
                outputMessage = stin.readLine();

                // Send the answer to the server
                out.println(outputMessage);

                // Read and display the server's feedback
                String feedback = in.readLine();
                if (feedback == null) {
                    System.out.println("Server disconnected.");
                    break;
                }
                System.out.println(feedback);
                System.out.println(); // Add an empty line for better readability
            }

            System.out.println("Thank you for participating in the quiz!");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Disconnected");
			}
        }
    }
}
