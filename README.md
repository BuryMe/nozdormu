**RocketMQ Extended Services —— Nozdormu**

Nozdormu is an extension service to serve Rocket MQ (open source version) , mainly for the enrichment of the message delay level , support for arbitrary time message delay . The project is based on Spring boot, using Mysql as a persistence solution , the use of Redission to achieve a delayed queue and distributed lock . nozdormu supports multi-node deployment .

**Dependence is as follows**

|  dependence | version |
| :------------ | :------------ |
| spring-boot-starter-parent | 2.3.7.RELEASE  |
|  rocketMq | 2.2.0 |
|  redisson | 3.20.1 |

**how to use**

Although this is a Spring boost web project, when it is started, it will start an mq listener. At present, it receives message data that needs to be delayed through mq, and of course it also supports http data interaction.

This is the mq configuration for receiving delay parameters.

    receiveMq:
      consumer:
        group: receive_group
        topic: receive_topic

This is the received data format.


    import lombok.Data;
    
    /**
     * @author seven up
     * @date 2023年04月11日 11:42 AM
     */
    @Data
    public class MsgCommand {
    
        private String uniqueKey;
    
        private String pushBody;
    
        private String pushTopic;
    
        private String pushTag;
    
        /**
         * Arbitrary extension time
         */
        private Long expectPushTime;
    
    }


Support to expand the delay queue by modifying the configuration parameters.

    queue:
      size: 4

Analog push is also supported

    mock:
      push: 1

Directly git clone to the local, and change the address of mysql and redis to your own service address.
