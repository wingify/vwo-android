package com.vwo.mobile;

/**
 * Created by aman on Wed 10/01/18 12:28.
 */

public class TestObject<Data, Result> {

    private Data data;
    private Result result;

    public TestObject(Data data, Result result) {
        this.data = data;
        this.result = result;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
