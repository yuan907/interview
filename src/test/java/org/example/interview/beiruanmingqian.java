package org.example.interview;

import org.camunda.feel.syntaxtree.In;

import java.util.*;
import java.util.stream.Collectors;

public class beiruanmingqian {

    public static void main(String[] args) {

        int[] nums = {1, 2, 2, 2, 3, 3, 4, 7, 7, 7, 8, 8, 9, 10, 10, 10, 1, 2, 4, 5};
        Map<Integer, Integer> result = new HashMap<>();
        for (int num : nums) {
            result.put(num, result.getOrDefault(num, 0) + 1);
        }

        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(result.entrySet());
        entryList.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        for (Map.Entry<Integer, Integer> entry : entryList) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }


    }
}
