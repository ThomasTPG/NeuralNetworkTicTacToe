package com.example.thomas.neuralnetworktictactoe;

import android.content.Context;

/**
 * Created by Thomas on 30/10/2016.
 */
public class BackPropagator {

    int[][] mInputs;
    HiddenNode[][] mHiddenNodes;
    OutputNode[][] mOutputs;
    int[] outputIndices;
    int moveNumber = 0;
    int COMPUTERWIN = -1;
    int PLAYERWIN = 1;
    int DRAW = 9;
    int resultOfGame;
    int numberOfInputNodes;
    int numberOfHiddenNodes;
    int numberOfOutputNodes;
    FileTools mFileTools;
    double infinitesimalChange = 0.001;

    public BackPropagator(Context c)
    {

        mFileTools = new FileTools(c);
        numberOfInputNodes = c.getResources().getInteger(R.integer.number_of_input_nodes);
        numberOfHiddenNodes = c.getResources().getInteger(R.integer.number_of_hidden_nodes);

        //Max number of moves = number of output nodes. This is why there are this many rows in the following arrays
        numberOfOutputNodes = c.getResources().getInteger(R.integer.number_of_output_nodes);

        mInputs = new int[numberOfInputNodes][numberOfOutputNodes];
        mHiddenNodes = new HiddenNode[numberOfHiddenNodes][numberOfOutputNodes];
        mOutputs = new OutputNode[numberOfOutputNodes][numberOfOutputNodes];
        outputIndices = new int[numberOfOutputNodes];


    }

    public void addMove(int[] input, HiddenNode[] hiddenNodes, OutputNode[] output, int outputIndex)
    {
        mInputs[moveNumber] = input;
        mHiddenNodes[moveNumber] = hiddenNodes;
        mOutputs[moveNumber] = output;
        outputIndices[moveNumber] = outputIndex;
        moveNumber++;
    }

    public void sendResult(int result)
    {
        resultOfGame = result;
    }

    public void propagate()
    {
        for (int ii = 0; ii < moveNumber; ii++)
        {
            float selectedSoftmax = mOutputs[ii][outputIndices[ii]].getSoftmaxValue();

            //Rate of change of softmax with respect to output node value is (DELTA(j,out) * P(out) - P(j)*P(out)) where out is the index of the chosen softmax value

            //Rate of change of output node value wrt to hidden weights(i->j) is the hidden node i

            //Now calculate the rate of change of softmax wrt weight (hidden i -> output j)

            //This matrix tells us how much to change hidden node weights by
            float derivitiveMatrixHN[][] = new float[numberOfHiddenNodes][numberOfOutputNodes];
            for (int jj = 0; jj < numberOfHiddenNodes; jj++)
            {
                for (int kk = 0; kk< numberOfOutputNodes; kk++)
                {
                    float dSMdOutputkk;
                    if (outputIndices[ii] == kk)
                    {
                        //If the sotmax value that was chosen was from the kkth output node
                        dSMdOutputkk = selectedSoftmax - selectedSoftmax*selectedSoftmax;
                    }
                    else
                    {
                        dSMdOutputkk = (-1) * selectedSoftmax * mOutputs[ii][kk].getSoftmaxValue();
                    }
                    derivitiveMatrixHN[jj][kk] = mHiddenNodes[ii][jj].getActivationValue() * dSMdOutputkk;
                }
            }

            //Now do the same for the input weights
            // Rate of change of zth output node wrt nth hidden node = weightHN(n->z)
            // Rate of change of nth hidden node wrt weighted sum = 1-tanh(weightedsum)^2
            //Rate of change of weighted sum(j) wrt inputweight (i->j) = input(i)

            //This matrix tells us how much the input weights change by
            float derivitiveMatrixIN[][] = new float[numberOfInputNodes][numberOfHiddenNodes];
            for (int jj = 0; jj < numberOfInputNodes; jj++)
            {
                for (int kk = 0; kk< numberOfHiddenNodes; kk++)
                {
                    float dSMdOutputkk;

                    float[][] weightOut = mFileTools.readOutputWeights();

                    float sum = 0;

                    for (int rr = 0; rr < numberOfOutputNodes; rr ++)
                    {
                        for (int ss = 0; ss < numberOfHiddenNodes; ss++)
                        {
                            if (outputIndices[ii] == rr)
                            {
                                //If the sotmax value that was chosen was from the kkth output node
                                dSMdOutputkk = selectedSoftmax - selectedSoftmax*selectedSoftmax;
                            }
                            else
                            {
                                dSMdOutputkk = (-1) * selectedSoftmax * mOutputs[ii][rr].getSoftmaxValue();
                            }
                            sum = sum + (float) (dSMdOutputkk * weightOut[ss][rr] * (1 - Math.pow(mHiddenNodes[ii][ss].getActivationValue(),2)));
                        }
                    }
                    derivitiveMatrixIN[jj][kk] = sum * mInputs[ii][jj];
                }
            }

            //Now change the weights by the required value
            //Change the input weights
            float[][] weightIn = mFileTools.readInputWeights();
            for (int dd = 0; dd < numberOfInputNodes; dd ++)
            {
                for (int ee = 0; ee < numberOfHiddenNodes; ee++)
                {
                    weightIn[dd][ee] = weightIn[dd][ee] - (float) (infinitesimalChange * derivitiveMatrixIN[dd][ee]);
                }
            }

            //Change the hidden weights
            float[][] weightOut = mFileTools.readOutputWeights();
            for (int dd = 0; dd < numberOfHiddenNodes; dd ++)
            {
                for (int ee = 0; ee < numberOfOutputNodes; ee++)
                {
                    weightOut[dd][ee] = weightOut[dd][ee] - (float) (infinitesimalChange * derivitiveMatrixHN[dd][ee]);
                }
            }





        }
    }

}
