package com.sky.controller.user;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> sumbit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        /**
         * TODO
         * 这里没有办法完成支付，所以直接返回一个空的 OrderPaymentVO,并完成支付成功的逻辑
         * 这里也只是为了测试
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
         */
        orderService.paySuccess(ordersPaymentDTO.getOrderNumber());
        OrderPaymentVO orderPaymentVO = new OrderPaymentVO();
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 客户催单
     * @param orderId
     */
    //TODO 待测
    @PutMapping("/reminder/{orderId}")
    @ApiOperation("客户催单")
    public Result reminder(@PathVariable("orderId") Long orderId) {
        orderService.reminder(orderId);
        return Result.success();
    }
}
