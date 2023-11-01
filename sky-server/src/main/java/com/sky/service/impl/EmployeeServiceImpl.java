package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        String Md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!Md5Password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() == null){ //employeeDTO中ID不存在--》新增员工功能
            System.out.println("员工保存的服务端id"+Thread.currentThread().getId());
            Employee employee = new Employee();

            //把EmployeeDTO的值遍历至been(只遍历了一部分)
            BeanUtils.copyProperties(employeeDTO,employee);

            //设置账号状态，默认正常状态，1表示正常 0表示锁定
            employee.setStatus(StatusConstant.ENABLE);//这样写有利于可变

            //设置默认密码(用常量赋值)
            String defaultPassword = DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes());
            employee.setPassword(defaultPassword);

            //设置当前时间以及修改时间
            employee.setUpdateTime(LocalDateTime.now());
            employee.setCreateTime(LocalDateTime.now());

            //设置当前记录创建人id和修改人id
            System.out.println("修改者id"+BaseContext.getCurrentId());
            employee.setCreateUser(BaseContext.getCurrentId());
            employee.setUpdateUser(BaseContext.getCurrentId());

            //将employee传入Mapper层
            employeeMapper.insert(employee);
        }else {//员工修改后后添加
            Employee employee = new Employee();
            //把EmployeeDTO的值遍历至been(只遍历了一部分)
            BeanUtils.copyProperties(employeeDTO,employee);

            //设置修改人和修改时间
            employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(BaseContext.getCurrentId());

            employeeMapper.updateEmployeeAll(employee);


        }


    }

    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageService(EmployeePageQueryDTO employeePageQueryDTO) {

        //查询前，需要调用 PageHelper.startPage()--物理分页，这样后面sql语句中会自动带limit;
        PageHelper.startPage( employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        //写个dao层的查询方法用Page接收com.gittub.pagehelper(规定好的，用了分页插件后用该类接收)
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        //从数据库获得数据后封装的page中获得total,以及records
        long total = page.getTotal();
        List<Employee> pageData = page.getResult();
        return new PageResult(total,pageData);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        //update employee set status=xx where id = xx;
        //可以扩大范围，让语句变得动态化一点(用Employee交流数据库)
        Employee employee = new Employee();
        employee.setStatus(status);
        employee.setId(id);
        employeeMapper.updateEmployeeAll(employee);
    }

    @Override
    public Employee getEmployeeId(Integer id) {
        Employee employee= employeeMapper.getEmployeeId(id);
        return employee;
    }


}
