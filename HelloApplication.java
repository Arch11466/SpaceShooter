package com.example.spacesooterproject1;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class HelloApplication extends Application {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/space_shooter_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: navy;");

        Text title = new Text("SPACE SHOOTER");
        title.setFont(Font.font(36));
        title.setFill(Color.YELLOW);

        Button playButton = new Button("PLAY");
        playButton.setOnAction(e -> showDifficultyScreen(primaryStage));

        Button leaderboardButton = new Button("LEADERBOARD");
        leaderboardButton.setOnAction(e -> showLeaderboard(primaryStage));

        root.getChildren().addAll(title, playButton, leaderboardButton);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter Menu");
        primaryStage.show();
    }

    private void showDifficultyScreen(Stage primaryStage) {
        Text prompt = new Text("Press E for Easy, M for Medium, H for Hard");
        prompt.setFont(Font.font(20));
        prompt.setFill(Color.WHITE);
        VBox root = new VBox(prompt);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root, 500, 600);
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case E -> new EasyGame().start(new Stage());
                case M -> new MediumGame().start(new Stage());
                case H -> new HardGame().start(new Stage());
            }
            primaryStage.close();
        });

        primaryStage.setScene(scene);
    }

    private void showLeaderboard(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(javafx.geometry.Pos.CENTER);
        root.setStyle("-fx-background-color: black;");
        Text title = new Text("LEADERBOARD");
        title.setFont(Font.font(30));
        title.setFill(Color.GOLD);
        root.getChildren().add(title);

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT score, recorded_at FROM leaderboard ORDER BY score DESC LIMIT 10")) {

            while (rs.next()) {
                int score = rs.getInt("score");
                Timestamp time = rs.getTimestamp("recorded_at");
                Text scoreEntry = new Text(score + " - " + time.toString());
                scoreEntry.setFill(Color.WHITE);
                root.getChildren().add(scoreEntry);
            }
        } catch (SQLException e) {
            Text error = new Text("No scores yet or DB error.");
            error.setFill(Color.GRAY);
            root.getChildren().add(error);
            e.printStackTrace();
        }

        Button backButton = new Button("BACK");
        backButton.setOnAction(e -> start(primaryStage));
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 500, 600);
        primaryStage.setScene(scene);
    }

    public static void saveScore(int score) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement ps = conn.prepareStatement("INSERT INTO leaderboard (score) VALUES (?)")) {
            ps.setInt(1, score);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
