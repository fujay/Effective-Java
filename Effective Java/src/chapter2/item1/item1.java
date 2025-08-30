package chapter2.item1;

/**
 * Item1: Consider static factory methods instead of constructors
 */
public class item1 {
    public static Boolean valueOf(boolean b) {
        return b ? Boolean.TRUE : Boolean.FALSE;
    }
}
