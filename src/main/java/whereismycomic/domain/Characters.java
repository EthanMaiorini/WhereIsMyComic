package whereismycomic.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Characters.
 */
@Entity
@Table(name = "characters")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Characters implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "thumbnail_content_type")
    private String thumbnailContentType;

    @OneToMany(mappedBy = "characters")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "comics", "characters" }, allowSetters = true)
    private Set<Series> series = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Characters id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return this.fullname;
    }

    public Characters fullname(String fullname) {
        this.setFullname(fullname);
        return this;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDescription() {
        return this.description;
    }

    public Characters description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getThumbnail() {
        return this.thumbnail;
    }

    public Characters thumbnail(byte[] thumbnail) {
        this.setThumbnail(thumbnail);
        return this;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailContentType() {
        return this.thumbnailContentType;
    }

    public Characters thumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
        return this;
    }

    public void setThumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
    }

    public Set<Series> getSeries() {
        return this.series;
    }

    public void setSeries(Set<Series> series) {
        if (this.series != null) {
            this.series.forEach(i -> i.setCharacters(null));
        }
        if (series != null) {
            series.forEach(i -> i.setCharacters(this));
        }
        this.series = series;
    }

    public Characters series(Set<Series> series) {
        this.setSeries(series);
        return this;
    }

    public Characters addSeries(Series series) {
        this.series.add(series);
        series.setCharacters(this);
        return this;
    }

    public Characters removeSeries(Series series) {
        this.series.remove(series);
        series.setCharacters(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Characters)) {
            return false;
        }
        return id != null && id.equals(((Characters) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Characters{" +
            "id=" + getId() +
            ", fullname='" + getFullname() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnail='" + getThumbnail() + "'" +
            ", thumbnailContentType='" + getThumbnailContentType() + "'" +
            "}";
    }
}
