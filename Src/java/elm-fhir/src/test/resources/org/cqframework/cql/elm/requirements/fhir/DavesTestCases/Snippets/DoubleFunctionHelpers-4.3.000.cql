library DoubleFunctionHelpers version '4.3.000'

using QICore version '4.1.1'

codesystem "ConditionClinicalStatusCodes": 'http://terminology.hl7.org/CodeSystem/condition-clinical'
code "active": 'active' from "ConditionClinicalStatusCodes"
code "recurrence": 'recurrence' from "ConditionClinicalStatusCodes"
code "relapse": 'relapse' from "ConditionClinicalStatusCodes"

context Patient

// define fluent function prevalenceInterval(condition Condition):
// if condition.clinicalStatus ~ "active"
// 	then
//   		Interval[start of condition.onset.toInterval(), end of condition.onset.toInterval()]
// 	else
//   		Interval[start of condition.onset.toInterval(), end of condition.onset.toInterval())

// define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
//   case
//     when choice is DateTime then Interval[choice as DateTime, choice as DateTime]
//     else null as Interval<DateTime>end

define fluent function prevalenceInterval(condition Condition):
if condition.clinicalStatus ~ "active"
  or condition.clinicalStatus ~ "recurrence"
  or condition.clinicalStatus ~ "relapse" then
  Interval[start of condition.onset.toInterval(), end of condition.abatementInterval()]
else
  Interval[start of condition.onset.toInterval(), end of condition.abatementInterval())

define fluent function abatementInterval(condition Condition):
	Interval[condition.abatement.low, condition.abatement.high)

define fluent function toInterval(choice Choice<DateTime, Quantity, Interval<DateTime>, Interval<Quantity>>):
  case
    when choice is DateTime then Interval[choice as DateTime, choice as DateTime]
    else null as Interval<DateTime>end