package hu.simonadamprog.dependency.analyzer.test.app;

import hu.simonadamprog.dependency.analyzer.circular.test.left.LeftUser;
import hu.simonadamprog.dependency.analyzer.circular.test.right.RightUser;
import hu.simonadamprog.dependency.analyzer.test.library.MyMath;
import hu.simonadamprog.dependency.analyzer.test.subsubproject.SubSubProjectUtil;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class Config {
    @PostConstruct
    public void postConstruct() {
        calculate();
        printTest();
        LeftUser.use();
        RightUser.use();
    }

    private void calculate() {
        System.out.println("Calculation result: " + MyMath.calculate());
    }

    private void printTest() {
        SubSubProjectUtil.printSomething();
    }
}
