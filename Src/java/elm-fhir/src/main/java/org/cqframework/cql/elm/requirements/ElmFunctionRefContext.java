package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.AliasRef;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmFunctionRefContext {
    public ElmFunctionRefContext(ElmExpressionDefContext expressionDef, FunctionRef function) {
        if (expressionDef == null) {
            throw new IllegalArgumentException("");
        }
        this.expressionContext = expressionDef;
        if (expressionDef.inQueryContext()){
            this.queryContext = expressionDef.getCurrentQueryContext();
        }
        if (function == null) {
            throw new IllegalArgumentException("");
        }
        this.function = function;
        if (function.getOperand().get(0) instanceof AliasRef){
            AliasRef alias = (AliasRef)function.getOperand().get(0);
            this.localAlias = alias.getName();
        }
    }

    protected String localAlias;
    public String getLocalAlias() {
        return this.localAlias;
    }

    protected ElmExpressionDefContext expressionContext;
    public ElmExpressionDefContext getExpressionContext() {
        return this.expressionContext;
    }
    protected FunctionRef function;
    public FunctionRef getFunction() {
        return this.function;
    }

    protected ElmQueryContext queryContext;
    public ElmQueryContext getQueryContext() {
        return this.queryContext;
    }
    public void setQueryContext(ElmQueryContext queryContext) {
        this.queryContext = queryContext;
    }
}
