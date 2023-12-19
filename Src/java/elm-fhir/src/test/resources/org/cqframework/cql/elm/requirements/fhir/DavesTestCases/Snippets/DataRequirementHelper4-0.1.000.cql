library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'

parameter "Measurement Period" Interval<DateTime>

context Patient

// why does this return a must support for status.value

//Expected Data Requirements
// Procedure 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - code, status

define "Retrieve Def":
  [Procedure: "Retrieve VS"] Retrieve
    where Retrieve.status = 'completed'