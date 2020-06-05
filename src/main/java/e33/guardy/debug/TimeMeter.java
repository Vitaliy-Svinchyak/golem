package e33.guardy.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TimeMeter {
    public static String MODULE_PATH_BUILDING = "PathBuilding";
    public static String MODULE_PATROL_PATH_BUILDING = "Patrol PathBuilding";
    final static Logger LOGGER = LogManager.getLogger();
    private static LinkedHashMap<String, FunctionCall> measuring = Maps.newLinkedHashMap();
    private static LinkedHashMap<String, FunctionCall> measuringTree = Maps.newLinkedHashMap();
    private static List<String> openedFunctions = Lists.newArrayList();

    public static void moduleStart(String moduleName) {
        start(moduleName);
    }

    public static void moduleEnd(String moduleName) {
        end(moduleName);
        List<FunctionCall> calls = new ArrayList<>(measuring.values());
        List<FunctionCall> treeCalls = new ArrayList<>(measuringTree.values());

        printCalls(calls);
        printCalls(treeCalls);

        measuring = Maps.newLinkedHashMap();
        measuringTree = Maps.newLinkedHashMap();
        openedFunctions = Lists.newArrayList();
    }

    private static void printCalls(List<FunctionCall> calls) {
        int maxFunctionNameLength = "name".length();
        int maxDurationLength = "duration".length();
        int maxCallLength = "calls".length();
        String nanos = calls.get(0).totalDuration.toString().substring(2);
        float totalNanos = Float.parseFloat(nanos.substring(0, nanos.length() - 1));

        for (FunctionCall call : calls) {
            if (getRealFunctionName(call.functionName).length() > maxFunctionNameLength) {
                maxFunctionNameLength = getRealFunctionName(call.functionName).length();
            }
            if (call.totalDuration.toString().length() > maxDurationLength) {
                maxDurationLength = call.totalDuration.toString().length();
            }
            if ((call.totalCalls + "").length() > maxCallLength) {
                maxCallLength = (call.totalCalls + "").length();
            }
        }

        LOGGER.info(addSpaceAtEnd("name", maxFunctionNameLength) + " | " + addSpaceAtEnd("duration", maxDurationLength) + " | " + "calls" + " | " + "percent" + " | " + "average");
        LOGGER.info(addSpaceAtEnd("-", maxFunctionNameLength + maxDurationLength + maxCallLength + 25, "-"));

        for (FunctionCall call : calls) {
            String cnanos = call.totalDuration.toString().substring(2);
            float callNanos = Float.parseFloat(cnanos.substring(0, cnanos.length() - 1));

            float percent = Math.round(callNanos / totalNanos * 100);
            String functionName = getRealFunctionName(call.functionName);
            LOGGER.info(addSpaceAtEnd(
                    functionName, maxFunctionNameLength) + " | "
                    + addSpaceAtEnd(call.totalDuration.toString().substring(2), maxDurationLength) + " | "
                    + addSpaceAtEnd(call.totalCalls + "", maxCallLength) + " | "
                    + addSpaceAtEnd(percent + "%", 6) + " | "
                    + call.getAverageDuration());
        }
    }

    private static String getRealFunctionName(String functionName) {
        if (functionName.indexOf('.') != -1) {
            String[] names = functionName.split("\\.");
            String realFunctionName = names[names.length - 1];
            functionName = addSpaceAtStart(realFunctionName, (names.length * 2) + realFunctionName.length(), '⮑');
        }

        return functionName;
    }

    private static String addSpaceAtEnd(String str, int neededLength, String... charToUse) {
        return str + repeatChar(neededLength - str.length(), charToUse.length == 0 ? " " : charToUse[0]);
    }

    private static String addSpaceAtStart(String str, int neededLength, char additionalCharacterBefore) {
        return repeatChar(neededLength - str.length(), " ") + additionalCharacterBefore + str;
    }

    private static String repeatChar(int neededLength, String charToUse) {
        return new String(new char[neededLength]).replace("\0", charToUse);
    }

    public static void start(String functionName) {
        if (measuring.get(functionName) == null) {
            measuring.put(functionName, new FunctionCall(functionName));
        }

        measuring.get(functionName).start();

        openedFunctions.add(functionName);
        String measuringTreeFunctionName = String.join(".", openedFunctions);
        if (measuringTree.get(measuringTreeFunctionName) == null) {
            measuringTree.put(measuringTreeFunctionName, new FunctionCall(measuringTreeFunctionName));
        }

        measuringTree.get(measuringTreeFunctionName).start();
    }

    public static void end(String functionName) {
        measuring.get(functionName).end();

        String measuringTreeFunctionName = String.join(".", openedFunctions);
        measuringTree.get(measuringTreeFunctionName).end();

        openedFunctions.remove(functionName);
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

        String getAverageDuration() {
            return Duration.ofNanos(this.totalDuration.toNanos() / this.totalCalls).toString().substring(2);
        }
    }
}
