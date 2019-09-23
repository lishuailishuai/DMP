package com.Location

import com.util.RptUtils
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.immutable

/**
  * 统计地域指标
  */
object LoactionRpt {

  def main(args: Array[String]): Unit = {
    //System.setProperty("hadoop.home.dir", "D:\\Huohu\\下载\\hadoop-common-2.2.0-bin-master")
    if(args.length != 2){
      println("输入目录不正确")
      sys.exit()
    }
    val Array(inputPath,outputPath) =args

    val spark = SparkSession
      .builder()
      .appName("ct")
      .master("local")
      .config("spark.serializer","org.apache.spark.serializer.KryoSerializer")
      .getOrCreate()
    // 获取数据
    val df: DataFrame = spark.read.parquet(inputPath)

    df.rdd.map(row=>{
      // 根据指标的字段获取数据  9个指标，每个指标7个字段  下面多添加了两个字段，广告成本和广告消费 double
      // REQUESTMODE	PROCESSNODE	ISEFFECTIVE	ISBILLING	ISBID	ISWIN	ADORDERID WinPrice adpayment
      val requestmode: Int = row.getAs[Int]("requestmode")
      val processnode = row.getAs[Int]("processnode")
      val iseffective = row.getAs[Int]("iseffective")
      val isbilling = row.getAs[Int]("isbilling")
      val isbid = row.getAs[Int]("isbid")
      val iswin = row.getAs[Int]("iswin")
      val adordeerid = row.getAs[Int]("adorderid")
      val winprice = row.getAs[Double]("winprice")
      val adpayment = row.getAs[Double]("adpayment")
      // 处理请求数
      val rptList: List[Double] = RptUtils.ReqPt(requestmode,processnode)
      // 处理展示点击
      val clickList: immutable.Seq[Double] = RptUtils.clickPt(requestmode,iseffective)
      // 处理广告
      val adList = RptUtils.adPt(iseffective,isbilling,isbid,iswin,adordeerid,winprice,adpayment)
      // 所有指标
      val allList:List[Double] = rptList ++ clickList ++ adList //得到一个新的，总的列表
      // 得到的总列表：一条数据的9个指标
      ((row.getAs[String]("provincename"),row.getAs[String]("cityname")),allList)
      //返回一个元组，元组里面有键和值，键又是一个元组。
    }).reduceByKey((list1,list2)=>{
      // list1(1,1,1,1).zip(list2(1,1,1,1))=list((1,1),(1,1),(1,1),(1,1))
      list1.zip(list2).map(t=>t._1+t._2)
    })
      .map(t=>t._1+","+t._2.mkString(","))

      .saveAsTextFile(outputPath)
  }
}
