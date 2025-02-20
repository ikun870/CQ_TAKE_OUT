package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper  userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate startTime, LocalDate endTime) {
        //List存放时间段内的所有的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        while (!startTime.isAfter(endTime)) {
            //将每天的日期存放到List中
            dateList.add(startTime);
            startTime = startTime.plusDays(1);
        }
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            //查询每个data的营业额；即查询每天的已完成的订单总金额
            //开始时间,精确到当天的00:00:00
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //结束时间,精确到当天的23:59:59
            LocalDateTime finishTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap hashMap = new HashMap();
            hashMap.put("beginTime", beginTime);
            hashMap.put("finishTime", finishTime);
            hashMap.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(hashMap);
            if(turnover == null){
                turnover = 0.0;
            }
            turnoverList.add(turnover);
        }

        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(turnoverList, ","))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate startTime, LocalDate endTime) {
        //List存放时间段内的所有的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        while (!startTime.isAfter(endTime)) {
            //将每天的日期存放到List中
            dateList.add(startTime);
            startTime = startTime.plusDays(1);
        }
        List<Integer> userList_new = new ArrayList<>();
        List<Integer> userList_total = new ArrayList<>();
        for(LocalDate date : dateList){
            //查询每个data的用户数；即查询每天的新增用户数
            //开始时间,精确到当天的00:00:00
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //结束时间,精确到当天的23:59:59
            LocalDateTime finishTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap hashMap = new HashMap();
            hashMap.put("beginTime", beginTime);

            Integer userCount_total = userMapper.countByMap(hashMap);
//            if(userCount_total == null){
//                userCount_total = 0;
//            }
            userList_total.add(userCount_total);

            hashMap.put("finishTime", finishTime);
            Integer userCount_new = userMapper.countByMap(hashMap);
//            if(userCount_new == null){
//                userCount_new = 0;
//            }
            userList_new.add(userCount_new);
        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(userList_new, ","))
                .totalUserList(StringUtils.join(userList_total, ","))
                .build();
    }

    /**
     * 订单统计
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    @Override
    public OrderReportVO getOrdersStatistics(LocalDate startTime, LocalDate endTime) {
        //List存放时间段内的所有的每天的日期
        List<LocalDate> dateList = new ArrayList<>();

        while (!startTime.isAfter(endTime)) {
            //将每天的日期存放到List中
            dateList.add(startTime);
            startTime = startTime.plusDays(1);
        }
        //查询每天的有效订单数和订单总数
        List<Integer> orderList_valid = new ArrayList<>();
        List<Integer> orderList_total = new ArrayList<>();
        Integer total = 0;
        Integer valid = 0;
        for(LocalDate date : dateList){
            //查询每个data的订单数；即查询每天的有效订单数
            //开始时间,精确到当天的00:00:00
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            //结束时间,精确到当天的23:59:59
            LocalDateTime finishTime = LocalDateTime.of(date, LocalTime.MAX);
            HashMap hashMap = new HashMap();
            hashMap.put("beginTime", beginTime);
            hashMap.put("finishTime", finishTime);

            //查询每个data的订单数；即查询每天的订单总数
            Integer orderCount_total = orderMapper.countByMap(hashMap);

            orderList_total.add(orderCount_total);

            hashMap.put("status", Orders.COMPLETED);
            Integer orderCount_valid = orderMapper.countByMap(hashMap);

            orderList_valid.add(orderCount_valid);


        }

        Integer all = orderList_total.stream().reduce(Integer::sum).get();
        Integer all_valid = orderList_valid.stream().reduce(Integer::sum).get();

        //计算订单完成率
        Double rate = 0.0;
        if(all != 0){
            rate = (double)all_valid/all;
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderList_total, ","))
                .validOrderCountList(StringUtils.join(orderList_valid, ","))
                .totalOrderCount(all)
                .validOrderCount(all_valid)
                .orderCompletionRate(rate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        //结束时间,精确到当天的23:59:59
        LocalDateTime finishTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop10(beginTime, finishTime);

        //将销量Top10商品的名称和销量分别存放到List中并用逗号分隔拼成字符串
        //两种方法：1.使用stream流 2.使用StringUtils.join
        String nameList = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.joining(","));

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");
        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    /**
     * 导出报表
     * @param response
     */
    @Override
    public void export(HttpServletResponse response) {
        //1.查询数据--最近30天的营业数据
        //开始时间,精确到当天的00:00:00
        LocalDateTime begin = LocalDateTime.of(LocalDate.now().minusDays(30), LocalTime.MIN);
        //结束时间,精确到当天的23:59:59
        LocalDateTime end = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);
        //2.写入Excel
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(resourceAsStream);
            XSSFSheet sheet = excel.getSheetAt(0);
            sheet.getRow(1).getCell(1).setCellValue("时间："+begin.toLocalDate()+"-"+end.toLocalDate());

            sheet.getRow(3).getCell(2).setCellValue(businessDataVO.getTurnover());
            sheet.getRow(3).getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            sheet.getRow(3).getCell(6).setCellValue(businessDataVO.getNewUsers());
            sheet.getRow(4).getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            sheet.getRow(4).getCell(4).setCellValue(businessDataVO.getUnitPrice());

            for(int i = 0; i < 30; i++){
                LocalDate time = begin.plusDays(i).toLocalDate();
                //查询每一天的营业数据
                BusinessDataVO businessData = workspaceService.getBusinessData(LocalDateTime.of(time, LocalTime.MIN), LocalDateTime.of(time, LocalTime.MAX));
                sheet.getRow(7+i).getCell(1).setCellValue(time.toString());
                sheet.getRow(7+i).getCell(2).setCellValue(businessData.getTurnover());
                sheet.getRow(7+i).getCell(3).setCellValue(businessData.getValidOrderCount());
                sheet.getRow(7+i).getCell(4).setCellValue(businessData.getOrderCompletionRate());
                sheet.getRow(7+i).getCell(5).setCellValue(businessData.getUnitPrice());
                sheet.getRow(7+i).getCell(6).setCellValue(businessData.getNewUsers());
            }
            //3.通过输出流，下载到浏览器
            ServletOutputStream outputStream = response.getOutputStream();
            excel.write(outputStream);

            //4.关闭资源
            excel.close();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
