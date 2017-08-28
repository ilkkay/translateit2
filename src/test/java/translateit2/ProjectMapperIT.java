package translateit2;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import translateit2.languagefile.LanguageFileFormat;
import translateit2.languagefile.LanguageFileType;
import translateit2.persistence.dto.ProjectDto;
import translateit2.persistence.dto.ProjectMapper;
import translateit2.persistence.dto.UnitDto;
import translateit2.persistence.model.Project;
import translateit2.persistence.model.Source;
import translateit2.persistence.model.Target;
import translateit2.persistence.model.Unit;

//http://www.vogella.com/tutorials/JUnit/article.html
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TranslateIt2v4Application.class)
@WebAppConfiguration
public class ProjectMapperIT {
    @Autowired
    private ProjectMapper modelMapper;

    @Test
    public void map_project3_return_name_projectname() {
        // given
        final Project prj = new Project();
        prj.setName("Translate It 2");
        prj.setSourceLocale(new Locale("fi_FI"));
        prj.setFormat(LanguageFileFormat.PROPERTIES);
        prj.setType(LanguageFileType.UTF_8);

        // when
        final ProjectDto result = modelMapper.map(prj, ProjectDto.class);

        // then
        assertThat(prj.getSourceLocale(), is(equalTo(result.getSourceLocale())));
        assertThat(prj.getName(), is(equalTo(result.getName())));
    }

    @Test
    public void map_unit_return_source_target() {
        // given
        final Unit unit = new Unit();
        unit.setSegmentKey("segmentKey");
        unit.setSerialNumber(666);
        final Source s = new Source();
        s.setText("source text");
        final Target t = new Target();
        t.setText("target text");
        unit.setSource(s);
        unit.setTarget(t);

        // when
        final UnitDto newUnitDto = modelMapper.map(unit, UnitDto.class);

        // then
        assertThat(unit.getSegmentKey(), is(equalTo(newUnitDto.getSegmentKey())));
        assertThat(unit.getSource().getText(), is(equalTo(newUnitDto.getSource().getText())));
        assertThat(unit.getTarget().getText(), is(equalTo(newUnitDto.getTarget().getText())));
    }

    @Test
    public void map_unitdto_return_source_target() {
        // given
        final UnitDto unit = new UnitDto();
        unit.setSegmentKey("segmentKey");
        unit.setSerialNumber(666);
        final Source s = new Source();
        s.setText("source text");
        final Target t = new Target();
        t.setText("target text");
        unit.setSource(s);
        unit.setTarget(t);

        // when
        final Unit newUnit = modelMapper.map(unit, Unit.class);

        // then
        assertThat(unit.getSegmentKey(), is(equalTo(newUnit.getSegmentKey())));
        assertThat(unit.getSource().getText(), is(equalTo(newUnit.getSource().getText())));
        assertThat(unit.getTarget().getText(), is(equalTo(newUnit.getTarget().getText())));
    }

    @Test
    public void shouldInstantiateMapper() {
        assertThat(modelMapper, is(not(equalTo(null))));
    }

    @Test
    public void test() {
        // modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        modelMapper.validate();
    }
}
