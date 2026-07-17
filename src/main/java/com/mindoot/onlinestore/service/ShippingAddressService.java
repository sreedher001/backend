package com.mindoot.onlinestore.service;

import com.mindoot.onlinestore.dto.ShippingAddressDTO;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public interface ShippingAddressService {
	ShippingAddressDTO createAddress(Long userId, ShippingAddressDTO dto);

	List<ShippingAddressDTO> getAllAddresses(Long userId);

	ShippingAddressDTO getAddressById(Long userId, Long addressId);

	ShippingAddressDTO updateAddress(Long userId, Long addressId, ShippingAddressDTO dto);

	void deleteAddress(Long userId, Long addressId);
}
