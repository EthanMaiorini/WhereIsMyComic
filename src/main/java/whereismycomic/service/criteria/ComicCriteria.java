package whereismycomic.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.BooleanFilter;
import tech.jhipster.service.filter.DoubleFilter;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.FloatFilter;
import tech.jhipster.service.filter.IntegerFilter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

/**
 * Criteria class for the {@link whereismycomic.domain.Comic} entity. This class is used
 * in {@link whereismycomic.web.rest.ComicResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /comics?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class ComicCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter issuenumber;

    private StringFilter location;

    private StringFilter title;

    private StringFilter description;

    private LongFilter seriesId;

    private Boolean distinct;

    public ComicCriteria() {}

    public ComicCriteria(ComicCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.issuenumber = other.issuenumber == null ? null : other.issuenumber.copy();
        this.location = other.location == null ? null : other.location.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.description = other.description == null ? null : other.description.copy();
        this.seriesId = other.seriesId == null ? null : other.seriesId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ComicCriteria copy() {
        return new ComicCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public IntegerFilter getIssuenumber() {
        return issuenumber;
    }

    public IntegerFilter issuenumber() {
        if (issuenumber == null) {
            issuenumber = new IntegerFilter();
        }
        return issuenumber;
    }

    public void setIssuenumber(IntegerFilter issuenumber) {
        this.issuenumber = issuenumber;
    }

    public StringFilter getLocation() {
        return location;
    }

    public StringFilter location() {
        if (location == null) {
            location = new StringFilter();
        }
        return location;
    }

    public void setLocation(StringFilter location) {
        this.location = location;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public StringFilter description() {
        if (description == null) {
            description = new StringFilter();
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public LongFilter getSeriesId() {
        return seriesId;
    }

    public LongFilter seriesId() {
        if (seriesId == null) {
            seriesId = new LongFilter();
        }
        return seriesId;
    }

    public void setSeriesId(LongFilter seriesId) {
        this.seriesId = seriesId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ComicCriteria that = (ComicCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(issuenumber, that.issuenumber) &&
            Objects.equals(location, that.location) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(seriesId, that.seriesId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, issuenumber, location, title, description, seriesId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ComicCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (issuenumber != null ? "issuenumber=" + issuenumber + ", " : "") +
            (location != null ? "location=" + location + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (description != null ? "description=" + description + ", " : "") +
            (seriesId != null ? "seriesId=" + seriesId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
