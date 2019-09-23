package com.util

import com.alibaba.fastjson.{JSON, JSONObject}

/**
  * 从高德地图获取商圈信息    也就是先获取json串，再解析商圈json串获取商圈信息
  */
object AmapUtil {

  /**
    * 解析经纬度
    * @param long
    * @param lat
    * @return
    */
  def getBusinessFromAmap(long:Double,lat:Double):String={
    // https://restapi.amap.com/v3/geocode/regeo?
    // location=116.310003,39.991957&key=59283c76b065e4ee401c2b8a4fde8f8b&extensions=all
    val location: String = long+","+lat
    // 获取URL
    val url = "https://restapi.amap.com/v3/geocode/regeo?location="+location+"&key=59283c76b065e4ee401c2b8a4fde8f8b"
    //调用Http接口发送请求
    val jsonstr: String = HttpUtil.get(url)
    // 解析json串
    val jSONObject1 = JSON.parseObject(jsonstr)
    // 判断当前状态是否为 1
    val status = jSONObject1.getIntValue("status")
    if(status == 0) return ""
    // 如果不为空
    //获取json对象，然后根据键获取值，这个值可以是其他普通类型，还可以是json对象，
    // 因为返回的商圈信息是一个json套json的json串。
    val jSONObject = jSONObject1.getJSONObject("regeocode")
    if(jSONObject == null) return ""
    val jsonObject2 = jSONObject.getJSONObject("addressComponent")
    if(jsonObject2 == null) return ""
    val jSONArray = jsonObject2.getJSONArray("businessAreas")
    if(jSONArray == null) return  ""

    // 定义集合取值  这里的val与可变集合之间没有必然关系，val:修饰的值不可变     可变集合：内容可以变，但就维护着一个对象。
    val result = collection.mutable.ListBuffer[String]()
    // 循环数组
    //注意:数组里面的每一个元素又是一个json对象
    for (item <- jSONArray.toArray()){
      if(item.isInstanceOf[JSONObject]){
        val json = item.asInstanceOf[JSONObject]
        val name = json.getString("name")
        result.append(name)
      }
    }
    // 商圈名字
    result.mkString(",")//为什么有多个名字：传入经纬度，定位到一个点，默认半径为1000内的地方，也就是多个商圈名字
  }
}
