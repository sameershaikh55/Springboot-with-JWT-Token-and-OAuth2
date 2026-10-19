/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.OrderNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.Order;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.service.UserService;

/**
 * @author tdoy
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class UserController {

	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(){
		
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty())
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{id}")
	public ResponseEntity<Object> getUser(@PathVariable Long id) throws UserNotFoundException, OrderNotFoundException{
		
		User user = userService.findUser(id);
		
		return new ResponseEntity<>(user, HttpStatus.OK);	
		
	}
	
	// TODO - createUser (@Mappings, URI=/users, and method)
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User nuser = userService.saveUser(user);
        return new ResponseEntity<>(nuser, HttpStatus.CREATED);
    }
	

	// TODO - updateUser (@Mappings, URI, and method)
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id,
                                           @RequestBody User user)
            throws UserNotFoundException {
        User updated = userService.updateUser(user, id);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
	
	// TODO - deleteUser (@Mappings, URI, and method)
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id)
            throws UserNotFoundException {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


	// TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
    @GetMapping("/users/{id}/orders")
    public ResponseEntity<Object> getUserOrders(@PathVariable Long id)
            throws UserNotFoundException {
        Set<Order> orders = userService.getUserOrders(id);
        if (orders.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

	
	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
    @GetMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Object> getUserOrder(@PathVariable Long uid,
                                               @PathVariable Long oid)
            throws UserNotFoundException, OrderNotFoundException {
        Order order = userService.getUserOrder(uid, oid);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


	// TODO - deleteUserOrder (@Mappings, URI, and method)
    @DeleteMapping("/users/{uid}/orders/{oid}")
    public ResponseEntity<Void> deleteUserOrder(@PathVariable Long uid,
                                                @PathVariable Long oid)
            throws UserNotFoundException, OrderNotFoundException {
        userService.deleteOrderForUser(uid, oid);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

	
	// TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
    @PostMapping("/users/{uid}/orders")
    public ResponseEntity<User> createUserOrder(@PathVariable Long uid,
    		@RequestBody Order order) throws UserNotFoundException, 
    		OrderNotFoundException {
        User user = userService.createOrdersForUser(uid, order);

        // HATEOAS: self + link to this order
        Link selfLink = linkTo(methodOn(UserController.class).getUser(uid)).withSelfRel();
        Link ordersLink = linkTo(methodOn(UserController.class).getUserOrders(uid)).withRel("orders");

        user.add(selfLink);
        user.add(ordersLink);

        if (order.getId() != null) {
            Link orderLink = linkTo(methodOn(UserController.class)
                    .getUserOrder(uid, order.getId())).withRel("order");
            user.add(orderLink);
        }

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }


	
}
