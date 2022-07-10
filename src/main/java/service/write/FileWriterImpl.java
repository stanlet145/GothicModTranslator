package service.write;

import org.apache.commons.lang3.StringUtils;
import service.read.FileReader;
import service.read.FileReaderImpl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class FileWriterImpl implements FileWriter {

    private FileReader fileReader = new FileReaderImpl();

    @Override
    public void writeBetweenFiles(String from, String to) {
        StringBuilder sB = new StringBuilder();
        Map<String, String> sourceKeysAndValues = fileReader.readDoubleSlashesFromSingleFile(from);
        fileReader.readEntireFile(to)
                .forEach(line -> {
                            Optional<String> s = replaceValueForLineWhenKeyFound(line, sourceKeysAndValues);
                            if (areChangesDetected(s)) {
                                sB.append(s.get()).append(System.getProperty("line.separator"));
                            } else {
                                sB.append(line).append(System.getProperty("line.separator"));
                            }
                        }

                );
        System.out.println(sB);
    }


    private boolean areChangesDetected(Optional<String> line) {
        return line.isPresent();
    }

    private void printAllResults(String s) {
        System.out.println(s);
    }

    private Optional<String> replaceValueForLineWhenKeyFound(String line, Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(e -> StringUtils.contains(line, e.getKey()))
                .map(v -> replaceValues(v.getValue(), line))
                .findFirst();
    }

    private String replaceValues(String valueToReplace, String line) {
        String valueToBeReplaced = fileReader.getValueForLine(line);
        return StringUtils.replace(line, valueToBeReplaced, valueToReplace);
    }
}
