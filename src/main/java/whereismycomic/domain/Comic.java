package whereismycomic.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Comic.
 */
@Entity
@Table(name = "comic")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Comic implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "issuenumber")
    private Integer issuenumber;

    @Column(name = "location")
    private String location;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "thumbnail")
    private byte[] thumbnail;

    @Column(name = "thumbnail_content_type")
    private String thumbnailContentType;

    @ManyToOne
    @JsonIgnoreProperties(value = { "comics", "characters" }, allowSetters = true)
    private Series series;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Comic id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIssuenumber() {
        return this.issuenumber;
    }

    public Comic issuenumber(Integer issuenumber) {
        this.setIssuenumber(issuenumber);
        return this;
    }

    public void setIssuenumber(Integer issuenumber) {
        this.issuenumber = issuenumber;
    }

    public String getLocation() {
        return this.location;
    }

    public Comic location(String location) {
        this.setLocation(location);
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTitle() {
        return this.title;
    }

    public Comic title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Comic description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getThumbnail() {
        return this.thumbnail;
    }

    public Comic thumbnail(byte[] thumbnail) {
        this.setThumbnail(thumbnail);
        return this;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailContentType() {
        return this.thumbnailContentType;
    }

    public Comic thumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
        return this;
    }

    public void setThumbnailContentType(String thumbnailContentType) {
        this.thumbnailContentType = thumbnailContentType;
    }

    public Series getSeries() {
        return this.series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    public Comic series(Series series) {
        this.setSeries(series);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Comic)) {
            return false;
        }
        return id != null && id.equals(((Comic) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Comic{" +
            "id=" + getId() +
            ", issuenumber=" + getIssuenumber() +
            ", location='" + getLocation() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", thumbnail='" + getThumbnail() + "'" +
            ", thumbnailContentType='" + getThumbnailContentType() + "'" +
            "}";
    }
}
