package com.ar.twitter.harvester;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;

import org.bson.Document;

import twitter4j.TwitterException;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * <font color="#000000">Main class for the harvester of followers in twitter.
 * </font>
 * 
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:20 p.m.
 */
public class Hasvestor {
	private static final String COL_FIELD_TWITTER_DESCRIPTION = "twitter_description";
	private static final String COL_FIELD_TWITTER_USERNAME = "twitter_username";
	private static final String COL_FIELD_TWITTER_ID = "twitter_id";
	private static final String COL_USUARIOS_SEGUIDORES_USUARIO = "Usuarios_Seguidores_Usuario_Principal";
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
		//updateUserFollowersFromTwitter(USUARIOACOPIAR); //uncomment this method for obtaining a refresh over the users data.
		//updateUserFollowerInformationInDatabase(MAINUSER); <--TODO: aun resta probar que funcione el update en la base de datos; lo dejamos para el final porque lo importante es tener los IDs y no la info de los usuarios en si mismos.
		
		//TODO: Realizar el seguimiento sistematico y controlado de todos los seguidores del usuario objetivo.
		
		//1st. Get ALL the users of your target user to follow that doesn't has a flag of like: "do not follow this".
		
		//Variables and accounts
		Account cuenta = new Account();
		FollowersHandler fHandler = new FollowersHandler();
		ArrayList<Document> idcandidates = null;

		// obtaining all users that follows the common user passed as argument.
		
		System.out.println("Followers that follow the main account from the Database.");
		
		// here we query the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		MongoCollection<Document> usuarioseguido = mongodao.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		BasicDBObject query = new BasicDBObject("Twitter_Dont_Follow_This", new BasicDBObject("$ne", true));
		
		FindIterable<Document> usuariospotencialesseguidores = usuarioseguido.find(query);
		
		
		//2nd. compare the selected users with the current account followers, mark the target user followers that appear there with a flag of "do not follow this"
		
		//3rd. Get the N users from the target user account that do not have a flag of "do not follow this"; N is the maximum expected number of following by day for your account according to Twitter policies.
		//4th. Follow the N subset users with a random time between every follow 
		//5th. Set those users (after succesfull following) as "do not follow this" in the target user.
		//6th. Update a collection of "Recently_followed_users" where the succesfully followed users are logged (this is for further management of the followed back that didn't work.
		
		mongodao.closeConnections();
		
		//TODO: Realizar el "unfollow" sistematico y controlado de todos los seguidos que no nos devolvieron el seguimiento.
		
		
		
				
		/*
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
	 * Gets the users data from the database, then gets all the users that has no data from twitter and sets the data for them.
	 * @param mainuser
	 */
	private static void updateUserFollowerInformationInDatabase(String mainuser) {
		ArrayList<Document> userfollowerdata = getStoredFollowersFromUser(mainuser);
		
		Account cuenta = new Account();
		ArrayList<Document> newuserdata = new ArrayList<Document>();
		
		twitter4j.User usr = null;
		
		for (Document usuarioincompleto: userfollowerdata){
			
			if((usuarioincompleto.getString(COL_FIELD_TWITTER_USERNAME).equals(""))||(usuarioincompleto.getString(COL_FIELD_TWITTER_USERNAME) == null)){
				//obtaining the fresh data from Twitter.
				usr = cuenta.getUserData(usuarioincompleto.getLong(COL_FIELD_TWITTER_ID));
				
				//TODO: aca hay que asignar la maxima cantidad de datos posibles; extraer info de twitter es caro en terminos de tiempo asi que hay que aprovecharlo.
				usuarioincompleto.put(COL_FIELD_TWITTER_USERNAME, usr.getName());
				usuarioincompleto.put(COL_FIELD_TWITTER_DESCRIPTION, usr.getDescription());
				
				//completing the update arraylist for later database update.
				newuserdata.add(usuarioincompleto);
	
				//if the rate limit status is weak, we stop for the seconds requested.
				cuenta.manageExceededRateLimit("/users/show/:id");
			}

		}
		
		//updating the new user and description info into the database.
		updateUserFollowerDatabase(newuserdata);
	}
	
	/**
	 * This helps us to update the user data of a given user of the collection COL_USUARIOS_SEGUIDORES_USUARIO
	 * @users contains the list of users to update.
	 */
	private static void updateUserFollowerDatabase(ArrayList<Document> users) {
		
		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		MongoCollection<Document> usuarioseguido = mongodao.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);

		//The update action being carried on.
		for (Document record: users){
			usuarioseguido.updateOne(eq(COL_FIELD_TWITTER_ID, record.getLong(COL_FIELD_TWITTER_ID).longValue()), record);	
		}

		//Closing the connection
		mongodao.closeConnections();
	}

	/**
	 * Returns an Arraylist of documents with the users data collections
	 * @param usuario
	 */
	private static ArrayList<Document> getStoredFollowersFromUser(String usuario) {

		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		
		MongoCursor<Document> previouslystored;
		
		//first check which are the followers
		MongoCollection<Document> usuarioseguido_collection = mongodao.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);	
		previouslystored = usuarioseguido_collection.find(eq("twitter_main_user", usuario)).iterator();
					
	
		//transforming the cursors into an arraylist
		ArrayList<Document> arrayfollowers = mongodao.transformsMongoCursorToArrayList(previouslystored);
		
		mongodao.closeConnections();
		
		return arrayfollowers;
		
	}



	/**
	 * This allows to extract the followers of a given user and set it into a MongoDB Database.
	 * For later usage.
	 * 
	 * @param usuario 
	 * @return
	 */
	public static ArrayList<Long> updateUserFollowersFromTwitter(String usuario) {
		Account cuenta = new Account();
		
		//obtaining all the users of the logged user from twitter
		ArrayList<Long> currentfollowersaccounts = cuenta
				.getFollowersIDbyUser(usuario);
		

		System.out.println("Followers that follow the main account...saved to the Database.");
		
		// here we update the followers for the given user in the database.
		MongoDAO mongodao = new MongoDAO();
		mongodao.connect(DATABASENAME);
		
		ArrayList<Document> newusersbeingfollowed = new ArrayList<Document>();
		Document record;
		Document previouslystored;
		
		//first check which are new followers
		MongoCollection<Document> usuarioseguido_collection = mongodao.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);	
		
		// we start to follow new candidates
		for (Long id : currentfollowersaccounts) {
			try {
				
				//if the collection exists, we do not insert anything
				if (usuarioseguido_collection!=null){
					previouslystored = usuarioseguido_collection.find(eq(COL_FIELD_TWITTER_ID, id)).first();
					
					if (previouslystored!=null)
						continue;
				} else {
					previouslystored = null;
				}
				
				//If the record doesn't exists, we insert a new one.
				if (previouslystored == null) {	
					record = mongodao.createAlreadyFollowedUserForMainUser(usuario,id, USERNAME, DESCRIPTION); //TODO cambiar las constantes por los valores reales.
					newusersbeingfollowed.add(record);
				}
				
			} catch (Exception e) {

				e.printStackTrace();
				mongodao.closeConnections();
				return null;
			}
		}
		
		//impacting the database and closing the connection
		mongodao.insertDocuments(COL_USUARIOS_SEGUIDORES_USUARIO, newusersbeingfollowed);
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
