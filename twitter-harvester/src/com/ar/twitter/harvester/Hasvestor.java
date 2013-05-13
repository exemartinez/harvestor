package com.ar.twitter.harvester;

import java.util.ArrayList;

import twitter4j.IDs;

/**
 * <font color="#000000">Main class for the harvester of followers in twitter.
 * </font>
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:20 p.m.
 */
public class Hasvestor {
	public FollowersHandler m_FollowersHandler;
	public Account m_Account;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("This is a console app for harvesting twitter followers...");
		Account cuenta= new Account();
		FollowersHandler fHandler = new FollowersHandler();

		//Here we obtain the population to be addressed by the algorithm
		long[] friendsaccounts= cuenta.getFriendsID();
		long[] accounts = cuenta.getFollowersIDbyUserID(friendsaccounts);
		
		//Will select, by their own criteria the best "100"
		//accounts between the given accounts and by the set constraints.
		fHandler.followAccount(100, accounts);
		
		//cuenta.tweet("Estoy probando la API de twitter, desde Java. Que ZEN que es programar de madrugada...");
		//cuenta.getRateLimitStatus();
		
		//String[] users = {"hernanemartinez"};
		//cuenta.getFollowersIDbyUser(users);
		
		//cuenta.getFriendsID();
		
		//cuenta.getFriendsUsersList("hernanemartinez");
	}

}
