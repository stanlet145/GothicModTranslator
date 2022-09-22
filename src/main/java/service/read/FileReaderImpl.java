package service.read;

import com.google.common.io.Files;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import service.utils.FileReadingUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FileReaderImpl implements FileReader {

    private final static String DOUBLE_SLASH = "//";
    private final static String DESCRIPTION = "description = \"";
    private final static String B_LOG_ENTRY = "B_LogEntry(";
    private final static String INFO_ADD_CHOICE = "Info_AddChoice(";

    @Override
    public Map<String, String> readDoubleSlashesFromAllLinesFromFiles(List<String> allLinesFromFiles) {
        return getMapContainingDoubleSlashes(detectValidScenario(allLinesFromFiles, DOUBLE_SLASH));
    }

    @Override
    public Map<String, String> readDescriptionsFromAllLinesFromFiles(List<String> allLinesFromFiles) {
        return getMapContainingScenario(detectValidScenario(allLinesFromFiles, DESCRIPTION), allLinesFromFiles, "instance ");
    }

    @Override
    public Map<String, String> readBLogEntriesFromSingleFile(List<String> allLinesFromFiles) {
        return getMapContainingScenario(detectValidScenario(allLinesFromFiles, B_LOG_ENTRY), allLinesFromFiles, "func ");
    }

    @Override
    public Map<String, String> readInfoAddChoicesFromSingleFile(List<String> allLinesFromFiles) {
        return getMapForAddChoice(detectValidScenario(allLinesFromFiles, INFO_ADD_CHOICE), allLinesFromFiles);
    }

    @Override
    public List<String> getFileContent(String fileName) {
        return getFileContent(readFileFromDirectory(fileName));
    }

    @Override
    public List<String> readEntireFile(String fileName) {
        return getFileContent(readFileFromDirectory(fileName));
    }

    @Override
    public String getValueForLine(String line) {
        return StringUtils.substringAfter(line, DOUBLE_SLASH);
    }

    private Map<String, String> getMapContainingDoubleSlashes(List<String> linesContainingDoubleSlash) {
        var map = new LinkedHashMap<String, String>();
        linesContainingDoubleSlash.forEach(s -> map.put(getKeyForDoubleSlashesLines(s), getValueForLine(s)));
        return map;
    }

    private Map<String, String> getMapContainingScenario(List<String> linesContainingDescriptions, List<String> allLinesForFiles, String argument) {
        var map = new LinkedHashMap<String, String>();
        linesContainingDescriptions
                .forEach(s -> {
                    String key = getKeyForArgument(s, allLinesForFiles, argument);
                    map.put(key, s);

                });
        return map;
    }

    private Map<String, String> getMapForAddChoice(List<String> linesContainingDescriptions, List<String> allLinesForFiles) {
        var map = new LinkedHashMap<String, String>();
        linesContainingDescriptions
                .forEach(s -> {
                    if (s.contains("\"")) {
                        String key = getKeyForArgument(s, allLinesForFiles, "func ");
                        String keyPart;
                        keyPart = getKeyPartForAddChoiceScenario(s);
                        key = keyPart;
                        map.put(key, s);
                    }
                });
        return map;
    }

    private String getKeyPartForAddChoiceScenario(String s) {
        String[] split = s.split("\"");
        return split[2].replace(");", "")
                .replace(",","");
    }

    private String getValueForLine(String line, String scenario) {
        return StringUtils.substringAfter(line, scenario);
    }

    private String getKeyForDoubleSlashesLines(String line) {
        return "\"" + StringUtils.substringBetween(line, "\"", "\"") + "\"";
    }

    private String getKeyForArgument(String line, List<String> allLinesForFiles, String argument) {
        //cofamy sie wstecz i szukamy nazwy instancji i ja zwracamy
        var nameOfInstance = "";
        for (int i = 0; i < allLinesForFiles.size(); i++) {
            if (allLinesForFiles.get(i).contains(line)) {
                for (int j = i; j > 0; j--) {
                    if (allLinesForFiles.get(j).contains(argument)) {
                        System.out.println(allLinesForFiles.get(j));
                        nameOfInstance = allLinesForFiles.get(j);
                        return nameOfInstance;
                    }
                }
            }
        }
        return nameOfInstance;
    }

    private List<String> detectValidScenario(List<String> lines, String detector) {
        return lines
                .stream()
                .filter(line -> StringUtils.contains(line, detector))
                .collect(Collectors.toList());
    }

    private List<String> getFileContent(File file) {
        return Try.of(() -> Files.readLines(file, StandardCharsets.UTF_8))
                .map(fileContent -> fileContent)
                .getOrElseThrow((Supplier<NoSuchElementException>) NoSuchElementException::new);
    }

    private File readFileFromDirectory(String fileName) {
        return Option.of(FileReadingUtils.tryReadAllFilesByPath(fileName).stream().findFirst().get())
                .getOrElseThrow(NoSuchElementException::new);
    }
}
