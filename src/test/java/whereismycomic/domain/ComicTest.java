package whereismycomic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import whereismycomic.web.rest.TestUtil;

class ComicTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comic.class);
        Comic comic1 = new Comic();
        comic1.setId(1L);
        Comic comic2 = new Comic();
        comic2.setId(comic1.getId());
        assertThat(comic1).isEqualTo(comic2);
        comic2.setId(2L);
        assertThat(comic1).isNotEqualTo(comic2);
        comic1.setId(null);
        assertThat(comic1).isNotEqualTo(comic2);
    }
}
