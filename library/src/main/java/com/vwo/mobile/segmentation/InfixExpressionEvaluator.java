package com.vwo.mobile.segmentation;

import java.util.List;
import java.util.Stack;

/**
 * Created by aman on Tue 09/01/18 12:26.
 */

public class InfixExpressionEvaluator {
    private Stack<Operator> operatorStack;
    private Stack<Boolean> operandStack;
    private List<Object> expression;

    public InfixExpressionEvaluator(List<Object> expression) {
        this.expression = expression;
        operatorStack = new Stack<>();
        operandStack = new Stack<>();
    }

    public boolean evaluate() {
        for (int index = 0; index < expression.size(); index++) {
            Object expressionElement = expression.get(index);
            if (expressionElement instanceof Operator) {
                Operator operator = (Operator) expressionElement;
                if (operator.equals(Operator.CLOSE_PARENTHESES)) {
                    processStack();
                } else {
                    operatorStack.push(operator);
                }
            } else {
                operandStack.add((Boolean) expressionElement);
            }
        }

        while (!operatorStack.empty()) {
            processStack();
        }

        return operandStack.pop();
    }

    private void processStack() {
        if (operatorStack.isEmpty()) {
            return;
        }
        Operator operator = operatorStack.pop();
        if (operator.equals(Operator.OPEN_PARENTHESES)) {
            return;
        }

        boolean rightOperand = operandStack.pop();
        boolean leftOperand = operandStack.pop();

        boolean result = operator.evaluate(leftOperand, rightOperand);
        operandStack.push(result);

        processStack();
    }

}
