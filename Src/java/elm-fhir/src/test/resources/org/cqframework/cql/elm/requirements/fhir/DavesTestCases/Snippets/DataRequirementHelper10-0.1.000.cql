library DataRequirementHelper version '0.1.000'

using QICore version '4.1.1'

include FHIRHelpers version '4.3.000' called FHIRHelpers
include DefHelpers version '4.3.000' called DefHelpers

parameter "Measurement Period" Interval<DateTime>

context Patient

// Data Requirements should be able to be mapped back from nested definitions
// In this case, the onset element can be traced back to the Condition retrieves

//Why does this pattern return dupliates

//Expected Data Requirements
// Condition
//  Valueset - 2.16.840.1.113883.3.600.1.1623
//  Must Support - code, onset 

define "Parent Def":
  DefHelpers."Retrieve Def" Retrieve