package com.roadtrip.algorithm;

import java.util.List;

/**
 * 插入排序算法实现
 */
public class InsertionSort {
    
    /**
     * 插入排序算法
     * 时间复杂度：
     * - 最好情况：O(n)，当输入已经排序时
     * - 最坏情况：O(n²)，当输入逆序排列时
     * - 平均情况：O(n²)
     * 空间复杂度：O(1)
     * 
     * @param <T> 元素类型，必须实现Comparable接口
     * @param list 待排序列表
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        int n = list.size();
        
        // 从第二个元素开始遍历
        for (int i = 1; i < n; i++) {
            // 保存当前要插入的元素
            T key = list.get(i);
            
            // 从已排序部分的最右侧开始比较
            int j = i - 1;
            
            // 将比当前元素大的已排序元素向右移动
            while (j >= 0 && list.get(j).compareTo(key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            
            // 在正确位置插入当前元素
            list.set(j + 1, key);
        }
    }
    
    /**
     * 字符串数组的插入排序
     * @param array 待排序字符串数组
     */
    public static void sort(String[] array) {
        int n = array.length;
        
        for (int i = 1; i < n; i++) {
            String key = array[i];
            int j = i - 1;
            
            while (j >= 0 && array[j].compareTo(key) > 0) {
                array[j + 1] = array[j];
                j--;
            }
            
            array[j + 1] = key;
        }
    }
} 