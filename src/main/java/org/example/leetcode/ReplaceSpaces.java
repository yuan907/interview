package org.example.leetcode;

/**
 * @Author: yuanchao
 * @DATE: 2023/5/17 9:49
 */
public class ReplaceSpaces {

    private static String str = "We are happy.";

    public static void main(String[] args) {
        replaceSpace(str);
    }

    public static String replaceSpace(String s) {
        StringBuilder res = new StringBuilder();
        for (Character c : s.toCharArray()) {
            if (c == ' ') {
                res.append("%20");
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }




}
