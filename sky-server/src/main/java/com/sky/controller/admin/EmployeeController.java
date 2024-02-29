package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
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
@Api(tags="员工相关接口")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value =  "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌 有什么作用？
        //在客户端进行后续请求时，可以将JWT令牌发送给服务器，
        // 服务器可以解析JWT令牌并验证其中包含的用户信息，从而进行用户身份验证，确保请求的合法性
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);
        // 这里为什么用builder
        // 如果对象有很多属性，且需要保证对象的不可变性和线程安全性，建议使用建造者模式；
        // 如果对象比较简单或者需要在创建后修改属性，直接使用new关键字即可
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
    public Result<String> logout() {
        return Result.success();
    }
    // save  RequestBody json 格式
    @PostMapping()
    @ApiOperation("新增员工")
    public  Result save(@RequestBody  EmployeeDTO employeeDTO){
        log.info("新增员工,{}",employeeDTO);
        employeeService.save(employeeDTO);
        return null ;
    }
    @GetMapping("/page")
    @ApiOperation("分页查询")
    public  Result<PageResult>page(EmployeePageQueryDTO employeePageQueryDTO){
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success( pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用或者禁用员工账号")
    public  Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);//后绪步骤定义
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("查询员工")
    public  Result getById(@PathVariable Long id){
        Employee employee = employeeService.getbyId(id);


        return Result.success(employee);
    }

    @PutMapping //TODO 为什么用putmapping
    @ApiOperation("编辑员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();

    }




}
