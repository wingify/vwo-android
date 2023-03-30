package com.vwo.mobile.v3;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by { Nabin Niroula } on 21/03/2023 for android-sdk.
 **/
public class MockServerResponseTest {

    public static final char ARRAY_START = '[';

    public static final char OBJECT_START = '{';

    @Test
    public void replaceWithApiV3EuModificationResponse_ReplaceSuccess() {
        StringBuilder data = new StringBuilder();
        MockServerResponse.replaceWithApiV3EuModificationResponse(data);

        boolean isCountGreaterThanZero = data.length() > 0;
        Assert.assertTrue(isCountGreaterThanZero);
    }

    @Test
    public void replaceWithApiV3EuModificationResponse_CompareFirstCharacter_ReplaceSuccess() {
        StringBuilder data = new StringBuilder();
        MockServerResponse.replaceWithApiV3EuModificationResponse(data);

        Assert.assertEquals(data.charAt(0), OBJECT_START);
    }

    @Test
    public void replaceWithLegacyApiV2Response_ReplaceSuccess() {
        StringBuilder data = new StringBuilder();
        MockServerResponse.replaceWithLegacyApiV2Response(data);

        boolean isCountGreaterThanZero = data.length() > 0;
        Assert.assertTrue(isCountGreaterThanZero);
    }

    @Test
    public void replaceWithLegacyApiV2Response_CompareFirstCharacter_ReplaceSuccess() {
        StringBuilder data = new StringBuilder();
        MockServerResponse.replaceWithLegacyApiV2Response(data);

        boolean isCountGreaterThanZero = data.length() > 0;
        Assert.assertTrue(isCountGreaterThanZero);

        Assert.assertEquals(data.charAt(0), ARRAY_START);
    }

}
