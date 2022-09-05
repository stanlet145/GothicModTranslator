package service.write;

import com.google.common.io.Files;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import service.read.FileReader;
import service.read.FileReaderImpl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static io.vavr.API.*;

public class FileWriterImpl implements FileWriter {

    private final FileReader fileReader = new FileReaderImpl();

    private static final String PREFIX_FOR_DESTINATION_FILE = "..\\files\\to\\";
    private static final String DESTINATION_PATH = "..\\files\\result\\";
    private static final String LINE_SEPARATOR = "line.separator";

    @Override
    public void writeBetweenFiles(List<String> allLinesFromFiles, String to) {
        var stringBuilder = new StringBuilder();
        var toFileContent = fileReader.readEntireFile(to);
        var slashesChanges = prepareDoubleSlashesChangesToWrite(stringBuilder, allLinesFromFiles, toFileContent);
        var descriptionChanges = prepareDescriptionChangesToWrite(stringBuilder, allLinesFromFiles, toFileContent);
        var allChangesMap = new LinkedHashMap<String, String>();
        allChangesMap.putAll(slashesChanges);
        allChangesMap.putAll(descriptionChanges);
        replaceLines(stringBuilder, toFileContent, allChangesMap);
        //  prepareDescriptionChangesToWrite(stringBuilder, fromFileContent, toFileContent);
        writeContentToFile(stringBuilder.toString(), to);
    }

    private Map<String, String> prepareDoubleSlashesChangesToWrite(StringBuilder stringBuilder, List<String> allLinesFromFiles, List<String> toFileContent) {
        return fileReader.readDoubleSlashesFromAllLinesFromFiles(allLinesFromFiles);
    }

    private void replaceLines(StringBuilder stringBuilder, List<String> toFileContent, Map<String, String> map) {
        toFileContent.forEach(line -> Try.of(() ->
                        replaceValueForLineWhenKeyWordFound(line, map))
                .peek(inlineChange -> Match(inlineChange.isPresent()).of(
                        Case($(true), () -> run(() -> buildLineFromChange(stringBuilder, Option.of(inlineChange.get()), line))),
                        Case($(false), () -> run(() -> buildLineFromChange(stringBuilder, Option.none(), line))))
                ));
    }

    private LinkedHashMap<String, String> prepareDescriptionChangesToWrite(StringBuilder stringBuilder, List<String> fileContent, List<String> toFileContent) {
        var sourceDescriptions = fileReader.readDescriptionsFromAllLinesFromFiles(fileContent);
        var mapOfDescriptions = new LinkedHashMap<String, String>();
        for (int i = 0; i < toFileContent.size(); i++) {
            int finalI = i;
            sourceDescriptions.forEach((s, s2) -> {
                if (toFileContent.get(finalI).contains(s)) {
                    for (int j = finalI; j < toFileContent.size(); j++) {
                        if (toFileContent.get(j).contains("description = \"")) {
                            System.out.println(toFileContent.get(j));
                            mapOfDescriptions.put(toFileContent.get(j), s2);
                            break;
                        }
                    }
                }
            });
        }
        return mapOfDescriptions;
    }

    private void writeContentToFile(String content, String to) {
        Try.run(() -> Files.write(content.getBytes(StandardCharsets.UTF_8), createDirectFile(to)))
                .onFailure(throwable -> System.out.println(throwable.getMessage()));
    }

    private File createDirectFile(String to) {
        return new File(StringUtils.replace(to, PREFIX_FOR_DESTINATION_FILE, DESTINATION_PATH));
    }

    private void buildLineFromChange(StringBuilder stringBuilder, Option<String> change, String line) {
        if (change.isDefined()) {
            stringBuilder.append(change.get()).append(System.getProperty(LINE_SEPARATOR));
        } else {
            stringBuilder.append(line).append(System.getProperty(LINE_SEPARATOR));
        }
    }

    private Optional<String> replaceValueForLineWhenKeyWordFound(String line, Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(keyWord -> StringUtils.contains(line, keyWord.getKey()))
                .map(valueToReplace -> replaceValues(valueToReplace.getValue(), line))
                .findFirst();
    }

    private Optional<String> replaceLineContainingPhrase(String line, List<String> sourceDescriptions) {
        return Optional.of("");
    }

    private String replaceValues(String valueToReplace, String line) {
        if (line.contains("description")) {
            return StringUtils.replace(line, line, valueToReplace);
        }
        return StringUtils.replace(line, fileReader.getValueForLine(line), valueToReplace);
    }
}
