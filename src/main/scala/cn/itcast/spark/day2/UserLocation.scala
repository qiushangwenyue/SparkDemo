package cn.itcast.spark.day2

import org.apache.spark.{SparkConf, SparkContext}

/**
  * @author y15079
  * @create 2018-03-06 23:03
  * @desc
  **/
object UserLocation {
  def main(args: Array[String]): Unit = {
    //配置环境变量后需要重启电脑才能生效，可以直接在程序配置环境变量 ，参考https://www.cnblogs.com/hyl8218/p/5492450.html
    System.setProperty("hadoop.home.dir", "D:\\hadoop-2.6.1")
    val conf = new SparkConf().setAppName("ForeachDemo").setMaster("local[2]")
    val sc = new SparkContext(conf)
    //sc.textFile("c://bs_log").map(_.split(",")).map(x => (x(0), x(1), x(2), x(3))) //需要在window下配置hadoop本地库，否则会报错
    val mbt = sc.textFile("D:\\IDEA\\HelloSpark\\src\\main\\files\\userlocal").map( line => {
      val fields = line.split(",")
      val eventType = fields(3)
      val time = fields(1)
      val timeLong = if (eventType == "1") -time.toLong else time.toLong
      (fields(0) + "_" + fields(2), timeLong)
    })
    //println(mbt.collect().toBuffer)

    //这种方式不好
    //(18611132889_9F36407EAD0629FC166F14DDE7970F68,54000)
    val rdd1 = mbt.groupBy(_._1).mapValues(_.foldLeft(0L)(_+_._2))
    val rdd2 = rdd1.map(t => {
      val mobile_bs = t._1
      val mobile = mobile_bs.split("_")(0)
      val lac = mobile_bs.split("_")(1)
      val time = t._2
      (mobile, lac, time)
    })
    val rdd3 = rdd2.groupBy(_._1)
    //ArrayBuffer((18688888888,List((18688888888,16030401EAFB68F1E3CDF819735E1C66,87600), (18688888888,9F36407EAD0629FC166F14DDE7970F68,51200))), (18611132889,List((18611132889,16030401EAFB68F1E3CDF819735E1C66,97500), (18611132889,9F36407EAD0629FC166F14DDE7970F68,54000))))
    val rdd4 = rdd3.mapValues(it => {
      it.toList.sortBy(_._3).reverse.take(2)
    })

    println(rdd4.collect().toBuffer)
    sc.stop()
  }

}
