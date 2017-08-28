package translateit2;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
public class LoggerIT {

	@Test
	public void testPerformSomeTask() throws Exception {
		MyLogger l = new MyLogger();
		l.performSomeTask();
		
		String level = l.logger.getLevel().toString();
		
		assert("NOTICE".equals(level));

	}

	static private class MyLogger {
		static final Logger logger = LogManager.getLogger(LoggerIT.class.getName());

		public void performSomeTask() {
			logger.log(Level.forName("NOTICE", 450), "This is a notice message from '{}'", this.toString());

			logger.trace("This is a trace message from '{}'", this.toString());
			logger.debug("This is a debug message from '{}'", this.toString());
			logger.info("This is an info message from '{}'", this.toString());
			logger.warn("This is a warn message from '{}'", this.toString());
			logger.error("This is an error message from '{}'", this.toString());
			logger.fatal("This is a fatal message from '{}'", this.toString());
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}

		public String concat(String a, String b) {
			logger.info("String a:" + a + ", String b:" + b);
			return a+b;
		}
	}
/*
	@Mock
	private Appender mockAppender;

	@Captor
	private ArgumentCaptor captorLoggingEvent;

	@Before
	public void setup() {
		Logger logger = LogManager.getLogger(LoggerIntegrationTest.class.getName());
		((org.apache.logging.log4j.core.Logger) logger).addAppender(mockAppender);
	}

	@After
	public void teardown() {
		((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).removeAppender(mockAppender);
	}


	@Test
	public void shouldConcatAndLog() {
		//given
		MyLogger example = new MyLogger();
		//when
		String result = example.concat("foo", "bar");
		//then
		assertThat("foobar", equalTo(result));

		verify(mockAppender).append((LogEvent) captorLoggingEvent.capture());
		LoggingEvent loggingEvent = (LoggingEvent) captorLoggingEvent.getValue();
		//Check log level
		assertThat(loggingEvent.getLevel(), equalTo(Level.INFO));
		//Check the message being logged
		assertThat(loggingEvent.getMessage(), 
				equalTo("String a:foo, String b:bar"));
	}
*/
}