package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api("员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/status/{status}")
    @ApiOperation("员工状态变更")
    //查询时Result带泛型，其它可以不带泛型。
    public Result startOrStop(@PathVariable("status") Integer status,Long id){
        log.info("传过来的参数status={},id={}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("员工分页查询")
    public Result<PageResult> listByPage(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页查询，参数为:{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageService(employeePageQueryDTO);
        return Result.success(pageResult);
    }



    /**
     * 员工保存
     * @param employeeDTO
     * @return
     */
    @ApiOperation("员工保存")
    @PostMapping
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        System.out.println("员工保存的"+Thread.currentThread().getId());
        log.info("employeeDTO的值{}",employeeDTO);
        employeeService.save(employeeDTO);
        //todo
        return Result.success();
    }

    /**
     * 根据id获得传过来的员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id获取员工信息")
    public Result<Employee> returnEmplyeeMsg(@PathVariable("id") Integer id){
        log.info("传过来的参数id={},id");
        Employee employee = employeeService.getEmployeeId(id);
        return Result.success(employee);
    }


    /**
     * 数据修改后保存
     * @param employeeDTO
     * @return
     */
    @ApiOperation("员工修改后保存")
    @PutMapping
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("employeeDTO的值{}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }


    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);
        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        //public static final String EMP_ID = "empId";
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        //雇员的id通过service层的/login方法从数据库(逻辑：数据库获得比对)
        //当放入claims 放入token ---》可以放到线程容器中，其它地方则能取出
        BaseContext.setCurrentId(employee.getId());

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工注销")
    public Result<String> logout() {
        return Result.success();
    }

}
