library DefHelpers version '4.3.000'

using QICore version '4.1.1'

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'

parameter "Measurement Period" Interval<DateTime>

define "Retrieve Def":
  [Condition: "Retrieve VS"] Retrieve
    where Retrieve.onset before "Measurement Period"
