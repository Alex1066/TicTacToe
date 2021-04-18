package com.example.tictactoe;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button[][] table = new Button[3][3];
    private boolean playerStarted = true;
    private int moveCount = 0;
    private String winner = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        TextView myAwesomeTextView = findViewById(R.id.textt);
        myAwesomeTextView.setText("Good Luck");
        String buttonID;
        int resID;

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttonID = "button_" + i + j;
                resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                table[i][j] = findViewById(resID);
                table[i][j].setOnClickListener(this);
            }
        }
        resID = getResources().getIdentifier("reset", "id", getPackageName());
        Button reset = findViewById(resID);
        reset.setOnClickListener(this);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == 1) {
            this.setPortraitSize(metrics);
        }
        this.setSizes(metrics);
//        final Button iv = findViewById(R.id.button_11);
//        iv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                iv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                System.out.println("imageview width:" + iv.getWidth() + " height:" + iv.getHeight());
//            }
//        });
//        final Button iv2 = findViewById(R.id.button_10);
//        iv2.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                iv2.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                System.out.println("imageview width:" + iv2.getWidth() + " height:" + iv2.getHeight());
//            }
//        });
//        final Button iv3 = findViewById(R.id.button_12);
//        iv3.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                iv3.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                System.out.println("imageview width:" + iv3.getWidth() + " height:" + iv3.getHeight());
//            }
//        });
    }

    private void setPortraitSize(DisplayMetrics metrics) {
        // The ratio between the width and the height of the device. This is use
        // to place the dividers in such a manner that the final board will have the
        // shape of a square.
        float displayConstant = metrics.widthPixels / (float) (metrics.heightPixels);
        // Vertical biases associated with divider 5, 4 and 3;
        float verticalBias5 = displayConstant / 3;
        float verticalBias4 = displayConstant / 3 * 2;
        float verticalBias3 = displayConstant;
        ConstraintLayout cl = findViewById(R.id.constraintLayoutPortrait);
        ConstraintSet cs = new ConstraintSet();
        cs.clone(cl);
        cs.setVerticalBias(R.id.divider5, verticalBias5);
        cs.setVerticalBias(R.id.divider4, verticalBias4);
        cs.setVerticalBias(R.id.divider3, verticalBias3);
        cs.applyTo(cl);
    }

    private void setSizes(DisplayMetrics metrics) {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == 2) {
            RelativeLayout relativeLayout = findViewById(R.id.relativeLayout);
            // A button used to get the margins of the table. Used below to compute the horizontal
            // bias to dividers.
            View view = findViewById(R.id.button_00);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            // We only need on margin as the top and bottom margins are equal.
            int outerMargins = lp.topMargin;
            // Width and height of screen in dp.
            float widthDp = metrics.widthPixels / metrics.density;
            float heightDp = metrics.heightPixels / metrics.density;
            // The width in dp of the relative layout in the landscape orientation.
            float layoutWidthDp;

            if ((widthDp - heightDp) < 0.333 * widthDp) {
                layoutWidthDp = 150 < 0.333 * widthDp ? 150 : (float) (0.9 * (widthDp - heightDp));
            } else {
                layoutWidthDp = (float) (0.333 * widthDp);
            }

            relativeLayout.getLayoutParams().width = (int) (layoutWidthDp * metrics.density);
            // Value of the space on the left and right of the table. To compute this
            // we have to subtract the width of relative layout width(the red section) and
            // the width of the table. The width of the table is equal to the height of the
            // screen(because we are in landscape) minus 24dp(the navigation bar) and minus
            // the left and right margins(which are the abstract borders of the table). The
            // margins should be converted in dp from px.
            float screenForMargins = (widthDp - (heightDp - 24 - 2 * outerMargins / metrics.density) - layoutWidthDp);
            // The space on the left and right of the table is determined by 2 dividers. These
            // are constrained as follows: left to the left side of the screen, and right
            // to the left side of the relative layout. But we don`t want them to be in the middle,
            // so we create a horizontal bias equal in magnitude but of opposite directions.
            // This bias helps position the board on the middle of the remaining screen of the
            // left of the relative layout.
            // Dividing the size of margins by 2 times the remaining width we got the bias for
            // one divider.
            float horizontalBias = screenForMargins / (2 * (widthDp - layoutWidthDp));

            // Here we clone the constraint layout. Make the changes and apply them to the original.
            ConstraintLayout cl = findViewById(R.id.constraint_layout);
            ConstraintSet cs = new ConstraintSet();
            cs.clone(cl);
            cs.setHorizontalBias(R.id.divider, horizontalBias);
            cs.setHorizontalBias(R.id.divider2, (float) 1 - horizontalBias);
            cs.applyTo(cl);
        }
    }

    @Override
    public void onClick(View view) {
        boolean reset = ((Button) view).getText().toString().equals("Reset");
        if (reset) {
            clearTable();
        }
        if (winner != null) {
            return;
        }
        if (!((Button) view).getText().toString().equals("")) {
            return;
        }
        if (playerStarted) {
            // The player makes an move. After that if the game is not ended, the computer will
            // make a move.
            ((Button) view).setText("X");
        } else {
            ((Button) view).setText("0");
        }
        moveCount++;
        // The gameNotEnded check if the game has ended after the player or computer moved.
        // If the game has ended, the method will set the winner.
        if (gameNotEnded("human")) {
            computerSmartMove();
            moveCount++;
            gameNotEnded("computer");
        }
        // If the game has ended, show a message.
        if (winner != null) {
            switch (winner) {
                case "human":
                    Toast.makeText(this, "Player won", Toast.LENGTH_SHORT).show();
                    break;
                case "computer":
                    Toast.makeText(this, "Computer won", Toast.LENGTH_SHORT).show();
                    break;
                case "tie":
                    Toast.makeText(this, "It's a draw", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method called upon resetting the table.
    private void clearTable() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                table[i][j].setText("");
            }
        }
        // If the player was the first this round, the next one computer will go first.
        if (playerStarted) {
            playerStarted = false;
            computerSmartMove();
            moveCount = 1;
        } else {
            playerStarted = true;
            moveCount = 0;
        }
        winner = null;
    }

    // This is a method that allows computer to make random moves.
    // Lowest level of AI it can posses.
    private void computerMove(String text) {
        List<Point> pairs = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (table[i][j].getText().toString().equals("")) {
                    pairs.add(new Point(i, j));
                }
            }
        }
        int index = (int) (Math.random() * pairs.size());
        int i = pairs.get(index).x;
        int j = pairs.get(index).y;
        table[i][j].setText(text);
    }

    // Minimax algorithm for searching the best move.
    private int minimax(String[][] board, int depth, boolean isMaximizing) {
        String result = checkGameEnded(board);
        if (result != null) {
            switch (result) {
                case "X":
                    return 1;

                case "0":
                    return -1;

                case "tie":
                    return 0;
            }
        }
        if (isMaximizing) {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "X";
                        double score = this.minimax(board, depth + 1, false);
                        board[i][j] = "";
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return (int) bestScore;
        } else {
            double bestScore = Double.POSITIVE_INFINITY;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "0";
                        double score = this.minimax(board, depth + 1, true);
                        board[i][j] = "";
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return (int) bestScore;
        }
    }

    // This method allows the computer to make the best choice at each step.
    // Highest level of AI (cannot be defeated).
    private void computerSmartMove() {
        // We need a board so we can simulate the games. It is better than the original
        // matrix of buttons.
        String[][] board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = table[i][j].getText().toString();
            }
        }
        // A list of pairs containing possible moves and their value given by the minimax algorithm.
        // A new move will be added only if it has a better or equal score as the previous one.
        // Some moves may not be added, but all the moves with the best outcome will be added.
        Map<Point, Double> choices = new HashMap<>();
        if (playerStarted) {
            double bestScore = Double.POSITIVE_INFINITY;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "0";
                        int score = this.minimax(board, 1, true);
                        board[i][j] = "";
                        if (score <= bestScore) {
                            bestScore = score;
                            choices.put(new Point(i, j), bestScore);
                        }
                    }
                }
            }
            // Here we want to remove the moves that do not have the best outcome value.
            // All the remaining moves are equality good.
            // To give this AI human-like moves (so it doesn't follow the same pattern every game)
            // we will choose a random move from the remaining as the computer's play.
            System.out.println("my choices are: " + choices);
            Iterator<Double> iterator = choices.values().iterator();
            while (iterator.hasNext()) {
                if (iterator.next() > bestScore) {
                    iterator.remove();
                }
            }
            System.out.println("and after the change: " + choices);
            List<Point> keysAsArray = new ArrayList<>(choices.keySet());
            Random r = new Random();
            int index = r.nextInt(keysAsArray.size());
            int i = keysAsArray.get(index).x;
            int j = keysAsArray.get(index).y;
            table[i][j].setText("0");
        } else {
            double bestScore = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (board[i][j].equals("")) {
                        board[i][j] = "X";
                        int score = this.minimax(board, 1, false);
                        board[i][j] = "";
                        if (score >= bestScore) {
                            bestScore = score;
                            choices.put(new Point(i, j), bestScore);
                        }
                    }
                }
            }
            Iterator<Double> iterator = choices.values().iterator();
            while (iterator.hasNext()) {
                if (iterator.next() < bestScore) {
                    iterator.remove();
                }
            }
            List<Point> keysAsArray = new ArrayList<>(choices.keySet());
            Random r = new Random();
            int index = r.nextInt(keysAsArray.size());
            int i = keysAsArray.get(index).x;
            int j = keysAsArray.get(index).y;
            table[i][j].setText("X");
        }
    }
    // This method check the game status and returns the player that won, a tie, or null.
    private String checkGameEnded(String[][] board) {
        for (int i = 0; i < 3; i++) {
            if ((board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]))
                    && (!board[i][0].equals(""))) {
                if ((board[i][0].equals("X"))) {
                    return "X";
                }
                return "0";
            }
            if ((board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]))
                    && (!board[0][i].equals(""))) {
                if ((board[0][i].equals("X"))) {
                    return "X";
                }
                return "0";
            }
        }

        if ((board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]))
                && (!board[0][0].equals(""))) {
            if ((board[0][0].equals("X"))) {
                return "X";
            }
            return "0";
        }

        if ((board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]))
                && (!board[0][2].equals(""))) {
            if ((board[0][2].equals("X"))) {
                return "X";
            }
            return "0";
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j].equals(""))
                    return null;
            }
        }
        return "tie";
    }

    // This method handles the special case, checking the status of the real game.
    // It returns false if game ended, and true if it didn't. Also it updates the status of
    // the winner variable so it can be used in the onClick method.
    private boolean gameNotEnded(String player) {
        String[][] fields = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                fields[i][j] = table[i][j].getText().toString();

        }        }

        for (int i = 0; i < 3; i++) {
            if ((fields[i][0].equals(fields[i][1]) && fields[i][1].equals(fields[i][2]))
                    && (!fields[i][0].equals(""))) {
                winner = player;
                return false;
            }
            if ((fields[0][i].equals(fields[1][i]) && fields[1][i].equals(fields[2][i]))
                    && (!fields[0][i].equals(""))) {
                winner = player;
                return false;
            }
        }

        if ((fields[0][0].equals(fields[1][1]) && fields[1][1].equals(fields[2][2]))
                && (!fields[0][0].equals(""))) {
            winner = player;
            return false;
        }

        if ((fields[0][2].equals(fields[1][1]) && fields[1][1].equals(fields[2][0]))
                && (!fields[0][2].equals(""))) {
            winner = player;
            return false;
        }
        if (moveCount >= 9) {
            winner = "tie";
            return false;
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("moveCount", moveCount);
        outState.putBoolean("playerStarted", playerStarted);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        playerStarted = savedInstanceState.getBoolean("playerStarted");
        moveCount = savedInstanceState.getInt("moveCount");
    }

}