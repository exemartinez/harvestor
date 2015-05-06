package com.ar.twitter.harvester;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDAO {

	private static final String LOCALHOST = "localhost";
	private MongoDatabase database;
	private MongoClient mongoClient;

	/**
	 * Performs connection to the database - MongoDB
	 */
	public void connect(String databasename) {
		// To directly connect to a single MongoDB server

		mongoClient = new MongoClient(LOCALHOST, 27017);
		database = mongoClient.getDatabase(databasename);

	}

	/**
	 * Inserting many documents at once (trying them as one transaction)
	 * 
	 * @param collectionName
	 * @param documents
	 */
	public void insertDocuments(String collectionName, ArrayList<Document> documents) {
		MongoCollection<Document> collection = getCollection(collectionName);

		// Insert many documents in a given collection
		collection.insertMany(documents);
	}
	

	/**
	 * Gets a Mongo collection for further processing. (or simple querying)
	 * @param collectionName
	 * @return
	 */
	public MongoCollection<Document> getCollection(String collectionName) {
		// Getting a new collection
		MongoCollection<Document> collection = database.getCollection(collectionName);
		
		if (collection == null) {
			database.createCollection(collectionName);
			collection = database.getCollection(collectionName);
		}
		
		return collection;
	}

	/**
	 * Creates a new log of user followers for a given user of any sort.
	 * 
	 * @param userid
	 * @param username
	 * @param description
	 */
	public Document createAlreadyFollowedUserForMainUser(String mainuser, Long userid,
			String username, String description) {
		// create the document
		return (new Document("twitter_main_user", mainuser).append("twitter_id", userid).append("twitter_username",
				username).append("twitter_description", description));

	}
	
	/**
	 * Creates a document of the collection AlreadyFollowedUsers
	 * 
	 * @param userid
	 * @param username
	 * @param description
	 */
	public Document createUserAlreadyFollowedUsersDocument(Long userid,
			String username, String description) {
		// create the document
		return (new Document("twitter_id", userid).append("twitter_username",
				username).append("twitter_description", description));

	}

	/**
	 * Closes all MongoDB connections
	 */
	public void closeConnections() {
		// Closing the connection
		mongoClient.close();
	}
	
	/**
	 * Transforms a MongoDB cursor into a common ArrayList<Document> class
	 * 
	 * @param previouslystored
	 * @return
	 */
	public ArrayList<Document> transformsMongoCursorToArrayList(MongoCursor<Document> previouslystored) {
		ArrayList<Document> arrayfollowers = new ArrayList<Document>();
		
		try {
		    while (previouslystored.hasNext()) {
		    	arrayfollowers.add(previouslystored.next());
		    }
		} finally {
			previouslystored.close();
		}
		
		return arrayfollowers;
	}

}
