package com.example.thomas.neuralnetworktictactoe;

import android.content.Context;

/**
 * Created by Thomas on 21/08/2016.
 */
public class FeedForward {

    private int numberOfInputNodes;
    private int numberOfHiddenNodoes;
    private int numberOfOutputNodes;
    private float[][] weightIn;
    private float[][] weightOut;
    private FileTools mFileTools;
    private int[] mInput;
    private HiddenNode[] mHiddenNodes;
    private OutputNode[] mOutputNodes;
    BackPropagator backPropagator;

    public FeedForward(Context c, int[] input, BackPropagator backPropagator)
    {
        numberOfHiddenNodoes = c.getResources().getInteger(R.integer.number_of_hidden_nodes);
        numberOfInputNodes = c.getResources().getInteger(R.integer.number_of_input_nodes);
        numberOfOutputNodes = c.getResources().getInteger(R.integer.number_of_output_nodes);
        this.backPropagator = backPropagator;

        weightIn = new float[numberOfInputNodes][numberOfHiddenNodoes];
        weightOut = new float[numberOfHiddenNodoes][numberOfOutputNodes];

        mFileTools = new FileTools(c);

        weightIn = mFileTools.readInputWeights();
        weightOut = mFileTools.readOutputWeights();

        mInput = input;

        mHiddenNodes = new HiddenNode[numberOfHiddenNodoes];

        mOutputNodes = new OutputNode[numberOfOutputNodes];

        calculateHiddenNodesWeightSum();
        calculateOutputNodeWeightSum();
        calculateSoftmaxValues();
    }

    private void calculateHiddenNodesWeightSum()
    {
        for (int hiddenNode = 0; hiddenNode < numberOfHiddenNodoes; hiddenNode ++)
        {
            calculateWeightedSumHidden(hiddenNode);
        }
    }

    private void calculateWeightedSumHidden(int hiddenNode)
    {
        float weightSum = 0;
        for (int ii = 0; ii < numberOfInputNodes; ii ++)
        {
            weightSum = weightSum + (mInput[ii] * weightIn[ii][hiddenNode]);
        }
        HiddenNode newHiddenNode = new HiddenNode();
        newHiddenNode.setWeightedSum(weightSum);
        newHiddenNode.setActivationValue((float) Math.tanh(weightSum));
        mHiddenNodes[hiddenNode] = newHiddenNode;
    }

    private void calculateOutputNodeWeightSum()
    {
        for (int outputNode = 0; outputNode < numberOfOutputNodes; outputNode ++)
        {
            calculateWeightedSumOutput(outputNode);
        }
    }

    private void calculateWeightedSumOutput(int outputNode)
    {
        float weightSum = 0;
        for (int ii = 0; ii < numberOfHiddenNodoes; ii++)
        {
            weightSum = weightSum + (mHiddenNodes[ii].getActivationValue() * weightOut[ii][outputNode]);
        }
        OutputNode newOutputNode = new OutputNode();
        newOutputNode.setWeightSum(weightSum);
        mOutputNodes[outputNode] = newOutputNode;
    }

    private void calculateSoftmaxValues()
    {
        float denominator = 0;
        for (int ii = 0; ii < numberOfOutputNodes; ii++)
        {
            denominator = denominator + (float) (Math.exp(mOutputNodes[ii].getWeightSum()));
        }
        for (int ii = 0; ii < numberOfOutputNodes; ii++)
        {
            float softmax = (float ) (Math.exp(mOutputNodes[ii].getWeightSum()) / denominator);
            mOutputNodes[ii].setSoftmaxValue(softmax);
        }
    }

    public int getBestMove()
    {
        int index = 0;
        float max = 0;
        for (int ii = 0; ii < numberOfOutputNodes; ii++)
        {
            if (mOutputNodes[ii].getSoftmaxValue() > max)
            {
                //Ensure that the square that the network thinks is the best move is empty
                if ((mInput[2*ii] == 0) && (mInput[2*ii + 1] == 0))
                {
                    max = mOutputNodes[ii].getSoftmaxValue();
                    index = ii;
                }
            }
        }
        backPropagator.addMove(mInput, mHiddenNodes,mOutputNodes, index);
        return index;
    }

    public OutputNode[] getOutputNodes()
    {
        return mOutputNodes;
    }



}
