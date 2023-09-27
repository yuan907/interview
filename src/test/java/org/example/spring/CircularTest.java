package org.example.spring;

/**
 *  循环依赖
 * */
public class CircularTest {

    public static void main(String[] args) {
        new CircularTest1();
    }

}

class CircularTest1 {
    private CircularTest2 circularTest2 = new CircularTest2();
}

class CircularTest2 {
    private CircularTest1 circularTest1 = new CircularTest1();
}
