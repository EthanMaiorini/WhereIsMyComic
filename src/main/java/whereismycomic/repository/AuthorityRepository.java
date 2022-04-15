package whereismycomic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import whereismycomic.domain.Authority;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {}
