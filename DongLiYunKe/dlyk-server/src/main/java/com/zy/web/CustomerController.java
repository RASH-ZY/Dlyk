package com.zy.web;

import com.alibaba.excel.EasyExcel;
import com.github.pagehelper.PageInfo;
import com.zy.constant.Constants;
import com.zy.model.TCustomer;
import com.zy.query.CustomerQuery;
import com.zy.result.CustomerExcel;
import com.zy.result.R;
import com.zy.service.CustomerService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class CustomerController {

    @Resource
    private CustomerService customerService;

    /**
     * 线索转换客户
     * @param customerQuery
     * @param token
     * @return
     */
    @PostMapping(value = "/api/clue/customer")
    public R convertCustomer(@RequestBody CustomerQuery customerQuery, @RequestHeader(value = Constants.TOKEN_NAME) String token) {
        customerQuery.setToken(token);
        Boolean flag = customerService.convertCustomer(customerQuery);
        return flag ? R.OK() : R.FAIL();
    }

    /**
     * 获取客户分页列表
     * @param current
     * @return
     */
    @GetMapping(value = "/api/customers")
    public R getCustomerByPage(@RequestParam(value = "current", required = false) Integer current) {
        if(current == null) {
            current = 1;
        }
        PageInfo<TCustomer> pageInfo = customerService.getCustomerByPage(current);
        return R.OK(pageInfo);
    }

    @GetMapping(value = "/api/exportExcel")
    public void exportExcel(HttpServletResponse response, @RequestParam(value = "ids", required = false) String ids) throws IOException {
        //要想让浏览器弹出下载框，后端要设置相应的响应头信息
        response.setContentType("application/octet-stream"); //数据以二进制流的形式响应到前端
        response.setCharacterEncoding("utf-8");
        //"Content-disposition"：是一个 HTTP 响应头，用于指示浏览器如何处理响应体中的内容( 下载附件 or 直接展示 )
        //"attachment"：是Content-disposition的一个值，表示响应体中的内容以附件的形式下载，而不是直接展示在前端页面
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(Constants.EXCEL_FILE_NAME + System.currentTimeMillis(), "utf-8") + ".xlsx");

        //2、后端查询数据库的数据，把数据写入Excel，然后把Excel以IO流的方式输出到前端浏览器（我们来实现）
        //ids不为空则用逗号分隔拼接为字符串数组; ids为空则new一个ArrayList<>()
        List<String> idList = StringUtils.hasText(ids) ? Arrays.asList(ids.split(",")) : new ArrayList<>();
//        System.out.println(idList);
        List<CustomerExcel> customerExcelList = customerService.getCustomerByExcel(idList);
        EasyExcel.write(response.getOutputStream(), CustomerExcel.class) //模板类CustomerExcel 数据库与Excel映射关系
                .sheet()
                .doWrite(customerExcelList); //数据库查询到的数据
    }

}
