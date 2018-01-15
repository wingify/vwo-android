package com.vwo.mobile.segmentation;

import com.vwo.mobile.TestObject;
import com.vwo.mobile.TestUtils;
import com.vwo.mobile.mock.VWOPersistDataShadow;

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

/**
 * Created by aman on Tue 09/01/18 13:15.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 17, shadows = {VWOPersistDataShadow.class},
        manifest = "AndroidManifest.xml")
public class InfixExpressionEvaluatorTest {
    @Test
    public void infixExpressionEvaluatorTest() throws IOException, JSONException {
        for (TestObject<List<Object>, Boolean> expression : getExpressionList()) {
            InfixExpressionEvaluator infixExpressionEvaluator = new InfixExpressionEvaluator(expression.getData());
            Assert.assertEquals(infixExpressionEvaluator.evaluate(), expression.getResult());
        }
    }

    public List<TestObject<List<Object>, Boolean>> getExpressionList() throws IOException, JSONException {
        String expressionList = TestUtils.readJsonFile(getClass(), "com/vwo/mobile/segmentation/infix_expressions.json");
        JSONArray jsonArray = new JSONArray(expressionList);
        List<TestObject<List<Object>, Boolean>> expressionArrays = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            List<Object> expression = TestingUtils.parseExpressionString(jsonObject.getString("expression"));
            StringBuilder stringBuilder = new StringBuilder();
            for (Object object : expression) {
                stringBuilder.append(object.toString()).append(" ");
            }
            System.out.println("Expression: " + stringBuilder.toString());
            expressionArrays.add(new TestObject<>(expression, jsonObject.getBoolean("result")));
        }

        return expressionArrays;
    }
}
