package com.example.mycalculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Stack;
import android.text.method.ScrollingMovementMethod;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private TextView detailTextView;
    private StringBuilder inputStringBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        resultTextView.setMovementMethod(new ScrollingMovementMethod());
        detailTextView = findViewById(R.id.detailTextView);
        inputStringBuilder = new StringBuilder();

        // 設置所有按鈕的點擊監聽器
        for (int i = 0; i <= 9; i++) {
            int buttonId = getResources().getIdentifier("btn_" + i, "id", getPackageName());
            Button numberButton = findViewById(buttonId);
            numberButton.setOnClickListener(new CalculatorClickListener());
        }

        // 設置運算符按鈕的點擊監聽器
        findViewById(R.id.btn_add).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_subtract).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_multiply).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_divide).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_equals).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_clear).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_delete).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_Lparentheses).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_Rparentheses).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.percent_button).setOnClickListener(new CalculatorClickListener());
        findViewById(R.id.btn_point).setOnClickListener(new CalculatorClickListener());

    }

    private class CalculatorClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            String text = detailTextView.getText().toString();
            char lastChar = text.charAt(text.length() - 1);

            if (id >= R.id.btn_0 && id <= R.id.btn_9) {
                // 數字按鈕被點擊
                String number = ((Button) v).getText().toString();
                inputStringBuilder.append(number);
            } else if (id == R.id.btn_Lparentheses) {
                inputStringBuilder.append("(");
            } else if (id == R.id.btn_Rparentheses) {
                inputStringBuilder.append(")");
            } else if (id == R.id.percent_button) {
                inputStringBuilder.append("%");
            } else if (id == R.id.btn_point) {
                inputStringBuilder.append(".");
            } else if (id == R.id.btn_equals) {
                calculateResult(text);
                return;
            } else if (id == R.id.btn_clear) {
                inputStringBuilder.setLength(0);
                detailTextView.setText("0");
                resultTextView.setText("0");
                return;
            } else if (id == R.id.btn_delete) {
                // 删除最后一个字符
                if (inputStringBuilder.length() > 1) {
                    if (lastChar == ' '){
                        inputStringBuilder.setLength(inputStringBuilder.length() - 2);
                    }
                    inputStringBuilder.deleteCharAt(inputStringBuilder.length() - 1);
                }else{
                    inputStringBuilder.setLength(0);
                    detailTextView.setText("0");
                    return;
                }
            }else {
                if (lastChar == ' '){
                    inputStringBuilder.setLength(inputStringBuilder.length() - 3);
                }
                if (id == R.id.btn_add) {
                    inputStringBuilder.append(" + ");
                } else if (id == R.id.btn_subtract) {
                    inputStringBuilder.append(" - ");
                } else if (id == R.id.btn_multiply) {
                    inputStringBuilder.append(" * ");
                } else if (id == R.id.btn_divide) {
                    inputStringBuilder.append(" / ");
                }
            }

            detailTextView.setText(inputStringBuilder.toString());
        }
    }
    private void calculateResult(String s) {
        try {
            isValid(s);
            calculate(s);
        } catch (Exception e) {
            resultTextView.setText("Error: " + e.getMessage());
            System.out.println(e.getMessage()); // 使用 System.out.println 來輸出異常信息
        }
    }

    public void isValid(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (c == '(') {
                stack.push(')');
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != c) {
                    throw new IllegalArgumentException("Missing \"(\" symbol");
                }
            }
        }
        if (!stack.isEmpty()) {
            throw new IllegalArgumentException("Missing \")\" symbol");
        }
    }


    // 處理操作的方法
    public double solve(double operand, char sign, Stack<Double> stack) {
        switch (sign) {
            case '+':
                return operand;
            case '-':
                return -operand;
            case '*':
                return stack.pop() * operand;
            case '/':
                // 確保使用整數除法
                return stack.pop() / operand;
            case '%':
                return stack.pop() / 100;
            case '.':
                if(operand == 0){
                    return stack.pop();
                }
                return stack.pop() + operand / Math.pow(10,(String.valueOf((int)operand).length()));
            default:
                throw new IllegalArgumentException("Invalid sign: " + sign);
        }
    }

    public void calculate(String s) {
        Stack<Double> stack = new Stack<>();
        double operand = 0;
        char sign = '+';

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == ' ') {
                continue;
            } else if (Character.isDigit(c)) {
                operand = operand * 10 + (c - '0');
            } else if (c == '(') {
                stack.push((double)sign);
                stack.push(Double.MIN_VALUE);  // 用Integer.MIN_VALUE作為'('的標記
                sign = '+';
                operand = 0;
            } else if (c == ')') {
                double result = solve(operand, sign, stack);
                operand = 0;

                while (stack.peek() != Double.MIN_VALUE) {
                    result += stack.pop();
                }

                stack.pop();  // 移除'('的標記
                sign = (char)stack.pop().intValue();  // 取出'('之前的符號

                operand = solve(result, sign, stack);
                sign = '+';
            } else {
                double result = solve(operand, sign, stack);
                operand = 0;
                stack.push(result);
                sign = c;
            }
        }

        double last = solve(operand, sign, stack);
        stack.push(last);

        double sum = 0;
        while (!stack.isEmpty()) {
            sum += stack.pop();
        }
        resultTextView.setText(String.valueOf(sum));
    }

}