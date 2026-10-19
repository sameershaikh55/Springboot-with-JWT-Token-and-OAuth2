/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.service.OrderService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
@PreAuthorize("hasAuthority('ADMIN')")
public class OrderController {
	@Autowired
	private OrderService orderService;

	// TODO - getAllBorrowOrders (@Mappings, URI=/orders, and method) + filter by expiry and paginate
	@GetMapping("/orders")
	public ResponseEntity<List<Order>> getAllBorrowOrders(
			@RequestParam(required = false) LocalDate expiry,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Pageable pageable = PageRequest.of(page, size);
		List<Order> orders;

		if (expiry != null) {
			orders = orderService.findByExpiryDate(expiry, pageable);
		} else {
			orders = orderService.findAllOrders();
			// Handle pagination slicing manually if findAllOrders() returns an unpaged list
			int start = (int) pageable.getOffset();
			int end = Math.min((start + pageable.getPageSize()), orders.size());
			if (start > orders.size()) {
				orders = new ArrayList<>();
			} else {
				orders = orders.subList(start, end);
			}
		}

		if (orders.isEmpty()) {
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<>(orders, HttpStatus.OK);
	}
	
	// TODO - getBorrowOrder (@Mappings, URI=/orders/{id}, and method)
	@GetMapping("/orders/{id}")
	public ResponseEntity<Order> getBorrowOrder(@PathVariable Long id) throws OrderNotFoundException {
		Order order = orderService.findOrder(id);

		// HATEOAS self link as expected by TestOrder
		if (!order.hasLinks()) {
			Link selfLink = linkTo(methodOn(OrderController.class).getBorrowOrder(id)).withSelfRel();
			order.add(selfLink);
		}

		return new ResponseEntity<>(order, HttpStatus.OK);
	}
	
	// TODO - updateOrder (@Mappings, URI=/orders/{id}, and method)
	@PutMapping("/orders/{id}")
	public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) throws OrderNotFoundException {
		Order updatedOrder = orderService.updateOrder(order, id);
		return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
	}
	
	// TODO - deleteBookOrder (@Mappings, URI=/orders/{id}, and method)
	@DeleteMapping("/orders/{id}")
	public ResponseEntity<Void> deleteBookOrder(@PathVariable Long id) throws OrderNotFoundException {
		orderService.deleteOrder(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
