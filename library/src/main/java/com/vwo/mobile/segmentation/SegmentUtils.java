package com.vwo.mobile.segmentation;

import com.vwo.mobile.models.Campaign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aman on Fri 05/01/18 18:04.
 */

public class SegmentUtils {

    public static boolean evaluateSegmentation(Campaign campaign) {
        if (campaign.getSegmentType() == null) {
            return false;
        }
        if (campaign.getSegmentType().equals(Campaign.SEGMENT_CUSTOM)) {
            List<Object> expression = new ArrayList<>();
            for (Segment segment : campaign.getSegments()) {
                CustomSegment customSegment = (CustomSegment) segment;
                expression.addAll(customSegment.toInfix());
            }

            // TODO: fix for bug in backend service serving data. send an extra operator.
            if (expression.get(0) instanceof Operator && ((Operator) expression.get(0)).canEvaluate()) {
                expression.remove(0);
            }

            InfixExpressionEvaluator infixExpressionEvaluator = new InfixExpressionEvaluator(expression);
            return infixExpressionEvaluator.evaluate();
        } else {
            return campaign.getSegments().get(0).evaluate();
        }
    }
}
