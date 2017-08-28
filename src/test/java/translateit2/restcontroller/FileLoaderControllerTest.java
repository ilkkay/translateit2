package translateit2.restcontroller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
//https://spring.io/guides/gs/uploading-files/
//https://github.com/spring-guides/gs-uploading-files/blob/master/complete/src/test/java/hello/FileUploadIntegrationTests.java
//http://stackoverflow.com/questions/26964688/multipart-file-upload-using-spring-rest-template-spring-web-mvc
//http://stackoverflow.com/questions/15404605/spring-resttemplate-invoking-webservice-with-errors-and-analyze-status-code
//http://stackoverflow.com/questions/28408271/how-to-send-multipart-form-data-with-resttemplate-spring-mvc
//http://forum.spring.io/forum/spring-projects/web/70845-sending-post-parameters-with-resttemplate-requests
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FileLoaderControllerTest {
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void justTesting() {
        assert(true);
    }

    // some HTTP codes:
    // 302=Found, 405=Method Not Allowed, 201=Created, 500=internal server error
    // 400=Bad Request, 404=Not Found
    //@Test
    public void shouldUploadFile() throws Exception {
        String testFile = "";
        ClassPathResource resource = null;
        MultiValueMap<String, Object> map = null;
        ResponseEntity<String> response = null;
        String responseBody = "";

        // TODO: 500 internal server error <=
        // javax.validation.ConstraintViolationException

        // WHEN loading empty file
        testFile = "empty-testupload.txt";
        resource = new ClassPathResource(testFile, getClass());
        map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("destination", "source");
        try {
            response = this.restTemplate.postForEntity("/work/{id}/sourceFile", map, String.class);
        } catch (HttpClientErrorException e) {
            fail("Unexpected exception " + e.getStatusCode() + e.getResponseBodyAsString());
        }
        // THEN create error page
        responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED); // Error
                                                                                       // page
                                                                                       // was
                                                                                       // created
        assertThat(responseBody.contains("error"), is(equalTo(true))); // Failed
                                                                       // to
                                                                       // store
                                                                       // empty
                                                                       // file

        // WHEN loading file without locale substring
        testFile = "testupload.txt";
        resource = new ClassPathResource(testFile, getClass());
        map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("destination", "source");
        try {
            response = this.restTemplate.postForEntity("/upload", map, String.class);
        } catch (HttpClientErrorException e) {
            fail("Unexpected exception " + e.getStatusCode() + e.getResponseBodyAsString());
        }
        // THEN create error page
        responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(responseBody.contains("error"), is(equalTo(true))); // The
                                                                       // language
                                                                       // code
                                                                       // is
                                                                       // missing
                                                                       // from
                                                                       // the
                                                                       // filename

        // WHEN loading file without properties extension
        testFile = "test_fi.txt";
        resource = new ClassPathResource(testFile, getClass());
        map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("destination", "source");
        try {
            response = this.restTemplate.postForEntity("/upload", map, String.class);
        } catch (HttpClientErrorException e) {
            fail("Unexpected exception " + e.getStatusCode() + e.getResponseBodyAsString());
        }
        // THEN create error page
        responseBody = response.getBody();
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.CREATED);
        assertThat(responseBody.contains("error"), is(equalTo(true))); // This
                                                                       // is not
                                                                       // a
                                                                       // valid
                                                                       // properties
                                                                       // file

        // WHEN loading a proper file
        testFile = "dotcms_en.properties";
        resource = new ClassPathResource(testFile, getClass());
        map = new LinkedMultiValueMap<String, Object>();
        map.add("file", resource);
        map.add("destination", "source");
        try {
            response = this.restTemplate.postForEntity("/upload", map, String.class);
        } catch (HttpClientErrorException e) {
            fail("Unexpected exception " + e.getStatusCode() + e.getResponseBodyAsString());
        }
        // THEN return FOUND http response
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.FOUND);
        assertThat(response.getHeaders().getLocation().toString())
                .startsWith("http://localhost:" + this.port + "/upload");
    }

}
