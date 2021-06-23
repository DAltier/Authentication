package com.codingdojo.danaaltier.authentication.services;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.codingdojo.danaaltier.authentication.models.User;
import com.codingdojo.danaaltier.authentication.repositories.UserRepository;

@Service
public class UserService {
	
	// Adding the User repository as dependency
    private final UserRepository userRepository;
    
    
    // Constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    
    // Register user and hash their password
    public User registerUser(User user) {
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        return userRepository.save(user);
    }
    
    
    // Find user by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    
    // Find user by id
    public User findUserById(Long id) {
    	Optional<User> u = userRepository.findById(id);
    	
    	if(u.isPresent()) {
            return u.get();
    	} else {
    	    return null;
    	}
    }
    
    
    // Authenticate user
    public boolean authenticateUser(String email, String password) {
        // first find the user by email
        User user = userRepository.findByEmail(email);
        // if we can't find it by email, return false
        if(user == null) {
            return false;
        } else {
            // if the passwords match, return true, else, return false
            if(BCrypt.checkpw(password, user.getPassword())) {
                return true;
            } else {
                return false;
            }
        }
    }
}
