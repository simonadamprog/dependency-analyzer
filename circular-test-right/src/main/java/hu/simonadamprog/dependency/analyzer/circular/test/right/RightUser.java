package hu.simonadamprog.dependency.analyzer.circular.test.right;

import hu.simonadamprog.dependency.analyzer.circular.test.left.LeftUsed;

public class RightUser {
    public static void use() {
        LeftUsed.printTest();
    }
}
