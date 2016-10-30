package com.example.thomas.neuralnetworktictactoe;

/**
 * Created by Thomas on 21/08/2016.
 */
public class OutputNode {

    private float weightSum;
    private float softmaxValue;

    public OutputNode()
    {

    }

    public float getWeightSum() {
        return weightSum;
    }

    public void setWeightSum(float weightSum) {
        this.weightSum = weightSum;
    }

    public float getSoftmaxValue() {
        return softmaxValue;
    }

    public void setSoftmaxValue(float softmaxValue) {
        this.softmaxValue = softmaxValue;
    }
}
