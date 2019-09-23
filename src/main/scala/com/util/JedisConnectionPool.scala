package com.util

import redis.clients.jedis.{Jedis, JedisPool, JedisPoolConfig}

/**
  * Redis连接
  */
object JedisConnectionPool {

  val config = new JedisPoolConfig()

  config.setMaxTotal(20)//最大连接数

  config.setMaxIdle(10)//最大空闲连接数
                                                                                 //毫秒
  private val pool = new JedisPool(config,"node4",6379,10000,"123")
                                                                                         //？？密码
  def getConnection():Jedis={
    pool.getResource
  }

}
