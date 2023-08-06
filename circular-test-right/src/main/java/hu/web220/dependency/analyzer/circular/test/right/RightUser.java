package hu.web220.dependency.analyzer.circular.test.right;

import hu.web220.dependency.analyzer.circular.test.left.LeftUsed;

public class RightUser {
    public static void use() {
        LeftUsed.printTest();
    }
}
