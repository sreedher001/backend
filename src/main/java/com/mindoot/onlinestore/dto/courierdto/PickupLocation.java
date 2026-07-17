package com.mindoot.onlinestore.dto.courierdto;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PickupLocation {

	private List<String> pickupLocationList;
}
