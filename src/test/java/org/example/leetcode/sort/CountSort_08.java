package org.example.leetcode.sort;

import java.util.Arrays;
public class CountSort_08 {
	public static void main(String[] args) {
		int[] array = { 4, 2, 2, 8, 3, 3, 1 };
		// 找到数组中最大的值 ---> max:8
		int max = findMaxElement(array);
		int[] sortedArr = countingSort(array, max + 1);
		System.out.println("计数排序后的数组： " + Arrays.toString(sortedArr));
	}
	private static int findMaxElement(int[] array) {
		int max = array[0];
		for (int val : array) {
			if (val > max)
				max = val;
		}
		return max;
	}
	private static int[] countingSort(int[] array, int range) { //range:8+1
		int[] output = new int[array.length]; 
		int[] count = new int[range];
		//初始化： count1数组
		for (int i = 0; i < array.length; i++) {
			count[array[i]]++;
		}
		//计数： count2数组，累加次数后的，这里用count2区分
		for (int i = 1; i < range; i++) {
			count[i] = count[i] + count[i - 1];
		}
		//排序：最后数组
		for (int i = 0; i < array.length; i++) {
			output[count[array[i]] - 1] = array[i];
			count[array[i]]--;
		}
		return output;
	}
}


