package com.example.calculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Stack;

/**
 * Main activity for the CalculatorApp.
 * Handles input for numeric and arithmetic operations, as well as clearing inputs.
 * All code is documented in Javadoc format.
 */
public class MainActivity extends AppCompatActivity {

    private TextView inputPanel;
    private String currentExpression = ""; // This will hold the full expression as a string
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputPanel = findViewById(R.id.inputPanel);
        inputPanel.setText(getString(R.string.input_panel_default)); // Use string resource for default text

        initializeButtons();
    }

    /**
     * Initializes all buttons and assigns click listeners.
     */
    private void initializeButtons() {
        // Number buttons (0-9 and decimal)
        findViewById(R.id.button0).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button1).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button2).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button3).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button4).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button5).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button6).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button7).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button8).setOnClickListener(this::onNumberClick);
        findViewById(R.id.button9).setOnClickListener(this::onNumberClick);
        findViewById(R.id.buttonDecimal).setOnClickListener(this::onNumberClick);

        // Operator buttons (+, -, *, /, parentheses)
        findViewById(R.id.buttonAdd).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonSubtract).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonMultiply).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonDivide).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonLeftParenthesis).setOnClickListener(this::onOperatorClick);
        findViewById(R.id.buttonRightParenthesis).setOnClickListener(this::onOperatorClick);

        // Clear and equals buttons
        findViewById(R.id.buttonClear).setOnClickListener(v -> clearLastEntry());
        findViewById(R.id.buttonAllClear).setOnClickListener(v -> clearAll());
        findViewById(R.id.buttonEquals).setOnClickListener(v -> evaluateExpression());
    }

    /**
     * Handles number and decimal point button clicks.
     * Appends the clicked value to the current expression and displays it.
     *
     * @param view The button clicked.
     */
    private void onNumberClick(View view) {
        Button button = (Button) view;
        if (isNewInput) {
            currentExpression = "";
            isNewInput = false;
        }
        currentExpression += button.getText().toString();
        inputPanel.setText(currentExpression);
    }

    /**
     * Handles operator button clicks (+, -, *, /, parentheses).
     * Appends the operator to the current expression and displays it.
     *
     * @param view The operator button clicked.
     */
    private void onOperatorClick(View view) {
        Button button = (Button) view;
        currentExpression += button.getText().toString();
        inputPanel.setText(currentExpression);
        isNewInput = false;
    }

    /**
     * Evaluates the full expression entered by the user and displays the result.
     * This method manually parses the expression and handles operator precedence.
     */
    private void evaluateExpression() {
        try {
            double result = evaluate(currentExpression);
            inputPanel.setText(String.valueOf(result));
            currentExpression = String.valueOf(result); // Update the expression with the result for continued calculation
        } catch (Exception e) {
            inputPanel.setText(getString(R.string.error_message)); // Use string resource for error message
        }

        isNewInput = true;
    }

    /**
     * Parses and evaluates a mathematical expression as a string.
     * Uses a stack-based approach to handle operator precedence and parentheses.
     *
     * @param expression The mathematical expression to evaluate.
     * @return The result of the evaluated expression.
     */
    private double evaluate(String expression) {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char ch = expression.charAt(i);

            if (Character.isDigit(ch) || ch == '.') {
                // Read the full number (could be multiple digits or a decimal number)
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
            } else if (ch == '(') {
                operators.push(ch);
            } else if (ch == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop(); // Remove the '(' from the stack
            } else if (isOperator(ch)) {
                // Handle operator precedence
                while (!operators.isEmpty() && precedence(ch) <= precedence(operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(ch);
            }
        }

        // Apply remaining operations in the stack
        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        // The final result will be the only number left in the stack
        return numbers.pop();
    }

    /**
     * Applies the given operator to two numbers and returns the result.
     *
     * @param operator The operator to apply.
     * @param b The second operand.
     * @param a The first operand.
     * @return The result of the operation.
     */
    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new UnsupportedOperationException("Cannot divide by zero");
                }
                return a / b;
        }
        return 0;
    }

    /**
     * Determines the precedence of an operator.
     * Higher return value means higher precedence.
     *
     * @param operator The operator to check.
     * @return The precedence of the operator.
     */
    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
        }
        return -1;
    }

    /**
     * Checks if the given character is a valid operator.
     *
     * @param ch The character to check.
     * @return True if the character is an operator, false otherwise.
     */
    private boolean isOperator(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    /**
     * Clears the last entry from the current expression.
     */
    private void clearLastEntry() {
        if (!currentExpression.isEmpty()) {
            currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
            inputPanel.setText(currentExpression);
        }
    }

    /**
     * Clears all inputs and resets the calculator.
     */
    private void clearAll() {
        currentExpression = "";
        inputPanel.setText(getString(R.string.input_panel_default)); // Use string resource for default message
        isNewInput = true;
    }
}
