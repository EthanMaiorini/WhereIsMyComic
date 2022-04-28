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
 * Criteria class for the {@link whereismycomic.domain.Series} entity. This class is used
 * in {@link whereismycomic.web.rest.SeriesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /series?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class SeriesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter comicId;

    private LongFilter charactersId;

    private Boolean distinct;

    public SeriesCriteria() {}

    public SeriesCriteria(SeriesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.comicId = other.comicId == null ? null : other.comicId.copy();
        this.charactersId = other.charactersId == null ? null : other.charactersId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public SeriesCriteria copy() {
        return new SeriesCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getComicId() {
        return comicId;
    }

    public LongFilter comicId() {
        if (comicId == null) {
            comicId = new LongFilter();
        }
        return comicId;
    }

    public void setComicId(LongFilter comicId) {
        this.comicId = comicId;
    }

    public LongFilter getCharactersId() {
        return charactersId;
    }

    public LongFilter charactersId() {
        if (charactersId == null) {
            charactersId = new LongFilter();
        }
        return charactersId;
    }

    public void setCharactersId(LongFilter charactersId) {
        this.charactersId = charactersId;
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
        final SeriesCriteria that = (SeriesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(comicId, that.comicId) &&
            Objects.equals(charactersId, that.charactersId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, comicId, charactersId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SeriesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (comicId != null ? "comicId=" + comicId + ", " : "") +
            (charactersId != null ? "charactersId=" + charactersId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
