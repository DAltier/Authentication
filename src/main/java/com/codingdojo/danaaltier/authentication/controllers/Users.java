package com.codingdojo.danaaltier.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codingdojo.danaaltier.authentication.models.User;
import com.codingdojo.danaaltier.authentication.services.UserService;
import com.codingdojo.danaaltier.authentication.validators.UserValidator;

@Controller
public class Users {
	
	// Adding the User service and validator as dependencies
	private final UserService userService;
	private final UserValidator userValidator;
 

	// Constructor
	public Users(UserService userService, UserValidator userValidator) {
        this.userService = userService;
        this.userValidator = userValidator;
    }
 
	
	// GET route for registration
	@RequestMapping("/registration")
	public String registerForm(@ModelAttribute("user") User user) {
		return "registrationPage.jsp";
	}
	
	
	// POST route for registration
	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		userValidator.validate(user, result);
    	if(result.hasErrors()) {
    		return "registrationPage.jsp";
    	}
    	User u = userService.registerUser(user);
    	session.setAttribute("userId", u.getId());
    	return "redirect:/home";
	}
	
	
	// GET route for login
	@RequestMapping("/login")
	public String login() {
		return "loginPage.jsp";
	}
 

	// POST route for login
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
		boolean isAuthenticated = userService.authenticateUser(email, password);
    	if(isAuthenticated) {
    		User u = userService.findByEmail(email);
    		session.setAttribute("userId", u.getId());
    		return "redirect:/home";
    	}else {
    		model.addAttribute("error", "Invalid Credentials. Please try again.");
    		return "loginPage.jsp";
    	}
	}
 
	
	// GET route for home
	@RequestMapping("/home")
	public String home(HttpSession session, Model model) {
		// get user from session, save them in the model and return the home page
    	Long userId = (Long) session.getAttribute("userId");
    	if (userId != null) {
	    	User u = userService.findUserById(userId);
	    	model.addAttribute("user",u);
	    	return "homePage.jsp";
    	} else {
    		return "loginPage.jsp";
    	}
	}
	
	
	// GET route for logout
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
    	return "redirect:/login";
	}
}
