package com.graph_Test

import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * 图计算案例（好友关联推荐）
  */
object GraphTest {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().appName("graph").master("local").getOrCreate()

    // 创建点和边

    // 构建点的集合
    val vertexRDD: RDD[(Long, (String, Int))] = spark.sparkContext.makeRDD(Seq(
      (1L,("小明",26)),//元组里面第一个参数放点（long类型），第二个参数放点的属性介绍
      (2L,("小红",30)),
      (6L,("小黑",33)),
      (9L,("小白",26)),
      (133L,("小黄",30)),
      (138L,("小蓝",33)),
      (158L,("小绿",26)),
      (16L,("小龙",30)),
      (44L,("小强",33)),
      (21L,("小胡",26)),
      (5L,("小狗",30)),
      (7L,("小熊",33))
    ))
    // 构造边的集合
    val edgeRDD: RDD[Edge[Int]] = spark.sparkContext.makeRDD(Seq(
      Edge(1L,133L,0),//边的第一个点，边的第二个点，边的属性（这里用0占位  因为我们只做连通图的效果）
      Edge(2L,133L,0),
      Edge(6L,133L,0),
      Edge(9L,133L,0),
      Edge(6L,138L,0),
      Edge(16L,138L,0),
      Edge(21L,138L,0),
      Edge(44L,138L,0),
      Edge(5L,158L,0),
      Edge(7L,158L,0)
    ))

    // 构建图
    val graph = Graph(vertexRDD,edgeRDD)
    // 取顶点
    val vertices: VertexRDD[VertexId] = graph.connectedComponents().vertices

    // 匹配数据
    vertices.join(vertexRDD).map{
      case (userId,(cnId,(name,age)))=>(cnId,List((name,age)))
    }
      .reduceByKey(_++_)
      .foreach(println)
  }
}