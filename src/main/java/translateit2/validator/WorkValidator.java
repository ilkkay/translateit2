package translateit2.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import translateit2.persistence.dao.WorkRepository;
import translateit2.persistence.dto.WorkDto;

// http://dolszewski.com/spring/custom-validation-annotation-in-spring/

// the following is IMPORTANT contains node issues etc. 
// https://access.redhat.com/webassets/avalon/d/red-hat-jboss-enterprise-application-platform/7.0.0/javadocs/org/hibernate/validator/internal/engine/constraintvalidation/ConstraintValidatorContextImpl.html
@ConfigurationProperties(prefix = "translateit2.validator")
public class WorkValidator implements ConstraintValidator<WorkConstraint, WorkDto> {

    @Autowired
    private WorkRepository workRepo;

    @Override
    public void initialize(WorkConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(WorkDto value, ConstraintValidatorContext context) {

        if (value == null)
            return true;

        boolean isValid = true;

        boolean isExisitingWorkWithSameVersion;
        if (value.getId() == null)
            isExisitingWorkWithSameVersion = workRepo.
            findByProjectIdAndVersion
            (value.getProjectId(), value.getVersion()).isPresent();
        else
            isExisitingWorkWithSameVersion = workRepo.
            findByProjectIdAndVersionAndIdNot
            (value.getProjectId(), value.getVersion(), value.getId()).isPresent();

        if (isExisitingWorkWithSameVersion) {
            isValid = false;
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("WorkDto.work_version_exists_already").addPropertyNode("version").addConstraintViolation(); // $NON-NLS-1$                
        }

        return isValid;
    }
}
