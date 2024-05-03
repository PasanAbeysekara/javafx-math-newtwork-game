package org.example.networkmathgame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloController {
    @FXML
    private Label scorePlayer1, scorePlayer2, questionLabel;
    @FXML
    private Button answerButton1, answerButton2, answerButton3;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    public void initialize() {
        try {
            setupNetworking("192.168.1.117", 12345); // Server IP and port
        } catch (IOException e) {
            questionLabel.setText("Failed to connect: " + e.getMessage());
        }
    }

    private void setupNetworking(String serverAddress, int serverPort) throws IOException {
        socket = new Socket(serverAddress, serverPort);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Thread receiveThread = new Thread(this::receiveMessages);
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void resetScores() {
        javafx.application.Platform.runLater(() -> {
            scorePlayer1.setText("Player 1: 0");
            scorePlayer2.setText("Player 2: 0");
        });
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                final String msg = message;
                javafx.application.Platform.runLater(() -> {
                    if (msg.startsWith("Question: ")) {
                        if (questionLabel.getText().startsWith("Game Over")) {
                            resetScores();  // Reset scores when a new game starts after "Game Over"
                        }
                        updateQuestion(msg.substring(10));
                    } else if (msg.startsWith("Player")) {
                        updateScores(msg);
                    } else if (msg.contains("Do you want to play again?")) {
                        showReplayDialog();
                    } else if (msg.contains("New game starting...")) {
                        resetScores();  // Reset scores upon receiving the new game start message
                    } else {
                        questionLabel.setText(msg);
                    }
                });
            }
        } catch (IOException e) {
            javafx.application.Platform.runLater(() -> questionLabel.setText("Error receiving message: " + e.getMessage()));
        }
    }

    private void updateQuestion(String question) {
        questionLabel.setText(question + " = ?");

        // Parsing the question
        String[] parts = question.split(" ");
        if (parts.length < 3) {
            questionLabel.setText("Invalid question data");
            return;
        }

        try {
            int number1 = Integer.parseInt(parts[0].trim());
            String operator = parts[1].trim();
            int number2 = Integer.parseInt(parts[2].trim());
            int correctAnswer = calculateAnswer(number1, operator, number2);
            List<Integer> answers = generateAnswers(correctAnswer);

            answerButton1.setText(String.valueOf(answers.get(0)));
            answerButton2.setText(String.valueOf(answers.get(1)));
            answerButton3.setText(String.valueOf(answers.get(2)));
        } catch (NumberFormatException e) {
            questionLabel.setText("Error in question format");
            System.out.println("Error parsing numbers in the question: " + e.getMessage());
        }
    }

    private int calculateAnswer(int number1, String operator, int number2) {
        switch (operator) {
            case "+": return number1 + number2;
            case "-": return number1 - number2;
            case "x": return number1 * number2;
            case "/": return number1 / number2; // Make sure to handle division by zero elsewhere
            default: throw new IllegalArgumentException("Invalid operator");
        }
    }

    private List<Integer> generateAnswers(int correctAnswer) {
        List<Integer> answers = new ArrayList<>();
        answers.add(correctAnswer);
        answers.add(correctAnswer + (int) (Math.random() * 10 + 1));  // Random number a bit higher
        answers.add(correctAnswer - (int) (Math.random() * 10 + 1)); // Random number a bit lower
        Collections.shuffle(answers);
        return answers;
    }

    private void updateScores(String scoreInfo) {
        if (scoreInfo.contains("Player 1")) {
            scorePlayer1.setText("Player 1: " + scoreInfo.split(":")[1].trim());
        } else {
            scorePlayer2.setText("Player 2: " + scoreInfo.split(":")[1].trim());
        }
    }

    @FXML
    protected void handleAnswer(ActionEvent event) {
        Button button = (Button) event.getSource();
        writer.println(button.getText());  // Send selected answer to the server
    }

    private void showReplayDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to play again?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                writer.println("yes");
            } else {
                writer.println("no");
            }
        });
    }
}
