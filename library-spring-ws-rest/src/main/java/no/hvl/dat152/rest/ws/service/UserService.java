/**
 * 
 */
package no.hvl.dat152.rest.ws.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import no.hvl.dat152.rest.ws.model.User;
import no.hvl.dat152.rest.ws.repository.UserRepository;

/**
 * @author tdoy
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	
	public List<User> findAllUsers(){
		
		List<User> allUsers = (List<User>) userRepository.findAll();
		
		return allUsers;
	}
	
	public User findUser(Long userid) throws UserNotFoundException {
		
		User user = userRepository.findById(userid)
				.orElseThrow(()-> new UserNotFoundException("User with id: "+userid+" not found"));
		
		return user;
	}
	
	
	// TODO public User saveUser(User user)
	public User saveUser(User user) {
		
		return null;
	}
	
	// TODO public void deleteUser(Long id) throws UserNotFoundException 
	
	// TODO public User updateUser(User user, Long id)
	
	// TODO public Set<Order> getUserOrders(Long userid) 
	
	// TODO public Order getUserOrder(Long userid, Long oid)
	
	// TODO public void deleteOrderForUser(Long userid, Long oid)
	
	// TODO public User createOrdersForUser(Long userid, Order order)
}
