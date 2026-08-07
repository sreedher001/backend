package com.mindoot.onlinestore.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindoot.onlinestore.enums.PurchaseType;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users", uniqueConstraints = { @UniqueConstraint(columnNames = "phoneNumber"),
		@UniqueConstraint(columnNames = "email") })
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Size(max = 20)
	private String username;

	@Column(unique = true, nullable = true)
	@Size(max = 50)
	@Email
	private String email;

	@Column(nullable = true)
	@Size(max = 15)
	private String phoneNumber;

	@Size(max = 20)
	private String country;

	@Size(max = 30)
	private String state;

	@Size(max = 10)
	private String pinCode;

	private String address;

	private String city;

	private LocalDate createdOn;

	private LocalDate updatedOn;

	@Size(max = 120)
	@JsonIgnore
	private String password;

	@Column(nullable = false)
	private boolean isEmailVerified = false;

	private boolean isPhoneNumberVerified = false;

	@JsonIgnore
	private String verificationToken;

	@JsonIgnore
	private String otp;

	@JsonIgnore
	private LocalDateTime otpExpiry;

	private boolean enabled = true;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private PurchaseType preferredPurchaseType = PurchaseType.RETAIL;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<WishlistItem> wishlistItems = new HashSet<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ShippingAddress> shippingAddresses = new HashSet<>();

	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}

	public User(String username, String email, String phoneNumber, String country, String state, String pinCode,
			String address, String password) {
		this.username = username;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.country = country;
		this.state = state;
		this.pinCode = pinCode;
		this.address = address;
		this.password = password;
	}

	public void setRoles(Set<Role> roles) {
		this.roles.clear();
		this.roles.addAll(roles);
	}
}
