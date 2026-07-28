package com.cloudmeal.user.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.api.ApiResponse;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.user.entity.AddressBook;
import com.cloudmeal.user.mapper.AddressBookMapper;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/user/addresses")
public class AddressController {
    private final AddressBookMapper mapper;
    public AddressController(AddressBookMapper mapper) { this.mapper = mapper; }
    @GetMapping public ApiResponse<List<AddressBook>> list() {
        return ApiResponse.success(mapper.selectList(Wrappers.<AddressBook>lambdaQuery()
                .eq(AddressBook::getUserId, CurrentUser.id()).orderByDesc(AddressBook::getIsDefault)));
    }
}
