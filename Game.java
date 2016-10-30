package com.example.thomas.neuralnetworktictactoe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Thomas on 21/08/2016.
 */
public class Game extends Activity {
    String[] board;
    private int dimension = 9;
    private String playerCounter;
    private String computerCounter;
    boolean playerTurn;
    int numberOfInputs;
    private int[][] winningMatrix = {{1,1,1,0,0,0,0,0,0},
                                     {0,0,0,1,1,1,0,0,0},
                                     {0,0,0,0,0,0,1,1,1},
                                     {1,0,0,1,0,0,1,0,0},
                                     {0,1,0,0,1,0,0,1,0},
                                     {0,0,1,0,0,1,0,0,1},
                                     {1,0,0,0,1,0,0,0,1},
                                     {0,0,1,0,1,0,1,0,0}};
    int result = 0;
    BackPropagator backPropagator;
    int COMPUTERWIN = -1;
    int PLAYERWIN = 1;
    int DRAW = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_main);


        board = new String[dimension];
        backPropagator = new BackPropagator(this);


        Intent startingInfo = getIntent();
        if(startingInfo.getStringExtra("WhoStarts").equals("Player"))
        {
            playerCounter = "O";
            computerCounter = "X";
            playerTurn = true;
        }
        else
        {
            playerCounter = "X";
            computerCounter = "O";
            playerTurn = false;
        }

        for (int ii = 0; ii < dimension; ii ++)
        {
            final int buttonId = ii;
            String buttonID = "button" + String.valueOf(ii + 1);
            int id = getResources().getIdentifier(buttonID, "id", "com.example.thomas.neuralnetworktictactoe");
            final Button b = (Button) findViewById(id);
            b.setText(" ");
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (playerTurn) {
                        if (board[buttonId] == null) {
                            b.setText(playerCounter);
                            board[buttonId] = playerCounter;
                            checkIfFinished();
                            if (result == 0) {
                                playerTurn = false;
                                computerTurn();
                            } else
                            {
                                end();

                            }

                        }
                    }

                }
            });
        }

        numberOfInputs = getResources().getInteger(R.integer.number_of_input_nodes);

        startGame();


    }

    private void startGame()
    {
        if (!playerTurn)
        {
            computerTurn();
        }
    }

    private void checkIfFinished()
    {
        int[] boardInt = new int[board.length];
        boolean finished = true;
        for (int jj = 0; jj<board.length;jj++)
        {
            if (board[jj] == computerCounter)
            {
                boardInt[jj] = -1;
            }
            else if (board[jj] == playerCounter)
            {
                boardInt[jj] = 1;
            }
            else
            {
                finished = false;
            }
        }
        for (int ii = 0; ii<winningMatrix.length; ii++)
        {
            int dotProd = dotProduct(boardInt, winningMatrix[ii]);
            if (dotProd == -3)
            {
                //Computer wins
                result = COMPUTERWIN;
                return;
            }
            if (dotProd == 3)
            {
                //Player wins
                result = PLAYERWIN;
                return;
            }
        }
        if (finished)
        {
            // This means that the grid is full, and no one wins - a draw!
            result = DRAW;
        }

    }

    private int dotProduct(int[] array1, int[] array2)
    {
        int dot = 0;
        for (int ii = 0; ii < array1.length; ii++)
        {
            dot = dot + array1[ii] * array2[ii];
        }
        return dot;
    }

    private void computerTurn()
    {
        int[] inputs = createInputArray();
        FeedForward ff = new FeedForward(this, inputs, backPropagator);
        int index = ff.getBestMove();
        board[index] = computerCounter;
        String buttonID = "button" + String.valueOf(index + 1);
        int id = getResources().getIdentifier(buttonID, "id", "com.example.thomas.neuralnetworktictactoe");
        final Button b = (Button) findViewById(id);
        b.setText(computerCounter);
        checkIfFinished();
        if (result == 0)
        {
            playerTurn = true;
        }
        else
        {
            end();
        }
    }

    private void end()
    {
        //If result = -1, computer wins. If result = 1, player wins. If result = 9, draw
        backPropagator.sendResult(result);
        backPropagator.propagate();
        Toast.makeText(getApplicationContext(), "WIN?" + result, Toast.LENGTH_LONG).show();
        Intent i = new Intent(Game.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private int[] createInputArray()
    {
        int[] inputArray = new int[numberOfInputs];
        if (numberOfInputs == 18)
        {
            for (int ii = 0; ii < dimension; ii ++)
            {
                if (board[ii] != null)
                {
                    if (board[ii].equals(playerCounter))
                    {
                        inputArray[2*ii] = 1;
                    }
                    if (board[ii].equals(computerCounter))
                    {
                        inputArray[2*ii + 1] = 1;
                    }
                }
            }
        }
        return inputArray;

    }
}
