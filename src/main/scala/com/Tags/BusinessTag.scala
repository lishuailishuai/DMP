package com.Tags

import ch.hsr.geohash.GeoHash
import com.util.{AmapUtil, JedisConnectionPool, String2Type, Tag}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.sql.Row

/**
  * 商圈标签
  */
object BusinessTag extends Tag{
  override def makeTags(args: Any*): List[(String, Int)] = {  //该方法是对每一条数据进行处理的
    var list = List[(String,Int)]()

    // 获取数据
    val row = args(0).asInstanceOf[Row]

//    注意:
//      中国的经纬度范围大约为：纬度3.86~53.55，经度73.66~135.05, 不在范围类的数据可需要处理.
    // 获取经纬度
    if(String2Type.toDouble(row.getAs[String]("long")) >=73
      && String2Type.toDouble(row.getAs[String]("long")) <=136
      && String2Type.toDouble(row.getAs[String]("lat"))>=3
      && String2Type.toDouble(row.getAs[String]("lat"))<=53){
      // 经纬度
      val long = row.getAs[String]("long").toDouble
      val lat = row.getAs[String]("lat").toDouble
      // 获取到商圈名称
      val business = getBusiness(long,lat)
      if(StringUtils.isNoneBlank(business)){
        val str = business.split(",")
        str.foreach(str=>{
          list:+=(str,1)
        })
      }
    }
    list
  }

  /**
    * 获取商圈信息
    */
  def getBusiness(long:Double,lat:Double):String={
    // GeoHash码   6就代表GeoHash码的长度（之前转换成十进制的个数）
    //GeoHash码就是reids中的key
    val geohash = GeoHash.geoHashStringWithCharacterPrecision(lat,long,6)
    // 数据库查询当前商圈信息
    var business: String = redis_queryBusiness(geohash)
    // 去高德请求
    if(business == null){
      business = AmapUtil.getBusinessFromAmap(long,lat)
      // 将高德获取的商圈存储数据库
      if(business!=null && business.length>0){
        redis_insertBusiness(geohash,business)//这两个参数怎么进行关联？？？？？？？？？？？？
                                         //多个相近的经纬度都会是同一个GeoHash码，便于在redis中进行存储
                               //对于精确范围，两者都可以决定，所以两者要搭配使用吧。。后者是通过url中传入的参数决定
      }
    }
    business
  }

  /**
    * 数据库获取商圈信息
    * @param geohash
    * @return
    */
  def redis_queryBusiness(geohash:String):String={
    val jedis = JedisConnectionPool.getConnection()
    val business = jedis.get(geohash)
    jedis.close()
    business
  }

  /**
    * 将商圈保存数据库
    */
  def redis_insertBusiness(geohash:String,business:String): Unit ={
    val jedis = JedisConnectionPool.getConnection()
    jedis.set(geohash,business)
    jedis.close()
  }
}
