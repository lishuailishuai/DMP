package com.Tags

import com.util.Tag
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

/**
  * 广告标签   继承Tag接口
  */
object TagsAd extends Tag{  //相当于静态，直接调用

  //该方法是对每一条数据的处理
  override def makeTags(args: Any*): List[(String, Int)] = {

    var list =List[(String,Int)]()
    // 获取数据类型  args(0)代表第一个参数
    val row = args(0).asInstanceOf[Row]//转类型
    // 获取广告位类型和名称
    val adType = row.getAs[Int]("adspacetype")
    // 广告位类型标签
    adType match {
      case v if v > 9 => list:+=("LC"+v,1)
      case v if v > 0 && v <= 9 => list:+=("LC0"+v,1)
    }
    // 广告名称
    val adName = row.getAs[String]("adspacetypename")
    if(StringUtils.isBlank(adName)){
      list:+=("LN"+adName,1)
    }
    // 渠道标签 （这里是写到广告标签里面）
    val channel = row.getAs[Int]("adplatformproviderid")
    list:+=("CN"+channel,1)

    list

  }
}
