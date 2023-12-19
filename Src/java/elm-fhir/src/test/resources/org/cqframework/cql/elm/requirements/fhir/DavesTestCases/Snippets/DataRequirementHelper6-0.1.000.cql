library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'
valueset "Retrieve VS2": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1624'

parameter "Measurement Period" Interval<DateTime>

context Patient

// Encounter 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - type, class
// Encounter 
//  Valueset - 2.16.840.1.113883.3.600.1.1624
//  Must Support - type, class

define "Retrieve Def":
  ([Encounter: "Retrieve VS"]
    union [Encounter: "Retrieve VS2"]) Retrieve
    return Tuple {
        foo: Retrieve.class
    }