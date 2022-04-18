package whereismycomic.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import whereismycomic.web.rest.TestUtil;

class CharactersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Characters.class);
        Characters characters1 = new Characters();
        characters1.setId(1L);
        Characters characters2 = new Characters();
        characters2.setId(characters1.getId());
        assertThat(characters1).isEqualTo(characters2);
        characters2.setId(2L);
        assertThat(characters1).isNotEqualTo(characters2);
        characters1.setId(null);
        assertThat(characters1).isNotEqualTo(characters2);
    }
}
