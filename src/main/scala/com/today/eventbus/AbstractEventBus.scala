package com.today.eventbus

import java.lang.reflect.Field
import javax.sql.DataSource

import com.github.dapeng.org.apache.thrift.TException
import com.today.eventbus.serializer.KafkaMessageProcessor
import org.slf4j.LoggerFactory
import scala.beans.BeanProperty
import wangzx.scala_commons.sql._

/**
  *
  * 描述: 消息总线 基类
  *
  * @author hz.lei
  * @since 2018年02月28日 下午2:46
  */
trait AbstractEventBus {

  private val logger = LoggerFactory.getLogger(classOf[AbstractEventBus])

  @BeanProperty
  var dataSource: DataSource = _


  @throws[TException]
  def fireEvent(event: Any): Unit = {
    dispatchEvent(event)
    persistenceEvent(event)
  }

  /**
    * 业务系统处理事件分发逻辑
    *
    * @param event
    */
  protected def dispatchEvent(event: Any): Unit

  /**
    * 持久化 event 消息 to database
    *
    * @param event
    * @throws
    */
  @throws[TException]
  private def persistenceEvent(event: Any): Unit = {
    logger.info("prepare to save event message")
    val processor = new KafkaMessageProcessor[Any]
    val bytes: Array[Byte] = processor.encodeMessage(event)
    val eventType = event.getClass.getName
    // fetch id
    val id = getMsgId(event)
    val executeSql = sql"INSERT INTO  dp_common_event set id=${id}, event_type=${eventType}, event_binary=${bytes}"
    dataSource.executeUpdate(executeSql)

    logger.info("save message successful ")
  }

  /**
    * Todo 临时解决方案
    * 反射拿到消息第一个id参数，作为存储数据库的 唯一 id
    *
    * @param event
    * @return
    */
  private def getMsgId(event: Any): Long = {
    try {
      val field: Field = event.getClass.getDeclaredField("id")
      field.setAccessible(true)
      field.get(event).asInstanceOf[Long]
    } catch {
      case e: Exception =>
        logger.error("获取消息id失败，请检查事件是否带有唯一id，以及字段名是否为id")
        throw e
    }
  }

  /**
    * scala object 基于Spring的初始化方法
    *
    * @return
    */
  def getInstance: this.type
}
