package com.example.thomas.neuralnetworktictactoe;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Thomas on 21/08/2016.
 */
public class FileTools {

    private String inputweightfilename = "Input_Weights.txt";
    private String inputweightfilePath;
    private File inputWeightFile;

    private String outputweightfilename = "Output_Weights.txt";
    private String outputweightfilePath;
    private File outputWeightFile;

    private int numberOfInputNodes;
    private int numberOfHiddenNodoes;
    private int numberOfOutputNodes;

    private Context mContext;

    private String deliminator = "#";

    public FileTools(Context c)
    {
        mContext = c;
        inputweightfilePath = mContext.getFilesDir() + "/" + inputweightfilename;
        inputWeightFile = new File(inputweightfilePath);

        outputweightfilePath = mContext.getFilesDir() + "/" + outputweightfilename;
        outputWeightFile = new File(outputweightfilePath);

        numberOfHiddenNodoes = c.getResources().getInteger(R.integer.number_of_hidden_nodes);
        numberOfInputNodes = c.getResources().getInteger(R.integer.number_of_input_nodes);
        numberOfOutputNodes = c.getResources().getInteger(R.integer.number_of_output_nodes);

    }

    public void createInputWeightFile()
    {
        createWeightFile(inputWeightFile, numberOfInputNodes, numberOfHiddenNodoes);
    }

    public void createOutputWeightFile()
    {
        createWeightFile(outputWeightFile, numberOfHiddenNodoes, numberOfOutputNodes);
    }

    private void updateWeightFile(float[][] matrix, int cols, int rows, File file)
    {
        file.delete();
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fileWriter);
            for (int ii = 0; ii < rows; ii ++)
            {
                String randomLine = "";
                for (int jj = 0; jj < cols; jj ++)
                {
                    randomLine = randomLine + String.valueOf(matrix[jj][ii]) + deliminator;
                }
                out.write(randomLine);
                out.newLine();
            }
            out.close();
            fileWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeWeightFile(float[][] matrix, boolean hidden)
    {
        if (hidden)
        {
            updateWeightFile(matrix, numberOfHiddenNodoes, numberOfOutputNodes, outputWeightFile);
        }
        else
        {
            updateWeightFile(matrix, numberOfInputNodes, numberOfHiddenNodoes, inputWeightFile);
        }
    }


    private void createWeightFile(File file, int rows, int cols)
    {
        if (!file.exists())
        {
            try {
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(fileWriter);
                for (int ii = 0; ii < cols; ii ++)
                {
                    String randomLine = "";
                    for (int jj = 0; jj < rows; jj ++)
                    {
                        float random = (float) (Math.random() * 0.1);
                        randomLine = randomLine + String.valueOf(random) + deliminator;
                    }
                    out.write(randomLine);
                    out.newLine();
                }
                out.close();
                fileWriter.close();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public float[][] readInputWeights()
    {
        return readArrayFromFile(numberOfInputNodes,numberOfHiddenNodoes,inputWeightFile);
    }


    private float[][] readArrayFromFile(int cols, int rows, File file)
    {
        float[][] weights = new float[cols][rows];

        try
        {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            for (int jj = 0; jj < rows; jj ++)
            {
                String line = fileReader.readLine();

                String[] splitLine = line.split(deliminator);
                for (int ii = 0; ii < cols; ii++)
                {
                    weights[ii][jj] = Float.valueOf(splitLine[ii]);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return weights;
    }

    public float[][] readOutputWeights()
    {
        return readArrayFromFile(numberOfHiddenNodoes,numberOfOutputNodes,outputWeightFile);
    }
}
