package com.ben.spring.controller;

import com.ben.spring.dao.AuthorRepository;
import com.ben.spring.dao.UserRepository;
import com.ben.spring.entity.Authorities;
import com.ben.spring.entity.Customer;
import com.ben.spring.entity.User;
import com.ben.spring.models.AuthenRequest;
import com.ben.spring.models.AuthenResponse;
import com.ben.spring.security.CustomUserDetailsService;
import com.ben.spring.security.JwtUtil;
import com.ben.spring.service.AuthorService;
import com.ben.spring.service.CustomerService;
import com.ben.spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@RestController
public class MainController {

    @Autowired
    CustomerService customerService;

    @Autowired
    UserService userService;

    @Autowired
    AuthorService authorService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/currentUser")
    @ResponseBody
    public String currentUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal.getName();
    }

    @GetMapping("/customers")
    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }

    @PostMapping("/customers")
    public Customer createCustomer(@RequestBody Customer customer) {
        customer.setId(0);
        customerService.save(customer);
        return customer;
    }

    @PutMapping("/customers")
    public Customer updateCustomer(@RequestBody Customer customer) {
        customerService.save(customer);
        return customer;
    }

    @DeleteMapping("/customers/{id}")
    public String deleteCustomer(@PathVariable int id) {
        Customer customer = customerService.findById(id);
        if (customer == null) {
            throw new RuntimeException("Employee is not found...");
        }

        customerService.deleteById(id);
        return "Deleted!!!";
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenRequest authenRequest) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenRequest.getUsername(), authenRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return ResponseEntity.ok(new AuthenResponse(jwt));
    }

}
