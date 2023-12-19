library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers

valueset "Retrieve VS": 'http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.600.1.1623'

parameter "Measurement Period" Interval<DateTime>

context Patient

// Data Requirements should be able to be mapped back to the Element that is passed into the function
// In this case, the onset element can be traced back to the Condition that was passed into the prevalenceInterval function

// Why isn't onset being returned

//Expected Data Requirements
// Condition 
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - code, onset

define "Retrieve Def":
  [Condition: "Retrieve VS"] Retrieve
     where Retrieve.prevalenceInterval () overlaps before "Measurement Period"

define fluent function prevalenceInterval(condition Condition):
  Interval[start of condition.onset.toInterval(), end of condition.onset.toInterval()]

define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
  case
	  when choice is DateTime then
    	Interval[choice as DateTime, choice as DateTime]
		else
			null as Interval<DateTime>
	end