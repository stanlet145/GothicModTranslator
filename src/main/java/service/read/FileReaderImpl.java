package service.read;

import com.google.common.io.Files;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FileReaderImpl implements FileReader {
    private final static String DOUBLE_SLASH = "//";
    private final static String DESCRIPTION = "description = ";
    private final static String B_LOG_ENTRY = "B_LogEntry(";
    private final static String INFO_ADD_CHOICE = "Info_AddChoice(";

    @Override
    public Map<String, String> readDoubleSlashesFromSingleFile(String fileName) {
        List<String> fileContent = getFileContent(readFileFromResources(fileName));

        List<String> linesContainingDoubleSlash = detectValidScenario(fileContent, DOUBLE_SLASH);
//        List<String> linesContainingDescription = detectValidScenario(fileContent, DESCRIPTION);
//        List<String> linesContainingBLogEntry = detectValidScenario(fileContent, B_LOG_ENTRY);
//        List<String> linesContainingInfoAddChoice = detectValidScenario(fileContent, INFO_ADD_CHOICE);
        return getMapContainingDoubleSlashes(linesContainingDoubleSlash);
    }

    @Override
    public List<String> readEntireFile(String fileName) {
        return getFileContent(readFileFromResources(fileName));
    }

    private Map<String, String> getMapContainingDoubleSlashes(List<String> linesContainingDoubleSlash) {
        var map = new LinkedHashMap<String, String>();
        linesContainingDoubleSlash.forEach(s -> map.put(getKeyForLine(s), getValueForLine(s)));
        return map;
    }

    public String getValueForLine(String line) {
        return StringUtils.substringAfter(line, DOUBLE_SLASH);
    }

    private String getKeyForLine(String line) {
        return StringUtils.substringBetween(line, "\"", "\"");
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

    private File readFileFromResources(String fileName) {
        return Try.of(() -> Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile())
                .onFailure(throwable -> System.out.println(throwable.getMessage()))
                .map(File::new)
                .getOrElseThrow((Supplier<NoSuchElementException>) NoSuchElementException::new);
    }
}
