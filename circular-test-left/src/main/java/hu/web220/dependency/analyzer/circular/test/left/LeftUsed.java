package hu.web220.dependency.analyzer.circular.test.left;

public class LeftUsed {
    public static void printTest() {
        System.out.printf("Print test in class %s%n",
                LeftUsed.class.getName());
    }
}
