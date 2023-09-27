package org.example.leetcode.sort;

import java.util.Arrays;
public class RaixSort_10 {
	public static void main(String[] args) {
		int[] arr = { 53, 3, 542, 748, 14, 214 };

		// 得到数组中最大的数
		int max = arr[0];// 假设第一个数就是数组中的最大数
		for (int i = 1; i < arr.length; i++) {
			if (arr[i] > max) {
				max = arr[i];
			}
		}
		// 得到最大数是几位数
		// 通过拼接一个空串将其变为字符串进而求得字符串的长度，即为位数
		int maxLength = (max + "").length();

		// 定义一个二维数组，模拟桶，每个桶就是一个一维数组
		// 为了防止放入数据的时候桶溢出，我们应该尽量将桶的容量设置得大一些
		int[][] bucket = new int[10][arr.length];
		// 记录每个桶中实际存放的元素个数
		// 定义一个一维数组来记录每个桶中每次放入的元素个数
		int[] bucketElementCounts = new int[10];

		// 通过变量n帮助取出元素位数上的数
		for (int i = 0, n = 1; i < maxLength; i++, n *= 10) {
			for (int j = 0; j < arr.length; j++) {
				// 针对每个元素的位数进行处理
				int digitOfElement = arr[j] / n % 10;
				// 将元素放入对应的桶中
				// bucketElementCounts[digitOfElement]就是桶中的元素个数，初始为0，放在第一位
				bucket[digitOfElement][bucketElementCounts[digitOfElement]] = arr[j];
				// 将桶中的元素个数++
				// 这样接下来的元素就可以排在前面的元素后面
				bucketElementCounts[digitOfElement]++;
			}
			// 按照桶的顺序取出数据并放回原数组
			int index = 0;
			for (int k = 0; k < bucket.length; k++) {
				// 如果桶中有数据，才取出放回原数组
				if (bucketElementCounts[k] != 0) {
					// 说明桶中有数据，对该桶进行遍历
					for (int l = 0; l < bucketElementCounts[k]; l++) {
						// 取出元素放回原数组
						arr[index++] = bucket[k][l];
					}
				}
				// 每轮处理后，需要将每个bucketElementCounts[k]置0
				bucketElementCounts[k] = 0;
			}
		}
		System.out.println(Arrays.toString(arr));//[3, 14, 53, 214, 542, 748]
	}
}
