/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2024 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.griffin.engine.functions.groupby;

import io.questdb.cairo.CairoConfiguration;
import io.questdb.cairo.sql.Function;
import io.questdb.griffin.FunctionFactory;
import io.questdb.griffin.SqlException;
import io.questdb.griffin.SqlExecutionContext;
import io.questdb.std.IntList;
import io.questdb.std.ObjList;

import static io.questdb.griffin.engine.functions.groupby.ApproxPercentileDoubleGroupByFunctionFactory.checkAndReturnPrecision;
import static io.questdb.griffin.engine.functions.groupby.ApproxPercentileDoubleGroupByFunctionFactory.checkPercentile;

public class ApproxPercentileLongGroupByFunctionFactory implements FunctionFactory {

    @Override
    public String getSignature() {
        return "approx_percentile(LDi)";
    }

    @Override
    public boolean isGroupBy() {
        return true;
    }

    @Override
    public Function newInstance(
            int position,
            ObjList<Function> args,
            IntList argPositions,
            CairoConfiguration configuration,
            SqlExecutionContext sqlExecutionContext
    ) throws SqlException {
        final Function exprFunc = args.getQuick(0);
        final Function percentileFunc = args.getQuick(1);
        final Function precisionFunc = args.getQuick(2);

        checkPercentile(percentileFunc, argPositions.getQuick(1));
        final int precision = checkAndReturnPrecision(precisionFunc, argPositions.getQuick(2));

        if (precision > 2) {
            return new ApproxPercentileLongPackedGroupByFunction(exprFunc, percentileFunc, precision, position);
        }
        return new ApproxPercentileLongGroupByFunction(exprFunc, percentileFunc, precision, position);
    }
}
