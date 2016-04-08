package com.ar.twitter.harvester;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import twitter4j.TwitterException;
import twitter4j.User;

/**
 * <font color="#000000">This will be the one that will process the data
 * obtained from the list of twitter accounts obtained.</font> <font
 * color="#000000"> </font><font color="#000000">-Propose the accounts to
 * follow.</font> <font color="#000000">-Set timer for every account.</font>
 * <font color="#000000">-Keep record of past followed accounts </font> <font
 * color="#000000">-Unfollow accounts that do not follow back in 24hs. </font>
 * 
 * @author hmartinez
 * @version 1.0
 * @updated 02-may-2013 10:49:19 p.m.
 */
public class FollowersHandler {

	public static final int DELAY_FOR_TWITTER_API = 5;
	public static final String COL_FIELD_TWITTER_DESCRIPTION = "twitter_description";
	public static final String COL_FIELD_TWITTER_USERNAME = "twitter_username";
	public static final String COL_FIELD_TWITTER_ID = "twitter_id";
	public static final String COL_USUARIOS_SEGUIDORES_USUARIO = "Usuarios_Seguidores_Usuario_Principal";
	public static final String DATABASENAME = "myDB";
	public static final String USUARIOACOPIAR = "florenciaypunto";
	public static final String MAINUSER = "hernanemartinez";
	public static final String USERNAME = "";
	public static final String DESCRIPTION = "";
	public static final long MAX_NUMBER_OF_USERS_FOLLOWED = 199;
	public static final long MAX_NUMBER_OF_USERS_UNFOLLOWED = 399;
	public Account m_Account;


	public void systematicallyUnfollowNonFollowersThatWereFollowed(String mainuser, Long maxNumberOfUsersUnfollowed) {
		//obtain the updated list of users that follows you, update the database, so afterward you could review all the users in the collection.
		updateUserFollowersFromTwitter(mainuser); 
		
		//update ALL the twitter ids that follows you back in all the OTHER users (main users), so you don't touch the wrong accounts (this means: twitter_dont_follow_this = true
		updateUsersThatAlreadyFollowsBack(mainuser); //THis is based on the current followers.
		
		//Variables and accounts
		System.out.println("Start the sistematic un-following.");
		
		// here we query the followers for the given user in the database.
		JongoDAO jongoDAO = new JongoDAO();
		jongoDAO.openConnection();
		
	    Random randomGenerator = new Random();
	    
	    long unfollowerUsers = 0;
	    
	    //unfollow the users that didn't follow you back and that has the flag "unfollowed" in null.
		org.jongo.MongoCursor<Document> followers = jongoDAO.getUsersThatDontFollowBack();
		
		Account cuenta = new Account();
		Document follower = null;
		User datosFollower = null;
		
		while((followers.hasNext()) && (unfollowerUsers <= maxNumberOfUsersUnfollowed)){
			
			follower = followers.next();

			//unfollow the user in twitter and register it in the database.
			try { 	
				
				try{				
					//unfollows the id, from the connected account
					datosFollower = cuenta.unfollow(follower.getLong("twitter_id"));
				} catch (Exception e){
					datosFollower = cuenta.unfollow(new Long(follower.getDouble("twitter_id").longValue()));
				}
				
				//update all the recently followed users as "unfollowed" equals to today datetime in the Database.
				if (datosFollower!=null) jongoDAO.updateUnfollowedFlag(datosFollower);
				
				unfollowerUsers++;
				
				//We set a Random wait in order to disguise the process to the twitter's policies watcher process.
				TimeUnit.SECONDS.sleep(randomGenerator.nextInt(DELAY_FOR_TWITTER_API));

			} catch (InterruptedException e) {
				System.out.println("Error following the user: (from Java Random number generator) " + follower.getLong("twitter_id").longValue());
				e.printStackTrace();
			} catch (Exception e){
				System.out.println("Error following the user: (from Java) " + follower.getLong("twitter_id").longValue());
				e.printStackTrace();
			}
				
			
		}
				
		jongoDAO.closeConnections();
	}


