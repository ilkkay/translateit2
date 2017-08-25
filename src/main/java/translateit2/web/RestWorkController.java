package translateit2.web;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import translateit2.configuration.LanguageServicesConfig;
import translateit2.exception.TranslateIt2Exception;
import translateit2.persistence.dto.WorkDto;
import translateit2.restapi.ViewWorks;
import translateit2.service.LoadingContractor;
import translateit2.service.WorkService;

@RestController
@RequestMapping("/api")
public class RestWorkController {

    public static final Logger logger = LoggerFactory.getLogger(RestWorkController.class);

    private WorkService workService;
    private LoadingContractor loadingContractor;
    private LanguageServicesConfig languageServices;

    @Autowired
    public RestWorkController(WorkService workService,
            LoadingContractor loadingContractor,
            LanguageServicesConfig languageServices) {
        this.workService = workService;
        this.loadingContractor = loadingContractor;
        this.languageServices = languageServices;
    }

    // -------------------Create a work
    // -------------------------------------------
    @RequestMapping(value = "/work/", method = RequestMethod.POST)
    public ResponseEntity<?> createWork(@RequestBody WorkDto work, UriComponentsBuilder ucBuilder) {
        logger.info("Creating Project : {}", work);

        WorkDto wrk = workService.createWorkDto(work,"Group name 1");

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/work/{id}").buildAndExpand(wrk.getId()).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
    }

    // ------------------- Delete a work
    // -----------------------------------------
    @RequestMapping(value = "/work/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteWork(@PathVariable("id") long id) {
        logger.info("Fetching & Deleting Work with id {}", id);

        workService.removeWorkDto(id);
        loadingContractor.removeUploadedSource(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // -------------------Retrieve Single work
    // ------------------------------------------
    @RequestMapping(value = "/work/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getWork(@PathVariable("id") long id) {
        logger.info("Fetching Work with id {}", id);

        WorkDto wrk = workService.getWorkDtoById(id);

        return new ResponseEntity<>(wrk, HttpStatus.OK);
    }

    // -------------------Retrieve All works
    // ---------------------------------------------
    @RequestMapping(value = "/project/{projectId}/work/", method = RequestMethod.GET)
    public ResponseEntity<?> getAllWorks(@PathVariable("projectId") long projectId) {

        logger.info("Listing all Works having project id {}", projectId);

        ViewWorks viewWorks = new ViewWorks();
        viewWorks.setSupportedPriorities(languageServices.getSupportedPriorities());
        viewWorks.setWorks(workService.getProjectWorkDtos(projectId));

        return new ResponseEntity<>(viewWorks, HttpStatus.OK);
    }

    // ------------------- Update a Work
    // ------------------------------------------------
    @RequestMapping(value = "/work/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateWork(@PathVariable("id") long id, @RequestBody WorkDto work) {
        logger.info("Updating Work with id {}", id);

        WorkDto updatedWork = workService.updateWorkDto(work);

        return new ResponseEntity<>(updatedWork, HttpStatus.OK);              
    }

    // -------------------Upload source file
    // ---------------------------------------------
    // TODO: upload /work/1/file?format=...
    @RequestMapping(value = "/work/{id}/sourceFile", method = RequestMethod.POST)
    public ResponseEntity<?> uploadSourceFile(@RequestParam(value = "workId") Long id,
            @RequestParam(value = "file") MultipartFile file, HttpServletRequest request) {
        try {
            loadingContractor.uploadSource(file, id);
            return new ResponseEntity<>(workService.getWorkDtoById(id), HttpStatus.OK); 

        } catch (TranslateIt2Exception e) {
            throw e;
        }        
    }
}