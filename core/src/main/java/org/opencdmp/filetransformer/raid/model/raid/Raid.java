package org.opencdmp.filetransformer.raid.model.raid;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Raid implements Serializable {

    @JsonProperty("identifier")
    private Identifier identifier;

    @JsonProperty("date")
    private Date date;

    @JsonProperty("titles")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Title> titles;

    @JsonProperty("descriptions")
    private List<Description> descriptions;

    @JsonProperty("contributors")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<Contributor> contributors;

    @JsonProperty("organisations")
    private List<Organisation> organisations;

    @JsonProperty("relatedObjects")
    private List<RelatedObject> relatedObjects;

    @JsonProperty("alternateIdentifiers")
    private List<AlternateIdentifier> alternateIdentifiers;

    @JsonProperty("alternateUrls")
    private List<AlternativeUrl> alternateUrls;

    @JsonProperty("relatedRaids")
    private List<RelatedRaid> relatedRaids;

    @JsonProperty("access")
    @JsonAlias("Access")
    private Access access;


    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public List<Title> getTitles() {
        return titles;
    }

    public void setTitles(List<Title> titles) {
        this.titles = titles;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Description> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<Description> descriptions) {
        this.descriptions = descriptions;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    public List<RelatedObject> getRelatedObjects() {
        return relatedObjects;
    }

    public void setRelatedObjects(List<RelatedObject> relatedObjects) {
        this.relatedObjects = relatedObjects;
    }

    public List<AlternateIdentifier> getAlternateIdentifiers() {
        return alternateIdentifiers;
    }

    public void setAlternateIdentifiers(List<AlternateIdentifier> alternateIdentifiers) {
        this.alternateIdentifiers = alternateIdentifiers;
    }

    public List<AlternativeUrl> getAlternateUrls() {
        return alternateUrls;
    }

    public void setAlternateUrls(List<AlternativeUrl> alternateUrls) {
        this.alternateUrls = alternateUrls;
    }

    public List<RelatedRaid> getRelatedRaids() {
        return relatedRaids;
    }

    public void setRelatedRaids(List<RelatedRaid> relatedRaids) {
        this.relatedRaids = relatedRaids;
    }

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }
}
