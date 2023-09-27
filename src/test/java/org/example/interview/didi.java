package org.example.interview;

import camundajar.impl.scala.Char;

import java.util.HashSet;
import java.util.Set;

//求一个字符串的最长不重复子串
//abcdchui -> dchui
public class didi {
    public static void main(String[] args) {

        String str = "abcdasd";

        String s = longestSubString(str);
        System.out.println(s);
    }

    public static String longestSubString(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        int left = 0, right = 0, start = 0, maxLength = 0;
//        HashSet<Character> set
//        Set<Character> set = new HashSet<>();
//        while (right < s.length()) {
//            if (!set.contains(s.charAt(right))) {
//                set.add(s.charAt(right));
//                right++;
//                maxLength = Math.max(maxLength, right - left);
//            } else {
//                set.remove(s.charAt(left));
//                left++;
//            }
//        }



        return s.substring(left,left+maxLength);
    }

}
