package com.vwo.mobile.segmentation;

import java.util.ArrayList;
import java.util.List;

import static com.vwo.mobile.segmentation.Operator.AND;
import static com.vwo.mobile.segmentation.Operator.CLOSE_PARENTHESES;
import static com.vwo.mobile.segmentation.Operator.OPEN_PARENTHESES;
import static com.vwo.mobile.segmentation.Operator.OR;

/**
 * Created by aman on Wed 10/01/18 12:25.
 */

public class TestingUtils {

    public static List<Object> parseExpressionString(String expressionString) {
        List<Object> expressionList = new ArrayList<>();
        for (char c : expressionString.toCharArray()) {
            switch (c) {
                case '0':
                    expressionList.add(false);
                    break;
                case '1':
                    expressionList.add(true);
                    break;
                case '(':
                    expressionList.add(OPEN_PARENTHESES);
                    break;
                case ')':
                    expressionList.add(CLOSE_PARENTHESES);
                    break;
                case '&':
                    expressionList.add(AND);
                    break;
                case '|':
                    expressionList.add(OR);
                    break;
                case ' ':
                    continue;
                default:
                    throw new IllegalArgumentException(String.format("Illegal char %s in expression", c));
            }
        }

        return expressionList;
    }
}
