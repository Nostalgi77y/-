package com.cloudmeal.config;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.auth.entity.Employee;
import com.cloudmeal.auth.mapper.EmployeeMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements ApplicationRunner {
    private final EmployeeMapper mapper;
    private final PasswordEncoder encoder;
    public DefaultAdminInitializer(EmployeeMapper mapper, PasswordEncoder encoder) {
        this.mapper = mapper; this.encoder = encoder;
    }
    @Override public void run(ApplicationArguments args) {
        if (mapper.selectCount(Wrappers.<Employee>lambdaQuery().eq(Employee::getUsername, "admin")) == 0) {
            Employee employee = new Employee();
            employee.setUsername("admin");
            employee.setPassword(encoder.encode("Admin@123456"));
            employee.setName("系统管理员");
            employee.setRole("ADMIN");
            employee.setStatus(1);
            mapper.insert(employee);
        }
    }
}
