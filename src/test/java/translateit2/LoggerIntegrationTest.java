package translateit2;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.model.Source;
import translateit2.persistence.model.State;
import translateit2.persistence.model.Target;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class LoggerIntegrationTest {

    @Test
    public void testPerformSomeTask() throws Exception {
        MyLogger l = new MyLogger();
        l.performSomeTask();
    }

    static private class MyLogger {
        static final Logger logger = LogManager.getLogger(LoggerIntegrationTest.class.getName());
        // static final ExtLogger logger = ExtLogger.create(MyLogger.class);

        public void performSomeTask() {
            logger.trace("This is a trace message from '{}'", this.toString());
            logger.debug("This is a debug message from '{}'", this.toString());
            logger.info("This is an info message from '{}'", this.toString());
            logger.warn("This is a warn message from '{}'", this.toString());
            logger.error("This is an error message from '{}'", this.toString());
            logger.fatal("This is a fatal message from '{}'", this.toString());

            logger.log(Level.forName("NOTICE", 450), "This is a notice message from '{}'", this.toString());

            UnitDto u = new UnitDto();
            Source s = new Source();
            s.setText("Orig.");
            Target t = new Target();
            t.setText("Alkup.");
            t.setState(State.NEW);
            u.setSource(s);
            u.setTarget(t);
            logger.info("Logging empty entity {}", ToStringBuilder.reflectionToString(u));
            logger.info("Logging empty entity {}", u.toString());

            logger.info("I am the {} year old {}", 1000, "man");
        }

        // Apache Commons Lang =>
        // override toString() with ToStringBuilder
        // http://howtodoinjava.com/apache-commons/how-to-override-tostring-effectively-with-tostringbuilder/
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

}