package cn.itcast.spark.day2

import java.net.URL

import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author y15079
  * @create 2018-03-07 10:23
  * @desc
  **/
object UrlCount {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("UrlCount").setMaster("local[2]")
    val sc = new SparkContext(conf)

    //rdd1将数据切分，元组中放的是（url, 1）
    val rdd1 = sc.textFile("D:\\IDEA\\HelloSpark\\src\\main\\files\\usercount\\itcast.log").map(line => {
      val f = line.split("\t")
      (f(1), 1)
    })
    val rdd2 = rdd1.reduceByKey(_+_)

    val rdd3 = rdd2.map(t => {
      val url = t._1
      val host = new URL(url).getHost
      (host, url, t._2)
    })

    //分组再排序，可能会造成内存爆掉
    val rdd4 = rdd3.groupBy(_._1).mapValues(it => {
      it.toList.sortBy(_._3).reverse.take(3) //rdd数据量大的时候，会把部分放到磁盘中，而java会溢出
    })
    println(rdd4.collect().toBuffer)

    sc.stop()
  }
}
