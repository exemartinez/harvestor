package com.ar.twitter.harvester;

import java.util.ArrayList;

import org.bson.Document;

import twitter4j.TwitterException;

import com.mongodb.client.MongoCollection;

import static com.mongodb.client.model.Filters.*;

/**
 * <font color="#000000">Main class for the harvester of followers in twitter.
 * </font>
 * 
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:20 p.m.
 */
public class Hasvestor {
	private static final String DATABASENAME = "myDB";
	private static final String USUARIOACOPIAR = "florenciaypunto";
	private static final String MAINUSER = "hernanemartinez";
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
		
		//********************************************
		//1st step: harvest the data from twitter API.
		//********************************************
		ArrayList<Long> currentfollowersaccounts = updateMainUserFollowersFromTwitter();
		
		
		/*
		//Variables and accounts
		Account cuenta = new Account();
		FollowersHandler fHandler = new FollowersHandler();
		ArrayList<Long> idcandidates = new ArrayList<Long>();

		// obtaining all users that follows the common user passed as argument.
		String specificUser = USUARIOACOPIAR; // TODO Esto despues hay que reemplazarlo por una variable.
		ArrayList<Long> possiblefollowersaccounts = cuenta
				.getFollowersIDbyUser(specificUser);
		
		System.out.println("Followers that follow the main account...saved to the Database.");
		
		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		MongoCollection<Document> usuarioseguido = mongodao.getCollection("Usuarios_Seguidores_Usuario");
		ArrayList<Document> userstofollow = new ArrayList<Document>();
		Document record;
		
		// we start to follow new candidates
		for (Long id : idcandidates) {
			try {
				
				record = mongodao.createAlreadyFollowedUserForMainUser(MAINUSER,id, USERNAME, DESCRIPTION); //TODO cambiar las constantes por los valores reales.
				userstofollow.add(record);
				
			} catch (TwitterException e) {

				e.printStackTrace();
				mongodao.closeConnections();
				break;
			}
		}
		
		//impacting the database and closing the connection
		mongodao.insertDocuments("Usuarios_Seguidores_Usuario", userstofollow);
		mongodao.closeConnections();
		
		
		//*************************************************
		//2nd step: query the data and start the following.
		//*************************************************
		
		// Now we compare, how much of them already follows us.
		for (Long idpossible : possiblefollowersaccounts) {
			for (Long idfollowers : currentfollowersaccounts) {
				if (idpossible.longValue() != idfollowers.longValue()) {
					idcandidates.add(idpossible.longValue());
				}

			}
		}

		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		MongoCollection<Document> usuarioseguido = mongodao.getCollection("Usuarios_Seguidos_Usuario_Principal");
		ArrayList<Document> userstofollow = new ArrayList<Document>();
		Document record;
		
		System.out.println("Followers saved to account.");
		// we start to follow new candidates
		for (Long id : idcandidates) {
			try {
				cuenta.follow(id);
				
				record = mongodao.createAlreadyFollowedUserForMainUser(MAINUSER,id, USERNAME, DESCRIPTION); //TODO cambiar las constantes por los valores reales.
				userstofollow.add(record);
				
			} catch (TwitterException e) {

				e.printStackTrace();
				mongodao.closeConnections();
				break;
			}
		}
		
		//impacting the database and closing the connection
		mongodao.insertDocuments("Usuarios_Seguidos_Usuario_Principal", userstofollow);
		mongodao.closeConnections();
		
		*/
	}

	/**
	 * @return
	 */
	public static ArrayList<Long> updateMainUserFollowersFromTwitter() {
		Account cuenta = new Account();
		FollowersHandler fHandler = new FollowersHandler();
		ArrayList<Long> idcandidates = new ArrayList<Long>();
		
		//obtaining all the users of the logged user from twitter
		ArrayList<Long> currentfollowersaccounts = cuenta
				.getFollowersIDbyUser(MAINUSER);
		

		System.out.println("Followers that follow the main account...saved to the Database.");
		
		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		
		ArrayList<Document> newusersbeingfollowed = new ArrayList<Document>();
		Document record;
		Document previouslystored;
		
		//first check which are new followers
		MongoCollection<Document> usuarioseguido_collection = mongodao.getCollection("Usuarios_Seguidores_Usuario_Principal");
		
		
		// we start to follow new candidates
		for (Long id : currentfollowersaccounts) {
			try {
				
				//if the collection exists, we do not insert anything
				if (usuarioseguido_collection!=null){
					previouslystored = usuarioseguido_collection.find(eq("twitter_id", id)).first();
					
					if (previouslystored!=null)
						continue;
				} else {
					previouslystored = null;
				}
				
				//If the record doesn't exists, we insert a new one.
				if (previouslystored == null) {	
					record = mongodao.createAlreadyFollowedUserForMainUser(MAINUSER,id, USERNAME, DESCRIPTION); //TODO cambiar las constantes por los valores reales.
					newusersbeingfollowed.add(record);
				}
				
			} catch (Exception e) {

				e.printStackTrace();
				mongodao.closeConnections();
				break;
			}
		}
		
		//impacting the database and closing the connection
		mongodao.insertDocuments("Usuarios_Seguidores_Usuario_Principal", newusersbeingfollowed);
		mongodao.closeConnections();
		return currentfollowersaccounts;
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
