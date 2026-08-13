package com.cloudmeal.user.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloudmeal.common.exception.BusinessException;
import com.cloudmeal.common.security.CurrentUser;
import com.cloudmeal.user.dto.AddressSaveRequest;
import com.cloudmeal.user.entity.AddressBook;
import com.cloudmeal.user.mapper.AddressBookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {
    private final AddressBookMapper mapper;
    public AddressService(AddressBookMapper mapper) { this.mapper = mapper; }

    public List<AddressBook> list() {
        return mapper.selectList(Wrappers.<AddressBook>lambdaQuery()
                .eq(AddressBook::getUserId, CurrentUser.id())
                .orderByDesc(AddressBook::getIsDefault).orderByDesc(AddressBook::getUpdatedTime));
    }

    @Transactional
    public AddressBook create(AddressSaveRequest request) {
        Long userId = CurrentUser.id();
        if (request.isDefault() || mapper.selectCount(Wrappers.<AddressBook>lambdaQuery().eq(AddressBook::getUserId, userId)) == 0) {
            clearDefault(userId);
        }
        AddressBook address = apply(new AddressBook(), request);
        address.setUserId(userId);
        if (request.isDefault() || mapper.selectCount(Wrappers.<AddressBook>lambdaQuery().eq(AddressBook::getUserId, userId)) == 0) address.setIsDefault(1);
        mapper.insert(address);
        return address;
    }

    @Transactional
    public AddressBook update(Long id, AddressSaveRequest request) {
        AddressBook address = owned(id);
        if (request.isDefault()) clearDefault(CurrentUser.id());
        apply(address, request);
        mapper.updateById(address);
        return address;
    }

    @Transactional
    public void remove(Long id) {
        AddressBook address = owned(id);
        mapper.deleteById(id);
        if (address.getIsDefault() == 1) {
            AddressBook next = mapper.selectOne(Wrappers.<AddressBook>lambdaQuery()
                    .eq(AddressBook::getUserId, CurrentUser.id()).last("LIMIT 1"));
            if (next != null) { next.setIsDefault(1); mapper.updateById(next); }
        }
    }

    @Transactional
    public void setDefault(Long id) {
        AddressBook address = owned(id);
        clearDefault(CurrentUser.id());
        address.setIsDefault(1);
        mapper.updateById(address);
    }

    private AddressBook owned(Long id) {
        AddressBook address = mapper.selectOne(Wrappers.<AddressBook>lambdaQuery()
                .eq(AddressBook::getId, id).eq(AddressBook::getUserId, CurrentUser.id()));
        if (address == null) throw new BusinessException("ADDRESS_NOT_FOUND", "收货地址不存在");
        return address;
    }
    private void clearDefault(Long userId) {
        AddressBook patch = new AddressBook(); patch.setIsDefault(0);
        mapper.update(patch, Wrappers.<AddressBook>lambdaQuery().eq(AddressBook::getUserId, userId));
    }
    private AddressBook apply(AddressBook address, AddressSaveRequest request) {
        address.setConsignee(request.consignee()); address.setPhone(request.phone());
        address.setProvince(request.province()); address.setCity(request.city());
        address.setDistrict(request.district()); address.setDetail(request.detail());
        address.setIsDefault(request.isDefault() ? 1 : 0);
        return address;
    }
}
