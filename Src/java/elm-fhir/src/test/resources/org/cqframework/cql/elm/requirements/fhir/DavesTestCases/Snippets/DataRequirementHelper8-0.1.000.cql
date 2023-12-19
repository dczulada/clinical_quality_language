library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'
valueset "Retrieve VS2": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1624'

parameter "Measurement Period" Interval<DateTime>

context Patient

// why status.value

// http://hl7.org/fhir/StructureDefinition/bmi 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - value, status

define "Retrieve Def":
  ( ["MedicationAdministration": "Retrieve VS"] Retrieve1
      where Retrieve1.status in { 'in-progress', 'completed' }
      return {
        id: Retrieve1.id,
        effective: Retrieve1.effective
      }
  )
    union ( ["Procedure": "Retrieve VS2"] Retrieve2
        where Retrieve2.status = 'completed'
        return {
          id: Retrieve2.id,
          effective: Retrieve2.performed
        }
    )