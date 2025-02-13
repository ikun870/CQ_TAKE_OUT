package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未支付订单
     */
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder() {
        log.info("处理超时订单:{}", LocalDateTime.now());
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);//往前推15分钟
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, localDateTime);
        if(ordersList!=null && !ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时，自动取消！");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder(){
        log.info("处理派送中订单:{}", LocalDateTime.now());
        //每天凌晨1点触发
        List<Orders> ordersList = orderMapper
                .getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusHours(-1));
        if(ordersList!=null && !ordersList.isEmpty()){
            for(Orders orders:ordersList){
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            }
        }
    }
}
