package com.rocketmq1.rocketmq1.notemplate.shunxuxiaofei;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;

/**
 * java类简单作用描述
 *
 * @ProjectName: rocketmq_lizi1
 * @Package: com.rocketmq_lizi.demo.test
 * @ClassName: shunxuxiaofei
 * @Description: java类作用描述
 * @Author: zhangq
 * @CreateDate: 2020-12-17 18:03
 * @UpdateUser: zhangq
 * @UpdateDate: 2020-12-17 18:03
 * @UpdateRemark: The modified content
 * @Version: 1.0 *
 */
public class shunxuxiaofei {

  public static void main(String[] args)
      throws InterruptedException, RemotingException, MQClientException, MQBrokerException {
    DefaultMQProducer producer = new DefaultMQProducer("please_rename_unique_group_name");

    producer.setNamesrvAddr("192.168.56.200:9876");

    try {
      producer.start();
    } catch (MQClientException e) {
      e.printStackTrace();
    }

    String[] tags = new String[]{"TagA", "TagC", "TagD"};
    shunxuxiaofei shunxuxiaofeis=new shunxuxiaofei();
    // 订单列表
    List<OrderStep> orderList = shunxuxiaofeis.buildOrders();

    Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    String dateStr = sdf.format(date);
    for (int i = 0; i < 10; i++) {
      // 加个时间前缀
      String body = dateStr + " Hello RocketMQ " + orderList.get(i);
      Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i, body.getBytes());

      SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
        @Override
        public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
          Long id = (Long) arg;  //根据订单id选择发送queue
          long index = id % mqs.size();
          return mqs.get((int) index);
        }
      }, orderList.get(i).getOrderId());//订单id

      System.out.println(String.format("SendResult status:%s, queueId:%d, body:%s",
          sendResult.getSendStatus(),
          sendResult.getMessageQueue().getQueueId(),
          body));
    }

//    producer.shutdown();
  }

  /**
   * 订单的步骤
   */
 class OrderStep {
    private long orderId;
    private String desc;

    public long getOrderId() {
      return orderId;
    }

    public void setOrderId(long orderId) {
      this.orderId = orderId;
    }

    public String getDesc() {
      return desc;
    }

    public void setDesc(String desc) {
      this.desc = desc;
    }

    @Override
    public String toString() {
      return "OrderStep{" +
          "orderId=" + orderId +
          ", desc='" + desc + '\'' +
          '}';
    }
  }

  /**
   * 生成模拟订单数据
   */
  public List<OrderStep> buildOrders() {
    List<OrderStep> orderList = new ArrayList<OrderStep>();

    OrderStep orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111039L);
    orderDemo.setDesc("创建");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111065L);
    orderDemo.setDesc("创建");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111039L);
    orderDemo.setDesc("付款");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103117235L);
    orderDemo.setDesc("创建");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111065L);
    orderDemo.setDesc("付款");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103117235L);
    orderDemo.setDesc("付款");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111065L);
    orderDemo.setDesc("完成");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111039L);
    orderDemo.setDesc("推送");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103117235L);
    orderDemo.setDesc("完成");
    orderList.add(orderDemo);

    orderDemo = new OrderStep();
    orderDemo.setOrderId(15103111039L);
    orderDemo.setDesc("完成");
    orderList.add(orderDemo);

    return orderList;
  }
}
