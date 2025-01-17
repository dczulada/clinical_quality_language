package org.opencds.cqf.cql.engine.elm.executing;

import org.opencds.cqf.cql.engine.exception.InvalidOperatorArgument;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.TemporalHelper;

/*
timezoneoffset from(argument DateTime) Decimal

NOTE: this is within the purview of DateTimeComponentFrom
  Description available in that class
*/

public class TimezoneOffsetFromEvaluator {

    public static Object timezoneOffsetFrom(Object operand) {
        if (operand == null) {
            return null;
        }

        if (operand instanceof DateTime) {
            return TemporalHelper.zoneToOffset(((DateTime) operand).getDateTime().getOffset());
        }

        throw new InvalidOperatorArgument(
                "TimezoneOffsetFrom(DateTime)",
                String.format("TimezoneOffsetFrom(%s)", operand.getClass().getName())
        );
    }

}
