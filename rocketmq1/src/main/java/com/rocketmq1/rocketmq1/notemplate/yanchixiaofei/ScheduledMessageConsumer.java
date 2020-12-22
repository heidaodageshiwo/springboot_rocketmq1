package com.rocketmq1.rocketmq1.notemplate.yanchixiaofei;

/**
 * java类简单作用描述
 *
 * @ProjectName: rocketmq_lizi1
 * @Package: com.rocketmq_lizi.demo.notemplate.yanchixiaofei
 * @ClassName: ScheduledMessageConsumer
 * @Description: java类作用描述
 * @Author: zhangq
 * @CreateDate: 2020-12-17 18:33
 * @UpdateUser: zhangq
 * @UpdateDate: 2020-12-17 18:33
 * @UpdateRemark: The modified content
 * @Version: 1.0 *
 */

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

public class ScheduledMessageConsumer {
  public static void main(String[] args) throws Exception {
    // 实例化消费者
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("ExampleConsumer");
    consumer.setNamesrvAddr("192.168.56.200:9876");
    // 订阅Topics
    consumer.subscribe("TestTopic", "*");
    // 注册消息监听者
    consumer.registerMessageListener(new MessageListenerConcurrently() {
      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context) {
        for (MessageExt message : messages) {
          // Print approximate delay time period
          System.out.println("Receive message[msgId=" + message.getMsgId() + "] " + (System.currentTimeMillis() - message.getStoreTimestamp()) + "ms later");
        }
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      }
    });
    // 启动消费者
    consumer.start();
  }
}
