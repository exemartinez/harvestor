
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.nio.charset.Charset;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;


/**
 * WARNING!!!
 * 
 * This test java class, test the REST service; WATCH IT! is user the Twitter API REST service, SO NO EXECUTION OF THIS, is truly a test. It runs for real!
 */

/**
 * @author HerMartinez
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes = Application.class)
@ContextConfiguration(locations={"classpath:WEB-INF/spring-config/root-context.xml"})
@WebAppConfiguration
public class HomeRestControllerTests {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	private String userName = "bdussault";

	private HttpMessageConverter<?> mappingJackson2HttpMessageConverter;

	@Autowired
	private WebApplicationContext webApplicationContext;

	/*
	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
				.filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

	}
	*/
	
	/**
	 * Set ups the entire mocking webapp context.
	 * @throws Exception
	 */
	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

	}

	/**
	 * Test method for
	 * {@link com.ar.twitter.harvester.controllers.HomeRestController#updateFromTwitter(java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateFromTwitter() {
        try {
        	
			mockMvc.perform(get("/updateFromTwitter/hernanemartinez")).andExpect(status().isOk());
			
		} catch (Exception e) {
			fail("Throws an exception.");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link com.ar.twitter.harvester.controllers.HomeRestController#updateFollowersInfo(java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateFollowersInfoString() {
        try {
        	
			mockMvc.perform(get("/updateFollowersInfo/hernanemartinez")).andExpect(status().isOk());
			
		} catch (Exception e) {
			fail("Throws an exception.");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link com.ar.twitter.harvester.controllers.HomeRestController#updateFollowersInfo(java.lang.String, java.lang.Long, java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateFollowersInfoStringLongString() {
        try {
        	
			mockMvc.perform(get("/followUsersOf/florenciaypunto/maxNumberOfUsersToFollow/50/updateFollowersFirst")).andExpect(status().isOk());
			
		} catch (Exception e) {
			fail("Throws an exception.");
			e.printStackTrace();
		}
	}

	/**
	 * Test method for
	 * {@link com.ar.twitter.harvester.controllers.HomeRestController#unfollowNonFollowersOf(java.lang.String, java.lang.Long, java.lang.String)}
	 * .
	 */
	@Test
	public void testUnfollowNonFollowersOf() {
        try {
        	
			mockMvc.perform(get("/unfollowNonFollowersOf/florenciaypunto/maxNumberOfUsersToFollow/50/updateFollowersFirst")).andExpect(status().isOk());
			
		} catch (Exception e) {
			fail("Throws an exception.");
			e.printStackTrace();
		}
	}

}
