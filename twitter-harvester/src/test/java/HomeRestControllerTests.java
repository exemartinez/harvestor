
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.ar.twitter.harvester.controllers.HomeRestController;


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
@WebAppConfiguration
@ContextConfiguration("classpath:WEB-INF/spring-config/root-context.xml")
public class HomeRestControllerTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
	
	/**
	 * Set ups the entire mocking webapp context.
	 * @throws Exception
	 */
    @Before
    public void setup() {
    	
        //this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    	//System.out.println("Name: " + wac.getResource("WEB-INF/spring-config/root-context.xml"));
        this.mockMvc = MockMvcBuilders.standaloneSetup(new HomeRestController()).build();
    }

	/**
	 * Test method for
	 * {@link com.ar.twitter.harvester.controllers.HomeRestController#updateFromTwitter(java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateFromTwitter() {
        try {
        	
			mockMvc.perform(get("/twitter-harvester/home/updateFromTwitter/hernanemartinez").contextPath("/twitter-harvester").servletPath("/home")).andExpect(status().isOk());
			
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
        	
			mockMvc.perform(get("/home/updateFollowersInfo/hernanemartinez")).andExpect(status().isOk());
			
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
        	
			mockMvc.perform(get("/home/followUsersOf/florenciaypunto/maxNumberOfUsersToFollow/50/updateFollowersFirst")).andExpect(status().isOk());
			
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
        	
			mockMvc.perform(get("/home/unfollowNonFollowersOf/florenciaypunto/maxNumberOfUsersToFollow/50/updateFollowersFirst")).andExpect(status().isOk());
			
		} catch (Exception e) {
			fail("Throws an exception.");
			e.printStackTrace();
		}
	}

}
