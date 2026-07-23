package com.diameter.controller;

import com.diameter.model.Customer;
import com.diameter.model.CustomerPackageRel;
import com.diameter.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.xml.bind.ValidationException;
import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
	private final CustomerService service;

	public CustomerController(CustomerService service) {
		this.service = service;
	}

	@PostMapping
	public ResponseEntity<Customer> createCustomer(@RequestBody @Valid Customer customer) throws ValidationException {
		logger.info("POST /customers - Creating new customer");
		Customer createdCustomer = service.createCustomer(customer);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
	}

	@GetMapping
	public ResponseEntity<?> getCustomers(@RequestParam(required=false) String id,

			@RequestParam(required=false)
			String name,

			@RequestParam(required=false)
			String userName,

			@RequestParam(defaultValue="0")
			int page,

			@RequestParam(defaultValue="10")
			int size
	){

		if(id != null || name != null || userName != null){
			return ResponseEntity.ok(service.getCustomers(id, name, userName));
		}

		return ResponseEntity.ok(
				service.getCustomers(null, null, null, page, size)
		);
	}

	@GetMapping("/packageRel")
	public ResponseEntity<List<CustomerPackageRel>> getCustomerPackageRel(@RequestParam(required = false) BigInteger custId,
			@RequestParam(required = false) BigInteger planId,
			@RequestParam(required = false) BigInteger custPackageId) {
		logger.info("GET /customers package-rel - Fetching with custId: {}, planId: {}, custPackageId: {}", custId, planId, custPackageId);
		List<CustomerPackageRel> customersRel = service.getCustomerPackageRel(custId, planId, custPackageId);
		return ResponseEntity.ok(customersRel);
	}
}