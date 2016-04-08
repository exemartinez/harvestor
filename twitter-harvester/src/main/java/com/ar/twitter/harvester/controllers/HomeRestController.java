package com.ar.twitter.harvester.controllers;

import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ar.twitter.harvester.FollowersHandler;

@RestController
@RequestMapping("/home")
public class HomeRestController {

	/**
	 * Login onto the system
	 * 
	 * @return
	 */
	@RequestMapping("/login")
	public @ResponseBody String login() {
		return "Entro";
	}

	/**
	 * Obtains a refresh over the users data. Returns a JSON with the full list of followers.
	 */
	@RequestMapping(value = "/refreshUserData/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> refreshUserData(@PathVariable String userId) {

		FollowersHandler fh = new FollowersHandler();
		ArrayList<Long> result = fh.updateUserFollowersFromTwitter(userId.trim()); // Obtains a refresh over the users data and stores it in MongoDB.
		
		//Setting the headers...this is for the converters to automatically trigger.
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Accept","application/json;charset=utf-8");
		
		return new ResponseEntity<>(result, httpHeaders, HttpStatus.CREATED);

	}
}
