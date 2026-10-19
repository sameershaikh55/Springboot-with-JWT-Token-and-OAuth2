/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
	@PreAuthorize("hasAuthority('ADMIN')")
	public ResponseEntity<Object> getUsers(){
		
		List<User> users = userService.findAllUsers();
		
		if(users.isEmpty())
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<>(users, HttpStatus.OK);
	}
	
	@GetMapping(value = "/users/{id}")
	@PreAuthorize("hasAuthority('ADMIN') or #id == authentication.details.userid")
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
        return new ResponseEntity<>(HttpStatus.OK);   
    }


	// TODO - getUserOrders (@Mappings, URI=/users/{id}/orders, and method)
    @GetMapping("/users/{id}/orders")
    @PreAuthorize("hasAuthority('ADMIN') or #uid == authentication.details.userid")
    public ResponseEntity<Object> getUserOrders(@PathVariable Long id)
            throws UserNotFoundException {
        Set<Order> orders = userService.getUserOrders(id);
        if (orders.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

	
	// TODO - getUserOrder (@Mappings, URI=/users/{uid}/orders/{oid}, and method)
    @GetMapping("/users/{uid}/orders/{oid}")
    @PreAuthorize("hasAuthority('ADMIN') or #uid == authentication.details.userid")
    public ResponseEntity<Object> getUserOrder(@PathVariable Long uid,
                                               @PathVariable Long oid)
            throws UserNotFoundException, OrderNotFoundException {
        Order order = userService.getUserOrder(uid, oid);
        return new ResponseEntity<>(order, HttpStatus.OK);
    }


	// TODO - deleteUserOrder (@Mappings, URI, and method)
    @DeleteMapping("/users/{uid}/orders/{oid}")
    @PreAuthorize("hasAuthority('ADMIN') or #uid == authentication.details.userid")
    public ResponseEntity<Void> deleteUserOrder(@PathVariable Long uid,
                                                @PathVariable Long oid)
            throws UserNotFoundException, OrderNotFoundException {
        userService.deleteOrderForUser(uid, oid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

	
 // TODO - createUserOrder (@Mappings, URI, and method) + HATEOAS links
    @PostMapping("/users/{uid}/orders")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('USER') and #uid == authentication.details.userid)")
    public ResponseEntity<Object> createUserOrder(@PathVariable Long uid, @RequestBody Order order)
            throws UserNotFoundException, OrderNotFoundException {

        // 1. Create the order for the user
        User user = userService.createOrdersForUser(uid, order);

        // 2. Add HATEOAS links to the orders
        for (Order o : user.getOrders()) {
            if (!o.hasLinks()) {
                o.add(linkTo(methodOn(UserController.class).getUserOrder(uid, o.getId())).withSelfRel());
                o.add(linkTo(methodOn(UserController.class).getUser(uid)).withRel("user"));
            }
        }

        // 3. Return the orders collection (JSON Array) so the test can parse the 'isbns' list
        return new ResponseEntity<>(user.getOrders(), HttpStatus.CREATED);
    }


	
}
