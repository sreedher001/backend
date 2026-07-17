package com.mindoot.onlinestore.service.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.mindoot.onlinestore.dto.ShippingAddressDTO;
import com.mindoot.onlinestore.exception.ApplicationException;
import com.mindoot.onlinestore.model.ShippingAddress;
import com.mindoot.onlinestore.model.User;
import com.mindoot.onlinestore.repository.ShippingAddressRepository;
import com.mindoot.onlinestore.repository.UserRepository;
import com.mindoot.onlinestore.service.ShippingAddressService;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

	@Autowired
	private ShippingAddressRepository shippingAddressRepository;

	@Autowired
	private UserRepository userRepository;

	@Override
	public ShippingAddressDTO createAddress(Long userId, ShippingAddressDTO dto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));

		ShippingAddress address = ShippingAddress.builder().name(user.getUsername())
				.country(dto.getCountry()).state(dto.getState())
				.pinCode(dto.getPinCode()).address(dto.getAddress()).city(dto.getCity())
				.phoneNumber(dto.getPhoneNumber()).isDefault(dto.isDefault()).user(user).build();
				
		return toDTO(shippingAddressRepository.save(address));
	}

	@Override
	public List<ShippingAddressDTO> getAllAddresses(Long userId) {
		return shippingAddressRepository.findByUserId(userId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	@Override
	public ShippingAddressDTO getAddressById(Long userId, Long addressId) {
		ShippingAddress address = shippingAddressRepository.findByIdAndUserId(addressId, userId)
				.orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
		return toDTO(address);
	}

	@Override
	public ShippingAddressDTO updateAddress(Long userId, Long addressId, ShippingAddressDTO dto) {
		ShippingAddress address = shippingAddressRepository.findByIdAndUserId(addressId, userId)
				.orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
address.setName(dto.getName());
		address.setCountry(dto.getCountry());
		address.setState(dto.getState());
		address.setPinCode(dto.getPinCode());
		address.setAddress(dto.getAddress());
		address.setCity(dto.getCity());
		address.setPhoneNumber(dto.getPhoneNumber());
		address.setDefault(dto.isDefault());

		return toDTO(shippingAddressRepository.save(address));
	}

	@Override
	public void deleteAddress(Long userId, Long addressId) {
		ShippingAddress address = shippingAddressRepository.findByIdAndUserId(addressId, userId)
				.orElseThrow(() -> new ApplicationException("User not found",HttpStatus.NOT_FOUND));
		shippingAddressRepository.delete(address);
	}

	private ShippingAddressDTO toDTO(ShippingAddress address) {
		return ShippingAddressDTO.builder().id(address.getId()).name(address.getName()).country(address.getCountry()).state(address.getState())
				.pinCode(address.getPinCode()).address(address.getAddress()).city(address.getCity())
				.phoneNumber(address.getPhoneNumber()).isDefault(address.isDefault()).build();
	}

}
