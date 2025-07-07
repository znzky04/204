package com.roadtrip.algorithm;

import java.util.List;

/**
 * 快速排序算法实现
 */
public class QuickSort {
    
    /**
     * 快速排序算法
     * 时间复杂度：
     * - 最好情况：O(n log n)
     * - 最坏情况：O(n²)，当输入已排序时
     * - 平均情况：O(n log n)
     * 空间复杂度：O(log n)
     * 
     * @param <T> 元素类型，必须实现Comparable接口
     * @param list 待排序列表
     */
    public static <T extends Comparable<T>> void sort(List<T> list) {
        quickSort(list, 0, list.size() - 1);
    }
    
    /**
     * 快速排序递归实现
     * @param <T> 元素类型
     * @param list 待排序列表
     * @param low 排序起始索引
     * @param high 排序结束索引
     */
    private static <T extends Comparable<T>> void quickSort(List<T> list, int low, int high) {
        if (low < high) {
            // 分区，获取分区点
            int pivotIndex = partition(list, low, high);
            
            // 递归排序左半部分
            quickSort(list, low, pivotIndex - 1);
            
            // 递归排序右半部分
            quickSort(list, pivotIndex + 1, high);
        }
    }
    
    /**
     * 分区方法
     * @param <T> 元素类型
     * @param list 待分区列表
     * @param low 分区起始索引
     * @param high 分区结束索引
     * @return 分区点索引
     */
    private static <T extends Comparable<T>> int partition(List<T> list, int low, int high) {
        // 选择最右侧元素作为枢轴
        T pivot = list.get(high);
        
        // i 表示小于枢轴的元素数量
        int i = low - 1;
        
        // 遍历区间，将小于枢轴的元素移到左侧
        for (int j = low; j < high; j++) {
            if (list.get(j).compareTo(pivot) <= 0) {
                i++;
                // 交换元素
                T temp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, temp);
            }
        }
        
        // 将枢轴放到正确位置
        T temp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, temp);
        
        return i + 1; // 返回枢轴位置
    }
    
    /**
     * 字符串数组的快速排序
     * @param array 待排序字符串数组
     */
    public static void sort(String[] array) {
        quickSort(array, 0, array.length - 1);
    }
    
    /**
     * 字符串数组的快速排序递归实现
     * @param array 待排序数组
     * @param low 排序起始索引
     * @param high 排序结束索引
     */
    private static void quickSort(String[] array, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(array, low, high);
            quickSort(array, low, pivotIndex - 1);
            quickSort(array, pivotIndex + 1, high);
        }
    }
    
    /**
     * 字符串数组的分区方法
     * @param array 待分区数组
     * @param low 分区起始索引
     * @param high 分区结束索引
     * @return 分区点索引
     */
    private static int partition(String[] array, int low, int high) {
        String pivot = array[high];
        int i = low - 1;
        
        for (int j = low; j < high; j++) {
            if (array[j].compareTo(pivot) <= 0) {
                i++;
                String temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        String temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        
        return i + 1;
    }
} 