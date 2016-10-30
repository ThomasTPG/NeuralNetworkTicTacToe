package com.example.thomas.neuralnetworktictactoe;

/**
 * Created by Thomas on 21/08/2016.
 */
public class HiddenNode {

    private float weightedSum;
    private float activationValue;

    public HiddenNode()
    {

    }

    public float getWeightedSum() {
        return weightedSum;
    }

    public void setWeightedSum(float weightedSum) {
        this.weightedSum = weightedSum;
    }

    public float getActivationValue() {
        return activationValue;
    }

    public void setActivationValue(float activationValue) {
        this.activationValue = activationValue;
    }
}
