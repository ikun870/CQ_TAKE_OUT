package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {

    /**
     * 营业额统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    TurnoverReportVO getTurnoverStatistics(LocalDate startTime, LocalDate endTime);

    /**
     * 用户统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);


    /**
     * 订单统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
     * 销量Top10商品统计
     * @param begin 开始时间
     * @param end 结束时间
     * @return
     */
    SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end);
}
