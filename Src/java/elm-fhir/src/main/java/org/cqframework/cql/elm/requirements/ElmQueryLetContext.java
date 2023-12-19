package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

import java.util.ArrayList;
import java.util.List;

public class ElmQueryLetContext {
    public ElmQueryLetContext(VersionedIdentifier libraryIdentifier, LetClause letClause) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }
        if (letClause == null) {
            throw new IllegalArgumentException("letClause is required");
        }
        this.libraryIdentifier = libraryIdentifier;
        this.letClause = letClause;
    }

    private VersionedIdentifier libraryIdentifier;
    private LetClause letClause;
    public LetClause getLetClause() {
        return letClause;
    }
    public String getIdentifier() {
        return letClause.getIdentifier();
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
            this.requirements = ElmDataRequirement.inferFrom((ElmExpressionRequirement)requirements);
        }
        else {
            // Should never land here, but defensively...
            this.reportRequirement(new ElmDataRequirement(this.libraryIdentifier, new Retrieve()));
        }
        for (ElmDataRequirement requirement : this.getRequirements()){
            requirement.setQuerySource(getLetClause());
        }
    }

    public void reportProperty(ElmPropertyRequirement propertyRequirement) {
        for (ElmDataRequirement requirement : this.getRequirements()){
            requirement.reportProperty(propertyRequirement);
        }
    }
}
