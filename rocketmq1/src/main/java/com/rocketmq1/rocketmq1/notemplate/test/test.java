package com.rocketmq1.rocketmq1.notemplate.test;

import java.util.List;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * java类简单作用描述
 *
 * @ProjectName: rocketmq_lizi1
 * @Package: com.rocketmq_lizi.demo.test
 * @ClassName: test
 * @Description: java类作用描述
 * @Author: zhangq
 * @CreateDate: 2020-12-16 11:05
 * @UpdateUser: zhangq
 * @UpdateDate: 2020-12-16 11:05
 * @UpdateRemark: The modified content
 * @Version: 1.0 *
 */
@RestController
public class test {

  @RequestMapping("/")
  public String get() {
    return "12";
  }


  public static void main(String[] args) throws MQClientException {
    // 实例化消费者
    DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("please_rename_unique_group_name");

    // 设置NameServer的地址
    consumer.setNamesrvAddr("192.168.56.200:9876");

    // 订阅一个或者多个Topic，以及Tag来过滤需要消费的消息
    consumer.subscribe("TopicTest", "*");
    // 注册回调实现类来处理从broker拉取回来的消息
    consumer.registerMessageListener(new MessageListenerConcurrently() {
      @Override
      public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,
          ConsumeConcurrentlyContext context) {
        System.out.printf("%s Receive New Messages: %s %n", Thread.currentThread().getName(), msgs);
        System.out.println("消息：" + new String(msgs.get(0).getBody()));
//        System.out.println("消费消息了11111111111111111");
        // 标记该消息已经被成功消费
        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
      }
    });
    // 启动消费者实例
    consumer.start();
    System.out.printf("Consumer Started.%n");
  }

}
