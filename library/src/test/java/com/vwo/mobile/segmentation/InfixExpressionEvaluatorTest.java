package com.vwo.mobile.segmentation;

import com.vwo.mobile.TestUtils;
import com.vwo.mobile.mock.VWOPersistDataMock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.vwo.mobile.segmentation.Operator.AND;
import static com.vwo.mobile.segmentation.Operator.CLOSE_PARENTHESES;
import static com.vwo.mobile.segmentation.Operator.OPEN_PARENTHESES;
import static com.vwo.mobile.segmentation.Operator.OR;

/**
 * Created by aman on Tue 09/01/18 13:15.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 17, shadows = {VWOPersistDataMock.class},
        manifest = "AndroidManifest.xml")
public class InfixExpressionEvaluatorTest {
    @Test
    public void infixExpressionEvaluatorTest() throws IOException, JSONException {
        for (Expression expression : getExpressionList()) {
            InfixExpressionEvaluator infixExpressionEvaluator = new InfixExpressionEvaluator(expression.getExpression());
            Assert.assertEquals(infixExpressionEvaluator.evaluate(), expression.getResult());
        }
    }

    public List<Expression> getExpressionList() throws IOException, JSONException {
        String expressionList = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/infix_expressions.json");
        JSONArray jsonArray = new JSONArray(expressionList);
        List<Expression> expressionArrays = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            List<Object> expression = parseExpressionString(jsonObject.getString("expression"));
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object : expression) {
                stringBuilder.append(object.toString()).append(" ");
            }
            System.out.println("Expression: " + stringBuilder.toString());
            expressionArrays.add(new Expression(expression, jsonObject.getBoolean("result")));
        }

        return expressionArrays;
    }

    private List<Object> parseExpressionString(String expressionString) {
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

    class Expression {
        private List<Object> expression;
        private boolean result;

        Expression(List<Object> expression, boolean result) {
            this.expression = expression;
            this.result = result;
        }

        public Expression(List<Object> expression) {
            this.expression = expression;
        }

        List<Object> getExpression() {
            return expression;
        }

        public void setExpression(List<Object> expression) {
            this.expression = expression;
        }

        boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }
    }
}