	public void automaticFollowingOfMainUser(String usuarioCopiar, long maxNumberUsersFollowed ) {
		//1st. Get ALL the users of your target user to follow that doesn't has a flag of like: "do not follow this".
		
		//Variables and accounts
		Account cuenta = new Account();

		// obtaining all users that follows the common user passed as argument.
		
		System.out.println("Start the sistematic following.");
		
		// here we query the followers for the given user in the database.
		JongoDAO jongoDAO = new JongoDAO();
		jongoDAO.openConnection();
	    Random randomGenerator = new Random();
	    long followerUsers = 0;
	    
		org.jongo.MongoCursor<Document> followersCandidates = jongoDAO.getFollowersCandidates(usuarioCopiar);
		
		//3rd. Get the N users from the target user account that do not have a flag of "do not follow this"; N is the maximum expected number of following by day for your account according to Twitter policies.
		Document userCandidate = null;
		
		User datosFollower = null;
		
		while((followersCandidates.hasNext()) && (followerUsers <= maxNumberUsersFollowed)){
			
			userCandidate =  followersCandidates.next();
			try {
				
				//4th. Follow the N subset users with a random time between every follow 	
				try{				
					datosFollower = cuenta.follow(userCandidate.getLong("twitter_id"));
				} catch (Exception e){
					datosFollower = cuenta.follow(new Long(userCandidate.getDouble("twitter_id").longValue()));
				}
				
				followerUsers++;
				
				//5th. Set those users (after succesfull following) as "do not follow this" in the target user.
				if (datosFollower!=null){
					//6th. Update a field of "Recently_followed_users" where the succesfully followed users are logged (this is for further management of the followed back that didn't work).
					jongoDAO.updateSpecificUserDataWithUser(datosFollower);
				}else{
					System.out.println("Can't follow the user: " + userCandidate.getLong("twitter_id").longValue());
				}
				
				//We set a Random wait in order to disguise the twitter policies.
				TimeUnit.SECONDS.sleep(randomGenerator.nextInt(5));
				
			} catch (TwitterException e) {
				System.out.println("Error following the user: " + userCandidate.getLong("twitter_id").longValue());
				e.printStackTrace();
			} catch (InterruptedException e) {
				System.out.println("Error following the user: (from Java Random number generator) " + userCandidate.getLong("twitter_id").longValue());
				e.printStackTrace();
			} catch (Exception e){
				System.out.println("Error following the user: (from Java) " + userCandidate.getLong("twitter_id").longValue());
				e.printStackTrace();
			}
			
		}
	
		jongoDAO.closeConnections();
	}


	/**
	 * Allows you, to update the others users in the database as followers of the current user, comparing his actual followers with the followers of the other users: if they are the same, they are flagged for not following them back by accident.
	 * NOTE: this updates a specific main_users followers.
	 * @param mainuser
	 */
	public  void updateUsersThatAlreadyFollowsBack(String mainuser, String targetUser) {
		//1st. Get all the followers of the current user. 
		JongoDAO jongoDao = new JongoDAO();
		
		jongoDao.openConnection();
		
		org.jongo.MongoCursor<Document> seguidores =  jongoDao.getUserFollowers(mainuser);
		
		//2nd. update all users in the collection that has the same "twitter_id" with the "do not follow this" to true.
		for(Document seguidor: seguidores){
			jongoDao.updateFollowerStatus(targetUser, seguidor, true);
		}
		
		jongoDao.closeConnections();
	}
	
	/**
	 * Allows you, to update ALL the others users in the database as followers of the current user; comparing his actual followers with the followers of the other users: if they are the same, they are flagged for not following them back by accident.
	 * @param mainuser
	 */
	public  void updateUsersThatAlreadyFollowsBack(String mainuser) {
		//1st. Get all the followers of the current user. 
		JongoDAO jongoDao = new JongoDAO();
		
		jongoDao.openConnection();
		
		org.jongo.MongoCursor<Document> seguidores =  jongoDao.getUserFollowers(mainuser);
		
		//2nd. update all users in the collection that has the same "twitter_id" with the "do not follow this" to true (to prevent further following) and twitter_is_follower to current datetime.
		for(Document seguidor: seguidores){
			jongoDao.updateFollowerStatus(seguidor, true);
		}
		
		jongoDao.closeConnections();
	}



	/**
	 * Gets the users data from the database, then gets all the users that has no data from twitter and sets the data for them.
	 * @param mainuser
	 */
	public void updateUserFollowerInformationInDatabase(String mainuser) {
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
	public void updateUserFollowerDatabase(ArrayList<Document> users) {
		
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
	public ArrayList<Document> getStoredFollowersFromUser(String usuario) {

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
	public ArrayList<Long> updateUserFollowersFromTwitter(String usuario) {
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
				
				//TODO: Esto hay que cambiarlo por una sentencia update de Jongo o un insert; ver la forma de mejorarlo.
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

}
