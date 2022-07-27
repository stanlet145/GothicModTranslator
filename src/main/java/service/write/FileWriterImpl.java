package service.write;

import com.google.common.io.Files;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.apache.commons.lang3.StringUtils;
import service.read.FileReader;
import service.read.FileReaderImpl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static io.vavr.API.*;

public class FileWriterImpl implements FileWriter {

    private final FileReader fileReader = new FileReaderImpl();

    private static final String PREFIX_FOR_DESTINATION_FILE = "pl/";
    private static final String DESTINATION_PATH = "src/main/resources/direct/";
    private static final String LINE_SEPARATOR = "line.separator";

    @Override
    public void writeBetweenFiles(String from, String to) {
        var stringBuilder = new StringBuilder();
        var sourceKeysAndValues = fileReader.readDoubleSlashesFromSingleFile(from);
        fileReader.readEntireFile(to)
                .forEach(line -> Try.of(() -> replaceValueForLineWhenKeyWordFound(line, sourceKeysAndValues))
                        .peek(inlineChange -> Match(inlineChange.isPresent()).of(
                                Case($(true), () -> run(() -> buildLineFromChange(stringBuilder, Option.of(inlineChange.get()), line))),
                                Case($(), () -> run(() -> buildLineFromChange(stringBuilder, Option.none(), line))))
                        ));
        writeContentToFile(stringBuilder.toString(), to);
    }

    private void writeContentToFile(String content, String to) {
        Try.run(() -> Files.write(content.getBytes(StandardCharsets.UTF_8),
                        new File(StringUtils.replace(to, PREFIX_FOR_DESTINATION_FILE, DESTINATION_PATH))))
                .onFailure(throwable -> System.out.println(throwable.getMessage()));
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

    private String replaceValues(String valueToReplace, String line) {
        return StringUtils.replace(line, fileReader.getValueForLine(line), valueToReplace);
    }
}
