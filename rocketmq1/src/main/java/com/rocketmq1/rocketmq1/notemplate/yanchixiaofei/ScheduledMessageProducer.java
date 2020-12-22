package com.rocketmq1.rocketmq1.notemplate.yanchixiaofei;

/**
 * java类简单作用描述
 *
 * @ProjectName: rocketmq_lizi1
 * @Package: com.rocketmq_lizi.demo.notemplate.yanchixiaofei
 * @ClassName: ScheduledMessageProducer
 * @Description: java类作用描述
 * @Author: zhangq
 * @CreateDate: 2020-12-17 18:34
 * @UpdateUser: zhangq
 * @UpdateDate: 2020-12-17 18:34
 * @UpdateRemark: The modified content
 * @Version: 1.0 *
 */
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;

public class ScheduledMessageProducer {
  public static void main(String[] args) throws Exception {
    // 实例化一个生产者来产生延时消息
    DefaultMQProducer producer = new DefaultMQProducer("ExampleProducerGroup");
    producer.setNamesrvAddr("192.168.56.200:9876");
    // 启动生产者
    producer.start();


    int totalMessagesToSend = 10;
    for (int i = 0; i < totalMessagesToSend; i++) {
      Message message = new Message("TestTopic", ("Hello scheduled message " + i).getBytes());
      // 设置延时等级3,这个消息将在10s之后发送(现在只支持固定的几个时间,详看delayTimeLevel)
      message.setDelayTimeLevel(2);
      // 发送消息
      producer.send(message);
    }
    // 关闭生产者
    producer.shutdown();
  }
}
