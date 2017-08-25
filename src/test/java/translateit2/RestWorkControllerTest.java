package translateit2;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import translateit2.persistence.dto.WorkDto;


@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = TranslateIt2v4Application.class, 
webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestWorkControllerTest {

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;

    // http://www.concretepage.com/spring/spring-mvc/spring-rest-client-resttemplate-consume-restful-web-service-example-xml-json#postforentity   
    @Test //@RequestMapping(value = "/work/{id}/sourceFile", method = RequestMethod.POST)
    public void shouldUploadFile() {

        ResponseEntity<?> response = null;
       
        // WHEN loading empty file
        String testFile = "empty-testupload.txt";
        ClassPathResource resource = new ClassPathResource(testFile, getClass());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("id", new Long(1));
        //map.add("file", resource);
        try {
            //ResponseEntity<?> response = this.restTemplate.postForEntity("/work/{id}/sourceFile", map, String.class);
            response = this.restTemplate.getForEntity("/work/1", WorkDto.class, map);
        } catch (Exception e) {
            fail("Unexpected exception ");
        } 
        
        // THEN 
       String responseBody = response.getBody().toString();
       
       return;
    }
    
    //@Test
    public void test() throws Exception {

        MvcResult result = this.mvc.perform(MockMvcRequestBuilders
                .get("/work/1").accept(MediaType.APPLICATION_JSON)).andReturn();
        String content = result.getResponse().getContentAsString();
        
        return;
    }

}
