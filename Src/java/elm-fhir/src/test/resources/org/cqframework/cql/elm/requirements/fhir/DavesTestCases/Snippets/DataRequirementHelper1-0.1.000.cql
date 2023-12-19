library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'

code "active": 'active' from "ConditionClinicalStatusCodes"

context Patient

// Data Requirements should be able to be mapped back to the Element that is passed into the function
// In this case, the clinicalStatus element can be traced back to the Condition that was passed into the isActive function

// Why isn't clinical status being returned

//Expected Data Requirements
// Condition 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - code, clinicalStatus

define "Retrieve Def":
  [Condition: "Retrieve VS"] Retrieve
     where Retrieve.isActive ( )

define fluent function isActive(condition Condition):
  condition.clinicalStatus ~ "active"