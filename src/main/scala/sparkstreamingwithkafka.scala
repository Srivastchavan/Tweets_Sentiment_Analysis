import java.util.Properties

import SentimentAnalyzer.mainSentiment
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.log4j.{Level, Logger}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}
import twitter4j.Status
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
object sparkstreamingwithkafka {
  def main(args: Array[String]) {

    if (args.length < 6) {
      System.out.println("Please specify six arguments namely inputTopic,TwitterAPIKey, APISecretKey, accessToken, accessTokenSecret and search terms")
      return
    }

    if (!Logger.getRootLogger.getAllAppenders.hasMoreElements) {
      Logger.getRootLogger.setLevel(Level.WARN)
    }

    val inputtopic = args(0).toString

    val APIKey = args(1)
    val APISecret = args(2)
    val accessToken = args(3)
    val accessTokenSecret = args(4)
    val searchTerm = args.slice(5, args.length)
    val kafkaBroker = "localhost:9092"

    val sparkConf = new SparkConf().setAppName("Spark-Streaming-with-Twitter-and-Kafka")

    if (!sparkConf.contains("spark.master")) {
      sparkConf.setMaster("local[2]")
    }

    val cb = new ConfigurationBuilder
    cb.setDebugEnabled(true).setOAuthConsumerKey(APIKey).setOAuthConsumerSecret(APISecret).setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret)

    val auth = new OAuthAuthorization(cb.build)
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    val tweets: DStream[Status] = TwitterUtils.createStream(ssc, Some(auth), searchTerm)
    val tweetsInEnglish = tweets.filter(_.getLang() == "en")

    val sentiments: DStream[Int] = tweetsInEnglish.map(_.getText).map(text => (mainSentiment(text)))

    val sentimentArray = Array("Very Negative", "Negative", "Neutral", "Positive", "Very Positive")
    sentiments.foreachRDD(rdd => {
      rdd.foreachPartition(partition => {
        val props = new Properties()
        val bootstrap = "localhost:9092"
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBroker)
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props.put("bootstrap.servers", bootstrap)
        val kp = new KafkaProducer[String, String](props)
        partition.foreach(record => {
          val sentiment = sentimentArray(record)
          val data = new ProducerRecord[String, String](inputtopic, null, sentiment)
          kp.send(data)
        })
        kp.close()
      })

    })

    ssc.start()
    ssc.awaitTermination()
  }

}
