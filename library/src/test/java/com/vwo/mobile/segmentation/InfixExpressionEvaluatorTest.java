package com.vwo.mobile.segmentation;

import com.vwo.mobile.mock.VWOPersistDataMock;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import edu.emory.mathcs.backport.java.util.Arrays;

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
    public void infixExpressionEvaluatorTest() {

        // true = true
        Object[] expression1 = {true};

        InfixExpressionEvaluator infixExpressionEvaluator1 = new InfixExpressionEvaluator(Arrays.asList(expression1));
        Assert.assertTrue(infixExpressionEvaluator1.evaluate());


        // false = false
        Object[] expression2 = {false};

        InfixExpressionEvaluator infixExpressionEvaluator2 = new InfixExpressionEvaluator(Arrays.asList(expression2));
        Assert.assertFalse(infixExpressionEvaluator2.evaluate());


        // (true) = true
        Object[] expression3 = {OPEN_PARENTHESES, true, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator3 = new InfixExpressionEvaluator(Arrays.asList(expression3));
        Assert.assertTrue(infixExpressionEvaluator3.evaluate());


        // (false) = false
        Object[] expression4 = {OPEN_PARENTHESES, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator4 = new InfixExpressionEvaluator(Arrays.asList(expression4));
        Assert.assertFalse(infixExpressionEvaluator4.evaluate());


        // true || false = true
        Object[] expression5 = {true, OR, false};

        InfixExpressionEvaluator infixExpressionEvaluator5 = new InfixExpressionEvaluator(Arrays.asList(expression5));
        Assert.assertTrue(infixExpressionEvaluator5.evaluate());


        // true && true = true
        Object[] expression6 = {true, AND, true};

        InfixExpressionEvaluator infixExpressionEvaluator6 = new InfixExpressionEvaluator(Arrays.asList(expression6));
        Assert.assertTrue(infixExpressionEvaluator6.evaluate());


        // true && false = false
        Object[] expression7 = {true, AND, false};

        InfixExpressionEvaluator infixExpressionEvaluator7 = new InfixExpressionEvaluator(Arrays.asList(expression7));
        Assert.assertFalse(infixExpressionEvaluator7.evaluate());


        // (true && false) = false
        Object[] expression8 = {OPEN_PARENTHESES, true, AND, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator8 = new InfixExpressionEvaluator(Arrays.asList(expression8));
        Assert.assertFalse(infixExpressionEvaluator8.evaluate());


        // (true) && (false)
        Object[] expression9 = {OPEN_PARENTHESES, true, CLOSE_PARENTHESES, AND, OPEN_PARENTHESES, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator9 = new InfixExpressionEvaluator(Arrays.asList(expression9));
        Assert.assertFalse(infixExpressionEvaluator9.evaluate());


        // (true && false || true) = true
        Object[] expression10 = {OPEN_PARENTHESES, true, AND, false, OR, true, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator10 = new InfixExpressionEvaluator(Arrays.asList(expression10));
        Assert.assertTrue(infixExpressionEvaluator10.evaluate());


        // (true && true || false) = true
        Object[] expression11 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator11 = new InfixExpressionEvaluator(Arrays.asList(expression11));
        Assert.assertTrue(infixExpressionEvaluator11.evaluate());


        // (true && true || false) && true = true
        Object[] expression12 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES, AND, true};

        InfixExpressionEvaluator infixExpressionEvaluator12 = new InfixExpressionEvaluator(Arrays.asList(expression12));
        Assert.assertTrue(infixExpressionEvaluator12.evaluate());


        // (true && true || false) && false = false
        Object[] expression13 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES, AND, false};

        InfixExpressionEvaluator infixExpressionEvaluator13 = new InfixExpressionEvaluator(Arrays.asList(expression13));
        Assert.assertFalse(infixExpressionEvaluator13.evaluate());


        // (true && true || false) && (true || false) = true
        Object[] expression14 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES, AND, OPEN_PARENTHESES, true, OR, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator14 = new InfixExpressionEvaluator(Arrays.asList(expression14));
        Assert.assertTrue(infixExpressionEvaluator14.evaluate());


        // (true && true || false) && (true && false) = false
        Object[] expression15 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES, AND, OPEN_PARENTHESES, true, AND, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator15 = new InfixExpressionEvaluator(Arrays.asList(expression15));
        Assert.assertFalse(infixExpressionEvaluator15.evaluate());


        // (true && true || false) && (true && true || false) = true
        Object[] expression16 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES, AND, OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator16 = new InfixExpressionEvaluator(Arrays.asList(expression16));
        Assert.assertTrue(infixExpressionEvaluator16.evaluate());


        // (true && true || false) && (true && true || false) && (true && true || false) = true
        Object[] expression17 = {OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES,
                AND, OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES,
                AND, OPEN_PARENTHESES, true, AND, true, OR, false, CLOSE_PARENTHESES};

        InfixExpressionEvaluator infixExpressionEvaluator17 = new InfixExpressionEvaluator(Arrays.asList(expression17));
        Assert.assertTrue(infixExpressionEvaluator17.evaluate());


        // (true && (true || false)) && (true && (true || false)) && (true && (true || false)) = true
        Object[] expression18 = {OPEN_PARENTHESES, true, AND, OPEN_PARENTHESES, true, OR, false, CLOSE_PARENTHESES, CLOSE_PARENTHESES,
                AND, OPEN_PARENTHESES, true, AND, OPEN_PARENTHESES, true, OR, false, CLOSE_PARENTHESES, CLOSE_PARENTHESES,
                AND, OPEN_PARENTHESES, true, AND, OPEN_PARENTHESES, true, OR, false, CLOSE_PARENTHESES, CLOSE_PARENTHESES,};

        InfixExpressionEvaluator infixExpressionEvaluator18 = new InfixExpressionEvaluator(Arrays.asList(expression18));
        Assert.assertTrue(infixExpressionEvaluator18.evaluate());
    }
}
