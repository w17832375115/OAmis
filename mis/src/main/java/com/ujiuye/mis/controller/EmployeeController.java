package com.ujiuye.mis.controller;

import com.github.pagehelper.PageInfo;
import com.ujiuye.mis.mapper.EmpRoleMapper;
import com.ujiuye.mis.pojp.EmpRole;
import com.ujiuye.mis.pojp.EmpRoleExample;
import com.ujiuye.mis.pojp.Employee;
import com.ujiuye.mis.qo.EmployeeQuery;
import com.ujiuye.mis.service.EmployeeService;
import com.ujiuye.mis.vo.R;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@RestController
@RequestMapping("emp")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping("{pageNum}/{pageSize}")
    public R findByPageQuary(@PathVariable int pageNum,
                             @PathVariable int pageSize,
                             EmployeeQuery employeeQuery){
        List<Employee> list = employeeService.findByPageQuery(pageNum,pageSize,employeeQuery);
        return CollectionUtils.isEmpty(list)?R.error().msg("查询失败"):R.ok().data("pageInfo",new PageInfo<>(list));
    }
    @PostMapping("save/{roleid}")
    public R save(@RequestBody Employee employee , @PathVariable int roleid){
        boolean ret = employeeService.save(employee,roleid);

        return ret?R.ok(): R.error().msg("保存失败");
    }

    @GetMapping("{eid}")
    public R findById(@PathVariable int eid){
        Employee employee = employeeService.findById(eid);
        return ObjectUtils.isEmpty(employee)?R.error().msg("修改失败"):R.ok().data("item",employee);
    }

    @PostMapping("update/{roleid}")
    public R update(@RequestBody Employee employee , @PathVariable int roleid){
        boolean ret = employeeService.update(employee,roleid);
        return ret?R.ok(): R.error().msg("保存失败");
    }

    @PostMapping("{ids}")
    public R delete(@PathVariable String ids){
        boolean ret = employeeService.delete(ids);
        return ret?R.ok(): R.error().msg("删除失败");
    }

    @GetMapping("download")
    public ResponseEntity<byte[]> download() throws IOException {

        List<Employee> list =  employeeService.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("员工信息");

        Row title = sheet.createRow(0);
        title.createCell(0).setCellValue("编号");
        title.createCell(1).setCellValue("姓名");
        title.createCell(2).setCellValue("性别");
        title.createCell(3).setCellValue("年龄");
        title.createCell(4).setCellValue("电话");
        title.createCell(5).setCellValue("入职时间");
        title.createCell(6).setCellValue("身份证号");
        title.createCell(7).setCellValue("用户名");
        title.createCell(8).setCellValue("备注");
        title.createCell(9).setCellValue("所属部门");

        for (Employee employee : list) {
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(employee.getEid());
            row.createCell(1).setCellValue(employee.getEname());
            row.createCell(2).setCellValue(employee.getEsex());
            row.createCell(3).setCellValue(employee.getEage());
            row.createCell(4).setCellValue(employee.getTelephone());
            row.createCell(5).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(employee.getHiredate()));
            row.createCell(6).setCellValue(employee.getPnum());
            row.createCell(7).setCellValue(employee.getUsername());
            row.createCell(8).setCellValue(employee.getRemark());
            row.createCell(9).setCellValue(employee.getDept().getDname());
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentDispositionFormData("attachment",new String("员工信息.xlsx".getBytes("utf-8"),"ISO-8859-1"));
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);


        return new ResponseEntity<>(bytes,httpHeaders, HttpStatus.OK);
    }

    @PostMapping("login")
    public R login(String username, String password, HttpSession session){
        if(StringUtils.isEmpty(username)){
            return R.error().msg("用户名不饿能为空");
        }
        if(StringUtils.isEmpty(password)){
            return R.error().msg("密码不能为空");
        }
        Employee employee = employeeService.login(username,password);
        if(ObjectUtils.isEmpty(employee)){
           return R.error().msg("用户名或密码错误,请检查后再试");
        }
        session.setAttribute("active",employee.getEid());
        return R.ok();
    }

    @PostMapping("logout")
    public R logout(HttpSession session){
        session.removeAttribute("active");
        return R.ok();
    }
}
