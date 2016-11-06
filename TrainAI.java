package com.example.thomas.neuralnetworktictactoe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Thomas on 21/08/2016.
 */
public class TrainAI extends Activity {
    String[] board;
    private int dimension = 9;
    private String playerCounter = "X";
    private String computerCounter = "O";
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
    BackPropagator backPropagator;
    int COMPUTERWIN = -1;
    int PLAYERWIN = 1;
    int DRAW = 9;
    int NOTFINISHED = 0;
    boolean running = false;
    TextView updates;
    int winCount = 0;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.train_main);
        updates = (TextView) findViewById(R.id.textviewtrain);
        mContext = this;



        numberOfInputs = getResources().getInteger(R.integer.number_of_input_nodes);

        Button b10 = (Button) findViewById(R.id.train10);
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running)
                {
                    trainNTimes(10);
                }
            }
        });

        Button b100 = (Button) findViewById(R.id.train100);
        b100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running)
                {
                    trainNTimes(100);
                }
            }
        });

        Button b1000 = (Button) findViewById(R.id.train1000);
        b1000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!running) {
                    trainNTimes(1000);
                }
            }
        });




    }

    private void trainNTimes(int n)
    {
        final int number = n;
        Thread thread = new Thread() {
            @Override
            public void run() {
                winCount = 0;
                for (int ii = 1; ii <= number; ii++)
                {
                    final int iteration = ii;
                    running = true;
                    board = new String[dimension];
                    backPropagator = new BackPropagator(mContext);
                    double random = Math.random();
                    if (random < 0.5)
                    {
                        computerTurn();
                    }
                    else
                    {
                        goodTurn();
                    }
                    TrainAI.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updates.setText("Played " + iteration  + " games. Computer beat random " + winCount + " times");
                        }
                    });

                }
                running = false;
            }
        };

        thread.start();

    }

    private int checkIfFinished()
    {
        int[] boardInt = new int[board.length];
        boolean finished = true;
        for (int jj = 0; jj<board.length;jj++)
        {
            if (board[jj] == null)
            {
                boardInt[jj] = 0;
                finished = false;
            }
            else
            {
                if (board[jj].equals(computerCounter))
                {
                    boardInt[jj] = -1;
                }
                else if (board[jj].equals(playerCounter))
                {
                    boardInt[jj] = 1;
                }
            }
        }
        for (int ii = 0; ii<winningMatrix.length; ii++)
        {
            int dotProd = dotProduct(boardInt, winningMatrix[ii]);
            if (dotProd == -3)
            {
                //Computer wins
                return COMPUTERWIN;
            }
            if (dotProd == 3)
            {
                //Player wins
                return PLAYERWIN;
            }
        }
        if (finished)
        {
            // This means that the grid is full, and no one wins - a draw!
            return DRAW;
        }
        return NOTFINISHED;

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
        if (checkIfFinished() == NOTFINISHED)
        {
            goodTurn();
        }
        else
        {
            end();
        }
    }

    private void randomTurn()
    {
        int[] emptyspaces = new int[board.length];
        int numberOfEmpty = 0;

        for (int ii =0; ii < board.length; ii++)
        {
            if (board[ii] == null)
            {
                emptyspaces[numberOfEmpty] = ii;
                numberOfEmpty++;
            }
        }

        int chosenSpace = (int) Math.floor(Math.random() * numberOfEmpty);
        board[chosenSpace] = playerCounter;
        if (checkIfFinished() == NOTFINISHED)
        {
            computerTurn();
        }
        else
        {
            end();
        }
    }

    public void goodTurn()
    {
        int[] emptyspaces = new int[board.length];
        int numberOfEmpty = 0;
        for (int ii =0; ii < board.length; ii++)
        {
            if (board[ii] == null)
            {
                emptyspaces[numberOfEmpty] = ii;
                numberOfEmpty++;
            }
        }

        int spaceToWin = -1;
        int spaceToBlock = -1;
        for (int jj = 0; jj < numberOfEmpty; jj++)
        {
            board[emptyspaces[jj]] = playerCounter;
            if (checkIfFinished() == PLAYERWIN)
            {
                spaceToWin = emptyspaces[jj];
            }
            board[emptyspaces[jj]] = computerCounter;
            if (checkIfFinished()  == COMPUTERWIN)
            {
                spaceToBlock = emptyspaces[jj];
            }
            board[emptyspaces[jj]] = null;
        }
        if (spaceToWin > -1)
        {
            board[spaceToWin] = playerCounter;
        }
        else if (spaceToBlock > -1)
        {
            board[spaceToBlock] = playerCounter;
        }
        else
        {
            if (board[4] == null)
            {
                board[4] = playerCounter;
            }
            else if (board[0] == null)
            {
                board[0] = playerCounter;
            }
            else if (board[2] == null)
            {
                board[2] = playerCounter;
            }
            else if (board[6] == null)
            {
                board[6] = playerCounter;
            }
            else if (board[8] == null)
            {
                board[8] = playerCounter;
            }
            else
            {
                board[emptyspaces[0]] = playerCounter;
            }
        }
        if (checkIfFinished() == NOTFINISHED)
        {
            computerTurn();
        }
        else
        {
            end();
        }
    }



    private void end()
    {

        //If result = -1, computer wins. If result = 1, player wins. If result = 9, draw
        if (checkIfFinished() == COMPUTERWIN)
        {
            winCount ++;
        }
        backPropagator.sendResult(checkIfFinished());
        backPropagator.propagate();
        backPropagator = null;
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
