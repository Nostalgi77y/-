package com.cloudmeal.auth.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.auth.dto.LoginRequest;
import com.cloudmeal.auth.entity.Employee;
import com.cloudmeal.auth.mapper.EmployeeMapper;
import com.cloudmeal.auth.security.JwtService;
import com.cloudmeal.auth.vo.LoginResponse;
import com.cloudmeal.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final EmployeeMapper employeeMapper;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(EmployeeMapper employeeMapper, PasswordEncoder encoder, JwtService jwtService) {
        this.employeeMapper = employeeMapper;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Employee employee = employeeMapper.selectOne(Wrappers.<Employee>lambdaQuery()
                .eq(Employee::getUsername, request.username()).eq(Employee::getStatus, 1));
        if (employee == null || !encoder.matches(request.password(), employee.getPassword())) {
            throw new BusinessException("AUTH_FAILED", "用户名或密码错误");
        }
        String token = jwtService.create(employee.getId(), employee.getUsername(), employee.getRole());
        return new LoginResponse(token, employee.getId(), employee.getName(), employee.getRole());
    }
}
