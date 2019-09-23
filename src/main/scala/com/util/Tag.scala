package com.util

/**
  * 打标签接口  抽象方法
  */
trait Tag {

  def makeTags(args:Any*):List[(String,Int)]
}