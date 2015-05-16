package com.ar.twitter.harvester;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bson.Document;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import twitter4j.User;

import com.mongodb.DB;
import com.mongodb.MongoClient;

/**
 * This class encapsulares the behavior of the Jongo runtime for making the querying of the mongodb database easier.
 * @author hernanezequielmartinez
 *
 */
public class JongoDAO {

	public static final String COL_FIELD_TWITTER_DESCRIPTION = "twitter_description";
	public static final String COL_FIELD_TWITTER_USERNAME = "twitter_username";
	public static final String COL_FIELD_TWITTER_ID = "twitter_id";
	public static final String COL_USUARIOS_SEGUIDORES_USUARIO = "Usuarios_Seguidores_Usuario_Principal";
	public static final String DATABASENAME = "myDB";
	
	private static final String LOCALHOST = "localhost";
	private MongoClient mongoClient = null;
	private DB dataBase = null;
	
	/**
	 * We get the users that are candidates to follow from the given user
	 * NOTE: the user followers, first hast to exist in the database
	 * @param mainuser2
	 * @param usuarioacopiar2
	 */
	public MongoCursor<Document> getUserFollowers(String mainuser) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);

		MongoCursor<Document> usuarios = usuariosseguidores.find("{twitter_main_user: \"" + mainuser + "\"}, {twitter_id: 1 , _id:0 }").as(Document.class);
		
		return usuarios;
	}

	/**
	 * updates the field twitter_dont_follow_this, for preventing all algorithm of following it.
	 * @param usuarioacopiar
	 * @param seguidor
	 * @param b
	 */
	public void updateFollowerStatus(String usuarioacopiar, Document seguidor,
			boolean b) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		usuariosseguidores.update("{twitter_main_user: \"" + usuarioacopiar.trim() + "\", twitter_id: " + seguidor.getLong("twitter_id").longValue() + "}").multi().with("{$set: {twitter_dont_follow_this: true}}");
		
		
	}

	/**
	 * updates the field twitter_dont_follow_this and twitter_is_follower, for all the main_users (that is for all the users followers).
	 * This makes the id (seguidor) for all the mainusers in the collection: a) unfollowable b) maked as a current follower.
	 * 
	 * @param seguidor
	 * @param b
	 */
	public void updateFollowerStatus(Document seguidor,
			boolean b) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		
		usuariosseguidores.update("{twitter_id: " + seguidor.getLong("twitter_id").longValue() + "}").multi().with("{$set: {twitter_dont_follow_this: true, twitter_is_follower_date: " + dateFormat.format(cal.getTime()) + "}}");
		
		
	}
	
	/**
	 * Creates a connection, if is already open, closes it.
	 */
	public void openConnection() {
	
		if (mongoClient == null) mongoClient = new MongoClient();	
		if (dataBase == null) dataBase = mongoClient.getDB(DATABASENAME);
		
	}

	/**
	 * Closes any open connections.
	 */
	public void closeConnections() {
	
		dataBase = null;
		mongoClient.close();
		mongoClient = null;
		
	}

	/**
	 * Gets the candidates for the user passed as paramenter. The idea is to return all the followers with the flag = "twitter_dont_follow_this" in FALSE
	 * @param usuarioacopiar
	 * @return
	 */
	public MongoCursor<Document> getFollowersCandidates(String mainuser) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);

		MongoCursor<Document> usuarios = usuariosseguidores.find("{twitter_main_user: \"" + mainuser + "\", twitter_dont_follow_this: false}").as(Document.class);
		
		return usuarios;
	}

	/**
	 * Updates the data of an specific user in the database.
	 * @param datosFollower
	 */
	public void updateSpecificUserDataWithUser(User datosFollower) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		
		usuariosseguidores.update("{twitter_id: " + datosFollower.getId() + "}").multi().with("{$set: {twitter_dont_follow_this: true, twitter_username: \"" + datosFollower.getName() + "\", twitter_description: \"" + datosFollower.getDescription() + "\", twitter_screenname: \"" + datosFollower.getScreenName() + "\", twitter_location: \"" + datosFollower.getLocation() + "\", twitter_followers_count: " + datosFollower.getFollowersCount() + ", twitter_friends_count: " + datosFollower.getFriendsCount() + ", twitter_favs_count: " + datosFollower.getFavouritesCount() + ", twitter_status_count: " + datosFollower.getStatusesCount() + ", Recently_followed_users: " + dateFormat.format(cal.getTime()) + " }}");
		
	}

	/**
	 * Returns all the users that you've followed, but that do not follow you back (and that you didn't unfollowed, previously)
	 * 
	 * If all this happened, then, probably, the user didn't followed you back after you followed him.
	 * @return
	 */
	public MongoCursor<Document> getUsersThatDontFollowBack() {

		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		MongoCursor<Document> found = usuariosseguidores.find("{twitter_unfollowed_date: {$eq: null}, twitter_dont_follow_this: true, twitter_is_follower_date: {$eq: null}}").as(Document.class);
		
		if (found.hasNext())
			return found;
		else
			return null;
	}

	/**
	 * Updates the flag of unfollowed user to current date of unfollowing; so we leave a record of the user following us or not and when.
	 * @param id
	 */
	public void updateUnfollowedFlag(User datosFollower) {
		
		Jongo jongo = new Jongo(dataBase);
		MongoCollection usuariosseguidores = jongo.getCollection(COL_USUARIOS_SEGUIDORES_USUARIO);
		
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		
		// we take advantage that the unfollowing procedure returns the user data.
		usuariosseguidores.update("{twitter_id: " + datosFollower.getId() + "}").multi().with("{$set: {twitter_dont_follow_this: true, twitter_username: \"" + datosFollower.getName() + "\", twitter_description: \"" + datosFollower.getDescription() + "\", twitter_screenname: \"" + datosFollower.getScreenName() + "\", twitter_location: \"" + datosFollower.getLocation() + "\", twitter_followers_count: " + datosFollower.getFollowersCount() + ", twitter_friends_count: " + datosFollower.getFriendsCount() + ", twitter_favs_count: " + datosFollower.getFavouritesCount() + ", twitter_status_count: " + datosFollower.getStatusesCount() + ", twitter_unfollowed_date: " + dateFormat.format(cal.getTime()) + " }}");
		
	}


}
