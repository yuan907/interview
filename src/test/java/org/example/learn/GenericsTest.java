package org.example.learn;

import java.util.ArrayList;
import java.util.List;

/**
 * 泛型
 * 使用T 表示当前满足一种类型
 * 使用？ 表示占位符，list 中支持多种数据元素
 * */
public class GenericsTest<T> {

    T content;

    public GenericsTest(T content) {
        this.content = content;
    }

    public void print() {
        System.out.println(content);
    }

    public static void main(String[] args) {

        GenericsTest<String> genericsTest = new GenericsTest("hello world");
        genericsTest.print();

        //使用T
        List<String> stringList =new ArrayList<>();
        stringList.add("hello world");
        printT(stringList);

        //使用？
        List list=new ArrayList();
        list.add("hello world");
        list.add(123);
        print(list);
    }

    //使用T
    private static <T> void printT(List<T> content)
    {
        System.out.println(content);
    }

    private static  void print(List<?> list)
    {
        System.out.println(list);
    }
}
