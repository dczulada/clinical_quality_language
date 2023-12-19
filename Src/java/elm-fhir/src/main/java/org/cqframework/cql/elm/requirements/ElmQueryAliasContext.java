package org.cqframework.cql.elm.requirements;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ArrayList;

import org.hl7.elm.r1.AliasedQuerySource;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmQueryAliasContext {
    public ElmQueryAliasContext(VersionedIdentifier libraryIdentifier, AliasedQuerySource querySource) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }
        if (querySource == null) {
            throw new IllegalArgumentException("querySource is required");
        }
        this.libraryIdentifier = libraryIdentifier;
        this.querySource = querySource;
    }

    private VersionedIdentifier libraryIdentifier;
    private AliasedQuerySource querySource;
    public AliasedQuerySource getQuerySource() {
        return querySource;
    }
    public String getAlias() {
        return querySource.getAlias();
    }

    private List<ElmDataRequirement> requirements = new ArrayList<ElmDataRequirement>();
    public List<ElmDataRequirement> getRequirements() {
        return requirements;
    }
    public void reportRequirement(ElmDataRequirement requirement) {
        requirements.add(requirement);
    }

    public void setRequirements(ElmRequirement requirements) {
        if (requirements instanceof ElmDataRequirement) {
            this.reportRequirement((ElmDataRequirement)requirements);
        }
        else if (requirements instanceof ElmExpressionRequirement) {
            for (ElmDataRequirement requirement : ElmDataRequirement.inferFrom((ElmExpressionRequirement)requirements)){
                this.reportRequirement(requirement);
            }
            if (requirements instanceof ElmOperatorRequirement){
                ElmOperatorRequirement elmOp = (ElmOperatorRequirement)requirements;
                for (ElmRequirement requirement :  elmOp.getRequirements()){
                    if (requirement instanceof ElmDataRequirement){
                        this.reportRequirement((ElmDataRequirement)requirement);
                    }
                }
            }
        }
        else {
            // Should never land here, but defensively...
            this.reportRequirement(new ElmDataRequirement(this.libraryIdentifier, new Retrieve()));
        }
        for (ElmDataRequirement requirement : this.getRequirements()){
            requirement.setQuerySource(getQuerySource());
        }
    }

    public void reportProperty(ElmPropertyRequirement propertyRequirement) {
        for (ElmDataRequirement requirement : this.getRequirements()){
            requirement.reportProperty(propertyRequirement);
        }
    }
}
