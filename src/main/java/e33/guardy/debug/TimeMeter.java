package e33.guardy.debug;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TimeMeter {
    public static String MODULE_PATH_BUILDING = "PathBuilding";
    public static String MODULE_PATROL_PATH_BUILDING = "Patrol PathBuilding";
    final static Logger LOGGER = LogManager.getLogger();
    private static LinkedHashMap<String, FunctionCall> measuring = Maps.newLinkedHashMap();

    public static void moduleStart(String moduleName) {
        start(moduleName);
    }

    public static void moduleEnd(String moduleName) {
        end(moduleName);
        List<FunctionCall> calls = new ArrayList<>(measuring.values());
//        calls.sort(((c1, c2) -> (int) c2.totalDuration.minus(c1.totalDuration).toNanos()));

        int maxFunctionNameLength = "name".length();
        int maxDurationLength = "duration".length();
        int maxCallLength = "calls".length();
        String nanos = calls.get(0).totalDuration.toString().substring(2);
        float totalNanos = Float.parseFloat(nanos.substring(0, nanos.length() - 1));

        for (FunctionCall call : calls) {
            if (call.functionName.length() > maxFunctionNameLength) {
                maxFunctionNameLength = call.functionName.length();
            }
            if (call.totalDuration.toString().length() > maxDurationLength) {
                maxDurationLength = call.totalDuration.toString().length();
            }
            if ((call.totalCalls + "").length() > maxCallLength) {
                maxCallLength = (call.totalCalls + "").length();
            }
        }

        LOGGER.info(addSpace("name", maxFunctionNameLength) + " | " + addSpace("duration", maxDurationLength) + " | " + "calls" + " | " + "percent");
        LOGGER.info(addSpace("-", maxFunctionNameLength + maxDurationLength + maxCallLength + 16, "-"));

        for (FunctionCall call : calls) {
            String cnanos = call.totalDuration.toString().substring(2);
            float callNanos = Float.parseFloat(cnanos.substring(0, cnanos.length() - 1));

            float percent = Math.round(callNanos / totalNanos * 100);
            LOGGER.info(addSpace(call.functionName, maxFunctionNameLength) + " | " + addSpace(call.totalDuration.toString(), maxDurationLength) + " | " + addSpace(call.totalCalls + "", maxCallLength) + " | " + percent + "%");
        }
    }

    private static String addSpace(String str, int neededLength, String... charToUse) {
        return str + new String(new char[neededLength - str.length()]).replace("\0", charToUse.length == 0 ? " " : charToUse[0]);
    }

    public static void start(String functionName) {
        if (measuring.get(functionName) == null) {
            measuring.put(functionName, new FunctionCall(functionName));
        }

        measuring.get(functionName).start();
    }

    public static void end(String functionName) {
        measuring.get(functionName).end();
    }

    private static class FunctionCall {
        String functionName;
        Instant start = null;
        Duration totalDuration = Duration.ZERO;
        int totalCalls = 0;

        FunctionCall(String functionName) {
            this.functionName = functionName;
        }

        void start() {
            if (this.start != null) {
                LOGGER.error("Function " + this.functionName + " already started.");
            }

            this.start = Instant.now();
        }

        void end() {
            this.totalDuration = this.totalDuration.plus(Duration.between(this.start, Instant.now()));
            this.totalCalls++;
            this.start = null;
        }

        Duration getAverageDuration() {
            return Duration.ofNanos(this.totalDuration.getNano() / this.totalCalls);
        }
    }
}
