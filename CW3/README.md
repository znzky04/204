# 美国旅行路线规划系统

这是一个基于Java实现的路线规划系统，可以帮助用户计算从一个城市到另一个城市的最短路径，同时访问指定的旅游景点。

## 项目结构

```
src/main/java/com/roadtrip/
├── model/              # 数据模型
│   ├── City.java       # 城市类
│   ├── Attraction.java # 景点类
│   ├── Road.java       # 道路类
│   └── Graph.java      # 图类，表示城市路网
├── algorithm/          # 算法实现
│   ├── DijkstraAlgorithm.java  # Dijkstra最短路径算法
│   ├── RouteOptimizer.java     # 路线优化器
│   ├── InsertionSort.java      # 插入排序
│   ├── QuickSort.java          # 快速排序
│   └── MergeSort.java          # 归并排序
├── service/            # 服务
│   └── RouteService.java       # 路线规划服务
├── util/               # 工具类
│   └── CSVDataLoader.java      # CSV数据加载工具
├── benchmark/          # 性能测试
│   └── SortingBenchmark.java   # 排序算法性能测试
├── RoadTripApp.java    # 主应用程序
├── TaskBExamples.java  # 任务B测试用例
└── TaskCBenchmark.java # 任务C测试用例
```

## 数据文件

* `Data/attractions.csv` - 旅游景点数据
* `Data/roads.csv` - 城市道路网络数据
* `Data/1000places_sorted.csv` - 1000个已排序地名
* `Data/1000places_random.csv` - 1000个随机顺序地名
* `Data/10000places_sorted.csv` - 10000个已排序地名
* `Data/10000places_random.csv` - 10000个随机顺序地名

## 功能

### 任务A - 路线规划程序设计

* 设计了城市、景点和路网的类结构
* 实现了图的邻接表存储结构
* 提供了标准接口方法计算路线

### 任务B - 图算法最短路径实现与评价

* 实现了Dijkstra最短路径算法
* 提供了三个测试用例：
  * Houston TX 到 Philadelphia PA（无中间景点）
  * Philadelphia PA 到 San Antonio TX（经过Hollywood Sign）
  * San Jose CA 到 Phoenix AZ（经过Liberty Bell和Millennium Park）

### 任务C - 排序算法性能评测

* 实现了三种排序算法：插入排序、快速排序、归并排序
* 测试了四种数据集上的性能表现
* 分析了算法的时间复杂度、空间复杂度和适用场景

## 使用方法

### 运行交互式路线规划应用

```bash
java com.roadtrip.RoadTripApp
```

### 运行任务B测试用例

```bash
java com.roadtrip.TaskBExamples
```

### 运行任务C排序算法性能测试

```bash
java com.roadtrip.TaskCBenchmark
```

## 项目详细说明

### 路线规划算法

路线规划采用贪心算法策略：先使用Dijkstra算法计算任意两点间的最短路径，然后通过贪心选择依次访问所有景点。这种方法在大多数情况下能产生较好的结果，但不保证全局最优解。

### 排序算法性能分析

* 插入排序：在小规模或几乎已排序的数据上表现最佳
* 快速排序：平均性能最好，但在已排序数据上性能可能下降
* 归并排序：性能稳定，不受数据分布影响，但需要额外空间

## 贡献者

* [Your Name]

## 许可证

本项目采用MIT许可证。 