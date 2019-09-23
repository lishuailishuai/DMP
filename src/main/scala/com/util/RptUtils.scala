package com.util

/**
  * 处理指标统计工具类   搞三个方法处理9个指标
  */
object RptUtils {

  // 处理请求数
  def ReqPt(requestmode:Int,processnode:Int):List[Double]={
    if(requestmode ==1 && processnode ==1){
      List[Double](1,0,0)
    }else if(requestmode ==1 && processnode ==2){
      List[Double](1,1,0)
    }else if(requestmode ==1 && processnode ==3){
      List[Double](1,1,1)
    }else{
      List[Double](0,0,0)
    }
  }
  // 处理点击展示数
  def clickPt(requestmode:Int,iseffective:Int):List[Double]={
    if(requestmode ==2 && iseffective ==1){
      List[Double](1,0)
    }else if(requestmode ==3 && iseffective ==1){
      List[Double](0,1)
    }else{
      List[Double](0,0)
    }
  }
  // 处理竞价，成功数广告成品和消费
  def adPt(iseffective:Int,isbilling:Int,
           isbid:Int,iswin:Int,adordeerid:Int,winprice:Double,adpayment:Double):List[Double]={
    if(iseffective ==1 && isbilling ==1 && isbid ==1){//在参与竞价的基础上才可以执行下面指标分析
      if(iseffective ==1 && isbilling ==1 && iswin ==1 && adordeerid !=0){
        List[Double](1,1,winprice/1000.0,adpayment/1000.0)
      }else{
        List[Double](1,0,0,0)//参与竞价，但竞价不成功，何来的广告成本与广告消费，所以后面都是0
      }
    }else{
      List[Double](0,0,0,0)
    }

  }
}
