library FunctionHelpers version '4.3.000'

using QICore version '4.1.1'

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
code "active": 'active' from "ConditionClinicalStatusCodes"

define fluent function isActive(condition Condition):
    condition.clinicalStatus ~ "active"
