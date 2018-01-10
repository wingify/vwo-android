package com.vwo.mobile.segmentation;


import android.support.annotation.NonNull;

public class Operator implements Comparable<Operator> {

    private static final String STRING_OR = "OR";
    public static final Operator OR = new Operator(STRING_OR, 8);
    private static final String STRING_AND = "AND";
    public static final Operator AND = new Operator(STRING_AND, 9);
    private static final String STRING_OPEN_PARENTHESES = "(";
    public static final Operator OPEN_PARENTHESES = new Operator(STRING_OPEN_PARENTHESES, 41);
    private static final String STRING_CLOSE_PARENTHESES = ")";
    public static final Operator CLOSE_PARENTHESES = new Operator(STRING_CLOSE_PARENTHESES, 42);

    private static final Operator[] operators = {OR, AND, OPEN_PARENTHESES, CLOSE_PARENTHESES};

    private String mOperator;
    private int precedence;

    private Operator(String operator, int precedence) {
        this.mOperator = operator;
        this.precedence = precedence;
    }

    public static Operator fromString(String operatorString) {
        for (Operator operator : operators) {
            if (operator.toString().equals(operatorString)) {
                return operator;
            }
        }
        return null;
    }

    public String toString() {
        return this.mOperator;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Operator) {
            return ((Operator) obj).mOperator.equals(this.mOperator);
        }
        return super.equals(obj);
    }

    public boolean canEvaluate() {
        switch (this.mOperator) {
            case STRING_OR:
                return true;
            case STRING_AND:
                return true;
            case STRING_OPEN_PARENTHESES:
            case STRING_CLOSE_PARENTHESES:
                return false;
            default:
                return false;
        }
    }

    @Override
    public int hashCode() {
        return precedence;
    }

    public boolean evaluate(boolean leftOperand, boolean rightOperand) {
        switch (this.mOperator) {
            case STRING_OR:
                return leftOperand || rightOperand;
            case STRING_AND:
                return leftOperand && rightOperand;
            case STRING_OPEN_PARENTHESES:
            case STRING_CLOSE_PARENTHESES:
                throw new ArithmeticException("Cannot perform this operation");
            default:
                return false;
        }
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param operator the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NonNull Operator operator) {
        return this.precedence - operator.precedence;
    }
}
