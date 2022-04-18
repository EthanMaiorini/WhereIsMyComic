package whereismycomic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import whereismycomic.web.rest.TestUtil;

class SeriesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Series.class);
        Series series1 = new Series();
        series1.setId(1L);
        Series series2 = new Series();
        series2.setId(series1.getId());
        assertThat(series1).isEqualTo(series2);
        series2.setId(2L);
        assertThat(series1).isNotEqualTo(series2);
        series1.setId(null);
        assertThat(series1).isNotEqualTo(series2);
    }
}
