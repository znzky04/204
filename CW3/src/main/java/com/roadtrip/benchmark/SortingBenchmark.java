package com.roadtrip.benchmark;

import com.roadtrip.algorithm.InsertionSort;
import com.roadtrip.algorithm.MergeSort;
import com.roadtrip.algorithm.QuickSort;
import com.roadtrip.util.CSVDataLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 排序算法性能测试类
 */
public class SortingBenchmark {
    
    // 测试数据文件路径
    private static final String[] TEST_FILES = {
        "Data/1000places_sorted.csv",
        "Data/1000places_random.csv",
        "Data/10000places_sorted.csv",
        "Data/10000places_random.csv"
    };
    
    // 排序算法名称
    private static final String[] ALGORITHM_NAMES = {
        "Insertion Sort",
        "Quick Sort",
        "Merge Sort"
    };
    
    /**
     * 测试排序算法性能
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 加载测试数据
            List<List<String>> testDataSets = new ArrayList<>();
            for (String file : TEST_FILES) {
                List<String> data = CSVDataLoader.loadSortingData(file);
                testDataSets.add(data);
            }
            
            // 打印表头
            System.out.println("\n=== Sorting Algorithm Performance Benchmark ===");
            System.out.printf("%-25s | %-15s | %-15s | %-15s%n", 
                    "Dataset", "Insertion (ns)", "Quick (ns)", "Merge (ns)");
            System.out.println("-".repeat(80));
            
            // 运行测试并显示结果
            for (int i = 0; i < TEST_FILES.length; i++) {
                String fileName = TEST_FILES[i].substring(TEST_FILES[i].lastIndexOf('/') + 1);
                List<String> dataSet = testDataSets.get(i);
                
                // 对每个算法运行测试
                long[] times = runTest(dataSet);
                
                // 打印结果
                System.out.printf("%-25s | %-15d | %-15d | %-15d%n", 
                        fileName, times[0], times[1], times[2]);
            }
            
            // 打印分析结论
            printAnalysisResults();
            
        } catch (IOException e) {
            System.err.println("Error loading test data: " + e.getMessage());
        }
    }
    
    /**
     * 运行性能测试
     * @param dataSet 测试数据集
     * @return 各算法运行时间（纳秒）
     */
    private static long[] runTest(List<String> dataSet) {
        long[] times = new long[3];
        
        // 测试插入排序
        times[0] = testInsertionSort(new ArrayList<>(dataSet));
        
        // 测试快速排序
        times[1] = testQuickSort(new ArrayList<>(dataSet));
        
        // 测试归并排序
        times[2] = testMergeSort(new ArrayList<>(dataSet));
        
        return times;
    }
    
    /**
     * 测试插入排序性能
     * @param data 测试数据
     * @return 运行时间（纳秒）
     */
    private static long testInsertionSort(List<String> data) {
        long startTime = System.nanoTime();
        InsertionSort.sort(data);
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    /**
     * 测试快速排序性能
     * @param data 测试数据
     * @return 运行时间（纳秒）
     */
    private static long testQuickSort(List<String> data) {
        long startTime = System.nanoTime();
        QuickSort.sort(data);
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    /**
     * 测试归并排序性能
     * @param data 测试数据
     * @return 运行时间（纳秒）
     */
    private static long testMergeSort(List<String> data) {
        long startTime = System.nanoTime();
        MergeSort.sort(data);
        long endTime = System.nanoTime();
        
        return endTime - startTime;
    }
    
    /**
     * 打印分析结论
     */
    private static void printAnalysisResults() {
        System.out.println("\n=== Analysis Results ===");
        
        // 输入顺序的影响
        System.out.println("\n1. Impact of Input Order (Sorted vs Random):");
        System.out.println("   - Insertion Sort: 输入顺序对插入排序影响极大。对于已排序数据，插入排序接近O(n)性能；对于随机数据，性能降至O(n²)。");
        System.out.println("   - Quick Sort: 输入顺序对快速排序也有显著影响。已排序数据可能导致最坏情况O(n²)；随机数据通常为O(n log n)。");
        System.out.println("   - Merge Sort: 输入顺序对归并排序影响较小，在各种数据上保持稳定的O(n log n)性能。");
        
        // 输入规模的影响
        System.out.println("\n2. Impact of Input Size (1000 vs 10000):");
        System.out.println("   - Insertion Sort: 当规模增加10倍时，运行时间大约增加100倍，符合O(n²)复杂度特性。");
        System.out.println("   - Quick Sort: 当规模增加10倍时，运行时间大约增加10-20倍，符合O(n log n)复杂度特性。");
        System.out.println("   - Merge Sort: 当规模增加10倍时，运行时间大约增加10-20倍，符合O(n log n)复杂度特性。");
        
        // 处理重复值时的稳定性
        System.out.println("\n3. Stability (When Sorting Duplicate Values):");
        System.out.println("   - Insertion Sort: 稳定排序，相等元素的相对顺序不变。");
        System.out.println("   - Quick Sort: 不稳定排序，相等元素的相对顺序可能改变。");
        System.out.println("   - Merge Sort: 稳定排序，相等元素的相对顺序不变。");
        
        // 内存使用限制
        System.out.println("\n4. Memory Usage Constraints:");
        System.out.println("   - Insertion Sort: 空间复杂度O(1)，原地排序，几乎不需要额外内存。");
        System.out.println("   - Quick Sort: 空间复杂度O(log n)，栈空间用于递归。");
        System.out.println("   - Merge Sort: 空间复杂度O(n)，需要额外临时数组空间。");
        
        // 适用场景总结
        System.out.println("\n5. Best Usage Scenarios:");
        System.out.println("   - Insertion Sort: 适用于小规模数据集或几乎已排序的数据集。在嵌入式系统等内存受限环境中优势明显。");
        System.out.println("   - Quick Sort: 大多数场景下的首选，平均性能最佳。适用于大规模随机数据，但对于特殊数据（如已排序）需谨慎使用。");
        System.out.println("   - Merge Sort: 稳定且可靠，性能稳定不受数据分布影响。适用于需要稳定排序的场景，如多级排序。");
    }
} 