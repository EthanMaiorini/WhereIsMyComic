package whereismycomic.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Series.
 */
@Entity
@Table(name = "series")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Series implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "series")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "series", "characters" }, allowSetters = true)
    private Set<Comic> comics = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "series", "comics" }, allowSetters = true)
    private Characters characters;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Series id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Series name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Comic> getComics() {
        return this.comics;
    }

    public void setComics(Set<Comic> comics) {
        if (this.comics != null) {
            this.comics.forEach(i -> i.setSeries(null));
        }
        if (comics != null) {
            comics.forEach(i -> i.setSeries(this));
        }
        this.comics = comics;
    }

    public Series comics(Set<Comic> comics) {
        this.setComics(comics);
        return this;
    }

    public Series addComic(Comic comic) {
        this.comics.add(comic);
        comic.setSeries(this);
        return this;
    }

    public Series removeComic(Comic comic) {
        this.comics.remove(comic);
        comic.setSeries(null);
        return this;
    }

    public Characters getCharacters() {
        return this.characters;
    }

    public void setCharacters(Characters characters) {
        this.characters = characters;
    }

    public Series characters(Characters characters) {
        this.setCharacters(characters);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Series)) {
            return false;
        }
        return id != null && id.equals(((Series) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Series{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
