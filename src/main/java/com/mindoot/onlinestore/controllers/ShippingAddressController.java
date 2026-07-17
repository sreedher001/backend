package com.mindoot.onlinestore.controllers;

import com.mindoot.onlinestore.dto.ShippingAddressDTO;
import com.mindoot.onlinestore.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
public class ShippingAddressController {

    @Autowired
    private ShippingAddressService shippingAddressService;

    @PostMapping("/create")
    public ResponseEntity<ShippingAddressDTO> createAddress(@PathVariable("userId") Long userId,
                                                            @RequestBody ShippingAddressDTO dto) {
        return ResponseEntity.ok(shippingAddressService.createAddress(userId, dto));
    }

    @GetMapping("/get-all-shipping-address")
    public ResponseEntity<List<ShippingAddressDTO>> getAllAddresses(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(shippingAddressService.getAllAddresses(userId));
    }

    @GetMapping("/shipping-address/{addressId}")
    public ResponseEntity<ShippingAddressDTO> getAddress(@PathVariable("userId") Long userId,
                                                         @PathVariable("addressId") Long addressId) {
        return ResponseEntity.ok(shippingAddressService.getAddressById(userId, addressId));
    }

    @PostMapping("/update-shipping-address/{addressId}")
    public ResponseEntity<ShippingAddressDTO> updateAddress(@PathVariable("userId") Long userId,
                                                            @PathVariable("addressId") Long addressId,
                                                            @RequestBody ShippingAddressDTO dto) {
        return ResponseEntity.ok(shippingAddressService.updateAddress(userId, addressId, dto));
    }

    @DeleteMapping("/remove-shipping-address/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable("userId") Long userId,
                                              @PathVariable("addressId") Long addressId) {
        shippingAddressService.deleteAddress(userId, addressId);
        return ResponseEntity.noContent().build();
    }
}

