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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	 * Obtains a refresh over the users data. Returns a JSON with the full list of followers USERID (not even the user name...).
	 */
	@RequestMapping(value = "/updateFromTwitter/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> updateFromTwitter(@PathVariable String userId) {
		ResponseEntity<?> entity = null;
		FollowersHandler fh = new FollowersHandler();
		ArrayList<Long> result = fh.updateUserFollowersFromTwitter(userId.trim()); // Obtains a refresh over the users data and stores it in MongoDB.
		
		//Setting the headers...
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Accept","application/json;charset=utf-8");
		
		//Doing the conversion on the fly...
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			entity = new ResponseEntity<>(mapper.writeValueAsString(result), httpHeaders, HttpStatus.CREATED);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
	
	/**
	 * Updates the user's followers ID with their full data.
	 */
	//TODO: aun resta probar que funcione el update en la base de datos; lo dejamos para el final porque lo importante es tener los IDs y no la info de los usuarios en si mismos.
	@RequestMapping(value = "/updateFollowersInfo/{userId}", method = RequestMethod.GET)
	public ResponseEntity<?> updateFollowersInfo(@PathVariable String userId) {
		ResponseEntity<?> entity = null;
		FollowersHandler fh = new FollowersHandler();
		
		//Setting the headers...
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Accept","application/json;charset=utf-8");
		
		try {
			
			fh.updateUserFollowerInformationInDatabase(userId.trim()); // TODO: This should return somekind of status...
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.CREATED);
			
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
	
	/**
	 * Given the target user, make the connected user to follow the amount of target user's followers that is passed as a path variable.
	 * This is the main functionality: to automate the copy of certain user followers by the connected user; throw the strategy of following them in the first place.
	 */
	@RequestMapping(value = "/followUsersOf/{userIdToCopy}/maxNumberOfUsersToFollow/{amountOfIterations}/{action}", method = RequestMethod.GET)
	public ResponseEntity<?> updateFollowersInfo(@PathVariable String userIdToCopy,@PathVariable Long amountOfIterations, @PathVariable String action) {
		ResponseEntity<?> entity = null;
		FollowersHandler fh = new FollowersHandler();
		HttpHeaders httpHeaders = new HttpHeaders();
		
		//Setting the headers...
		httpHeaders.add("Accept","application/json;charset=utf-8");
		
		try {
			
			if (action.equals("updateFollowersFirst")) {
				fh.updateUsersThatAlreadyFollowsBack(userIdToCopy.trim()); //updates the followers of the main user in order to indentify those that already followe you; so you don't follow them back...THEY ALREADY FOLLOW you, so the "following" is unnecesary.
			}
			
			//TODO: averiguar de que es ese error de "not enough parameters passed to query y arreglarlo".
			//TODO: This should return something; like an "OK", or a list, or something describing status.
			fh.automaticFollowingOfUsersFollowers(userIdToCopy, amountOfIterations); // follow the users that follows the parametrized usuarioCopiar, follows the amount of users passed as parameter.
		
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
	
	/**
	 * Given the target user, make the connected user to follow the amount of target user's followers that is passed as a path variable.
	 * This is the main functionality: to automate the copy of certain user followers by the connected user; throw the strategy of following them in the first place.
	 */
	@RequestMapping(value = "/unfollowNonFollowersOf/{userId}/maxNumberOfUsersToFollow/{amountOfIterations}/{action}", method = RequestMethod.GET)
	public ResponseEntity<?> unfollowNonFollowersOf(@PathVariable String userId,@PathVariable Long amountOfIterations, @PathVariable String action) {
		ResponseEntity<?> entity = null;
		FollowersHandler fh = new FollowersHandler();
		HttpHeaders httpHeaders = new HttpHeaders();
		
		//Setting the headers...
		httpHeaders.add("Accept","application/json;charset=utf-8");
		
		try {
			
			if (action.equals("updateFollowersFirst")) {
				fh.updateUsersThatAlreadyFollowsBack(userId.trim()); //updates the followers of the main user
			}
			
			//Systematically unfollow the users that were followed but that didn't followed back; WATCH IT: this has to be done at least with a day of delay from the following for being effective.
			fh.systematicallyUnfollowNonFollowersThatWereFollowed(userId, amountOfIterations);
		
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
			entity = new ResponseEntity<>("", httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return entity;
	}
	
	//TODO:following from the commandline & REST
	//TODO:unfollowing from the commandline & REST
	//TODO: Armar test de unidad.
	//TODO: Refactorizar MONGODB a JONGODB.
	//TODO: Agrega el refactoring de los limites de FollowUP con SpringAOP (requiere R&D).
	//TODO: Armar interfaz web
	//TODO: Generar los codigos de unfollow inteligente (con ratios y evaliacion de seguidores seguidos por cada uno)
	//TODO: generar seguimiento distribuido por las proximas 24hs
	//TODO: generar segumiento distribuido por las proximas 24hs.
	
}
