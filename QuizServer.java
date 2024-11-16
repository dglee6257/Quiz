import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * QuizServer listens for client connections and conducts a quiz by sending questions,
 * receiving answers, evaluating correctness, and maintaining the score.
 */
public class QuizServer {
    public static void main(String[] args) throws Exception {
        
        ServerSocket listener = new ServerSocket(8888);
        ExecutorService pool = Executors.newFixedThreadPool(20);
        while (true){
            Socket socket = listener.accept();
            pool.execute(new Capitalizer(socket));
        }
    }

    private static class Capitalizer implements Runnable {
        private Socket socket;
        Capitalizer(Socket socket) {
        this.socket = socket;
        }

        @Override
        public void run() {
        // Define quiz questions and their corresponding answers
        String[] questions = {
            "What is the capital of France?",
            "Who wrote the play 'Romeo and Juliet'?",
            "What is the chemical symbol for water?",
            "In which year did World War II end?",
            "What is the largest planet in our solar system?"
        };
        String[] answers = {
            "Paris",
            "William Shakespeare",
            "H2O",
            "1945",
            "Jupiter"
        };

        try {
            // Initialize PrintWriter with autoFlush set to true
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputMessage;
            int score = 0;

            for (int i = 0; i < questions.length; i++) {
                // Send the question to the client
                out.println(questions[i]);

                // Wait for the client's answer
                inputMessage = in.readLine();

                // Check if the answer is correct
                if (inputMessage.equalsIgnoreCase(answers[i])) {
                    out.println("You're correct!");
                    score += 10;
                } else {
                    out.println("Incorrect. The correct answer is: " + answers[i]);
                }
            }

            // Send the final score to the client
            out.println("Your final score is: " + score);

            // Send termination signal
            out.println("end");

        } catch (Exception e) {
            System.out.println("Error:" + socket);
            } finally {
            try { socket.close(); } catch (IOException e) {}
            System.out.println("Closed: " + socket);
            }
        }
    }
}
