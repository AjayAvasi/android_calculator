package com.ajay.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onEquals(View v)
    {
        TextView answer = findViewById(R.id.answer);
        String equation = answer.getText().toString();
        ArrayList<String> strNumbers = new ArrayList<>();
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<Double> xPower = new ArrayList<>();
        ArrayList<Character> operations = new ArrayList<>();
        for(int i = 0; i < equation.length(); i++)
        {
            char currChar = equation.charAt(i);
            if (currChar == '-' && i != 0 && equation.charAt(i - 1) != '^') {
                strNumbers.add(equation.substring(0,i));
                operations.add('+');
                equation = "-" + equation.substring(i + 1);
                i = 0;
            }
            else if (currChar == '+' || currChar == '*' || currChar == '/' || (currChar == '^' && equation.charAt(i - 1) != 'x'))
            {
                strNumbers.add(equation.substring(0,i));
                operations.add(currChar);
                equation = equation.substring(i + 1);
                i = 0;
            }
        }
        strNumbers.add(equation);

        for (String strNumber : strNumbers) {
            if (strNumber.isEmpty())
            {
                throwError();
                return;
            }
            if (!strNumber.contains("x")) {
                xPower.add(0.0);
            } else {
                if (strNumber.contains("^")) {
                    String power = strNumber.substring(strNumber.indexOf("^") + 1);

                    xPower.add(Double.parseDouble(power));
                } else {
                    xPower.add(1.0);
                }
                strNumber = strNumber.substring(0, strNumber.indexOf("x"));

            }
            if (strNumber.isEmpty())
                strNumber = "1";
            if (strNumber.equals("-"))
                strNumber = "-1";
            if (isNotNumeric(strNumber)) {
                throwError();
                return;
            }

            numbers.add(Double.parseDouble(strNumber));

        }
        for (int i = 0; i < operations.size(); i++) {
            char operator = operations.get(i);
            double num1 = numbers.get(i);
            double num2 = numbers.get(i + 1);
            if (operator == '^') {
                numbers.set(i, Math.pow(num1, num2));
                numbers.remove(i + 1);
                operations.remove(i);
                i--;
            }
        }
        for (int i = 0; i < operations.size(); i++)
        {
            char operator = operations.get(i);
            double num1 = numbers.get(i);
            double num2 = numbers.get(i + 1);
            if(operator == '*')
            {
                numbers.set(i, num1 * num2);
                numbers.remove(i + 1);
                xPower.set(i, xPower.get(i) + xPower.get(i+1));
                xPower.remove(i + 1);
                operations.remove(i);
                i--;
            }
            if(operator == '/')
            {
                if (num2 == 0)
                {
                    throwError();
                    return;
                }
                numbers.set(i, num1 / num2);
                numbers.remove(i + 1);
                xPower.set(i, xPower.get(i) - xPower.get(i+1));
                xPower.remove(i + 1);
                operations.remove(i);
                i--;
            }
        }
        //Selection Sort
        for (int i = 0; i < xPower.size() - 1; i++)
        {
            int maxIndex = i;
            for (int j = i + 1; j < xPower.size(); j++)
            {
                if (xPower.get(j) > xPower.get(maxIndex))
                    maxIndex = j;
            }
            double num = numbers.get(maxIndex);
            double tempPower = xPower.get(maxIndex);
            xPower.set(maxIndex, xPower.get(i));
            xPower.set(i, tempPower);
            numbers.set(maxIndex, numbers.get(i));
            numbers.set(i, num);

        }

        for (int i = 0; i < operations.size(); i++)
        {

            double num1 = numbers.get(i);
            double num2 = numbers.get(i+1);

            if (xPower.get(i) == (double)xPower.get(i + 1)) {
                numbers.set(i, num1 + num2);
                numbers.remove(i + 1);
                xPower.remove(i+1);
                operations.remove(i);
                i--;
            }

        }

        String simplifiedEquation = "";
        for (int i = 0; i < numbers.size(); i++)
        {
            if (i != 0)
            {
                if (numbers.get(i) > 0)
                    simplifiedEquation += "+";
            }
            if (numbers.get(i) == -1 && xPower.get(i) != 0)
                simplifiedEquation += "-";
            else if (xPower.get(i) == 0 ||(xPower.get(i) != 0 && numbers.get(i) != 1)) {
                simplifiedEquation += removeExtraneousDecimal(Double.toString(numbers.get(i)));
            }

            if (xPower.get(i) != 0)
                simplifiedEquation += "x";

            if (xPower.get(i) != 1 && xPower.get(i) != 0)
            {
                simplifiedEquation += "^"+removeExtraneousDecimal(Double.toString(xPower.get(i)));
            }
        }
        answer.setText(simplifiedEquation);
    }
    public void throwError()
    {
        TextView answer = findViewById(R.id.answer);
        answer.setText("Error");
    }
    public static boolean isNotNumeric(String str)
    {
        String valid = "0123456789.";
        if (str.startsWith("-") && str.length() > 1)
            str = str.substring(1);
        for (int i = 0; i < str.length(); i++)
        {
            if (valid.indexOf(str.charAt(i)) == -1)
                return true;
        }
        return false;
    }
    public static String removeExtraneousDecimal(String strNum)
    {
        if (strNum.endsWith(".0"))
            return strNum.substring(0, strNum.indexOf("."));
        return strNum;
    }
    public void addCharacter(View v)
    {
        TextView answer = findViewById(R.id.answer);
        Button character = findViewById(v.getId());
        if (answer.getText().toString().equals("Error"))
            answer.setText("");
        if (v.getId() != R.id.variable )
        {

            answer.setText(answer.getText().toString() + character.getText());
        }else
        {
            answer.setText(answer.getText().toString() + "x");
        }
    }
    public void onDelete(View v)
    {
        TextView answer = findViewById(R.id.answer);
        String text = answer.getText().toString();
        int len = text.length();
        if (len == 0)
            throwError();
        else if (text.equals("Error")) {
            answer.setText("");
        } else
            answer.setText(text.substring(0, len-1));
    }
    public void onClear(View v)
    {
        TextView answer = findViewById(R.id.answer);
        answer.setText("");
    }
}