library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

parameter "Measurement Period" Interval<DateTime>

context Patient

// why status.value

// http://hl7.org/fhir/StructureDefinition/bmi 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - value, status

define "Retrieve Def":
  ["observation-bmi"] BMI
    where BMI.value > 0 'kg/m2'
      and BMI.status in { 'final', 'amended', 'corrected' }