library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

codesystem "SNOMEDCT": 'http://snomed.info/sct'

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'
valueset "Retrieve VS2": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1624'
valueset "Reason VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1625'

code "Reason Code": '720834000' from "SNOMEDCT" display 'Reason Code'

parameter "Measurement Period" Interval<DateTime>

context Patient

define "Retrieve Def":
  [ObservationNotDone: "Retrieve VS"] Retrieve
    with "Retrieve Def2" Retrieve2
      such that Retrieve.issued during Retrieve2.period
    where ( Retrieve.notDoneReason in "Reason VS"
           or Retrieve.notDoneReason ~ "Reason Code" )
      and Retrieve.status = 'cancelled'

define "Retrieve Def2":
    [Encounter: "Retrieve VS2"] Retrieve