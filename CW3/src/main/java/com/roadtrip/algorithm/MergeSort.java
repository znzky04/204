package com.roadtrip.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * 归并排序算法实现
 */
public class MergeSort {
    
    /**
     * 归并排序算法
     * 时间复杂度：
     * - 最好情况：O(n log n)
     * - 最坏情况：O(n log n)
     * - 平均情况：O(n log n)
     * 空间复杂度：O(n)
     * 
     * @param <T> 元素类型，必须实现Comparable接口
     * @param list 待排序列表
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        // 如果列表长度为0或1，已经有序，直接返回
        if (list.size() <= 1) {
            return;
        }
        
        // 创建临时列表存储归并结果
        List<T> temp = new ArrayList<>(list);
        
        // 调用递归合并排序
        mergeSort(list, temp, 0, list.size() - 1);
    }
    
    /**
     * 归并排序递归实现
     * @param <T> 元素类型
     * @param list 待排序列表
     * @param temp 临时存储列表
     * @param left 左边界索引
     * @param right 右边界索引
     */
    private static <T extends Comparable<T>> void mergeSort(List<T> list, List<T> temp, int left, int right) {
        if (left < right) {
            // 找出中间点
            int mid = left + (right - left) / 2;
            
            // 递归排序左半部分
            mergeSort(list, temp, left, mid);
            
            // 递归排序右半部分
            mergeSort(list, temp, mid + 1, right);
            
            // 合并已排序的子列表
            merge(list, temp, left, mid, right);
        }
    }
    
    /**
     * 合并两个已排序的子列表
     * @param <T> 元素类型
     * @param list 待合并列表
     * @param temp 临时存储列表
     * @param left 左边界索引
     * @param mid 中间点索引
     * @param right 右边界索引
     */
    private static <T extends Comparable<T>> void merge(List<T> list, List<T> temp, int left, int mid, int right) {
        // 复制要合并的元素到临时列表
        for (int i = left; i <= right; i++) {
            temp.set(i, list.get(i));
        }
        
        // 左半部分起始索引
        int i = left;
        // 右半部分起始索引
        int j = mid + 1;
        // 合并后的当前位置索引
        int k = left;
        
        // 从两个子列表中取出较小的元素放入原列表
        while (i <= mid && j <= right) {
            if (temp.get(i).compareTo(temp.get(j)) <= 0) {
                list.set(k, temp.get(i));
                i++;
            } else {
                list.set(k, temp.get(j));
                j++;
            }
            k++;
        }
        
        // 将左半部分剩余元素放入原列表
        while (i <= mid) {
            list.set(k, temp.get(i));
            i++;
            k++;
        }
        
        // 右半部分的剩余元素已经在正确位置，无需复制
    }
    
    /**
     * 字符串数组的归并排序
     * @param array 待排序字符串数组
     */
    public static void sort(String[] array) {
        if (array.length <= 1) {
            return;
        }
        
        String[] temp = new String[array.length];
        mergeSort(array, temp, 0, array.length - 1);
    }
    
    /**
     * 字符串数组的归并排序递归实现
     * @param array 待排序数组
     * @param temp 临时存储数组
     * @param left 左边界索引
     * @param right 右边界索引
     */
    private static void mergeSort(String[] array, String[] temp, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(array, temp, left, mid);
            mergeSort(array, temp, mid + 1, right);
            merge(array, temp, left, mid, right);
        }
    }
    
    /**
     * 字符串数组的合并方法
     * @param array 待合并数组
     * @param temp 临时存储数组
     * @param left 左边界索引
     * @param mid 中间点索引
     * @param right 右边界索引
     */
    private static void merge(String[] array, String[] temp, int left, int mid, int right) {
        // 复制元素到临时数组
        for (int i = left; i <= right; i++) {
            temp[i] = array[i];
        }
        
        int i = left;
        int j = mid + 1;
        int k = left;
        
        while (i <= mid && j <= right) {
            if (temp[i].compareTo(temp[j]) <= 0) {
                array[k] = temp[i];
                i++;
            } else {
                array[k] = temp[j];
                j++;
            }
            k++;
        }
        
        while (i <= mid) {
            array[k] = temp[i];
            i++;
            k++;
        }
    }
} 