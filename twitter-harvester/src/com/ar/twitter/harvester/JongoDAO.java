package com.ar.twitter.harvester;

import org.bson.Document;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

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

		MongoCursor<Document> usuarios = usuariosseguidores.find("{twitter_main_user: '" + mainuser + "'}, {twitter_id: 1 , _id:0 }").as(Document.class);
		
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
}
