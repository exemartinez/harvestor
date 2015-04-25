package com.ar.twitter.harvester;

import java.util.ArrayList;

import twitter4j.TwitterException;

/**
 * <font color="#000000">Main class for the harvester of followers in twitter.
 * </font>
 * 
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:20 p.m.
 */
public class Hasvestor {
	private static final String USERNAME = "";
	private static final String DESCRIPTION = "";
	public FollowersHandler m_FollowersHandler;
	public Account m_Account;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
		System.out
				.println("This is a console app for harvesting twitter followers...");
		Account cuenta = new Account();
		FollowersHandler fHandler = new FollowersHandler();
		ArrayList<Long> idcandidates = new ArrayList<Long>();

		// obtaining all users that follows the common user passed as argument.
		String specificUser = "florenciaypunto"; // TODO Esto despues hay que reemplazarlo por una variable.
		ArrayList<Long> possiblefollowersaccounts = cuenta
				.getFollowersIDbyUser(specificUser);
		ArrayList<Long> currentfolloersaccounts = cuenta
				.getFollowersIDbyUser("hernanemartinez");
		
		// Now we compare, how much of them already follows us.
		for (Long idpossible : possiblefollowersaccounts) {
			for (Long idfollowers : currentfolloersaccounts) {
				if (idpossible.longValue() != idfollowers.longValue()) {
					idcandidates.add(idpossible.longValue());
				}

			}
		}

		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect("myDB");
		
		System.out.println("Followers saved to account.");
		// we start to follow new candidates
		for (Long id : idcandidates) {
			try {
				cuenta.follow(id);
				mongodao.createUserAlreadyFollowedUsersDocument(id, USERNAME, DESCRIPTION); //TODO cambiar las constantes por los valores reales.
			} catch (TwitterException e) {

				e.printStackTrace();
				mongodao.closeConnections();
				break;
			}
		}
		mongodao.closeConnections();
	}

	private static void old_code() {
		System.out
				.println("This is a console app for harvesting twitter followers...");
		Account cuenta = new Account();
		FollowersHandler fHandler = new FollowersHandler();

		// Here we obtain the population to be addressed by the algorithm
		long[] friendsaccounts = cuenta.getFriendsID();
		long[] accounts = cuenta.getFollowersIDbyUserID(friendsaccounts);

		// Will select, by their own criteria the best "100"
		// accounts between the given accounts and by the set constraints.
		fHandler.followAccount(100, accounts);

		// cuenta.tweet("Estoy probando la API de twitter, desde Java. Que ZEN que es programar de madrugada...");
		// cuenta.getRateLimitStatus();

		// String[] users = {"hernanemartinez"};
		// cuenta.getFollowersIDbyUser(users);

		// cuenta.getFriendsID();

		// cuenta.getFriendsUsersList("hernanemartinez");
	}

}
