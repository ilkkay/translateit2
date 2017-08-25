package translateit2.web;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import translateit2.exception.TranslateIt2Exception;
import translateit2.fileloader.FileLoader;
import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.dto.WorkDto;
import translateit2.restapi.ViewUnits;
import translateit2.service.LoadingContractor;
import translateit2.service.WorkService;
import translateit2.util.WorkStatisticsLogic;

@RestController
@RequestMapping("/api")
public class RestUnitController {
    public static final Logger logger = LoggerFactory.getLogger(RestUnitController.class);

    private final FileLoader fileLoader;
    private WorkService workService;
    private LoadingContractor loadingContractor;
    private WorkStatisticsLogic workStatisticsLogic;

    @Autowired
    public RestUnitController(FileLoader fileLoader,
            WorkService workService,
            WorkStatisticsLogic workStatisticsLogic,
            LoadingContractor loadingContractor) {
        this.workService = workService;
        this.loadingContractor = loadingContractor;
        this.fileLoader = fileLoader;
        this.workStatisticsLogic = workStatisticsLogic;
    }

    // -------------------Get path to download file
    // ------------------------------------------
    @RequestMapping(value = "/work/{id}/downloadUrl", method = RequestMethod.GET)
    public ResponseEntity<?> getDownloadPath(@PathVariable("id") long id, UriComponentsBuilder ucBuilder) {
        logger.info("Creating url path for workId {}", id);

        try {
            Stream<Path> downloadStream = loadingContractor.downloadTarget(id);
            String filename = "/api/files/" + downloadStream.findFirst().get().getFileName().toString();
            UriComponents uriComponents = ucBuilder.scheme("http").host("localhost").path(filename).build();

            return new ResponseEntity<>(uriComponents, HttpStatus.OK);

        } catch (TranslateIt2Exception e) {
            throw e;
        } 
    }

    // -------------------Retrieve Single unit
    // ------------------------------------------
    @RequestMapping(value = "/work/unit/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUnit(@PathVariable("id") long id) {
        logger.info("Fetching Unit with id {}", id);

        UnitDto unt = workService.getUnitDtoById(id);

        return new ResponseEntity<>(unt, HttpStatus.OK);
    }

    // -------------------Retrieve the Units by pages
    // ---------------------------------------------
    // /work/{workId}/units?pageNum=1&pageSize=4
    @RequestMapping(value = "/work/{workId}/units", method = RequestMethod.GET)
    public ResponseEntity<?> getAllUnits(@PathVariable("workId") long workId, 
            @RequestParam Map<String,String> allRequestParams) {

        logger.info("Getting Units for workId {}", workId);
        
        int pageSize = Integer.parseInt(allRequestParams.get("pageSize"));
        int pageNumber = Integer.parseInt(allRequestParams.get("pageNum"));

        ViewUnits viewUnits = new ViewUnits();
        
        viewUnits.setUnits(workService.getPage(workId, pageNumber, pageSize));
        viewUnits.setPageCount(workService.getUnitDtoCount(workId) / pageSize + 1);
        viewUnits.setStatistics(workStatisticsLogic.getStatistics(workId));

        return new ResponseEntity<>(viewUnits, HttpStatus.OK);
    }

    // ------------------- download file
    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        logger.info("Downloading file {}", filename);
        
        try {
            Resource file = fileLoader.loadAsResource(filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (TranslateIt2Exception e) {
            throw e;
        }

    }

    // ------------------- Update a Unit => 
    // TODO: => /units/{id} vai /unit
    // ------------------------------------------------
    @RequestMapping(value = "/work/unit/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUnit(@PathVariable("id") long workId, @RequestBody UnitDto unit) {

        UnitDto updatedUnit = workService.updateTranslatedUnitDto(unit, unit.getWorkId());        
        workService.updateProgress(updatedUnit.getWorkId());

        return new ResponseEntity<>(updatedUnit, HttpStatus.OK);
    }

    // -------------------Upload target file 
    // TODO: => target-file
    // ---------------------------------------------
    @RequestMapping(value = "/work/{id}/targetFile", method = RequestMethod.POST)
    public ResponseEntity<?> uploadTargetFile(@RequestParam(value = "workId") Long id,
            @RequestParam(value = "file") MultipartFile file, HttpServletRequest request // ({
            , UriComponentsBuilder ucBuilder) {

        try {
            loadingContractor.uploadTarget(file, id);
            WorkDto dto = workService.updateProgress(id);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(ucBuilder.path("/api/{id}/units").buildAndExpand(dto.getId()).toUri());
            return new ResponseEntity<Void>(headers, HttpStatus.CREATED);            

        } catch (TranslateIt2Exception e) {
            throw e;
        }  
    }

}