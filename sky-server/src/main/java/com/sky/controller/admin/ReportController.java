package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 数据报表相关接口
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "数据报表接口")
@Slf4j
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    /**
     * 营业额统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public Result<TurnoverReportVO> turnoverReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd")  LocalDate end) {
        log.info("营业额统计：startTime={}, endTime={}", begin, end);
        return Result.success(reportService.getTurnoverStatistics(begin, end));
    }

    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    /**
     * 用户统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public Result<UserReportVO > userStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计：startTime={}, endTime={}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    /**
     * 订单统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public Result<OrderReportVO> ordersStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计：startTime={}, endTime={}", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    @GetMapping("/top10")
    @ApiOperation("销量Top10商品统计")
    /**
     * 销量Top10商品统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public Result<SalesTop10ReportVO> top10(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("销量Top10商品统计：startTime={}, endTime={}", begin, end);
        return Result.success(reportService.getTop10(begin, end));
    }

}
