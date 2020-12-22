package com.rocketmq1.rocketmq1;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Rocketmq1ApplicationTests {



	//1、Producer端发送同步消息
//  这种可靠性同步地发送方式使用的比较广泛，比如：重要的消息通知，短信通知。
	@Test
	void contextLoads()
			throws UnsupportedEncodingException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
		// 实例化消息生产者Producer
		DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
		// 设置NameServer的地址
		producer.setNamesrvAddr("192.168.56.200:9876");
		// 启动Producer实例
		producer.start();
		for (int i = 0; i < 1; i++) {
			// 创建消息，并指定Topic，Tag和消息体
			Message msg = new Message("TopicTest" /* Topic */,
					"TagA" /* Tag */,
					("Hello RocketMQ 张强测试123 " + i)
							.getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
			);
			// 发送消息到一个Broker
			SendResult sendResult = producer.send(msg);
			// 通过sendResult返回消息是否成功送达
			System.out.printf("%s%n", sendResult);
		}
		// 如果不再发送消息，关闭Producer实例。
		producer.shutdown();
	}

	/*2、发送异步消息
    异步消息通常用在对响应时间敏感的业务场景，即发送端不能容忍长时间地等待Broker的响应。*/
	@Test
	void yibu()
			throws MQClientException, InterruptedException, UnsupportedEncodingException, RemotingException {
		// 实例化消息生产者Producer
		DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
		// 设置NameServer的地址
		producer.setNamesrvAddr("192.168.56.200:9876");
		// 启动Producer实例
		producer.start();
		producer.setRetryTimesWhenSendAsyncFailed(0);

		int messageCount = 10;
		// 根据消息数量实例化倒计时计算器
		final CountDownLatch2 countDownLatch = new CountDownLatch2(messageCount);
		for (int i = 0; i < messageCount; i++) {
			final int index = i;
			// 创建消息，并指定Topic，Tag和消息体
			Message msg = new Message("TopicTest",
					"TagA",
					"OrderID188",
					"Hello world".getBytes(RemotingHelper.DEFAULT_CHARSET));
			// SendCallback接收异步返回结果的回调
			producer.send(msg, new SendCallback() {
				@Override
				public void onSuccess(SendResult sendResult) {
					System.out.printf("%-10d OK %s %n", index,
							sendResult.getMsgId());
				}

				@Override
				public void onException(Throwable e) {
					System.out.printf("%-10d Exception %s %n", index, e);
					e.printStackTrace();
				}
			});
		}
		// 等待5s
		countDownLatch.await(5, TimeUnit.SECONDS);
		// 如果不再发送消息，关闭Producer实例。
		producer.shutdown();
	}
	/*
  3、单向发送消息
  这种方式主要用在不特别关心发送结果的场景，例如日志发送*/
	@Test
	void simple()
			throws MQClientException, UnsupportedEncodingException, RemotingException, InterruptedException {
		// 实例化消息生产者Producer
		DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");
		// 设置NameServer的地址
		producer.setNamesrvAddr("192.168.56.200:9876");
		// 启动Producer实例
		producer.start();
		for (int i = 0; i < 10; i++) {
			// 创建消息，并指定Topic，Tag和消息体
			Message msg = new Message("TopicTest" /* Topic */,
					"TagA" /* Tag */,
					("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
			);
			// 发送单向消息，没有任何返回结果
			producer.sendOneway(msg);

		}
		// 如果不再发送消息，关闭Producer实例。
		producer.shutdown();
	}


	public static void main(java.lang.String[] args) throws MQClientException {
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
