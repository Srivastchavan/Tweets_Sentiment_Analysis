Do the following steps to run the code-

Jar file s3 link - https://cs6350asg3.s3.amazonaws.com/sparkstreamingwithkafka-assembly-0.1.jar

1. Create the Assembly jar file using the Scala code.
2. Install Kafka and Start the zookeeper in command prompt(.\bin\windows\zookeeper-server-start.bat config\zookeeper.properties)
3. Start the Kafka in command prompt(.\bin\windows\kafka-server-start.bat config\server.properties)
4. Create kafka topic in command prompt(.\bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic "topicA")
5. Install and Start ElasticSearch in command prompt(.\bin\elasticsearch.bat)
6. Install and Start Kibana in command prompt(.\bin\kibana.bat)
7. Install Logstash and create a config file(logstash-sample.conf) with the topic name created in step 4 and port details in the config folder
8. Run the logstash in command prompt(.\bin\logstash.bat -f c:\logstash\config\logstash-sample.conf)
9. Now run the assembly jar file created in step 1 by using below command -
spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0
--class ClassName PathToJarFile TopicName TwitterAPIKey APISecretKey AccessToken AccessTokenSecret SearchTerm

Example
spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0 --class sparkstreamingwithkafka "C:\Users\Srivastchavan\Desktop\CS6350_Asg3\target\scala-2.11\sparkstreamingwithkafka-assembly-0.1" "topicA" "OSBF7NYWtycD4WfniiIiySUNr" "LB2fRitXhchrU0d4sl7HhVAGijspWO5BLtTgKq3X7quf0ZMekj" "1387912183007301638-8UJg6dWtsJIxiXqUTkXyaK2PB8CCbe" "g8iKR2jMJweBX56YWrzrZ3bJXysOCLGgwTru5F1aHiVJ2" "UnitedStates"

10. Finally open the link -http://localhost:5601/app/lens#/ and set up horizontal axis, vertical axis and Break Down by parameters.

